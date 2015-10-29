package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;
public class Task {
	
	String DEFAULT_END_DATE = "someday";
	String DEFAULT_PRIORITY = "med";
	String DEFAULT_CATEGORY = "none";
	String DEFAULT_STATUS = "false";
	private static final String ID_HEADER = "t";	
	
	protected String _id;
	protected String _description;
	protected String _endDate = DEFAULT_END_DATE;
	protected String _priority = DEFAULT_PRIORITY;
	protected String _category = DEFAULT_CATEGORY;
	protected String _status = DEFAULT_STATUS;
	protected boolean _isDone = false;
	protected boolean _isClashing = false;
	protected ArrayList<Note> _notes = new ArrayList<Note>();
	
	protected Task() {
		
	}
	
	public Task(String[] contents) throws ParseException {
		_description = contents[0];
		for (int i = 1; i<contents.length; i++) {
			if (contents[i].contains("p:")) {
				_priority = contents[i].substring(2).toLowerCase();
			} else if (contents[i].contains("c:")) {
				_category = contents[i].substring(2);
			} else { //date manipulation
				Span aSpan = Chronic.parse(contents[i]);	
				_endDate = manipulateDate(aSpan.getBeginCalendar());
			}
		}
	}
	
	protected String manipulateDate(Calendar theDate) throws ParseException {	
		//set default time
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a"); 
		
		return dateFormat.format(theDate.getTime());
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
	
	public String getStatus(){
		return _status;
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

	public Task copy() {
		Task copiedTask = new Task(_id, _description, _endDate, _priority, _category);
		for(Note aNote:_notes){
			copiedTask.addNote(aNote.toString());
		}
		return copiedTask;
	}
	
	public boolean isOverdue() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		try {
			Date dueDate;
			Date currentDate = new Date();
			if(_endDate.toLowerCase().contains("m")) { //if deadline has a specified time
				dateFormat = new SimpleDateFormat();
				dueDate = dateFormat.parse(_endDate);
			} else { //if deadline has no specified time: put it at 11:59:59
				dueDate = dateFormat.parse(_endDate);
				Calendar endDate = Calendar.getInstance();
				endDate.setTime(dueDate);
				endDate.set(Calendar.HOUR_OF_DAY, 23);
				endDate.set(Calendar.MINUTE, 59);
				endDate.set(Calendar.SECOND, 59);
				dueDate = endDate.getTime();
			}
			return (dueDate.compareTo(currentDate) < 0);
		} catch (ParseException e) {
			//Case: Someday
			return false;
		}
	}
	
	void setClashing(boolean isClashing) {
		_isClashing = isClashing;
	}
	
	public boolean isClashing() {
		return _isClashing;
	}
}
