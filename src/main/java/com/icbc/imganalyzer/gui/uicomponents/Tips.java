package com.icbc.imganalyzer.gui.uicomponents;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javazoom.jl.player.Player;

/**
* @author ZhijunXu
* @version create time：2019年4月1日 下午9:32:02
* class description
*/
public class Tips {
	
	public static final File cant_dectect_face = new File(Tips.class.getResource("/tips/cant-dectect-face.mp3").getFile());
	public static final File glasess = new File(Tips.class.getResource("/tips/glasess.mp3").getFile());
	public static final File occluded = new File(Tips.class.getResource("/tips/occluded.mp3").getFile());
	public static final File too_blur = new File(Tips.class.getResource("/tips/too-blur.mp3").getFile());
	public static final File too_bright = new File(Tips.class.getResource("/tips/too-bright.mp3").getFile());
	public static final File too_dark = new File(Tips.class.getResource("/tips/too-dark.mp3").getFile());
	
	private static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
	private static Future<Boolean> future;
	public static void play(File cant_dectect_face) {
		if(future == null || future.isDone()) {
			future = singleThreadExecutor.submit(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					// TODO Auto-generated method stub
					new Player(new FileInputStream(cant_dectect_face)).play();
					return true;
				}
			});
		}
	}

}
