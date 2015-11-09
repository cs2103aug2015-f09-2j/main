package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;

//@@author A0126223U
public class Task {
	
	final String DEFAULT_END_DATE = "someday";
	final String DEFAULT_PRIORITY = "low";
	final String DEFAULT_CATEGORY = "none";
	final String DEFAULT_STATUS = "false";
	final String DEFAULT_ALARM = "off";
	final String PRIORITY_LOW = "low";
	final String PRIORITY_MED = "med";
	final String PRIORITY_HIGH = "high";
	
	static final String ID_HEADER = "t";	
	public static final String DATE_FORMAT = "dd MMM yyyy HH:mm";
	
	//Constant strings
	protected static final String PRIORITY_HEADER = "p:";
	protected static final String CATEGORY_HEADER = "c:";
	protected static final String EMPTY = null;
	protected static final String EMPTY_SPACE = " ";
	protected static final String ID_DISPLAY = ". ";
	
	private static final String ERROR_PARSE = "JChronic is unable to parse date";
	
	protected String _id;
	protected String _description;
	protected String _endDate = DEFAULT_END_DATE;
	protected String _priority = DEFAULT_PRIORITY;
	protected String _category = DEFAULT_CATEGORY;
	protected String _alarm = DEFAULT_ALARM;
	protected String _status = DEFAULT_STATUS;
	protected boolean _isDone = false;
	protected boolean _isClashing = false;
	protected ArrayList<Note> _notes = new ArrayList<Note>();
	
	DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT); 
	
	protected Task() {
		
	}
	
	public Task(String[] contents) throws ParseException {
		_description = contents[0];
		for (int i = 1; i<contents.length; i++) {
			if (contents[i].contains(PRIORITY_HEADER)) {
				_priority = processPriority(contents[i]);
			} else if (contents[i].contains(CATEGORY_HEADER)) {
				_category = contents[i].substring(CATEGORY_HEADER.length());
			} else { //date manipulation
				Span aSpan = Chronic.parse(contents[i]);
				if(aSpan == null) {
					throw new ParseException(ERROR_PARSE,0);
				} else {
					_endDate = manipulateDate(aSpan.getBeginCalendar());
				}
			}
		}
	}
	
	private String processPriority(String priorityString) {
		priorityString = priorityString.substring(PRIORITY_HEADER.length()).toLowerCase();
		switch(priorityString) {
		
			case PRIORITY_HIGH : 
				 return PRIORITY_HIGH;
				 //break;
			
			case PRIORITY_MED :
				 return PRIORITY_MED;
				 //break;
			
			case PRIORITY_LOW :
				 return PRIORITY_LOW;
				 //break;
				 
			default :
				 return DEFAULT_PRIORITY;
				 //break;
		}
	}

	protected String manipulateDate(Calendar theDate) throws ParseException {	
		return dateFormat.format(theDate.getTime());
	}

	public Task(int id, String description, String endDate, String priority, String category, String alarm) {
		_id = ID_HEADER + Integer.toString(id);
		_description = description.trim();
		if (!endDate.equals(EMPTY)) {
			setEndDate(endDate);
		}
		if (!priority.equals(EMPTY)) {
			setPriority(priority);
		}
		if (!category.equals(EMPTY)) {
			setCategory(category);
		}
		if (!alarm.equals(EMPTY)) {
			setAlarm(alarm);
		}
	}
	
	public Task(String id, String description, String endDate, String priority, String category, String alarm) {
		_id = id.trim();
		_description = description.trim();
		if (!endDate.equals(EMPTY)){
			setEndDate(endDate);
		}
		if (!priority.equals(EMPTY)){
			setPriority(priority);
		}
		if (!category.equals(EMPTY)){
			setCategory(category);
		}
		if (!alarm.equals(EMPTY)){
			setAlarm(alarm);
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
	
	public String getStatus(){
		return _status;
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
		} catch (NullPointerException e) {
			return 0;
		}
	}
	
	public String getAlarm(){
		return _alarm;
	}
	
	public void setAlarm(String alarm){
		_alarm = alarm;
	}
	
	public boolean hasAlarm(){
		if (_alarm.equals(DEFAULT_ALARM)){
			return false;
		}else{
			return true;
		}
	}
	
	public String getAlarmOffset(){
		return _endDate;
	}
	
	public Date getAlarmDate(){
		assert(!_alarm.equals(DEFAULT_ALARM));
		try {
			return dateFormat.parse(_alarm);
		} catch (ParseException e) {
			return null;
		}
	}
	@Override
	public String toString() {

		return _id + ID_DISPLAY + _description + EMPTY_SPACE + _endDate + EMPTY_SPACE 
				+ _alarm + EMPTY_SPACE + _priority + EMPTY_SPACE + _category + EMPTY_SPACE +_isDone;

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
		Task copiedTask = new Task(_id, _description, _endDate, _priority, _category, _alarm);
		for(Note aNote:_notes){
			copiedTask.addNote(aNote.toString());
		}
		return copiedTask;
	}
	
	public boolean isOverdue() {
		try {
			Date dueDate;
			Date currentDate = new Date();
			if(_endDate.toLowerCase().contains(":")) { //if deadline has a specified time
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

	public boolean hasNote(int noteID) {
		if(_notes.isEmpty()) {
			return false;
		} else if (noteID > 0 && noteID <= _notes.size() ) {
			return true;
		} else {
			return false;
		}
	}

	public void deleteNote(int noteID) {
		_notes.remove(noteID - 1);
	}

	public void updateNote(int noteID, String string) {
		_notes.set(noteID - 1, new Note(string));		
	}
}
