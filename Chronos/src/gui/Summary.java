package gui;

import java.io.IOException;
import java.util.Collections;

//import javax.media.jai.remote.NegotiableCollection;

import application.Task;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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

	@FXML
	private TableColumn<Task, String> noteCol;

	@FXML
	private TableColumn<Task, String> priorityCol;

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
		noteCol.setCellValueFactory(new PropertyValueFactory<Task, String>("note"));
		priorityCol.setCellValueFactory(new PropertyValueFactory<Task, String>("priority"));

		/*summaryTable.setRowFactory(new Callback<TableView<Task>, TableRow<Task>>() {

			@Override
			public TableRow<Task> call(TableView<Task> tableView) {
				final TableRow<Task> row = new TableRow<Task>() {

					@Override
					protected void updateItem(Task event, boolean empty) {
						super.updateItem(event, empty);
						// TableRow currentRow = getTableRow();
						// Task currentTask = currentRow == null ? null :(Task)currentRow.getItem();
						if (true) {
							// if (event.getPriority() =="high") {

							getStyleClass().add("priorityHigh");
						} else
							System.out.println("Empty");
					}
				};

				return row;
			}
		});*/
		//TableColumn<Task, Boolean> column = new TableColumn<>("priority");
		priorityCol.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
		      @Override 
		      public TableCell<Task, String> call(TableColumn<Task, String> priority) {
		        return new TableCell<Task, String>() {
		          @Override 
		          public void updateItem(final String item, final boolean empty) {
		            super.updateItem(item, empty);
		         // clear any custom styles
		            this.getTableRow().getStyleClass().remove("priorityHigh");
		            this.getTableRow().getStyleClass().remove("priorityLow");
		           // update the item and set a custom style if necessary
		            if (item != null) {
		              setText(item.toString());
		              if(item.toString().contains("high")) {
		              this.getTableRow().getStyleClass().add(item.toString().contains("high") ? "priorityHigh" : "priorityLow");
		              }
		            }
		          }
		        };
		      }
		    });

		  
		/*
		 * summaryTable.setRowFactory(new Callback<TableView<Task>,
		 * TableRow<Task>>() {
		 * 
		 * @Override public TableRow<Task> call(TableView<Task> tableView) {
		 * final TableRow<Task> row = new TableRow<Task>() {
		 * 
		 * @Override protected void updateItem(Task person, boolean empty) {
		 * super.updateItem(person, empty); if (events.contains(getIndex())) {
		 * // if (! getStyleClass().contains("highlightedRow")) // {
		 * getStyleClass().add("priorityHigh"); // } } else { //
		 * getStyleClass().removeAll(Collections.singleton("highlightedRow")); }
		 * } }; return row; } });
		 */

		/*
		 * priorityCol.setCellFactory(new Callback<TableColumn<Task, String>,
		 * TableCell<Task, String>>() {
		 * 
		 * 
		 * 
		 * @Override public TableCell<Task, String> call(TableColumn<Task,
		 * String> param) { return new TableCell<Task, String>() {
		 * 
		 * @Override protected void updateItem(String name, boolean empty) {
		 * super.updateItem(name, empty); if (!empty) { if
		 * (name.contains("high")) { System.out.println("yeah");
		 * getStyleClass().add("priorityHigh");
		 * this.setTextFill(Color.BLUEVIOLET); } setText(name); } else {
		 * setText("empty"); // for debugging purposes } } }; } });
		 */
		/*
		 * priorityCol.setCellFactory(new Callback<TableColumn<Task, String>,
		 * TableCell<Task, String>>() { public TableCell<Task, String>
		 * call(TableColumn<Task, String> param) { return new TableCell<Task,
		 * String>() {
		 * 
		 * @Override public void updateItem(String item, boolean empty) {
		 * super.updateItem(item, empty); if (!isEmpty()) {
		 * this.setTextFill(Color.RED); // Get fancy and change color based on
		 * data if (item.contains("t")) this.setTextFill(Color.BLUEVIOLET);
		 * setText(item); } } }; } });
		 */

	}
}
