package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import application.Feedback;
import application.Instruction;
import application.Logic;
import application.Task;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GUI extends Application {

	private static final String WINDOW_TITLE = "Chronos V0.3";
	private static final String MESSAGE_WELCOME = "Welcome to Chronos V0.3! Where would you like Chronos to store your tasks and events?";
	private static final String MESSAGE_LOADED = "Welcome to Chronos V0.3! Add a task to get started.";

	private static final String ROOT_LAYOUT_FXML = "RootLayout.fxml";

	private static final int DATA_FIRST = 0;

	private static final int EXIT_NORMAL = 0;
	private static final String MESSAGE_SET_UP = "Chronos is set up properly";
	private static final String MESSAGE_SET_UP_FAIL = "Failed to set up Chrons";
	private static final String MESSAGE_DETAILED_VIEW_FAIL = "Failed to set up DetailedView Pane";
	private static final String MESSAGE_SUMMARY_FAIL = "Failed to set up Summary Pane";
	private static final String MESSAGE_COMMAND_BAR_FAIL = "Failed to set up Command Bar Pane";
	
	private BorderPane rootLayout;
	private Logic logic;
	private static CommandBarController commandBarController = null;
	private static Summary summary = null;
	private static DetailedView detailView = null;
	private static Logger log = Logger.getLogger("GUILog");
	private boolean setUp = false;
	
	boolean isHandlingCommand = false;
	private Instruction currentInstruction = null;

	private boolean _isNewUser;
	private ObservableList<Task> events = FXCollections.observableArrayList();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			initRootLayout();
			initPrimaryStage(primaryStage);
			initLogic();

			addCommandBar(this);
			addSummary(this);
			log.info(String.format(MESSAGE_SET_UP));
			
			checkNewUser();
		} catch (IOException e) {
			log.warning(MESSAGE_SET_UP_FAIL);
			assert(setUp == false);
		}
	}

	// check if savefile exists
	private void checkNewUser() {	
		if (logic.isSavePresent()) {
			_isNewUser = false;
			updateFeedback(logic.executeUserCommand("d"));
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

	private void addDetailView(GUI gui, ArrayList<Task> data) {
		try {
			detailView = new DetailedView(this);
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

	private void addSummary(GUI gui) {
		try {
			summary = new Summary(this);
			rootLayout.setCenter(summary);
		} catch (IOException e) {
			log.warning(MESSAGE_SUMMARY_FAIL);
		}
	}

	/*
	 * private ObservableList<Task> getEvents() { ArrayList<Task> entries =
	 * logic.getTasks(); for (int i = 0; i < entries.size(); i++){
	 * events.add(entries.get(i)); } return events; }
	 */
	private void addCommandBar(GUI gui) {
		try {
			commandBarController = new CommandBarController(gui);
			rootLayout.setTop(commandBarController);
		} catch (IOException e) {
			log.warning(MESSAGE_COMMAND_BAR_FAIL);
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
	}

	public void handleCommand(String text) {
			if (_isNewUser) {
				updateFeedback(logic.setSavePath(text));
				summary.setVisible(true);
				_isNewUser = false;
			} else {
				Feedback commandFeedback = logic.executeUserCommand(text);
				updateFeedback(commandFeedback);
			}
		
	}

	// get items arrayList from Logic and print them out
	private void updateSummary(ArrayList<Task> eventList) {
		//events = FXCollections.observableArrayList(eventList);
		summary.display(eventList);
	}

	private void updateFeedback(Feedback feedback) {
		if (feedback.isProgramExiting()) {
			System.exit(EXIT_NORMAL);
		}
		// choose between summary or detail view
		if (feedback.isInSummaryView()) {
			addSummary(this);
		} else {
			addDetailView(this, feedback.getData());
		}
		if (feedback.hasData()) {
			updateSummary(feedback.getData());
		} else {
			// update display
			updateFeedback(logic.executeUserCommand("d")); //Logic: refactor this
		}
		commandBarController.displayFeedback(feedback.getMessage());
	}

	public void handleCommandPattern(String text) {
		currentInstruction = Logic.getCommandInstruction(text);
		isHandlingCommand = true;
		handleCommandPattern();
	}

	public void handleCommandPattern() {
		//assert Instruction != null
		
		//display to feedback String
		String feedbackString = currentInstruction.getCommandPattern();
		if (currentInstruction.hasInstructions()) {
			feedbackString = feedbackString + "\n" + currentInstruction.getNextInstruction();
		}
		commandBarController.displayFeedback(feedbackString);
		
		//display command pattern to Command Bar (ideal)
		commandBarController.updateCommandBar(currentInstruction.getNextRequiredField());
		
		currentInstruction.nextStep();
		
		if(currentInstruction.isFinished()) {
			isHandlingCommand = false;
			commandBarController.hasAComma = false;
		} 
	}
	
	
}
