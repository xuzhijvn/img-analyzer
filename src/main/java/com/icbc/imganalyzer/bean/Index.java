package com.icbc.imganalyzer.bean;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.HashMap;
import com.icbc.bas.ai.face.AgeCheck;

/**
* @author ZhijunXu
* @version create time：2019年1月7日 下午7:29:33
* class description
*/
public class Index extends HashMap<Integer, Object[]>{
	
	private static final long serialVersionUID = -1990508151760292564L;
	
	public final static Integer isDectectFace = 1;
	public final static Integer brightness = 2;
	public final static Integer blur = 3;
	public final static Integer isOccluded = 4;
	public final static Integer isGlasess = 5;
	public final static Integer gender = 6;
	public final static Integer age = 7;

	
	public Index() {
		// TODO Auto-generated constructor stub
	}
	
	public Index(BufferedImage grabbedImage) {
		byte[] img = bufferedImage2ByteArray(grabbedImage);
		//是否检测到人脸
		int[] g1 = new int[10];
		int[] g2 = new int[10];
		int[] g3 = new int[10];
		int[] g4 = new int[10];
		float[] g5 = new float[10];
		int g = AgeCheck.faceDetect(img, grabbedImage.getWidth(), grabbedImage.getHeight(), g1, g2, g3, g4, g5);
		Object[] obj = new Object[2];
		if (g == 1) {
			obj[0] = "Yes";
			obj[1] = true;
		}else {
			obj[0] = "No";
			obj[1] = false;
//			Tips.play(Tips.cant_dectect_face);
		}
		this.put(Index.isDectectFace, obj);
		//亮度
		float f1 = 50;
		float f2 = -50;
		int f = AgeCheck.predictBright(img, grabbedImage.getWidth(), grabbedImage.getHeight(), f1, f2);
		if (f > 0) {
			obj = new Object[2];
			if (f == 1) {
				obj[0] = "Ok";
				obj[1] = true;
			}
			if (f == 2) {
				obj[0] = "过亮";
				obj[1] = false;
			}
			if (f == 3) {
				obj[0] = "过暗";
				obj[1] = false;
			}
			this.put(Index.brightness, obj);
		}
		//口罩
		boolean c = AgeCheck.isMask(img, grabbedImage.getWidth(), grabbedImage.getHeight());
		obj = new Object[2];
		if (c == false) {
			obj[0] = "No";
			obj[1] = true;
		} else {
			obj[0] = "Yes";
			obj[1] = false;
//			if(g == 1) Tips.play(Tips.occluded);
		}
		this.put(Index.isOccluded, obj);
		//眼镜
		boolean b = AgeCheck.isGlasses(img, grabbedImage.getWidth(), grabbedImage.getHeight());
		obj = new Object[2];
		if (b == false) {
			obj[0] = "No";
			obj[1] = true;
		} else {
			obj[0] = "Yes";
			obj[1] = false;
//			if(g == 1) Tips.play(Tips.glasess);
		}
		this.put(Index.isGlasess, obj);
		//模糊度
		float[] d0 = new float[10];
		int d = AgeCheck.scoreBlur(img, grabbedImage.getWidth(), grabbedImage.getHeight(), d0);
		obj = new Object[2];
		if (d == 1) {
			obj[0] = "Ok";
			obj[1] = true;
		}
		this.put(Index.blur, obj);
		//年龄、性别
		float[] a1 = new float[10];
		float[] a2 = new float[10];
		AgeCheck.whatGenderAge(img, grabbedImage.getWidth(), grabbedImage.getHeight(), a1, a2);
		obj = new Object[2];
		obj[0] = Math.round(a1[0]);
		obj[1] = true;
		this.put(Index.age, obj);
		
		Object[] obj_1 = new Object[2];
		obj_1[1] = true;
		if (a2[0] == 1) {
			obj_1[0] = "男";
		} else {
			obj_1[0] = "女";
		}
		this.put(Index.gender, obj_1);
	}
	
	private static byte[] bufferedImage2ByteArray(BufferedImage bi) {
		byte[] imageBytes = ((DataBufferByte) bi.getData().getDataBuffer()).getData();
		return imageBytes;
	}
}
