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
	boolean hasAComma = false;
	
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
		commandBar.requestFocus();
		commandBar.setEditable(true);
		if (event.getCode() == KeyCode.ENTER) {
			gui.handleCommand(commandBar.getText());
			commandBar.clear();
		} else if (event.getCode() == KeyCode.SPACE) {
			if(!gui.isHandlingCommand && hasOnlyOneWord()) {
				gui.handleCommandPattern(commandBar.getText().trim());
			}
		} else if (event.getCode() == KeyCode.COMMA) {
			if(gui.isHandlingCommand) {
				hasAComma = true;
				gui.handleCommandPattern();
			} 
		}
		
		if(commandBar.getText().trim().equals("")) {
			commandBar.clear();
			gui.isHandlingCommand = false;
		}
	}
	
	private boolean hasOnlyOneWord() {
		String[] commands = commandBar.getText().split(" ");
		return commands.length == 1;
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

	public void updateCommandBar(String requiredField) {
		if (!requiredField.equals("")) { //add a required field
			commandBar.requestFocus(); // get focus first
			commandBar.setEditable(false);
			String commandText = commandBar.getText();
			int startingRange = commandText.length();
			if (hasAComma) {
				commandBar.appendText(", " + requiredField);
				startingRange += 2;
				hasAComma = false;
			} else {
				commandBar.appendText(" " + requiredField); //edit this
				startingRange++;
			}
			commandBar.selectRange(startingRange,  startingRange + requiredField.length());
		}
	}

}
