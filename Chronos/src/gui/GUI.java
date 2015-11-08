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
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.ImageIcon;

public class GUI extends Application implements NativeKeyListener {

	private static Stage _stage;

	private static final String WINDOW_TITLE = "Chronos V0.4";
	private static final String MESSAGE_WELCOME = "Welcome to Chronos V0.4! Where would you like Chronos to store your tasks and events?";
	private static final String MESSAGE_LOADED = "Welcome to Chronos V0.4! Add a task to get started.";
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

	private static final String CLOSE_SYSTEM = "Exit";

	private static final String MESSAGE_ALARM = "%1$s\n%2$s\nis due soon";

	private BorderPane rootLayout;
	protected static Logic logic;
	private static CommandBarController commandBarController = null;
	private static SummaryController summary = null;
	private static DetailedViewController detailView = null;
	private static Logger log = Logger.getLogger("GUILog");
	private static Logger keyboardLogger;
	private boolean setUp = false;
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
			initRootLayout();
			initPrimaryStage(primaryStage);
			initLogic();

			addCommandBar(this);
			addSummary();
			log.info(String.format(MESSAGE_SET_UP));

			checkNewUser();
			
			primaryStage.setOnCloseRequest(e -> System.exit(EXIT_NORMAL));
		} catch (IOException e) {
			log.warning(MESSAGE_SET_UP_FAIL);
			assert(setUp == false);
		}
	}

	// check if save file exists
	private void checkNewUser() {
		if (logic.isSavePresent()) {
			_isNewUser = false;
			updateFeedback(logic.updateDisplay());
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
		createTray();
		primaryStage.setScene(scene);
		primaryStage.show();
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
	 * 
	 * @param stage
	 * @param scene
	 */
	private void createTray() {
		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();
			ImageIcon image = null;
			image = new ImageIcon(getClass().getResource("/gui/logo.jpg"));

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
				if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
		            hide(_stage);
		        }
				if(e.getKeyCode() == NativeKeyEvent.VC_CONTROL_L) {
					_stage.show();
					_stage.toFront();
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

	protected static void triggerAlarm(Task currentTask) {
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(_stage);
		VBox dialogVbox = new VBox();
		dialogVbox.alignmentProperty().set(Pos.CENTER);
		String message = String.format(MESSAGE_ALARM, currentTask.getId(), currentTask.getDescription());
		Text messageShown = new Text(message);
		messageShown.setFont(Font.font("Verdana"));
		messageShown.setTextAlignment(TextAlignment.CENTER);
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