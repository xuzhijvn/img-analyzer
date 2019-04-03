package com.icbc.imganalyzer.gui.uicomponents;
/**
* @author ZhijunXu
* @version create time：2019年1月7日 下午5:24:42
* class description
*/

import com.icbc.imganalyzer.bean.Index;
import com.icbc.imganalyzer.gui.uicomponents.TreeTableViewController.Items;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableView;

public class ItemUtil {
	static ObservableList<Items> dummyData = FXCollections.observableArrayList();
	static TreeTableView<Items> treeTableView = null;
	
	public static void setDummyData(Index index) {
//		treeTableView.getColumns().get(0).setVisible(false);
//		treeTableView.getColumns().get(0).setVisible(true);
		dummyData.clear();

		dummyData.add(new Items(Items.IS_DECTECD_FACE,Items.IS_DECTECD_FACE_REFER, index.get(Index.isDectectFace)[0].toString()));
		dummyData.add(new Items(Items.BRIGHTNESS,Items.BRIGHTNESS_REFER, index.get(Index.brightness)[0].toString()));
		dummyData.add(new Items(Items.Blur,Items.Blur_REFER, index.get(Index.blur)[0].toString()));// 模糊度
		dummyData.add(new Items(Items.IS_GLASSES,Items.IS_GLASSES_REFER, index.get(Index.isGlasess)[0].toString()));
		dummyData.add(new Items(Items.IS_OCCLUDED,Items.IS_OCCLUDED_REFER, index.get(Index.isOccluded)[0].toString()));// 是否遮挡
		dummyData.add(new Items(Items.Gender,Items.Gender_REFER, index.get(Index.gender)[0].toString()));
		dummyData.add(new Items(Items.AGE,Items.AGE_REFER, index.get(Index.age)[0].toString()));
		
	}
	
	public static ObservableList<Items> initDummyData(TreeTableView<Items> treeTableView) {
		ItemUtil.treeTableView = treeTableView;
		dummyData.add(new Items(Items.IS_DECTECD_FACE,Items.IS_DECTECD_FACE_REFER));
		dummyData.add(new Items(Items.BRIGHTNESS,Items.BRIGHTNESS_REFER));
		dummyData.add(new Items(Items.Blur,Items.Blur_REFER));
		dummyData.add(new Items(Items.IS_GLASSES,Items.IS_GLASSES_REFER));
		dummyData.add(new Items(Items.IS_OCCLUDED,Items.IS_OCCLUDED_REFER));
		dummyData.add(new Items(Items.Gender,Items.Gender_REFER));
		dummyData.add(new Items(Items.AGE,Items.AGE_REFER));
		return dummyData;
	}
}

