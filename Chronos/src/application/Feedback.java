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
	private boolean _hasData;
	private boolean _isExiting = false;
	private boolean _isInSummaryView = true;

	public Feedback() {
		_isExiting = true;
	}
	
	public Feedback(String feedback) {
		_feedbackString = feedback;
		_hasData = false;
		_taskData = new ArrayList<Task>();
	}
	
	public Feedback(String feedback, ArrayList<Task> data) {
		_feedbackString = feedback;
		_taskData = data;
		_hasData = true;
	}
	
	public String getMessage() {
		return _feedbackString;
	}
	
	public ArrayList<Task> getData() {
		return _taskData;
	}
	
	public boolean hasData(){
		return _hasData;
	}

	public boolean isProgramExiting() {
		return _isExiting;
	}
	
	void setProgramExiting(boolean isExiting) {
		_isExiting = isExiting;
	}

	public boolean isInSummaryView() {
		return _isInSummaryView;
	}
	
	void setSummaryView(boolean isInSummaryView) {
		_isInSummaryView = isInSummaryView;
	}
}
