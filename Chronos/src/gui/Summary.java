package gui;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
//import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;

public class Summary extends StackPane {

	private static final String SUMMARY_LAYOUT_FXML = "SummaryLayout.fxml";

	// private GUI gui;

	@FXML
	private TableView<Item> summaryTable;

	@FXML
	private TableColumn<Item, String> IDCol;

	@FXML
	private TableColumn<Item, String> timeCol;

	@FXML
	private TableColumn<Item, String> titleCol;

	@FXML
	private TableColumn<Item, String> categoryCol;

	public Summary(GUI gui) throws IOException {
		// this.gui = gui;
		FXMLLoader loader = new FXMLLoader(getClass().getResource(SUMMARY_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void display(ObservableList<Item> events) {
		summaryTable.setItems(events);
		IDCol.setCellValueFactory(new PropertyValueFactory<Item, String>("ID"));
		timeCol.setCellValueFactory(new PropertyValueFactory<Item, String>("time"));
		titleCol.setCellValueFactory(new PropertyValueFactory<Item, String>("title"));
		categoryCol.setCellValueFactory(new PropertyValueFactory<Item, String>("category"));
	}

}
