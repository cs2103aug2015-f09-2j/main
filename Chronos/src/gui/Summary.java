package gui;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
//import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

public class Summary extends StackPane {

	private static final String SUMMARY_LAYOUT_FXML = "SummaryLayout.fxml";

	//private GUI gui;

	@FXML
	private ListView<String> summaryList;
	
	public Summary(GUI gui) throws IOException {
		//this.gui = gui;
		FXMLLoader loader = new FXMLLoader(getClass().getResource(SUMMARY_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

	}
	
	public void display(ObservableList<String> events){
		summaryList.setItems((ObservableList<String>) events);
	}

	public void clear() {
		summaryList.getItems().clear();
	}
}
