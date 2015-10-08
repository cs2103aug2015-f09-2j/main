package application;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



public class Parser {

	private Task item;
	private Integer id;
	private String taskID = "t";
	private static Preferences _userPrefs;
	
	public Parser(Preferences userPrefs) {
		_userPrefs = userPrefs;
		id = _userPrefs.getInt("count", 0);
	}
	
	public JSONObject createItem(String content) {
		_userPrefs.putInt("count", ++id);
		String[] contents = content.split(", ");
		JSONObject entry = new JSONObject();
		entry.put("id", taskID + id);
		entry.put("description", contents[0]);
		entry.put("priority", "");
		entry.put("category", "");
		entry.put("due date", "");
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
				entry.put("due date",contents[i]); // DateFormat.getInstance().format(
			}
		}
		return entry;
	}
	
	public String getID(String contentString) {
		String[] contents = contentString.split(", ");
		String itemID = contents[0];
		return itemID;
	}
	
	public JSONArray convertToJsonArray(String[] content) {
		JSONArray taskArr = new JSONArray();
		int numContents;
		
		for(numContents=0; numContents<content.length; numContents++) {
			taskArr.add(content[numContents]);
		}
		return taskArr;
	}
	
	public ArrayList<Task> convertToTaskArray (JSONArray contents) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		if(contents != null){
			for(int i=0; i<contents.size(); i++){
				JSONObject anItem = (JSONObject)contents.get(i);
				String taskId = anItem.get("id").toString();
				String description = anItem.get("description").toString();
				String endDate = anItem.get("due date").toString();
				String priority = anItem.get("priority").toString();
				String category = anItem.get("category").toString();
				Task aTask = new Task(taskId, description, endDate, priority, category);
				tasks.add(aTask);
			}
		} 
		return tasks;
	}
}

