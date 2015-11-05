package gui;

import javafx.geometry.Insets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.sun.prism.paint.Color;

import application.Feedback;
import application.Instruction;
import application.Logic;
import application.Task;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GUI extends Application {
	
	private static Stage _stage;

	private static final String WINDOW_TITLE = "Chronos V0.3";
	private static final String MESSAGE_WELCOME = "Welcome to Chronos V0.3! Where would you like Chronos to store your tasks and events?";
	private static final String MESSAGE_LOADED = "Welcome to Chronos V0.3! Add a task to get started.";
	private static final String ROOT_LAYOUT_FXML = "RootLayout.fxml";
	private static final String DISPLAY = "d";

	private static final int DATA_FIRST = 0;
	private static final int EXIT_NORMAL = 0;
	
	private static final String MESSAGE_SET_UP = "Chronos is set up properly";
	private static final String MESSAGE_SET_UP_FAIL = "Failed to set up Chrons";
	private static final String MESSAGE_DETAILED_VIEW_FAIL = "Failed to set up DetailedView Pane";
	private static final String MESSAGE_SUMMARY_FAIL = "Failed to set up Summary Pane";
	private static final String MESSAGE_COMMAND_BAR_FAIL = "Failed to set up Command Bar Pane";
	private static final String MESSAGE_FREE_TIME_DISPLAY_FAIL = "Failed to set up FreeTimeDisplay Pane";
	private static final String MESSAGE_ALARM = "%1$s\n%2$s\nis due soon";
	private BorderPane rootLayout;
	protected static Logic logic;
	private static CommandBarController commandBarController = null;
	private static SummaryController summary = null;
	private static DetailedViewController detailView = null;
	private static FreeTimeDisplayController freeTimeDisplay = null;
	private static Logger log = Logger.getLogger("GUILog");
	private boolean setUp = false;

	boolean isHandlingCommand = false;
	private Instruction currentInstruction = null;

	private boolean _isNewUser;

	public static void main(String[] args) {
		launch(args);
	}

	//load the root UI 
	@Override
	public void start(Stage primaryStage) {
		try {
			initRootLayout();
			initPrimaryStage(primaryStage);
			initLogic();
			addCommandBar(this);
			addSummary();
			log.info(String.format(MESSAGE_SET_UP));

			checkNewUser();
		} catch (IOException e) {
			log.warning(MESSAGE_SET_UP_FAIL);
			assert(setUp == false);
		}
	}

	// check if save file exists
	private void checkNewUser() {
		if (logic.isSavePresent()) {
			_isNewUser = false;
			updateFeedback(logic.executeUserCommand(DISPLAY));
			commandBarController.displayFeedback(MESSAGE_LOADED);
		} else {
			_isNewUser = true;
			initNewUser();
		}
	}

	private void initNewUser() {
		commandBarController.displayFeedback(MESSAGE_WELCOME);
		summary.setVisible(false);
	}

	//load the DetailedView frame and display the first task in the task array
	private void addDetailView(ArrayList<Task> data) {
		try {
			detailView = new DetailedViewController();
			rootLayout.setCenter(detailView);
			Task taskToView = data.get(DATA_FIRST);
			detailView.display(taskToView);
		} catch (IOException e) {
			log.warning(MESSAGE_DETAILED_VIEW_FAIL);
		}
	}

	private void initLogic() {
		logic = Logic.getInstance();
	}

	private void addSummary() {
		try {
			summary = new SummaryController();
			rootLayout.setCenter(summary);
		} catch (IOException e) {
			log.warning(MESSAGE_SUMMARY_FAIL);
		}
	}

	private void addCommandBar(GUI gui) {
		try {
			commandBarController = new CommandBarController(gui);
			rootLayout.setTop(commandBarController);
		} catch (IOException e) {
			log.warning(MESSAGE_COMMAND_BAR_FAIL);
		}
	}

	private void addFreeTimeDisplay() {
		try {
			freeTimeDisplay = new FreeTimeDisplayController();
			rootLayout.setCenter(freeTimeDisplay);
			freeTimeDisplay.display();
		} catch (IOException e) {
			log.warning(MESSAGE_FREE_TIME_DISPLAY_FAIL);
		}
	}

	private void initRootLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ROOT_LAYOUT_FXML));
		rootLayout = loader.load();
	}

	private void initPrimaryStage(Stage primaryStage) {
		primaryStage.setTitle(WINDOW_TITLE);

		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
		_stage = primaryStage;
	}

	//this will be called by CommandBarController class if enter if detected and the command
	//is passed to this method
	public void handleCommand(String text) {
		if (_isNewUser) {
			updateFeedback(logic.setSavePath(text));
			summary.setVisible(true);
			_isNewUser = false;
		} else {
			// TODO: change this
			if (text.contains("free")) {
				addFreeTimeDisplay();
			} else {
				Feedback commandFeedback = logic.executeUserCommand(text);
				updateFeedback(commandFeedback);
			}
		}
	}
	
	protected static void triggerAlarm(Task currentTask){
		final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(_stage);
        VBox dialogVbox = new VBox();
        dialogVbox.alignmentProperty().set(Pos.CENTER);
        String message = String.format(MESSAGE_ALARM, currentTask.getId(),currentTask.getDescription());
        Text messageShown = new Text(message);
        messageShown.setFont(Font.font("Verdana"));
        dialogVbox.getChildren().add(messageShown);
        Scene dialogScene = new Scene(dialogVbox, 300, 100);
        dialog.setScene(dialogScene);
        dialog.show();
        logic.switchOffAlarm(currentTask);
	}

	// get items arrayList from Logic and print them out
	private void updateSummary(ArrayList<Task> eventList) {
		summary.display(eventList);
	}

	private void updateFeedback(Feedback feedback) {
		if (feedback.isProgramExiting()) {
			System.exit(EXIT_NORMAL);
		}
		// choose between summary or detail view
		if (feedback.isInSummaryView()) {
			addSummary();
		} else {
			addDetailView(feedback.getData());
		}
		if (feedback.hasData()) {
			updateSummary(feedback.getData());
		} else {
			// update display
			updateFeedback(logic.executeUserCommand(DISPLAY)); 
		}
		commandBarController.displayFeedback(feedback.getMessage());
	}

	//called by CommandBarController if space is keyed in to show helping message
	public void handleCommandPattern(String text) {
		currentInstruction = Logic.getCommandInstruction(text);
		isHandlingCommand = true;
		handleCommandPattern();
	}

	//called by CommandBarController if comma is keyed in to show extra helping message
	public void handleCommandPattern() {
		// display to feedback String
		String feedbackString = currentInstruction.getCommandPattern();
		if (currentInstruction.hasInstructions()) {
			feedbackString = feedbackString + "\n" + currentInstruction.getNextInstruction();
		}
		commandBarController.displayFeedback(feedbackString);

		// display command pattern to Command Bar (ideal)
		commandBarController.updateCommandBar(currentInstruction.getNextRequiredField());

		currentInstruction.nextStep();

		if (currentInstruction.isFinished()) {
			isHandlingCommand = false;
			commandBarController.hasAComma = false;
		}
	}

	public void retrievePastCommand() {
		String pastCommand = Logic.getPreviouslyTypedCommand();
		try {
			commandBarController.displayTypedCommand(pastCommand);
		} catch (NullPointerException e) {
			//do nothing
		}
	}

	public void retrieveNextCommand() {
		String pastCommand = Logic.getNextTypedCommand();
		try {
			commandBarController.displayTypedCommand(pastCommand);
		} catch (NullPointerException e) {
			//do nothing
		}
	}	
	
}
