package com.icbc.imganalyzer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * @author ZhijunXu
 * @version create time：2019年1月11日 上午9:29:49 class description
 */
public class ImageUtil {
	public static Mat bufferedImage2Mat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}
	public static byte[] bufferedImage2ByteArray(BufferedImage bi) {
		byte[] imageBytes = ((DataBufferByte) bi.getData().getDataBuffer()).getData();
		return imageBytes;
	}
}
