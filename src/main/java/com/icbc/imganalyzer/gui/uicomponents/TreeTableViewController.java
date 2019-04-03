package com.icbc.imganalyzer.gui.uicomponents;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.datafx.controller.ViewController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javax.annotation.PostConstruct;
import java.util.function.Function;

/**
 * 
 * @author ZhijunXu
 * @version create time：2019年1月3日 上午10:41:21 class description
 */
@ViewController(value = "/fxml/ui/TreeTableView.fxml", title = "Material Design Example")
public class TreeTableViewController {

	private static final String PREFIX = "( ";
	private static final String POSTFIX = " )";

	// readonly table view
	@FXML
	public JFXTreeTableView<Items> treeTableView;
	@FXML
	private JFXTreeTableColumn<Items, String> nameColumn;
	@FXML
	private JFXTreeTableColumn<Items, String> referColumn;
	@FXML
	private JFXTreeTableColumn<Items, String> actualColumn;
	@FXML
	private JFXTextField searchField;

	@FXML
	private StackPane root;
	@FXML
	private Label treeTableViewCount;
	@FXML
	private VBox items;

	/**
	 * init fxml when loaded.
	 */
	@SuppressWarnings("static-access")
	@PostConstruct
	public void init() {
		root.setMargin(items, new Insets(0, 75, 0, 5));
		setupReadOnlyTableView();
	}

	private <T> void setupCellValueFactory(JFXTreeTableColumn<Items, T> column,
			Function<Items, ObservableValue<T>> mapper) {
		
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Items, T> param) -> {
			if (column.validateValue(param)) {
				return mapper.apply(param.getValue().getValue());
			} else {
				return column.getComputedValue(param);
			}
		});
	}
	private PseudoClass errorRow = PseudoClass.getPseudoClass("error-row"); 

	private void setupReadOnlyTableView() {
		setupCellValueFactory(nameColumn, Items::nameProperty);
		setupCellValueFactory(referColumn, Items::referProperty);
		setupCellValueFactory(actualColumn, Items::actualProperty);

		ObservableList<Items> dummyData = ItemUtil.initDummyData(treeTableView);

		treeTableView.setRowFactory(ttv -> {
			TreeTableRow<Items> row = new TreeTableRow<Items>() {
				@Override
				protected void updateItem(Items items, boolean empty) {

					super.updateItem(items, empty);
					if (empty) {
						pseudoClassStateChanged(errorRow, false);
					} else {
						String actual = items.actual.getValue();
						String refer = items.refer.getValue();
						String name = items.name.getValue();
						if(Items.isValid(name,refer, actual) ==  false) {
							pseudoClassStateChanged(errorRow, true);
						}else {
							pseudoClassStateChanged(errorRow, false);
						}
					}
				}
			};
			return row;
		});
		treeTableView.setRoot(new RecursiveTreeItem<>(dummyData, RecursiveTreeObject::getChildren));
		treeTableView.setShowRoot(false);
		treeTableViewCount.textProperty()
				.bind(Bindings.createStringBinding(() -> PREFIX + treeTableView.getCurrentItemsCount() + POSTFIX,
						treeTableView.currentItemsCountProperty()));

		searchField.textProperty().addListener(setupSearchField(treeTableView));
	}

	private ChangeListener<String> setupSearchField(final JFXTreeTableView<TreeTableViewController.Items> tableView) {
		return (o, oldVal, newVal) -> tableView.setPredicate(itemProp -> {
			final Items item = itemProp.getValue();
			return item.name.get().contains(newVal) || item.refer.get().contains(newVal)
					|| item.actual.get().contains(newVal);
		});
	}

	/*
	 * data class
	 */
	public static final class Items extends RecursiveTreeObject<Items> {
		final StringProperty name;
		final StringProperty refer;
		final StringProperty actual;
		
		public final static String IS_DECTECD_FACE = "是否检测到人脸?";
		public final static String BRIGHTNESS = "亮度";
		public final static String Blur = "模糊度";
		public final static String IS_GLASSES = "是否墨镜?";
		public final static String IS_OCCLUDED = "是否口罩?";
		public final static String Gender = "性别";
		public final static String AGE = "年龄";
		
		public final static String IS_DECTECD_FACE_REFER = "Yes";
		public final static String BRIGHTNESS_REFER  = "Ok";
		public final static String Blur_REFER  = "Ok";
		public final static String IS_GLASSES_REFER  = "No";
		public final static String IS_OCCLUDED_REFER  = "No";
		public final static String Gender_REFER  = "男/女";
		public final static String AGE_REFER  = ">0";
		
		public Items(String name, String refer, String actual) {
			this.name = new SimpleStringProperty(name);
			this.refer = new SimpleStringProperty(refer);
			this.actual = new SimpleStringProperty(actual);
		}
		
		public Items(String name, String refer) {
			this.name = new SimpleStringProperty(name);
			this.refer = new SimpleStringProperty(refer);
			this.actual = new SimpleStringProperty("");
		}

		StringProperty nameProperty() {
			return name;
		}

		StringProperty referProperty() {
			return refer;
		}

		StringProperty actualProperty() {
			return actual;
		}
		
		static boolean isValid(String name, String refer, String actual) {
			if(name.equals(IS_DECTECD_FACE) && actual.equals("No")) {
				Tips.play(Tips.cant_dectect_face);
				return false;
			}else if(name.equals(IS_OCCLUDED) && actual.equals("Yes")) {
				Tips.play(Tips.occluded);
				return false;
			}else if(name.equals(IS_GLASSES) && actual.equals("Yes")) {
				Tips.play(Tips.glasess);
				return false;
			}
			return true;
		}
	}
}
