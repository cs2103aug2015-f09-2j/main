package gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class DetailedView extends BorderPane {

	private static final String DETAIL_LAYOUT_FXML = "DetailedViewLayout.fxml";

	//private GUI gui;

	@FXML
	private TextFlow eventTitle;
	
	@FXML
	private TextFlow description;
	
	public DetailedView(GUI gui) throws IOException {
		//this.gui = gui;
		FXMLLoader loader = new FXMLLoader(getClass().getResource(DETAIL_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		loader.load();

	}
	
	public void display(String title, String notes){
		Text text1 =  new Text(title);
		Text text2 =  new Text(notes);
		text1.setFont(Font.font ("Verdana", 30));
		text2.setFont(Font.font ("Verdana", 15));
		eventTitle.getChildren().addAll(text1);
		description.getChildren().addAll(text2);
	}


}

