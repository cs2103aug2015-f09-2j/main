package application;

import java.util.ArrayList;

public class Task {
	
	private String DEFAULT_END_DATE = "someday";
	private String DEFAULT_PRIORITY = "med";
	private String DEFAULT_CATEGORY = "none";
	
	private String _id;
	private String _description;
	private String _endDate = DEFAULT_END_DATE;
	private String _priority = DEFAULT_PRIORITY;
	private String _category = DEFAULT_CATEGORY;
	private boolean _isDone;
	private ArrayList<Note> _notes;
	
	
	public Task(String id, String description, String endDate, String priority, String category) {
		_id = id.trim();
		_description = description.trim();
		if(!endDate.equals(null)){
			setEndDate(endDate);
		}
		if(!priority.equals(null)){
			setEndDate(priority);
		}
		if(!category.equals(null)){
			setEndDate(category);
		}
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
		_endDate = endDate.trim();
	}

	public String getPriority() {
		return _priority;
	}

	void setPriority(String priority) {
		_priority = priority.trim();
	}

	public String getCategory() {
		return _category;
	}

	void setCategory(String category) {
		_category = category.trim();
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
	
	public void addNote(String noteString){
			_notes.add(new Note(noteString));
	}
	
	//for detailed view
	public ArrayList<String> getNotes(){
		ArrayList<String> notes = new ArrayList<String>();
		assert !notes.isEmpty();
		for(int i=0; i<_notes.size(); i++){
			notes.add(_notes.get(i).toString());
		}		
		return notes;
	}
	
}
