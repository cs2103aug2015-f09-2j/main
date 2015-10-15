package application;

import java.util.ArrayList;

public class Task {
	
	private String _id;
	private String _description;
	private String _endDate;
	private String _priority;
	private String _category;
	private boolean _isDone;
	private ArrayList<Note> _notes;
	
	
	public Task(String id, String description, String endDate, String priority, String category) {
		_id = id.trim();
		_description = description.trim();
		_endDate = endDate.trim();
		_priority = priority.trim();
		_category = category.trim();
		_isDone = false;
	}

	public String getId() {
		return _id;
	}
	
	public String getDescription() {
		return _description;
	}

	void setDescription(String description) {
		_description = description;
	}

	public String getEndDate() {
		return _endDate;
	}

	void setEndDate(String endDate) {
		_endDate = endDate;
	}

	public String getPriority() {
		return _priority;
	}

	void setPriority(String priority) {
		_priority = priority;
	}

	public String getCategory() {
		return _category;
	}

	void setCategory(String category) {
		_category = category;
	}
	
	@Override
	public String toString() {
		return _id + ". " + _description + " " + _endDate + " " + _priority + " " + _category;
	}
	
	public boolean isTaskComplete() {
		return _isDone;
	}
	
	public void markTaskAsDone(boolean status) {
		_isDone = status;
	}
	
	public String getNoteString(){
		String noteString = "";
		
		if(_notes.size() == 0) {
			return "No notes available.";
		} 
		
		for(int i=0; i<_notes.size(); i++){
			String oneNote = (i+1) + ". " + _notes.get(i).toString() + "\n";
			noteString += oneNote;
		}
		
		return noteString;
	}
	
}
