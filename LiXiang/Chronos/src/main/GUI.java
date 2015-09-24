package main;

//@import url("stylesheet.css");
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class GUI extends Application {

	private static final String WINDOW_TITLE = "Chronos V0.1";
	//private StackPane rootLayout;
	private BorderPane rootLayout;
	//private Logic logic;
	private static final String ROOT_LAYOUT_FXML = "RootLayout.fxml";
	private static CommandBarController commandBarController = null;
	private static Summary summary = null;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		initRootLayout();
		initPrimaryStage(primaryStage);
		//initLogic();
		
		addCommandBar(this);
		addSummary(this);
		
	}

	/*private void initLogic() {
		logic = new Logic;
		
	}*/

	private void addSummary(GUI gui) throws IOException{
		summary = new Summary(this);
		rootLayout.setCenter(summary);
		summary.addRows(getEvents());
		
	}

	private ObservableList<String> getEvents() {
		ObservableList<String> events = FXCollections.observableArrayList();
		events.add("Birthday Preparation");
		events.add("Meeting with Boss");
		return events;
	}

	private void addCommandBar(GUI gui) throws IOException {
		commandBarController = new CommandBarController(gui);
		rootLayout.setTop(commandBarController);
		commandBarController.displayMessage("Welcome");
		commandBarController.displayDate(getDate());
		
	}

	private void initRootLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ROOT_LAYOUT_FXML));
		//rootLayout = new StackPane();
		//Label date = new Label(getDate());
		//rootLayout.getChildren().add(date);
		 rootLayout = loader.load();
	}

	private String getDate() {
		Date date = new Date();
		SimpleDateFormat formatedDate = 
				new SimpleDateFormat ("E dd MMM yyyy");
		return "TODAY: " + formatedDate.format(date);
	}

	private void initPrimaryStage(Stage primaryStage) {
		primaryStage.setTitle(WINDOW_TITLE);
		
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void handleKeyPress(String text) {
		System.out.println(text);
		
		if(text.contains("detail")) {
			System.out.println("clear");
			summary.clear();
			summary.addRows(getNewEvents());
		}
		// TODO call logic
		//logic(text);
	}

	private ObservableList getNewEvents() {
		ObservableList<String> events = FXCollections.observableArrayList();
		events.add("Birthday Preparation\n buy cake");
		events.add("Meeting with Boss\n discuss project");
		return events;
	}
}
