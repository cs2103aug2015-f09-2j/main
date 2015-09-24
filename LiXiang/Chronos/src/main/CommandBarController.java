package main;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class CommandBarController extends BorderPane {

	private static final String COMMAND_BAR_LAYOUT_FXML = "CommandBarLayout.fxml";
	private GUI gui;

	@FXML
	private TextField commandBar;
	
	@FXML
	private Label helpingMessage;
	
	@FXML
	private Label date;

	public CommandBarController(GUI gui) throws IOException {
		this.gui = gui;
		FXMLLoader loader = new FXMLLoader(getClass().getResource(COMMAND_BAR_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		loader.load();

	}

	public void onKeyPress(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			gui.handleKeyPress(commandBar.getText());
			//System.out.println(commandBar.getText());
			commandBar.clear();
		}

	}
	
	public void displayMessage(String helpingText){
		helpingMessage.setText(helpingText);
	}
	
	public void displayDate(String todayDate){
		//System.out.println(todayDate);
		date.setText(todayDate);
	}

}
