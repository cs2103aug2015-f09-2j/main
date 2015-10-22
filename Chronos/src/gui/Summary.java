package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

//import javax.media.jai.remote.NegotiableCollection;

import application.Task;
import javafx.collections.FXCollections;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class Summary extends StackPane {

	private static final String SUMMARY_LAYOUT_FXML = "SummaryLayout.fxml";
	private static final String PRIORITY_HIGH_STYLE = "priorityHigh";
	private static final String PRIORITY_MED_STYLE = "priorityMed";
	private static final String COMPLETED_TASK_STYLE = "done";
	private static final String OVERDUE_STYLE = "overdue";

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
	@FXML
	private TableView<Task> eventTable;

	@FXML
	private TableColumn<Task, String> EIDCol;

	@FXML
	private TableColumn<Task, String> EtimeCol;

	@FXML
	private TableColumn<Task, String> EtitleCol;

	@FXML
	private TableColumn<Task, String> EcategoryCol;

	@FXML
	private TableColumn<Task, String> EnoteCol;

	@FXML
	private TableColumn<Task, String> EpriorityCol;

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

	public void display(ArrayList<Task> eventList) {
		ObservableList<Task> tasks = FXCollections.observableArrayList();
		ObservableList<Task> events = FXCollections.observableArrayList();
		for (int i = 0; i < eventList.size(); i++) {
			if (eventList.get(i).getId().contains("t")) {
				tasks.add(eventList.get(i));
			} else {
				events.add(eventList.get(i));
				System.out.println("yeah");
			}
		}
		setTaskColumns(tasks);
		setEventColumns(events);
		updateStyle(priorityCol);
		updateStyle(EpriorityCol);
	}

	private void updateStyle(TableColumn<Task, String> col) {
		// update the colour for high-priority task and done task
		col.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
			@Override
			public TableCell<Task, String> call(TableColumn<Task, String> priority) {
				return new TableCell<Task, String>() {

					@Override
					public void updateItem(final String item, final boolean empty) {
						super.updateItem(item, empty);

						cleanCurrentStyle(this.getTableRow());

						addStyle(item);
					}

					// update the item and set a custom style if necessary
					private void addStyle(final String item) {
						TableRow currentRow = getTableRow();
						Task currentTask = currentRow == null ? null : (Task) currentRow.getItem();
						if (item != null) {
							if (currentTask.getPriority().toLowerCase().contains("high")) {
								this.getTableRow().getStyleClass().add(PRIORITY_HIGH_STYLE);
							} else if (currentTask.getPriority().toLowerCase().contains("med")) {
								this.getTableRow().getStyleClass().add(PRIORITY_MED_STYLE);
							}
							if (currentTask.isTaskComplete() == true) {
								this.getTableRow().getStyleClass().add(COMPLETED_TASK_STYLE);
							}
							if (currentTask.isOverdue() == true) {
								this.getTableRow().getStyleClass().add(OVERDUE_STYLE);
							}
						}
					}
				};
			}
		});

		// if highlighting of a particular cell is needed
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

	// clear any custom styles
	private void cleanCurrentStyle(TableRow tableRow) {
		tableRow.getStyleClass().remove(PRIORITY_HIGH_STYLE);
		tableRow.getStyleClass().remove(PRIORITY_MED_STYLE);
	}

	private void setTaskColumns(ObservableList<Task> tasks) {
		summaryTable.setItems(tasks);
		IDCol.setCellValueFactory(new PropertyValueFactory<Task, String>("id"));
		timeCol.setCellValueFactory(new PropertyValueFactory<Task, String>("endDate"));
		titleCol.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
		categoryCol.setCellValueFactory(new PropertyValueFactory<Task, String>("category"));
		noteCol.setCellValueFactory(new PropertyValueFactory<Task, String>("notesNo"));
		priorityCol.setCellValueFactory(new PropertyValueFactory<Task, String>("priority"));
	}

	private void setEventColumns(ObservableList<Task> events) {
		eventTable.setItems(events);
		EIDCol.setCellValueFactory(new PropertyValueFactory<Task, String>("id"));
		EtimeCol.setCellValueFactory(new PropertyValueFactory<Task, String>("endDate"));
		EtitleCol.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
		EcategoryCol.setCellValueFactory(new PropertyValueFactory<Task, String>("category"));
		EnoteCol.setCellValueFactory(new PropertyValueFactory<Task, String>("notesNo"));
		EpriorityCol.setCellValueFactory(new PropertyValueFactory<Task, String>("priority"));
	}
}
