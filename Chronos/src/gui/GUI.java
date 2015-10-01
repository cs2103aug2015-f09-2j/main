package gui;

//@import url("stylesheet.css");
import java.io.IOException;

import application.Logic;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class GUI extends Application {

	private static final String WINDOW_TITLE = "Chronos V0.1";
	//private StackPane rootLayout;
	private BorderPane rootLayout;
	private Logic logic;
	private static final String ROOT_LAYOUT_FXML = "RootLayout.fxml";
	private static CommandBarController commandBarController = null;
	private static Summary summary = null;
	private static DetailedView detailView = null;
	
	private ObservableList<String> events = FXCollections.observableArrayList();
	
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
		//addDetailView(this);
		
	}

	private void addDetailView(GUI gui) throws IOException {
		detailView = new DetailedView(this);
		rootLayout.setCenter(detailView);
		detailView.display("cook dinner","cook fried rice with chicken and vegetable");
	}

	private void initLogic() {
		logic = new Logic();
		
	}

	private void addSummary(GUI gui) throws IOException{
		summary = new Summary(this);
		rootLayout.setCenter(summary);
		summary.display(getEvents());
		
	}

	private ObservableList<String> getEvents() {
		//ObservableList<String> events = FXCollections.observableArrayList();
		events.add("Birthday Preparation");
		events.add("Meeting with Boss");
		return events;
	}

	private void addCommandBar(GUI gui) throws IOException {
		commandBarController = new CommandBarController(gui);
		rootLayout.setTop(commandBarController);
		updateFeedback("welcome");
		
	}

	private void initRootLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ROOT_LAYOUT_FXML));
		//rootLayout = new StackPane();
		//Label date = new Label(getDate());
		//rootLayout.getChildren().add(date);
		 rootLayout = loader.load();
	}

	

	private void initPrimaryStage(Stage primaryStage) {
		primaryStage.setTitle(WINDOW_TITLE);
		
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void handleCommand(String text) throws IOException {
		//System.out.println(text);
		
		if(text.contains("detail")) {
			//summary.clear();
			//summary.display(updateSummary());
			//displayDetails(this);
			addDetailView(this);
		}
		if(text.contains("summary")) {
			addSummary(this);
		}
		
		updateFeedback(logic.executeUserCommand(text));
		updateSummary(logic.executeUserCommand(text));
		if(logic.isProgramExiting()){
			System.exit(0);
		}
	}
	
	private void updateSummary(String event) {
		events.add(event);
		summary.display(events);
		
	}

	private void updateSummary() {
		/*
		ObservableList<String> events = FXCollections.observableArrayList();
		events.add("Birthday Preparation and buy cake");
		events.add("Meeting with Boss and discuss project");
		*/
		summary.display(events);
	}
	
	private void updateFeedback (String feedback) {
		commandBarController.displayFeedback(feedback);
	}
}
