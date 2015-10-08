package application;

import java.util.ArrayList;

public class Feedback {

	private String _feedbackString;
	private ArrayList<Task> _taskData;

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
}
