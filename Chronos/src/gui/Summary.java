package gui;

import java.io.IOException;
import java.util.ArrayList;

import application.Logic;
import application.Event;
import application.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public class Summary extends StackPane {

	private static final String SUMMARY_LAYOUT_FXML = "SummaryLayout.fxml";
	private static final String PRIORITY_HIGH_STYLE = "priorityHigh";
	private static final String PRIORITY_MED_STYLE = "priorityMed";
	private static final String COMPLETED_TASK_STYLE = "done";
	private static final String OVERDUE_STYLE = "overdue";
	private static final String CLASH_STYLE = "clash";
	private static final String HAVE_NOTES = "haveNotes";
	private static final String NOTES_OVERDUE = "overdueHaveNotes";

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
	private TableView<Event> eventTable;

	@FXML
	private TableColumn<Event, String> EIDCol;

	@FXML
	private TableColumn<Event, String> EtimeCol;

	@FXML
	private TableColumn<Event, String> EtitleCol;

	@FXML
	private TableColumn<Event, String> EcategoryCol;

	@FXML
	private TableColumn<Event, String> EnoteCol;

	@FXML
	private TableColumn<Event, String> EpriorityCol;

	@FXML
	private TableColumn<Event, String> EndtimeCol;

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
		ObservableList<Event> events = FXCollections.observableArrayList();
		for (int i = 0; i < eventList.size(); i++) {
			if(eventList.get(i) instanceof Event) {
				events.add((Event)eventList.get(i));
			} else {
				tasks.add((Task) eventList.get(i));
			}
		}
		setTaskColumns(tasks);
		setEventColumns(events);
		updateTaskStyle(priorityCol);
		updateEventStyle(EpriorityCol);
	}

	private void updateTaskStyle(TableColumn<Task, String> col) {
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
						Logic logic = Logic.getInstance();
						TableRow currentRow = getTableRow();
						Task currentTask = currentRow == null ? null : (Task) currentRow.getItem();
						if (item != null) {
							if (currentTask.getPriority().toLowerCase().contains("high")) {
								this.getTableRow().getStyleClass().add(PRIORITY_HIGH_STYLE);
							} else if (currentTask.getPriority().toLowerCase().contains("med")) {
								this.getTableRow().getStyleClass().add(PRIORITY_MED_STYLE);
							}
							if (currentTask.isOverdue()) {
								this.getTableRow().getStyleClass().add(OVERDUE_STYLE);
							}
							if (logic.checkForClashes(currentTask)) {
								this.getTableRow().getStyleClass().add(CLASH_STYLE);
							}
							
							if(currentTask.getNotesNo()>0) {
								this.getTableRow().getStyleClass().add(HAVE_NOTES);
							}
							
							if(currentTask.isOverdue()&&currentTask.getNotesNo()>0) {
								this.getTableRow().getStyleClass().add(NOTES_OVERDUE);
							}
							if (currentTask.isTaskComplete()) {
								cleanCurrentStyle(this.getTableRow());
								this.getTableRow().getStyleClass().add(COMPLETED_TASK_STYLE);
							}
						}
					}
				};
			}
		});
	}

	private void updateEventStyle(TableColumn<Event, String> col) {
		// update the colour for high-priority task and done task
		col.setCellFactory(new Callback<TableColumn<Event, String>, TableCell<Event, String>>() {
			@Override
			public TableCell<Event, String> call(TableColumn<Event, String> priority) {
				return new TableCell<Event, String>() {

					@Override
					public void updateItem(final String item, final boolean empty) {
						super.updateItem(item, empty);

						cleanCurrentStyle(this.getTableRow());

						addStyle(item);
					}

					// update the item and set a custom style if necessary
					private void addStyle(final String item) {
						
						Logic logic = Logic.getInstance();
						TableRow currentRow = getTableRow();
						Task currentTask = currentRow == null ? null : (Event) currentRow.getItem();
						if (item != null) {
							if (currentTask.getPriority().toLowerCase().contains("high")) {
								this.getTableRow().getStyleClass().add(PRIORITY_HIGH_STYLE);
							} else if (currentTask.getPriority().toLowerCase().contains("med")) {
								this.getTableRow().getStyleClass().add(PRIORITY_MED_STYLE);
							}
							
							if (currentTask.isOverdue()) {
								this.getTableRow().getStyleClass().add(OVERDUE_STYLE);
							}
							if (logic.checkForClashes(currentTask)) {
								this.getTableRow().getStyleClass().add(CLASH_STYLE);
							}	
							if(currentTask.getNotesNo()>0) {
								this.getTableRow().getStyleClass().add(HAVE_NOTES);
							}
							
							if(currentTask.isOverdue()&&currentTask.getNotesNo()>0) {
								this.getTableRow().getStyleClass().add(NOTES_OVERDUE);
							}
							if (currentTask.isTaskComplete()) {
								cleanCurrentStyle(this.getTableRow());
								this.getTableRow().getStyleClass().add(COMPLETED_TASK_STYLE);
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
		tableRow.getStyleClass().remove(HAVE_NOTES);
		tableRow.getStyleClass().remove(NOTES_OVERDUE);
		tableRow.getStyleClass().remove(OVERDUE_STYLE);
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

	private void setEventColumns(ObservableList<Event> events) {
		eventTable.setItems(events);
		EIDCol.setCellValueFactory(new PropertyValueFactory<Event, String>("id"));
		EtimeCol.setCellValueFactory(new PropertyValueFactory<Event, String>("startDate"));
		EndtimeCol.setCellValueFactory(new PropertyValueFactory<Event, String>("endDate"));
		EtitleCol.setCellValueFactory(new PropertyValueFactory<Event, String>("description"));
		EcategoryCol.setCellValueFactory(new PropertyValueFactory<Event, String>("category"));
		EnoteCol.setCellValueFactory(new PropertyValueFactory<Event, String>("notesNo"));
		EpriorityCol.setCellValueFactory(new PropertyValueFactory<Event, String>("priority"));
	}
}
