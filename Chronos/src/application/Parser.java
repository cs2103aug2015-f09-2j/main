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
		//int numContents;
		//id++;
		//String[] contents = content.split(", ");
		JSONObject entry = new JSONObject();
		entry.put("content", content);
		/*for(numContents=0; numContents<contents.length; numContents++) {
			convertToJsonArray(contents[numContents]);
			if((contents[numContents].startsWith("p"))) {
				contents[numContents] = contents[numContents].replaceFirst("p:", "");
			}
			else if((contents[numContents].startsWith("c"))) {
				contents[numContents] = contents[numContents].replaceFirst("c:", "");
			}
		}
		if(!contents[4].isEmpty()) {
			item = new Item(taskID.concat(id.toString()), contents[2].toString(), contents[0].toString(), contents[4].toString());
		} else {
			item = new Item(taskID.concat(id.toString()), contents[2].toString(), contents[0].toString(), "");
		}
		*/
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

