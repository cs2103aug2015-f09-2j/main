package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.logging.Logger;


public class Parser {
	
	//Strings for Log
	private static final String LOG_PARSER = "ParserLog";
	private static final String LOG_ADD = "New Item Added to Storage";
	
	//Strings for Error Messages
	private static final String EXCEPTION_NO_DESC = "No Task Description";
	
	//Strings for createItem
	static final String CONTENT_SEPARATOR = ", ";
	private static final String CONTENT_EMPTY = "";
	
	//Strings for parseUserInput()
	private static final String INPUT_SEPARATOR = " ";
	private static final int INPUT_ARG_COUNT = 2;
	
	//Strings for the JSONObjects
	static final String JSON_ID = "id";
	static final String JSON_DESC = "description";
	static final String JSON_PRIORITY = "priority";
	static final String JSON_CATEGORY = "category";
	static final String JSON_START_DATE = "start date";
	static final String JSON_END_DATE = "due date";
	static final String JSON_NOTES = "notes";
	static final String JSON_COMPLETE = "complete";
	static final String JSON_ALARM = "alarm";
	
	//Important constants for contents[]
	private static final int CONTENT_DESC = 0;
	private static final String CONTENT_EVENT_STRING = " to ";
	
	private static Parser _theParser;
	private static Logger log = Logger.getLogger(LOG_PARSER);
	
	private Parser(){
		
	}
	
	public static Parser getInstance() {
		if (_theParser == null) {
			_theParser = new Parser();
		}
		return _theParser;
	}
	
	/**
	 * This operation is used by the AddCommand to create the task/event 
	 * which the user wants to add.
	 * @param content	User input string
	 * @return
	 * @throws ParseException
	 * @throws Exception
	 */
	public Task createItem(String content) throws ParseException,Exception {
		String[] contents = getContentArray(content); 
		if(contents[CONTENT_DESC] == CONTENT_EMPTY) {
			throw new NullPointerException(EXCEPTION_NO_DESC);
		}
		return createItem(contents);
	}
	
	private Task createItem(String[] contents) throws ParseException,Exception {
		Task createdItem;
		if (findEventString(contents) > 0) {
			createdItem = new Event(contents);
		} else {
			createdItem = new Task(contents);
		}
		return createdItem;
	}
	
	/**
	 * This method helps to split the user input into different categories.
	 * @param content		user input string
	 * @return				String[]
	 */
	public String[] getContentArray(String content) {
		return content.split(CONTENT_SEPARATOR);
	}
	
	/**
	 * This method finds out if the user input string is for an event type of string.
	 * @param contents		Array of string of different categories of the user input
	 * @return
	 */
	private int findEventString(String[] contents) {
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].contains(CONTENT_EVENT_STRING)) {
				return i;
			}
		}
		return CONTENT_DESC;
	}
	
	//Used by the Add and Note Commands
	/**
	 * This method is for the conversion from Task to JSONObject for storing 
	 * in storage file.
	 * @param createdTask		Task that is needed to be stored in file
	 * @return
	 */
	public JSONObject convertToJSON(Task createdTask) {
		return putIntoEntry(createdTask);
	}
	
	private JSONObject putIntoEntry(Task createdTask) {
		JSONObject entry = new JSONObject();
		entry.put(JSON_ID, createdTask.getId());
		entry.put(JSON_DESC, createdTask.getDescription());
		entry.put(JSON_PRIORITY, createdTask.getPriority());
		entry.put(JSON_CATEGORY, createdTask.getCategory());
		entry.put(JSON_ALARM, createdTask.getAlarm());
		if (createdTask instanceof Event) {
			Event createdEvent = (Event) createdTask;
			entry.put(JSON_START_DATE, createdEvent.getStartDate());
		}
		entry.put(JSON_END_DATE, createdTask.getEndDate());
		entry.put(JSON_COMPLETE, createdTask.isTaskComplete());
		if(createdTask.getNotesNo() > 0) {
			entry.put(JSON_NOTES, convertNotesToJSONArray(createdTask.getNotes()));
		}
		return entry;
	}
	
	private JSONArray convertNotesToJSONArray(ArrayList<String> notes) {
		JSONArray notesArray = new JSONArray();
		for (String aNote:notes) {
			JSONObject anObject = new JSONObject();
			anObject.put("note", aNote);
			notesArray.add(anObject);
		}
		return notesArray;
	}
	
	//@@author A0125424N
	//Used by the DisplayCommand
	/**
	 * This method is used by display command and it converts JSONArray to 
	 * TaskArray.
	 * @param contents			Array of items in file
	 * @return tasks			ArrayList of tasks
	 */
	public ArrayList<Task> convertToTaskArray (JSONArray contents) {
		return addTaskToList(contents);
	}
	
	//@@author A0125424N
	private ArrayList<Task> addTaskToList(JSONArray contents) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		if (contents != null) {
			for (int i = 0; i < contents.size(); i++) {
				JSONObject anItem = (JSONObject)contents.get(i);
				Task aTask = convertToTask(anItem);
				tasks.add(aTask);
			}
		}
		assert tasks != null;
		return tasks;
	}
	
	/**
	 * This method is used by display, done, note, search and view commands
	 * and it find the task/event that contains the same ID as the user 
	 * requested.
	 * @param taskID				ID of the selected task
	 * @param entries				storage file
	 * @return selectedTask			Task which the user requested for
	 */
	//@@author A0125424N
	//Used by Display, Done, Note, Search and View Commands
	public Task retrieveTask(String taskID, JSONArray entries) {
		return findTask(taskID, entries);
	}
	
	//@@author A0125424N
	private Task findTask(String taskID, JSONArray entries) {
		Task selectedTask = null;
		for(int i = 0; i<entries.size(); i++) {
			JSONObject anEntry = (JSONObject) entries.get(i);
			String entryID = anEntry.get(JSON_ID).toString();
			if(entryID.equals(taskID)){
				selectedTask = convertToTask(anEntry);
			}
		}
		return selectedTask;
	}
	
	/**
	 * This method does the conversion from JSONObject to Task.
	 * @param anEntry		An JSONObject from storage file
	 * @return convertedTask/convertedEvent		Task/Event which is converted
	 */
	public Task convertToTask(JSONObject anEntry) {
		String id = anEntry.get(JSON_ID).toString();
		String description = anEntry.get(JSON_DESC).toString();
		String endDate = anEntry.get(JSON_END_DATE).toString();
		String priority = anEntry.get(JSON_PRIORITY).toString();
		String category = anEntry.get(JSON_CATEGORY).toString();
		String alarm = anEntry.get(JSON_ALARM).toString();
		boolean completion = anEntry.get(JSON_COMPLETE).equals("true");
		if (anEntry.containsKey(JSON_START_DATE)) {
			Event convertedEvent = (Event) convertToEvent(anEntry, id, description, endDate, priority, category, completion,alarm);
			if (anEntry.containsKey(JSON_NOTES)) {
				convertedEvent = (Event) updatedTaskNotes(convertedEvent, anEntry);
			}
			return convertedEvent;
		} else {
			Task convertedTask = convertTask(id, description, endDate, priority, category, completion,alarm);
			if(anEntry.containsKey(JSON_NOTES)){
				convertedTask = updatedTaskNotes(convertedTask, anEntry);
			}
			return convertedTask;
		}
		
	}
	
	private Task convertToEvent(JSONObject anEntry, String id, String description, String endDate, String priority, String category, boolean completion, String alarm) {
		String startDate = anEntry.get(JSON_START_DATE).toString();
		Event convertedEvent = new Event(id, description, startDate, endDate, priority, category, alarm);
		convertedEvent.markTaskAsDone(completion);
		return convertedEvent;
	}
	
	private Task convertTask(String id, String description, String endDate, String priority, String category, boolean completion, String alarm) {
		Task convertedTask = new Task(id, description, endDate, priority, category, alarm);
		convertedTask.markTaskAsDone(completion);
		return convertedTask;
	}
	
	/**
	 * This method update the notes for specific task.
	 * @param convertedTask		selected task to update notes
	 * @param anEntry			specified task in JSONObject type
	 * @return convertedTask
	 */
	private Task updatedTaskNotes(Task convertedTask, JSONObject anEntry) {
		ArrayList<String> notes = retrieveNotes(anEntry);
		for(String aNote: notes) {
			convertedTask.addNote(aNote);
		}
		return convertedTask;
	}

	private ArrayList<String> retrieveNotes(JSONObject anEntry) {
		JSONArray notesArray = (JSONArray) anEntry.get(JSON_NOTES);
		ArrayList<String> notes = new ArrayList<String>();
		for(int i=0; i < notesArray.size(); i++){
			JSONObject anObject = (JSONObject) notesArray.get(i);
			notes.add(anObject.get("note").toString());
		}
		return notes;
	}
	
	
	//@@author A0125424N
	//Used by the Update Command
	/**
	 * This method is used by the update command which store the updated string
	 * into an arraylist.
	 * @param updateString			The whole input string which the user wants to update
	 * @return updateDetails		Arraylist which stores the updated string
	 */
	public ArrayList<String> parseUpdateString(String updateString) {
		String[] details = updateString.split(CONTENT_SEPARATOR);
		ArrayList<String> updateDetails = new ArrayList<String>();
		return updateDetailsArray(details, updateDetails);
	}
	
	//Used by the Alarm Command
	public String[] parseAlarmString(String alarmString){
		return alarmString.split(CONTENT_SEPARATOR);
	}
	
	//@@author A0125424N
	//note: for tasks only
	private ArrayList<String> updateDetailsArray(String[] details, ArrayList<String> updateDetails) {
		updateDetails.add(details[0]);
		for(int i=1;i<details.length; i++){
			switch(details[i].substring(0, 2)){
				case "p:":
					updateDetails.add(JSON_PRIORITY); 
					updateDetails.add(details[i].substring(2));
					break;
				case "c:":
					updateDetails.add(JSON_CATEGORY); 
					updateDetails.add(details[i].substring(2));
					break;
				case "e:":
					updateDetails.add(JSON_END_DATE); 
					updateDetails.add(details[i].substring(2));
					break;
				case "b:":
					updateDetails.add(JSON_START_DATE); 
					updateDetails.add(details[i].substring(2));
					break;
				case "s:":
					updateDetails.add(JSON_COMPLETE); 
					updateDetails.add(details[i].substring(2));
					break;
				default:
					updateDetails.add(JSON_DESC); 
					updateDetails.add(details[i]);
					break;
			}
		}
		return updateDetails;
	}
	
	//@@author A0125424N
	/**
	 * This method checks if id exists in the file.
	 * @param taskID		ID of task which need to be checked
	 * @param entries		storage file
	 * @return true if ID exists. False if it doesn't.
	 */
	public boolean isExistingId(String taskID, JSONArray entries) {
		Task checkTaskExist = null;
		checkTaskExist = retrieveTask(taskID, entries);
		if(checkTaskExist==null) {
			return false;
		} else {
			return true;
		}
	}

	public String[] parseUserContent(String userInput) {
		return userInput.split(INPUT_SEPARATOR, INPUT_ARG_COUNT);
	}

	public boolean checkForClashes(Task taskToCheck, JSONArray entries) {
		for (int i = 0; i < entries.size(); i++) {
			JSONObject anEntry = (JSONObject) entries.get(i);
			if(taskToCheck.getId().equals(anEntry.get(JSON_ID).toString())){
				continue;
			}
			String endString = anEntry.get(JSON_END_DATE).toString();
			boolean isClashing;
			if (anEntry.containsKey(JSON_START_DATE)) {
				String startString = anEntry.get(JSON_START_DATE).toString();
				isClashing = compareDates(taskToCheck, endString, startString);
			} else {
				isClashing = compareDates(taskToCheck, endString);
			}
			if (isClashing) {
				return true;
			}
		}
		return false;
	}

	private boolean compareDates(Task taskToCheck, String endString) {
		try {
			DateFormat dateFormat = new SimpleDateFormat();
			Date endDate = dateFormat.parse(endString);
			if (taskToCheck instanceof Event) {
				Date eventStart = dateFormat.parse(((Event) taskToCheck).getStartDate());
				Date eventEnd = dateFormat.parse(((Event) taskToCheck).getEndDate());
				return endDate.after(eventStart) && endDate.before(eventEnd);
				//return true if endDate is between eventStart and eventEnd
			} else {
				Date taskEnd = dateFormat.parse(taskToCheck.getEndDate());
				return taskEnd.compareTo(endDate) == 0;
			}
		} catch (ParseException e) {
			//For someday
			return false;
		}
	}

	private boolean compareDates(Task taskToCheck, String endString, String startString) {
		try {
			DateFormat dateFormat = new SimpleDateFormat();
			Date endDate = dateFormat.parse(endString);
			Date startDate = dateFormat.parse(startString);
			if (taskToCheck instanceof Event) {
				Date eventStart = dateFormat.parse(((Event) taskToCheck).getStartDate());
				Date eventEnd = dateFormat.parse(((Event) taskToCheck).getEndDate());
				//return true if endDate or startDate is between eventStart and eventEnd
				return (endDate.after(eventStart) && endDate.before(eventEnd)) || startDate.after(eventStart) && startDate.before(eventEnd);
			} else {
				//return true if endDate or startDate is equal to eventEnd;
				Date taskEnd = dateFormat.parse(taskToCheck.getEndDate());
				return taskEnd.after(startDate) && taskEnd.before(endDate);
			}
		} catch (ParseException e) {
			//for someday
			return false;
		}
	}

	public ArrayList<String> parseExtendString(String extendString) {
		String[] details = extendString.split(CONTENT_SEPARATOR);
		ArrayList<String> extendDetails = new ArrayList<String>();
		extendDetails.add(details[0]);
		extendDetails.add(JSON_END_DATE);
		extendDetails.add(details[1]);
		return extendDetails;
	}
}