package application;

import java.util.ArrayList;

public class Task {
	
	private static int _totalTaskCount = 0; // get from Storage or prefs
	
	private String _id;
	private String _description;
	private String _endDate;
	private String _priority;
	private String _category;
	private ArrayList<Note> _notes;
	
	public Task(String description, String endDate, String priority, String category) {
		_id = "t" + Integer.toString(++_totalTaskCount);
		_description = description;
		_endDate = endDate;
		_priority = priority;
		_category = category;
	}

	public String getDescription() {
		return _description;
	}

	void setDescription(String _description) {
		this._description = _description;
	}

	public String getEndDate() {
		return _endDate;
	}

	void setEndDate(String _endDate) {
		this._endDate = _endDate;
	}

	public String getPriority() {
		return _priority;
	}

	void setPriority(String _priority) {
		this._priority = _priority;
	}

	public String getCategory() {
		return _category;
	}

	void setCategory(String _category) {
		this._category = _category;
	}

	public ArrayList<Note> getNotes() {
		return _notes;
	}
	
	
	
}
