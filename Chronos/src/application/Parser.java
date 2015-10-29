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
	
	//Used by the AddCommand
	public Task createItem(String content) throws ParseException {
		String[] contents = content.split(CONTENT_SEPARATOR);
		if(contents[CONTENT_DESC] == CONTENT_EMPTY) {
			throw new NullPointerException(EXCEPTION_NO_DESC);
		}
		Task createdItem;
		if (findEventString(contents) > 0) {
			createdItem = new Event(contents);
		} else {
			createdItem = new Task(contents);
		}
		return createdItem;
	}
	
	private int findEventString(String[] contents) {
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].contains(CONTENT_EVENT_STRING)) {
				return i;
			}
		}
		return CONTENT_DESC;
	}
	
	//Used by the Add and Note Commands
	public JSONObject convertToJSON(Task createdTask) {
		return putIntoEntry(createdTask);
	}
	
	private JSONObject putIntoEntry(Task createdTask) {
		JSONObject entry = new JSONObject();
		entry.put(JSON_ID, createdTask.getId());
		entry.put(JSON_DESC, createdTask.getDescription());
		entry.put(JSON_PRIORITY, createdTask.getPriority());
		entry.put(JSON_CATEGORY, createdTask.getCategory());
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
	
	//Used by the DisplayCommand
	public ArrayList<Task> convertToTaskArray (JSONArray contents) {
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
	
	//Used by Display, Done, Note, Search and View Commands
	public Task retrieveTask(String taskID, JSONArray entries) {
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

	private Task convertToTask(JSONObject anEntry) {
		String id = anEntry.get(JSON_ID).toString();
		String description = anEntry.get(JSON_DESC).toString();
		String endDate = anEntry.get(JSON_END_DATE).toString();
		String priority = anEntry.get(JSON_PRIORITY).toString();
		String category = anEntry.get(JSON_CATEGORY).toString();
		boolean completion = anEntry.get(JSON_COMPLETE).equals("true");
		if (anEntry.containsKey(JSON_START_DATE)) {
			String startDate = anEntry.get(JSON_START_DATE).toString();
			Event convertedEvent = new Event(id, description, startDate, endDate, priority, category);
			convertedEvent.markTaskAsDone(completion);
			if (anEntry.containsKey(JSON_NOTES)) {
				convertedEvent = (Event) updatedTaskNotes(convertedEvent, anEntry);
			}
			return convertedEvent;
		} else {
			Task convertedTask = new Task(id, description, endDate, priority, category);
			convertedTask.markTaskAsDone(completion);
			if(anEntry.containsKey(JSON_NOTES)){
				convertedTask = updatedTaskNotes(convertedTask, anEntry);
			}
			return convertedTask;
		}
		
	}

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
	
	//Used by the Update Command
	public ArrayList<String> parseUpdateString(String updateString) {
		String[] details = updateString.split(CONTENT_SEPARATOR);
		ArrayList<String> updateDetails = new ArrayList<String>();
		return updateDetailsArray(details, updateDetails);
	}
	
	//note: for tasks only
	public ArrayList<String> updateDetailsArray(String[] details, ArrayList<String> updateDetails) {
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
}