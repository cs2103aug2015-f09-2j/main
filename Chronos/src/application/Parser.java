package application;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



public class Parser {

	private Task item;
	private Integer id;
	private String taskID;
	
	public Parser() {
		id = 0;
		taskID = "t";
	}
	
	public JSONObject createItem(String content) {
		String[] contents = content.split(", ");
		JSONObject entry = new JSONObject();

		entry.put("id", taskID + ++id);
		entry.put("description", contents[0]);
		for(int i = 1; i<contents.length; i++){
			if(contents[i].charAt(1) == ':'){ // p: or c:
				switch(contents[i].charAt(0)){
					case 'p':
						entry.put("priority", contents[i]);
						break;
					case 'c':
						entry.put("category", contents[i]);
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
	
	public ArrayList<Task> convertToTaskArray (JSONArray content) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		if(content != null){
			//convert to tasks
		} 
		return tasks;
	}

	public Task createTask(String content) {
		String[] contents = content.split(", ");
		String taskID = "t" + ++id;
		String dueDate;
		String dueTime;
		String priority;
		String category;
		for(int i = 1; i<contents.length; i++){
			//date due
		}
		//Task createdTask = new Task(taskID, contents[0] ,enddate,priority, category);
		return null;
	}
}

