package com.icbc.imganalyzer;
/**
* @author ZhijunXu
* @version create time：2019年1月7日 上午11:04:01
* class description
*/
import java.util.ArrayList; 
import java.util.List; 
import java.util.Random; 
import java.util.function.Function; 

import javafx.application.Application; 
import javafx.beans.property.IntegerProperty; 
import javafx.beans.property.SimpleIntegerProperty; 
import javafx.beans.property.SimpleStringProperty; 
import javafx.beans.property.StringProperty; 
import javafx.beans.value.ObservableValue; 
import javafx.collections.ListChangeListener.Change; 
import javafx.css.PseudoClass; 
import javafx.scene.Scene; 
import javafx.scene.control.TreeItem; 
import javafx.scene.control.TreeTableColumn; 
import javafx.scene.control.TreeTableRow; 
import javafx.scene.control.TreeTableView; 
import javafx.stage.Stage; 

public class TreeTableViewHighlightSelectedPath extends Application { 

    private PseudoClass childOfSelected = PseudoClass.getPseudoClass("child-of-selected"); 
    private PseudoClass parentOfSelected = PseudoClass.getPseudoClass("parent-of-selected"); 

    @Override 
    public void start(Stage primaryStage) { 
     TreeTableView<Item> table = new TreeTableView<>(createRandomTree(50)); 

     table.setRowFactory(ttv -> { 
      TreeTableRow<Item> row = new TreeTableRow<Item>() { 
       @Override 
       protected void updateItem(Item item, boolean empty) { 
        super.updateItem(item, empty); 
        if (empty) { 
         pseudoClassStateChanged(parentOfSelected, false); 
         pseudoClassStateChanged(childOfSelected, false); 
        } else { 
         updateState(this); 
        } 
       } 
      }; 

      table.getSelectionModel().getSelectedItems().addListener(
        (Change<? extends TreeItem<Item>> c) -> updateState(row)); 

      return row ; 
     }); 

     table.getColumns().add(column("Item", Item::nameProperty)); 
     table.getColumns().add(column("Value", Item::valueProperty)); 

     Scene scene = new Scene(table, 800, 800); 
     scene.getStylesheets().add("table-row-highlight.css"); 
     primaryStage.setScene(scene); 
     primaryStage.show(); 

    } 

	private <T> void updateState(TreeTableRow<T> row) {
		TreeTableView<T> table = row.getTreeTableView();
		TreeItem<T> item = row.getTreeItem();

		// if item is selected, just use default "selected" highlight,
		// and set "child-of-selected" and "parent-of-selected" to false:
		if (item == null || table.getSelectionModel().getSelectedItems().contains(item)) {
			row.pseudoClassStateChanged(childOfSelected, false);
			row.pseudoClassStateChanged(parentOfSelected, false);
			return;
		}

		// check to see if item is parent of any selected item:
		for (TreeItem<T> selectedItem : table.getSelectionModel().getSelectedItems()) {
			for (TreeItem<T> parent = selectedItem.getParent(); parent != null; parent = parent.getParent()) {
				if (parent == item) {
					row.pseudoClassStateChanged(parentOfSelected, true);
					row.pseudoClassStateChanged(childOfSelected, false);
					return;
				}
			}
		}

		// check to see if item is child of any selected item:
		for (TreeItem<T> ancestor = item.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
			if (table.getSelectionModel().getSelectedItems().contains(ancestor)) {
				row.pseudoClassStateChanged(childOfSelected, true);
				row.pseudoClassStateChanged(parentOfSelected, false);
				return;
			}
		}

		// if we got this far, clear both pseudoclasses:

		row.pseudoClassStateChanged(childOfSelected, false);
		row.pseudoClassStateChanged(parentOfSelected, false);

	} 

    private <S,T> TreeTableColumn<S,T> column(String title, Function<S, ObservableValue<T>> property) { 
     TreeTableColumn<S,T> column = new TreeTableColumn<>(title); 
     column.setCellValueFactory(cellData -> property.apply(cellData.getValue().getValue())); 
     return column ; 
    } 

    private TreeItem<Item> createRandomTree(int numNodes) { 
     Random rng = new Random(); 
     TreeItem<Item> root = new TreeItem<>(new Item("Item 1", rng.nextInt(1000))); 
     root.setExpanded(true); 
     List<TreeItem<Item>> items = new ArrayList<>(); 
     items.add(root); 

     for (int i = 2 ; i <= numNodes; i++) { 
      Item item = new Item("Item "+i, rng.nextInt(1000)); 
      TreeItem<Item> treeItem = new TreeItem<>(item); 
      treeItem.setExpanded(true); 
      items.get(rng.nextInt(items.size())).getChildren().add(treeItem); 
      items.add(treeItem); 
     } 

     return root ; 
    } 

    public static class Item { 
     private StringProperty name = new SimpleStringProperty(); 
     private IntegerProperty value = new SimpleIntegerProperty(); 

     public Item(String name, int value) { 
      setName(name); 
      setValue(value); 
     } 

     public final StringProperty nameProperty() { 
      return this.name; 
     } 


     public final java.lang.String getName() { 
      return this.nameProperty().get(); 
     } 


     public final void setName(final java.lang.String name) { 
      this.nameProperty().set(name); 
     } 


     public final IntegerProperty valueProperty() { 
      return this.value; 
     } 


     public final int getValue() { 
      return this.valueProperty().get(); 
     } 


     public final void setValue(final int value) { 
      this.valueProperty().set(value); 
     } 

     @Override 
     public String toString() { 
      return String.format("%s (%d)", getName(), getValue()); 
     } 

    } 

    public static void main(String[] args) { 
     launch(args); 
    } 
} 