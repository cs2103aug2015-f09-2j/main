package application;

import java.util.ArrayList;


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
	private static final String CONTENT_SEPARATOR = ", ";
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
	
	public Task createItem(String content) {
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
	
	public JSONObject convertToJSON(Task createdTask) {
		JSONObject entry = new JSONObject();
		entry.put(JSON_ID, createdTask.getId());
		entry.put(JSON_DESC, createdTask.getDescription());
		entry.put(JSON_PRIORITY, createdTask.getPriority());
		entry.put(JSON_CATEGORY, createdTask.getCategory());
		if (createdTask instanceof Event) {
			//entry.put(JSON_START_DATE, createdTask.getStartDate());
		}
		entry.put(JSON_END_DATE, createdTask.getEndDate());
		entry.put(JSON_COMPLETE, createdTask.isTaskComplete());
		return entry;
	}
	
	public ArrayList<Task> convertToTaskArray (JSONArray contents) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		if(contents != null){
			for(int i=0; i<contents.size(); i++){
				JSONObject anItem = (JSONObject)contents.get(i);
				Task aTask = convertToTask(anItem);
				tasks.add(aTask);
			}
		}
		assert tasks != null;
		return tasks;
	}

	public Task retrieveTask(String taskID, JSONArray entries) {
		Task selectedTask = null;
		for(int i = 0; i<entries.size(); i++) {
			JSONObject anEntry = (JSONObject) entries.get(i);
			String entryID = anEntry.get("id").toString();
			if(entryID.equals(taskID)){
				selectedTask = convertToTask(anEntry);
			}
		}
		return selectedTask;
	}

	private Task convertToTask(JSONObject anEntry) {
		String id = anEntry.get("id").toString();
		String description = anEntry.get("description").toString();
		String endDate = anEntry.get("due date").toString();
		String priority = anEntry.get("priority").toString();
		String category = anEntry.get("category").toString();
		boolean completion = anEntry.get("complete").equals("true");
		Task convertedTask = new Task(id, description, endDate, priority, category);
		convertedTask.markTaskAsDone(completion);
		return convertedTask;
	}

	public ArrayList<String> parseUpdateString(String updateString) {
		String[] details = updateString.split(", ");
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
				case "d:":
					updateDetails.add(JSON_END_DATE); 
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
}