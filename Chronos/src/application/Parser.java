package application;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.logging.Logger;


public class Parser {
	
	//Strings for parseUserInput()
	private static final String INPUT_SEPARATOR = " ";
	private static final int INPUT_ARG_COUNT = 2;
	
	//Strings for ID Generation
	private static final String TASK_HEADER = "t";	

	private Integer id;
	private String taskID = TASK_HEADER;
	private static Preferences _userPrefs;
	
	private static Logger log = Logger.getLogger("ParserLog");
	
	public Parser(Preferences userPrefs) {
		_userPrefs = userPrefs;
		id = _userPrefs.getInt("count", 0);
	}
	
	public JSONObject createItem(String content) {
		String[] contents = content.split(", ");
		if (contents[0] == "") {
			throw new NullPointerException("No Task Description");
		}
		_userPrefs.putInt("count", ++id);
		JSONObject entry = new JSONObject();
		log.info("adding new item");
		return putEntryJSONObj(entry, contents);
	}
	
	public JSONObject putEntryJSONObj(JSONObject entry, String[] contents) {
		entry.put("id", taskID + id);
		entry.put("description", contents[0]);
		entry.put("priority", "low");
		entry.put("category", "none");
		entry.put("due date", "someday");
		entry.put("notes", "none");
		entry.put("complete", "false");
		
		for(int i = 1; i<contents.length; i++){
			if(contents[i].charAt(1) == ':'){ // p: or c:
				switch(contents[i].charAt(0)){
					case 'p':
						entry.put("priority", contents[i].substring(2));
						break;
					case 'c':
						entry.put("category", contents[i].substring(2));
				}
			} else {
				entry.put("due date",contents[i]); // format date
			}
		}
		assert entry != null;
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

	public String changeDirectory(String newDirectory) {
		String oldDirectory = null;
		oldDirectory = _userPrefs.get("path", "none");
		_userPrefs.put("path", newDirectory);
		return oldDirectory;
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
	
	public ArrayList<String> updateDetailsArray(String[] details, ArrayList<String> updateDetails) {
		updateDetails.add(details[0]);
		for(int i=1;i<details.length; i++){
			switch(details[i].substring(0, 2)){
				case "p:":
					updateDetails.add("priority"); 
					updateDetails.add(details[i].substring(2));
					break;
				case "c:":
					updateDetails.add("category"); 
					updateDetails.add(details[i].substring(2));
					break;
				case "d:":
					updateDetails.add("due date"); 
					updateDetails.add(details[i].substring(2));
					break;
				case "s:":
					updateDetails.add("complete"); 
					updateDetails.add(details[i].substring(2));
					break;
				default:
					updateDetails.add("description"); 
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