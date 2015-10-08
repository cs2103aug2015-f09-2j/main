package application;

import java.util.ArrayList;

/**
 * This class is for communication between the GUI and Logic classes.
 * It contains a String and data (ArrayList<Task>). 
 * It will be returned whenever Logic executes the following methods:
 * 	- executeUserCommand()
 *  - setSavePath()
 */

public class Feedback {

	private String _feedbackString;
	private ArrayList<Task> _taskData;

	public Feedback(String feedback) {
		_feedbackString = feedback;
		_taskData = new ArrayList<Task>();
	}
	
	public Feedback(String feedback, ArrayList<Task> data) {
		_feedbackString = feedback;
		_taskData = data;
	}
	
	public String getMessage() {
		return _feedbackString;
	}
	
	public ArrayList<Task> getData() {
		return _taskData;
	}

	public boolean hasData() {
		return !_taskData.isEmpty();
	}
}
