package com.icbc.imganalyzer.gui.main;

import com.github.sarxos.webcam.Webcam;
import com.icbc.imganalyzer.bean.Index;
import com.icbc.imganalyzer.datafx.ExtendedAnimatedFlowContainer;
import com.icbc.imganalyzer.gui.uicomponents.ItemUtil;
import com.icbc.imganalyzer.gui.uicomponents.TreeTableViewController;
import com.jfoenix.controls.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javax.annotation.PostConstruct;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.math.geometry.shape.Rectangle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@ViewController(value = "/fxml/Main.fxml", title = "Material Design Example")
public final class MainController implements Initializable {
	static {
//		Webcam.setDriver(new OpenImajDriver());
	}
	private static final HaarCascadeDetector detector = new HaarCascadeDetector();
	private static final Stroke STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
			new float[] { 1.0f }, 0.0f);

	private List<DetectedFace> faces = null;
	private BufferedImage troll = null;
	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML
	private StackPane root;

	@FXML
	private JFXDrawer drawer;

	@FXML
	Button btnStartCamera;

	@FXML
	Button btnStopCamera;

	@FXML
	Button btnDisposeCamera;

	@FXML
	ComboBox<WebCamInfo> cbCameraOptions;

	@FXML
	BorderPane bpWebCamPaneHolder;

	@FXML
	FlowPane fpBottomPane;

	@FXML
	ImageView imgWebCamCapturedImage;

	private class WebCamInfo {

		private String webCamName;
		private int webCamIndex;

		public String getWebCamName() {
			return webCamName;
		}

		public void setWebCamName(String webCamName) {
			this.webCamName = webCamName;
		}

		public int getWebCamIndex() {
			return webCamIndex;
		}

		public void setWebCamIndex(int webCamIndex) {
			this.webCamIndex = webCamIndex;
		}

		@Override
		public String toString() {
			return webCamName;
		}
	}

	private BufferedImage grabbedImage;
	private Webcam selWebCam = null;
	private boolean stopCamera = false;
	private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
	private String cameraListPromptText = "Choose Camera";

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		fpBottomPane.setDisable(true);
		ObservableList<WebCamInfo> options = FXCollections.observableArrayList();
		int webCamCounter = 0;
		for (Webcam webcam : Webcam.getWebcams()) {
			WebCamInfo webCamInfo = new WebCamInfo();
			System.out.println(webcam.getViewSizes()[webcam.getViewSizes().length - 3]);
			System.out.println(webcam.getViewSizes()[webcam.getViewSizes().length - 2]);
			System.out.println(webcam.getViewSizes()[webcam.getViewSizes().length - 1]);
//			webcam.setViewSize(new Dimension(640,480));
			webcam.setViewSize(webcam.getViewSizes()[webcam.getViewSizes().length - 2]);
			webCamInfo.setWebCamIndex(webCamCounter);
			webCamInfo.setWebCamName(webcam.getName());
			options.add(webCamInfo);
			webCamCounter++;
		}
		cbCameraOptions.setItems(options);
		cbCameraOptions.setPromptText(cameraListPromptText);
		cbCameraOptions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WebCamInfo>() {

			@Override
			public void changed(ObservableValue<? extends WebCamInfo> arg0, WebCamInfo arg1, WebCamInfo arg2) {
				if (arg2 != null) {
					System.out.println(
							"WebCam Index: " + arg2.getWebCamIndex() + ": WebCam Name:" + arg2.getWebCamName());
					initializeWebCam(arg2.getWebCamIndex());
				}
			}
		});
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setImageViewSize();
			}
		});
	}

	protected void setImageViewSize() {
		double height = bpWebCamPaneHolder.getHeight();
		double width = bpWebCamPaneHolder.getWidth();
		imgWebCamCapturedImage.setFitHeight(height);
		imgWebCamCapturedImage.setFitWidth(width);
		imgWebCamCapturedImage.prefHeight(height);
		imgWebCamCapturedImage.prefWidth(width);
		imgWebCamCapturedImage.setPreserveRatio(true);
	}

	protected void initializeWebCam(final int webCamIndex) {
		Task<Void> webCamIntilizer = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				if (selWebCam == null) {
					selWebCam = Webcam.getWebcams().get(webCamIndex);
					selWebCam.open();
				} else {
					closeCamera();
					selWebCam = Webcam.getWebcams().get(webCamIndex);
					selWebCam.open();
				}
				startWebCamStream();
				return null;
			}
		};
		new Thread(webCamIntilizer).start();
		fpBottomPane.setDisable(false);
		btnStartCamera.setDisable(true);
	}
	
	private static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
	private volatile static Future<Index> future ;
	protected void startWebCamStream() {
		stopCamera = false;
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				while (!stopCamera) {
					try {
						if ((grabbedImage = selWebCam.getImage()) != null) {
							faces = detector.detectFaces(ImageUtilities.createFImage(grabbedImage));
							Graphics2D g2 = grabbedImage.createGraphics();
							paint(g2);
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									final Image mainiamge = SwingFXUtils.toFXImage(grabbedImage, null);
									imageProperty.set(mainiamge);
								}
							});
							// Update UI here.
							//由于创建Index对象耗时长，
							if(future == null || future.isDone()) {
								future = singleThreadExecutor.submit(new Callable<Index>() {
									@Override
									public Index call() throws Exception {
										// TODO Auto-generated method stub
										return new Index(grabbedImage);
									}
								});
							}
							singleThreadExecutor.submit(new Runnable() {
								@Override
								public void run() {
									try {
										ItemUtil.setDummyData(future.get());
									} catch (InterruptedException | ExecutionException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}
							});
//							Platform.runLater(() -> {
//							});
							grabbedImage.flush();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		};
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		imgWebCamCapturedImage.imageProperty().bind(imageProperty);
	}

	private void paint(Graphics2D g2) {
		if (faces == null) {
			return;
		}

		Iterator<DetectedFace> dfi = faces.iterator();
		while (dfi.hasNext()) {
			DetectedFace face = dfi.next();
			Rectangle bounds = face.getBounds();
			int dx = (int) (0.1 * bounds.width);
			int dy = (int) (0.2 * bounds.height);
			int x = (int) bounds.x - dx;
			int y = (int) bounds.y - dy;
			int w = (int) bounds.width + 2 * dx;
			int h = (int) bounds.height + dy;
			g2.drawImage(troll, x, y, w, h, null);
			g2.setStroke(STROKE);
			g2.setColor(Color.RED);
			g2.drawRect(x, y, w, h);
		}
	}

	private void closeCamera() {
		if (selWebCam != null) {
			selWebCam.close();
		}
	}

	public void stopCamera(ActionEvent event) {
		stopCamera = true;
		btnStartCamera.setDisable(false);
		btnStopCamera.setDisable(true);
	}

	public void startCamera(ActionEvent event) {
		stopCamera = false;
		startWebCamStream();
		btnStartCamera.setDisable(true);
		btnStopCamera.setDisable(false);
	}

	public void disposeCamera(ActionEvent event) {
		stopCamera = true;
		closeCamera();
		btnStopCamera.setDisable(true);
		btnStartCamera.setDisable(true);
	}

	/**
	 * init fxml when loaded.
	 */
	@PostConstruct
	public void init() throws Exception {
		// create the inner flow and content
		context = new ViewFlowContext();
		// set the default controller
		Flow innerFlow = new Flow(TreeTableViewController.class);
		final FlowHandler flowHandler = innerFlow.createHandler(context);
		context.register("ContentFlowHandler", flowHandler);
		context.register("ContentFlow", innerFlow);
		final Duration containerAnimationDuration = Duration.millis(320);
		drawer.setContent(flowHandler
				.start(new ExtendedAnimatedFlowContainer(containerAnimationDuration, ContainerAnimations.SWIPE_LEFT)));
		context.register("ContentPane", drawer.getContent().get(0));
	}

}
