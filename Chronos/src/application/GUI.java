package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUI extends Application{

	private TextField tfInput;
	private Text allTheText;
	private static Logic mainLogic;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
try {
			mainLogic = new Logic();

	
			BorderPane rootPane = new BorderPane();			
			
			//Command Pane
			tfInput = new TextField();
			rootPane.setTop(tfInput);
			
			//Text Feedback Pane
			//TODO: Edit this to fit User Guide
			ScrollPane textPane = new ScrollPane();
			allTheText = new Text("Launching skeleton..."); //should be the feedback String in the middle
			textPane.setContent(allTheText);
			rootPane.setCenter(textPane);
			
			//Event Handlers
			tfInput.setOnKeyPressed(new EventHandler<KeyEvent>(){
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.ENTER) {
						allTheText.setText(allTheText.getText() + "\n" + mainLogic.executeUserCommand(tfInput.getText()));
						tfInput.clear();
						textPane.setVvalue(1.0); //Set scrollpane to bottom
						if (mainLogic.isProgramExiting()) {
							System.exit(0);
						}
					}
				}
				
			});
			
			Scene scene = new Scene(rootPane,600,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}

}
