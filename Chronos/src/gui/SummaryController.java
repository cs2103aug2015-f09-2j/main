//@@author A0115448E
package gui;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
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

public class SummaryController extends StackPane {

	private static final String SUMMARY_LAYOUT_FXML = "SummaryLayout.fxml";
	private static final String PRIORITY_HIGH_STYLE = "priorityHigh";
	private static final String PRIORITY_MED_STYLE = "priorityMed";
	private static final String COMPLETED_TASK_STYLE = "done";
	private static final String OVERDUE_STYLE = "overdue";
	private static final String CLASH_STYLE = "clash";
	private static final String HAVE_NOTES = "haveNotes";
	private static final String NOTES_OVERDUE = "overdueHaveNotes";
	private static final String ALARM = "alarm";
	private static final String ALARM_OVERDUE = "alarmOverdue";
	private static final String ALARM_NOTES = "alarmHaveNotes";
	private static final String ALARM_OVERDUE_NOTES = "alarmOverdueHaveNotes";
	private static final String HIGH_PRIORITY = "high";
	private static final String MED_PRIORITY = "med";
	private static final int MAX_ID_LENGTH = 3;
	private static final int PIXEL_PER_LETTER = 7;
	private static final int ID_COL_WIDTH = 35;
	private static final int TITLE_COL_WIDTH = 276;

	@FXML
	private TableView<Task> taskTable;

	@FXML
	private TableColumn<Task, String> TaskIDCol;

	@FXML
	private TableColumn<Task, String> TaskTimeCol;

	@FXML
	private TableColumn<Task, String> TaskTitleCol;

	@FXML
	private TableColumn<Task, String> TaskCategoryCol;

	@FXML
	private TableColumn<Task, String> TaskPriorityCol;

	@FXML
	private TableView<Event> eventTable;

	@FXML
	private TableColumn<Event, String> EventIDCol;

	@FXML
	private TableColumn<Event, String> EventTimeCol;

	@FXML
	private TableColumn<Event, String> EventTitleCol;

	@FXML
	private TableColumn<Event, String> EventCategoryCol;

	@FXML
	private TableColumn<Event, String> EventPriorityCol;

	@FXML
	private TableColumn<Event, String> EventEndTimeCol;

	public SummaryController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(SUMMARY_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

	}

	public void display(ArrayList<Task> eventList) {
		ObservableList<Task> tasks = FXCollections.observableArrayList();
		ObservableList<Event> events = FXCollections.observableArrayList();

		eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		addTasksInList(eventList, tasks, events);
		setTaskColumns(tasks);
		setEventColumns(events);
		updateTaskStyle(TaskPriorityCol);
		updateEventStyle(EventPriorityCol);
	}

	//categorise eventlists tasks into task and event and add into respective lists
	private void addTasksInList(ArrayList<Task> eventList, ObservableList<Task> tasks, ObservableList<Event> events) {
		int maxIdLen = 0;
		for (int i = 0; i < eventList.size(); i++) {
			if(eventList.get(i) instanceof Event) {
				events.add((Event)eventList.get(i));
			} else {
				tasks.add((Task) eventList.get(i));
			}
			if(eventList.get(i).getId().length()>maxIdLen) {
				maxIdLen = eventList.get(i).getId().length();
			}
		}
		if(maxIdLen > MAX_ID_LENGTH) { //if task id is too long
			updateColWidth(maxIdLen);
		}
	}

	//display the full id and compensate the increase in ID column width in title column
	private void updateColWidth(int maxIdLen) {
		TaskIDCol.setPrefWidth(ID_COL_WIDTH + PIXEL_PER_LETTER * (maxIdLen- MAX_ID_LENGTH));
		EventIDCol.setPrefWidth(ID_COL_WIDTH + PIXEL_PER_LETTER * (maxIdLen- MAX_ID_LENGTH));
		TaskTitleCol.setPrefWidth(TITLE_COL_WIDTH - PIXEL_PER_LETTER * (maxIdLen- MAX_ID_LENGTH));
		EventTitleCol.setPrefWidth(TITLE_COL_WIDTH - PIXEL_PER_LETTER * (maxIdLen- MAX_ID_LENGTH));
	}

	//update the style for task table
	private void updateTaskStyle(TableColumn<Task, String> col) {
		// update the colour for task under different conditions
		col.setCellFactory(new Callback<TableColumn<Task, String>, TableCell<Task, String>>() {
			@Override
			public TableCell<Task, String> call(TableColumn<Task, String> priority) {
				return new TableCell<Task, String>() {
					@Override
					public void updateItem(final String item, final boolean empty) {
						super.updateItem(item, empty);
						addStyle(item);
					}

					// update the item and set a custom style if necessary
					private void addStyle(final String item) {
						Logic logic = Logic.getInstance();
						TableRow<?> currentRow = getTableRow();
						Task currentTask = currentRow == null ? null : (Task) currentRow.getItem();
						if (item != null) {
							SummaryController.this.addStyle(logic, currentTask,currentRow);
						}
					}	
				};
			}
		});
	}

	//update style of event table
	private void updateEventStyle(TableColumn<Event, String> col) {
		// update the colour for events under different conditions
		col.setCellFactory(new Callback<TableColumn<Event, String>, TableCell<Event, String>>() {
			@Override
			public TableCell<Event, String> call(TableColumn<Event, String> priority) {
				return new TableCell<Event, String>() {
					@Override
					public void updateItem(final String item, final boolean empty) {
						super.updateItem(item, empty);
						addStyle(item);
					}

					// update the item and set a custom style if necessary
					private void addStyle(final String item) {
						Logic logic = Logic.getInstance();
						TableRow<?> currentRow = getTableRow();
						Task currentTask = currentRow == null ? null : (Event) currentRow.getItem();
						if (item != null) {
							SummaryController.this.addStyle(logic, currentTask,currentRow);
						}
					}
				};
			}
		});
	}


	private void addStyle(Logic logic, Task currentTask, TableRow<?> currentRow) {
		//priority
		if (currentTask.getPriority().toLowerCase().contains(HIGH_PRIORITY)) {
			currentRow.getStyleClass().add(PRIORITY_HIGH_STYLE);
		} else if (currentTask.getPriority().toLowerCase().contains(MED_PRIORITY)) {
			currentRow.getStyleClass().add(PRIORITY_MED_STYLE);
		}
		
		//clash
		if (logic.checkForClashes(currentTask)) {
			currentRow.getStyleClass().add(CLASH_STYLE);
		}
		
		//overdue and notes
		if (currentTask.isOverdue()) {
			currentRow.getStyleClass().add(OVERDUE_STYLE);
		}
		
		if(currentTask.getNotesNo()>0) {
			currentRow.getStyleClass().add(HAVE_NOTES);
		}
		
		if(currentTask.hasAlarm()){
			currentRow.getStyleClass().add(ALARM);
			checkAlarm(currentTask);
		}
		if(currentTask.isOverdue()&&currentTask.getNotesNo()>0) {
			currentRow.getStyleClass().add(NOTES_OVERDUE);
		}
		if(currentTask.isOverdue()&&currentTask.hasAlarm()) {
			currentRow.getStyleClass().add(ALARM_OVERDUE);
		}
		if(currentTask.hasAlarm()&&currentTask.getNotesNo()>0) {
			currentRow.getStyleClass().add(ALARM_NOTES);
		}
		if(currentTask.isOverdue()&&currentTask.getNotesNo()>0&&currentTask.hasAlarm()) {
			currentRow.getStyleClass().add(ALARM_OVERDUE_NOTES);
		}
		
		//completed task should not have any other style
		if (currentTask.isTaskComplete()) {
			cleanCurrentStyle(currentRow);
			currentRow.getStyleClass().add(COMPLETED_TASK_STYLE);
		}
	}
	
	//@@author A0131496A
	/**
	 * To be called when the currentTask has an alarm.
	 * This method calls GUI to give alarm if the current time is later than the alarm the user set
	 * @param currentTask
	 */
	private void checkAlarm(Task currentTask){
		Date alarm = currentTask.getAlarmDate();
		//format the current time the same way as the alarm for easy comparison
		new SimpleDateFormat(Task.DATE_FORMAT); 
		Date now = new Date();
		if (alarm.before(now)){
			GUI.triggerAlarm(currentTask);
		}
	}
	
	//@@author A0115448E
	// clear any custom styles
	private void cleanCurrentStyle(TableRow<?> tableRow) {
		tableRow.getStyleClass().remove(HAVE_NOTES);
		tableRow.getStyleClass().remove(NOTES_OVERDUE);
		tableRow.getStyleClass().remove(OVERDUE_STYLE);
		tableRow.getStyleClass().remove(ALARM);
		tableRow.getStyleClass().remove(ALARM_NOTES);
		tableRow.getStyleClass().remove(ALARM_OVERDUE);
		tableRow.getStyleClass().remove(ALARM_OVERDUE_NOTES);
	}

	//add information to Task table
	private void setTaskColumns(ObservableList<Task> tasks) {
		taskTable.setItems(tasks);
		TaskIDCol.setCellValueFactory(new PropertyValueFactory<Task, String>("id"));
		TaskTimeCol.setCellValueFactory(new PropertyValueFactory<Task, String>("endDate"));
		TaskTitleCol.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
		TaskCategoryCol.setCellValueFactory(new PropertyValueFactory<Task, String>("category"));
		TaskPriorityCol.setCellValueFactory(new PropertyValueFactory<Task, String>("priority"));
	}

	//add information to Event table
	private void setEventColumns(ObservableList<Event> events) {
		eventTable.setItems(events);
		EventIDCol.setCellValueFactory(new PropertyValueFactory<Event, String>("id"));
		EventTimeCol.setCellValueFactory(new PropertyValueFactory<Event, String>("startDate"));
		EventEndTimeCol.setCellValueFactory(new PropertyValueFactory<Event, String>("endDate"));
		EventTitleCol.setCellValueFactory(new PropertyValueFactory<Event, String>("description"));
		EventCategoryCol.setCellValueFactory(new PropertyValueFactory<Event, String>("category"));
		EventPriorityCol.setCellValueFactory(new PropertyValueFactory<Event, String>("priority"));
	}
}
