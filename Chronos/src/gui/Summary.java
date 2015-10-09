package gui;

import java.io.IOException;

import application.Task;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
//import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class Summary extends StackPane {

	private static final String SUMMARY_LAYOUT_FXML = "SummaryLayout.fxml";

	// private GUI gui;

	@FXML
	private TableView<Task> summaryTable;

	@FXML
	private TableColumn<Task, String> IDCol;

	@FXML
	private TableColumn<Task, String> timeCol;

	@FXML
	private TableColumn<Task, String> titleCol;

	@FXML
	private TableColumn<Task, String> categoryCol;

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

	public void display(ObservableList<Task> events) {
		summaryTable.setItems(events);
		IDCol.setCellValueFactory(new PropertyValueFactory<Task, String>("id"));
		timeCol.setCellValueFactory(new PropertyValueFactory<Task, String>("endDate"));
		titleCol.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
		categoryCol.setCellValueFactory(new PropertyValueFactory<Task, String>("category"));
		IDCol.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
			public TableCell<Task, String> call(TableColumn<Task, String> param) {
				return new TableCell<Task, String>() {

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (!isEmpty()) {
							this.setTextFill(Color.RED);
							// Get fancy and change color based on data
							if (item.contains("t"))
								this.setTextFill(Color.BLUEVIOLET);
							setText(item);
						}
					}
				};
			}
		});
	}

}
