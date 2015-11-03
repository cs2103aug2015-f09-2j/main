package gui;

import java.io.IOException;

import application.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class FreeTimeDisplay extends BorderPane {
	private static final String FREE_TIME_DISPLAY_LAYOUT_FXML = "FreeTimeDisplayLayout.fxml";
	
	@FXML
	private TableView<String> freeTime;

	@FXML
	private TableColumn<String, String> startTime;

	@FXML
	private TableColumn<String, String> endTime;
	
	@FXML
	private TableColumn<String, String> tasks;

	public FreeTimeDisplay() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FREE_TIME_DISPLAY_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void display() {
		
	}
	
	
}
