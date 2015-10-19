package application;

import java.util.ArrayList;

public class Task {
	
	protected String DEFAULT_END_DATE = "someday";
	protected String DEFAULT_PRIORITY = "med";
	protected String DEFAULT_CATEGORY = "none";
	
	protected String _id;
	protected String _description;
	protected String _endDate = DEFAULT_END_DATE;
	protected String _priority = DEFAULT_PRIORITY;
	protected String _category = DEFAULT_CATEGORY;
	protected boolean _isDone;
	protected ArrayList<Note> _notes;
	
	
	public Task(String id, String description, String endDate, String priority, String category) {
		_id = id.trim();
		_description = description.trim();
		if(!endDate.equals(null)){
			setEndDate(endDate);
		}
		if(!priority.equals(null)){
			setPriority(priority);
		}
		if(!category.equals(null)){
			setCategory(category);
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
	
	public int getNotesNo() {
		try{
			return _notes.size();
		} catch (NullPointerException e) { //for when +notes is empty
			return 0;
		}
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
		for(int i=0; i<_notes.size(); i++){
			notes.add(_notes.get(i).toString());
		}		
		return notes;
	}
	
}
