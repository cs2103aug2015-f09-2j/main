package gui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class CommandBarController extends BorderPane {

	private static final String COMMAND_BAR_LAYOUT_FXML = "CommandBarLayout.fxml";
	private GUI gui;

	@FXML
	private TextField commandBar;
	
	@FXML
	private Label feedback;
	
	@FXML
	private Label date;

	public CommandBarController(GUI gui) throws IOException {
		this.gui = gui;
		FXMLLoader loader = new FXMLLoader(getClass().getResource(COMMAND_BAR_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		loader.load();
		displayDate();
	}

	public void onKeyPress(KeyEvent event) throws IOException {
		if (event.getCode() == KeyCode.ENTER) {
			gui.handleCommand(commandBar.getText());
			commandBar.clear();
		} else if (event.getCode() == KeyCode.SPACE) {
			//System.out.println(commandBar.getText());
			gui.handleCommandPattern(commandBar.getText().trim());
		}
	}
	
	public void displayFeedback(String helpingText){
		feedback.setText(helpingText);
	}
	
	private void displayDate(){
		//System.out.println(todayDate);
		date.setText(getDate());
	}
	
	private String getDate() {
		Date date = new Date();
		SimpleDateFormat formatedDate = 
				new SimpleDateFormat ("E dd MMM yyyy");
		return "TODAY: " + formatedDate.format(date);
	}

	public void updateCommandBar(String commandPattern) {
		commandBar.appendText(commandPattern); //edit this
		commandBar.positionCaret(3);
	}

}
