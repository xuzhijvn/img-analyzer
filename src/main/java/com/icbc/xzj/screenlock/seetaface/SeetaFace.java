package com.icbc.xzj.screenlock.seetaface;
/**
* @author ZhijunXu
* @version create time：2019年1月11日 下午7:22:42
* class description
*/
public class SeetaFace {

	public static native float[] getFeature(byte[] imgColor, byte[] imgGray, int width, int height);
	
	public static native double getSimilarity(float[] feature1, float[] feature2);
}
