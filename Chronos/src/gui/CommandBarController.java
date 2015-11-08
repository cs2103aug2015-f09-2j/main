//@@author A0115448E
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
	private final String DATE = "TODAY: %1$s";
	SimpleDateFormat formatedDate = 
			new SimpleDateFormat ("E dd MMM yyyy");
	private static final int ONE_WORD = 1;
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
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		displayDate();
		commandBar.requestFocus(); // get focus first
		System.out.println("ok");
		
	}

	//@@author A0126223U
	/**
     * Facilitates outcomes once certain keys are pressed
     *
     * @param requiredField text that will appear on the command bar upon typing
     */
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
		} else if (event.getCode() == KeyCode.UP) {
			gui.isHandlingCommand = false;
			gui.retrievePastCommand();
		} else if (event.getCode() == KeyCode.DOWN) {
			gui.isHandlingCommand = false;
			gui.retrieveNextCommand();
		}
		
		if(gui.isHandlingCommand && commandBar.getText().trim().equals("")) {
			commandBar.clear();
			gui.isHandlingCommand = false;
		}
	}
	
	private boolean hasOnlyOneWord() {
		String[] commands = commandBar.getText().split(" ");
		return commands.length == ONE_WORD;
	}

	//@@author A0115448E
	public void displayFeedback(String helpingText){
		feedback.setText(helpingText);
	}
	
	private void displayDate(){
		date.setText(getDate());
	}
	
	private String getDate() {
		Date date = new Date();
		return String.format(DATE, formatedDate.format(date));
	}

	//@@author A0126223U
	/**
     * Adds required fields onto the command bar as the user types
     *
     * @param requiredField text that will appear on the command bar upon typing
     */
	public void updateCommandBar(String requiredField) {
		if (!requiredField.equals("")) { //add a required field
			commandBar.requestFocus(); // get focus first
			commandBar.setEditable(false); //prevent deletion of the selection
			String commandText = commandBar.getText();
			int startingRange = commandText.length();
			if (hasAComma) {
				commandBar.appendText(", " + requiredField);
				startingRange += 2;
				hasAComma = false;
			} else {
				commandBar.appendText(" " + requiredField);
				startingRange++;
			}
			commandBar.selectRange(startingRange,  startingRange + requiredField.length());
		}
	}
	
	public void displayTypedCommand(String pastCommand) {
		commandBar.setText(pastCommand);
	}
	
	public void prompText (String text) {
		commandBar.setPromptText(text);
	}

}
