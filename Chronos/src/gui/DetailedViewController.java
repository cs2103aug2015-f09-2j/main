package gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import application.Event;
import application.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class DetailedViewController extends BorderPane {

	private static final String DETAIL_LAYOUT_FXML = "DetailedViewLayout.fxml";
	private final String ID = "ID: %1$s ";
	private final String START_DATE = "Starting Time: %1$s ";
	private final String END_DATE = "Ending Time: %1$s ";
	private final String PRIORITY = "Priority: %1$s ";
	private final String CATEGORY = "Category: %1$s ";
	private final String NOTES = "Notes: ";
	private final String NOTE = "      %1$s.  %2$s";
	private final int HOURS_PER_DAY = 24;
	private final String HOURS = "%1$s hours";
	private final String DAYS = "%1$s days ";
	private final String OVERDUE = "Since";
	private final String NOT_OVERDUE = "Until";
	private final String DATE_FORMAT = "dd/MM/yy hh:mm";
	private final String INDICATOR_TIME = "m";

	// displayed items
	@FXML
	private TextFlow eventTitle;

	@FXML
	private ListView<String> details;

	@FXML
	private Label diffTime;

	@FXML
	private Label status;

	public DetailedViewController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(DETAIL_LAYOUT_FXML));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GUI will call this method if user want to view certain task
	public void display(Task taskToView) {
		displayTitle(taskToView);
		displayDetails(taskToView);
		displayCountDown(taskToView);
	}

	// display the title of the task on the top
	private void displayTitle(Task taskToView) {
		eventTitle.getChildren().addAll(new Text(taskToView.getDescription()));
	}

	// display the details of the task on the left
	private void displayDetails(Task taskToView) {
		ObservableList<String> items = FXCollections.observableArrayList();
		addInfo(taskToView, items);
		details.setItems(items);
	}

	// display the count down of the task on the right
	private void displayCountDown(Task taskToView) {
		setTimeDiff(taskToView);
		setStatus(taskToView);
	}

	// add information about the id, time, priority and category to the list
	// object
	private void addInfo(Task taskToView, ObservableList<String> items){
		items.add(String.format(ID, taskToView.getId()));
		if (taskToView instanceof Event) {
			items.add(String.format(START_DATE, ((Event) taskToView).getStartDate()));
		}
		items.add(String.format(END_DATE, taskToView.getEndDate()));
		items.add(String.format(PRIORITY, taskToView.getPriority()));
		items.add(String.format(CATEGORY, taskToView.getCategory()));
		items.add(String.format(NOTES));
		ArrayList<String> notes = taskToView.getNotes();
		for (int i = 1; i <= notes.size(); i++) {
			items.add(String.format(NOTE,i,notes.get(i-1)));
		}
	}

	// display the time difference between current date and task date
	private void setTimeDiff(Task taskToView) {
		String endDate;
		if (taskToView instanceof Event) {
			endDate = ((Event) taskToView).getStartDate();
		} else {
			endDate = taskToView.getEndDate();
		}
		// timeDiff is negative for overdue event
		int hourDiff = Math.abs(calculateTimeLeft(endDate)); 
		String timeDiffFormatted = formatTimeDiff(hourDiff);
		diffTime.setText(timeDiffFormatted);
	}

	//display "since" for overdue task and "until" otherwise
	private void setStatus(Task taskToView) {
		if (taskToView.isOverdue()) {
			status.setText(OVERDUE);
		} else {
			status.setText(NOT_OVERDUE);
		}
	}

	// find the difference between current time and task end Time
	private int calculateTimeLeft(String endDate) {
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		try {
			Date dueDate;
			Date currentDate = new Date();
			if (endDate.toLowerCase().contains(INDICATOR_TIME)) { // if deadline has a specified time
				dateFormat = new SimpleDateFormat();
				dueDate = dateFormat.parse(endDate);
			} else { // if deadline has no specified time: put it at 11:59:59
				dueDate = dateFormat.parse(endDate);
				dueDate = setSpecificTime(dueDate);
			}
			long diff = TimeUnit.MILLISECONDS.toHours(dueDate.getTime() - currentDate.getTime());
			return (int) diff;
		} catch (ParseException e) {
			// Case: Someday
			return 0;
		}
	}

	//set specific time to be 23:59:59
	private Date setSpecificTime(Date dueDate) {

		Calendar assumedEndDate = Calendar.getInstance();
		assumedEndDate.setTime(dueDate);
		assumedEndDate.set(Calendar.HOUR_OF_DAY, 23);
		assumedEndDate.set(Calendar.MINUTE, 59);
		assumedEndDate.set(Calendar.SECOND, 59);
		dueDate = assumedEndDate.getTime();
		return dueDate;
	}

	//convert the integer hourDiff into presentable String
	private String formatTimeDiff(int hourDiff) {
		String timeDiffFormatted = "";
		if (hourDiff >= HOURS_PER_DAY) { //if more than 1 day
			int dayDiff = hourDiff / HOURS_PER_DAY;
			hourDiff = hourDiff % HOURS_PER_DAY;
			timeDiffFormatted = String.format(DAYS, dayDiff);
		}
		timeDiffFormatted = timeDiffFormatted + String.format(HOURS, hourDiff);
		return timeDiffFormatted;
	}

}
