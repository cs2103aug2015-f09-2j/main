package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import application.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class DetailedView extends BorderPane {

	private static final String DETAIL_LAYOUT_FXML = "DetailedViewLayout.fxml";

	// private GUI gui;

	@FXML
	private TextFlow eventTitle;

	@FXML
    private ListView<String> details;

	public DetailedView(GUI gui) throws IOException {
		// this.gui = gui;
		FXMLLoader loader = new FXMLLoader(getClass().getResource(DETAIL_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		loader.load();

	}

	public void display(Task taskToView) {
		Text text1 = new Text(taskToView.getDescription());
		ListView<String> list = new ListView<String>();
		ObservableList<String> items =FXCollections.observableArrayList (
				"ID: "+taskToView.getId(), "Category: "+taskToView.getCategory(), 
				"Priority: "+taskToView.getPriority(), "Notes: "+taskToView.getDescription());
		/*ArrayList<String> notes = taskToView.getNotes();
		for (int i=0; i<notes.size(); i++) {
			items.addAll(notes.get(i));
		}*/
		details.setItems(items);
		text1.setFont(Font.font("",FontWeight.BOLD,30));

		text1.setTextAlignment(TextAlignment.CENTER);
		eventTitle.getChildren().addAll(text1);
	}

}
