# Li Xiang
###### bin\gui\RootLayout.fxml
``` fxml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.text.*?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import java.lang.*?>

<BorderPane id="root" prefHeight="400.0" prefWidth="600.0" stylesheets="/gui/stylesheet.css" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
   <center>
      <StackPane prefHeight="150.0" prefWidth="200.0" BorderPane.alignment="CENTER" />
   </center>
</BorderPane>
```
###### bin\gui\stylesheet.css
``` css
#root { 
	-fx-padding: 10.0px; 
	} 
	
.labeled {
	-fx-text-alignment: center; 
}	

.table-cell {
  -fx-font: 10.0pt "Arial";
  -fx-alignment: center ;
}	

.clash { 
  -fx-control-inner-background: violet;
  -fx-accent: derive(-fx-control-inner-background, -40.0%);
  -fx-cell-hover-color: derive(-fx-control-inner-background, -20.0%);
  
}


.priorityHigh .text {
  -fx-fill: red;
}

.priorityMed .text {
  -fx-fill: green;
}

.done .text {
	-fx-strikethrough: true;
	-fx-fill: gray;
}

.txtFlow {
	-fx-font-size: 30;
	-fx-font-weight: bold;
	-fx-text-alignment: center;
	-fx-background-color: white;
	-fx-border-color: lightgrey;
}

.listView .text {
	-fx-font-size: 15;
}

.table {
	-fx-padding: -2em 0;
}

.overdue {
	/*-fx-control-inner-background: goldenrod;*/
	
	-fx-background-image: url("./exclamation.png");
	-fx-background-size: 20 25;
	-fx-background-repeat: stretch; 
	-fx-background-position: 235 center;
}

.overdue .text {
	 -fx-font-weight: bold;
	 -fx-font-style:italic; 
}

.autofill-text {
    -fx-skin: "np.com.ngopal.control.AutoFillTextBoxSkin";
}

.haveNotes  {
	-fx-background-image: url("./asterisk.png");
	-fx-background-size: 15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 485 center;
}

.alarm{
	-fx-background-image: url("./alarm.png");
	-fx-background-size: 15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 470 center;
}

.overdueHaveNotes {
	-fx-background-image: url("./exclamation.png"), url("./asterisk.png");
	-fx-background-size: 20 25, 15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 235 center, 485 center;
}

.alarmOverdue{
	-fx-background-image: url("./exclamation.png"), url("./alarm.png");
	-fx-background-size: 20 25,15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 235 center, 470 center;
}

.alarmHaveNotes{
	-fx-background-image: url("./alarm.png"), url("./asterisk.png");
	-fx-background-size: 15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 470 center, 485 center;
}

.alarmOverdueHaveNotes{
	-fx-background-image: url("./exclamation.png"),url("./alarm.png"), url("./asterisk.png");
	-fx-background-size: 20 25, 15 15, 15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 235 center, 470 center, 485 center;
}

.countdown {
	-fx-background-image: url("./countdown.jpg");
	-fx-background-size: 250 260;
	-fx-background-repeat: stretch;
	-fx-background-position: center center;
}

.time1 {
	-fx-font-size: 29;
	-fx-font-weight: 900;
	-fx-font-family: 'Brush Script MT', cursive;
}

.time2 {
	-fx-font-size: 23;
	 -fx-font-weight: bold;
	-fx-font-family: 'Brush Script MT', cursive;
}
```
###### src\gui\CommandBarController.java
``` java
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
	}

	//Trigger when something is keyed in
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

	public void displayFeedback(String helpingText){
		feedback.setText(helpingText);
	}
	
	private void displayDate(){
		date.setText(getDate());
	}
	
	private String getDate() {
		Date date = new Date();
		SimpleDateFormat formatedDate = 
				new SimpleDateFormat ("E dd MMM yyyy");
		return String.format(DATE, formatedDate.format(date));
	}

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

}
```
###### src\gui\RootLayout.fxml
``` fxml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.text.*?>
<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import java.lang.*?>

<BorderPane id="root" prefHeight="400.0" prefWidth="600.0" stylesheets="/gui/stylesheet.css" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1">
   <center>
      <StackPane prefHeight="150.0" prefWidth="200.0" BorderPane.alignment="CENTER" />
   </center>
</BorderPane>
```
###### src\gui\stylesheet.css
``` css
#root { 
	-fx-padding: 10.0px; 
	} 
	
.labeled {
	-fx-text-alignment: center; 
}	

.table-cell {
  -fx-font: 10.0pt "Arial";
  -fx-alignment: center ;
}	

.clash { 
  -fx-control-inner-background: violet;
  -fx-accent: derive(-fx-control-inner-background, -40.0%);
  -fx-cell-hover-color: derive(-fx-control-inner-background, -20.0%);
  
}


.priorityHigh .text {
  -fx-fill: red;
}

.priorityMed .text {
  -fx-fill: green;
}

.done .text {
	-fx-strikethrough: true;
	-fx-fill: gray;
}

.txtFlow {
	-fx-font-size: 30;
	-fx-font-weight: bold;
	-fx-text-alignment: center;
	-fx-background-color: white;
	-fx-border-color: lightgrey;
}

.listView .text {
	-fx-font-size: 15;
}

.table {
	-fx-padding: -2em 0;
}

.overdue {
	/*-fx-control-inner-background: goldenrod;*/
	
	-fx-background-image: url("./exclamation.png");
	-fx-background-size: 20 25;
	-fx-background-repeat: stretch; 
	-fx-background-position: 235 center;
}

.overdue .text {
	 -fx-font-weight: bold;
	 -fx-font-style:italic; 
}

.autofill-text {
    -fx-skin: "np.com.ngopal.control.AutoFillTextBoxSkin";
}

.haveNotes  {
	-fx-background-image: url("./asterisk.png");
	-fx-background-size: 15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 485 center;
}

.alarm{
	-fx-background-image: url("./alarm.png");
	-fx-background-size: 15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 470 center;
}

.overdueHaveNotes {
	-fx-background-image: url("./exclamation.png"), url("./asterisk.png");
	-fx-background-size: 20 25, 15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 235 center, 485 center;
}

.alarmOverdue{
	-fx-background-image: url("./exclamation.png"), url("./alarm.png");
	-fx-background-size: 20 25,15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 235 center, 470 center;
}

.alarmHaveNotes{
	-fx-background-image: url("./alarm.png"), url("./asterisk.png");
	-fx-background-size: 15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 470 center, 485 center;
}

.alarmOverdueHaveNotes{
	-fx-background-image: url("./exclamation.png"),url("./alarm.png"), url("./asterisk.png");
	-fx-background-size: 20 25, 15 15, 15 15;
	-fx-background-repeat: stretch; 
	-fx-background-position: 235 center, 470 center, 485 center;
}

.countdown {
	-fx-background-image: url("./countdown.jpg");
	-fx-background-size: 250 260;
	-fx-background-repeat: stretch;
	-fx-background-position: center center;
}

.time1 {
	-fx-font-size: 29;
	-fx-font-weight: 900;
	-fx-font-family: 'Brush Script MT', cursive;
}

.time2 {
	-fx-font-size: 23;
	 -fx-font-weight: bold;
	-fx-font-family: 'Brush Script MT', cursive;
}
```
