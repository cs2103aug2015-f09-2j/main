package application;

import java.util.ArrayList;

public class Task {
	
	String DEFAULT_END_DATE = "someday";
	String DEFAULT_PRIORITY = "med";
	String DEFAULT_CATEGORY = "none";
	
	private static final String ID_HEADER = "t";	
	
	protected String _id;
	protected String _description;
	protected String _endDate = DEFAULT_END_DATE;
	protected String _priority = DEFAULT_PRIORITY;
	protected String _category = DEFAULT_CATEGORY;
	protected boolean _isDone = false;
	protected ArrayList<Note> _notes;
	
	public Task(String[] contents) {
		_description = contents[0];
		for (int i = 1; i<contents.length; i++) {
			if (contents[i].contains("p:")) {
				_priority = contents[i].substring(2);
			} else if (contents[i].contains("c:")) {
				_category = contents[i].substring(2);
			} else { //date manipulation
				_endDate = contents[i];
			}
		}
	}
	
	public Task(int id, String description, String endDate, String priority, String category) {
		_id = ID_HEADER + Integer.toString(id);
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
	}
	
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
	}

	public String getId() {
		return _id;
	}
	
	void setId(int id) {
		_id = ID_HEADER + Integer.toString(id);
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
