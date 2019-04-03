package com.icbc.imganalyzer;
import java.awt.Dimension;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;


public class WebcamPanelExample {

	public static void main(String[] args) throws InterruptedException {

		Webcam webcam = Webcam.getDefault();
		Dimension[] nonStandardResolutions = new Dimension[] {
				WebcamResolution.PAL.getSize(),
				WebcamResolution.HD.getSize(),
				new Dimension(2000, 1000),
				new Dimension(1000, 500),
			};
		//@formatter:on

		// your camera have to support HD720p to run this code
		webcam.setCustomViewSizes(nonStandardResolutions);
		webcam.setViewSize(WebcamResolution.HD.getSize());
//		webcam.setViewSize(WebcamResolution.VGA.getSize());

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(true);

		JFrame window = new JFrame("Test webcam panel");
		window.add(panel);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
}