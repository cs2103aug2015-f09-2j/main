//author: Li Xiang
package main;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class Summary extends BorderPane {

	private static final String SUMMARY_BAR_LAYOUT_FXML = "SummaryLayout.fxml";

	private GUI gui;

	@FXML
	private ListView summaryList;
	
	public Summary(GUI gui) throws IOException {
		this.gui = gui;
		FXMLLoader loader = new FXMLLoader(getClass().getResource(SUMMARY_BAR_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		loader.load();

	}
	
	public void addRows(ObservableList events){
		summaryList.setItems(events);
	}

	public void clear() {
		summaryList.getItems().clear();
	}
}
