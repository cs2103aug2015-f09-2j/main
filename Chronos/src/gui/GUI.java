//@@author A0115448E
package gui;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import application.Feedback;
import application.Instruction;
import application.Logic;
import application.Task;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.application.Platform;

import javax.swing.ImageIcon;

public class GUI extends Application implements NativeKeyListener {

	private static Stage _stage;

	private static final String WINDOW_TITLE = "Chronos V0.5";
	private static final String MESSAGE_WELCOME = "Welcome to Chronos V0.5! Where would you like Chronos to store your tasks and events?";
	private static final String MESSAGE_LOADED = "Welcome to Chronos V0.5! Add a task to get started.";
	private static final String ROOT_LAYOUT_FXML = "RootLayout.fxml";

	private static final int DATA_FIRST = 0;
	private static final int EXIT_NORMAL = 0;

	private static final String MESSAGE_SET_UP = "Chronos is set up properly";
	private static final String MESSAGE_SET_UP_FAIL = "Failed to set up Chrons";
	private static final String MESSAGE_DETAILED_VIEW_FAIL = "Failed to set up DetailedView Pane";
	private static final String MESSAGE_SUMMARY_FAIL = "Failed to set up Summary Pane";
	private static final String MESSAGE_COMMAND_BAR_FAIL = "Failed to set up Command Bar Pane";
	private static final String MESSAGE_TRAYICON_FAIL = "Failed to set up tray icon in system tray";
	private static final String MESSAGE_REGISTER_NATIVEHOOK_FAIL = "Failed to register nativehook";
	private static final String MESSAGE_UNREGISTER_NATIVEHOOK_FAIL = "Failed to unregister nativehook";
	private static final String MESSAGE_DIRECTORY = "Type the directory here";
	private static final String PATTERN_ADD = "add (description), (date), c:(category), p:(priority)";
	private static final String CLOSE_SYSTEM = "Exit";
	private static final String MESSAGE_ALARM = "%1$s\n%2$s\n%3$s\nis due soon";

	private BorderPane rootLayout;
	protected static Logic logic;
	private static CommandBarController commandBarController = null;
	private static SummaryController summary = null;
	private static DetailedViewController detailView = null;
	private static Logger log = Logger.getLogger("GUILog");
	private static Logger keyboardLogger;
	private boolean setUp = false;
	private boolean isControlPressed = false;
	private boolean isEnterPressed = false;
	private TrayIcon trayIcon;
	private SystemTray tray;

	boolean isHandlingCommand = false;
	private Instruction currentInstruction = null;

	private boolean _isNewUser;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			setUpBasicFrams(primaryStage);
			log.info(String.format(MESSAGE_SET_UP));
			checkNewUser();
			//perform proper closing of program
			primaryStage.setOnCloseRequest(e -> System.exit(EXIT_NORMAL));
		} catch (IOException e) {
			log.warning(MESSAGE_SET_UP_FAIL);
			assert(setUp == false);
		}
	}

	private void setUpBasicFrams(Stage primaryStage) throws IOException {
		initRootLayout();
		initPrimaryStage(primaryStage);
		initLogic();

		addCommandBar(this);
		addSummary();
	}

	// check if save file exists and display instruction depending on whether isNewUser
	private void checkNewUser() {
		if (logic.isSavePresent()) {
			_isNewUser = false;
			updateFeedback(logic.updateDisplay());
			commandBarController.prompText(PATTERN_ADD);
			commandBarController.displayFeedback(MESSAGE_LOADED);
		} else {
			_isNewUser = true;
			initNewUser();
		}
	}

	//if newUser, set up the directory first
	private void initNewUser() {
		commandBarController.prompText(MESSAGE_DIRECTORY);
		commandBarController.displayFeedback(MESSAGE_WELCOME);
		summary.setVisible(false);
	}

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

	private void initRootLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ROOT_LAYOUT_FXML));
		rootLayout = loader.load();
	}

	private void initPrimaryStage(Stage primaryStage) {
		primaryStage.setTitle(WINDOW_TITLE);
		Platform.setImplicitExit(false);
		Scene scene = new Scene(rootLayout);
		registerKeyboard();
		_stage = primaryStage;
		createTray(primaryStage);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// get items arrayList from Logic and print them out
	private void updateSummary(ArrayList<Task> eventList) {
		summary.display(eventList);
	}
	
	//@@author A0125424N-reused
	private void registerKeyboard() {
		 try {
	        	GlobalScreen.registerNativeHook();
	        	turnOffKeyboardLog();
	        }
	        catch (NativeHookException ex) {
	           log.warning(MESSAGE_REGISTER_NATIVEHOOK_FAIL);
	           handleCommand(CLOSE_SYSTEM);
	        }

	        GlobalScreen.addNativeKeyListener(new GUI());
	    }

	//@@author A0125424N-reused
    private void turnOffKeyboardLog() {
    	LogManager.getLogManager().reset();
    	keyboardLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    	keyboardLogger.setLevel(Level.OFF);
	}

	// @@author A0125424N-reused
	/**
	 * This method creates a tray and subsequently a tray icon for the
	 * application.
	 * @param primaryStage 
	 * 
	 * @param stage
	 * @param scene
	 */
	private void createTray(Stage primaryStage) {
		if (SystemTray.isSupported()) {
			primaryStage.getIcons().add(new Image("gui/logo.jpg"));
			tray = SystemTray.getSystemTray();
			ImageIcon image = null;
			image = new ImageIcon(getClass().getResource("./logo.jpg"));

			trayIcon = new TrayIcon(image.getImage());
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				log.warning(MESSAGE_TRAYICON_FAIL);
			}
		}
	}


	//@@author A0125424N
	public void nativeKeyPressed(final NativeKeyEvent e) {
		Platform.runLater((new Runnable() {
			@Override
			public void run() {
				if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL_R || e.getKeyCode() == NativeKeyEvent.VC_CONTROL_L) {
					isControlPressed = true;
					
				} else if (e.getKeyCode() == NativeKeyEvent.VC_ENTER) {
					isEnterPressed = true;
				} else {
					isControlPressed = false;
					isEnterPressed = false;
				}
				
				if (isControlPressed && isEnterPressed) {
					_stage.show();
					_stage.toFront();
					isControlPressed = false;
					isEnterPressed = false;
				}
				
				if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
		            hide(_stage);
		        }
			}		
		}));    
	}

	// @@author A0125424N
	/**
	 * This method brings the program to the back of other applications, and is
	 * run when called upon at any time.
	 * 
	 * @param stage
	 */
	private void hide(final Stage stage) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (SystemTray.isSupported()) {
					stage.hide();
				} else {
					handleCommand(CLOSE_SYSTEM);
				}
			}
		});
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

	//@@author A0131496A
	/**
	 * This method produce a pop-up window as the alarm.
	 * The pop-up contains the id and description of the task/event,
	 * and the start time for event or the due time for task.
	 * After the user closes the pop-up, the alarm is turned off.
	 * @param currentTask
	 */
	protected static void triggerAlarm(Task currentTask) {
		Stage dialog = new Stage();
		dialog.initOwner(_stage);
		VBox dialogVbox = createDialog(currentTask);
		Scene dialogScene = new Scene(dialogVbox, 300, 100);
		dialog.setScene(dialogScene);
		dialog.show();
		logic.switchOffAlarm(currentTask);
	}

	//@@author A0131496A
	private static VBox createDialog(Task currentTask) {
		VBox dialogVbox = new VBox();
		dialogVbox.alignmentProperty().set(Pos.CENTER);
		Text messageShown = getShownMessage(currentTask);
		dialogVbox.getChildren().add(messageShown);
		return dialogVbox;
	}

	//@@author A0131496A
	private static Text getShownMessage(Task currentTask) {
		String warnTime = logic.getAlarmOffset(currentTask);
		String message = String.format(MESSAGE_ALARM, currentTask.getId(), warnTime,currentTask.getDescription());
		Text messageShown = new Text(message);
		messageShown.setFont(Font.font("Arial"));
		messageShown.setTextAlignment(TextAlignment.CENTER);
		return messageShown;
	}

	private void updateFeedback(Feedback feedback) {
		if (feedback.isProgramExiting()) {
			try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e) {
				log.warning(MESSAGE_UNREGISTER_NATIVEHOOK_FAIL);
			}
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
			updateFeedback(logic.updateDisplay());
		}
		commandBarController.displayFeedback(feedback.getMessage());
	}

	public void handleCommandPattern(String text) {
		currentInstruction = Logic.getCommandInstruction(text);
		isHandlingCommand = true;
		handleCommandPattern();
	}

	public void handleCommandPattern() {
		// assert Instruction != null

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
			// do nothing
		}
	}

	public void retrieveNextCommand() {
		String pastCommand = Logic.getNextTypedCommand();
		try {
			commandBarController.displayTypedCommand(pastCommand);
		} catch (NullPointerException e) {
			// do nothing
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// To do nothing
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// To do nothing
	}

}