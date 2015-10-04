package gui;

import java.io.IOException;

import application.Logic;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class GUI extends Application {

	private static final String WINDOW_TITLE = "Chronos V0.1";
	private BorderPane rootLayout;
	private Logic logic;
	private static final String ROOT_LAYOUT_FXML = "RootLayout.fxml";
	private static CommandBarController commandBarController = null;
	private static Summary summary = null;
	private static DetailedView detailView = null;

	private ObservableList<Item> events = FXCollections.observableArrayList();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		initRootLayout();
		initPrimaryStage(primaryStage);
		initLogic();

		addCommandBar(this);
		addSummary(this);

	}

	private void addDetailView(GUI gui) throws IOException {
		detailView = new DetailedView(this);
		rootLayout.setCenter(detailView);
		detailView.display("cook dinner", "cook fried rice with chicken and vegetable");
	}

	private void initLogic() {
		logic = new Logic();

	}

	private void addSummary(GUI gui) throws IOException {
		summary = new Summary(this);
		rootLayout.setCenter(summary);
		summary.display(getEvents());

	}

	private ObservableList<Item> getEvents() {
		events.add(new Item("e1", "4:00-6:00", "Birthday Celebration", "Personal"));
		events.add(new Item("e2", "1:00-2:00", "Meeting with boss", "Work"));
		return events;
	}

	private void addCommandBar(GUI gui) throws IOException {
		commandBarController = new CommandBarController(gui);
		rootLayout.setTop(commandBarController);
		updateFeedback("welcome");

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

	public void handleCommand(String text) throws IOException {

		if (text.contains("detail")) {
			addDetailView(this);
		}
		if (text.contains("summary")) {
			addSummary(this);
		}

		updateFeedback(logic.executeUserCommand(text));
		// updateSummary(logic.executeUserCommand(text));
		// logic may return a collection of item
		//TODO: add items individually to events
		if (logic.isProgramExiting()) {
			System.exit(0);
		}
	}

	private void updateSummary(Item newItem) {
		events.add(newItem);
		summary.display(events);

	}

	private void updateFeedback(String feedback) {
		commandBarController.displayFeedback(feedback);
	}
}
