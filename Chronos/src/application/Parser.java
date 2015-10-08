package application;

import java.lang.String;

import org.json.simple.JSONArray;



public class Parser {

	private JSONArray taskArr;
	private Task item;
	private Integer id;
	private String taskID;
	
	public Parser() {
		taskArr = new JSONArray();
		id = 0;
		taskID = "t";
	}
	
	public Task createItem(String content) {
		//int numContents;
		id++;
		String[] contents = content.split(", ");
		
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
		return item;
	}
	
	public String getID(String contentString) {
		String[] contents = contentString.split(", ");
		String itemID = contents[0];
		return itemID;
	}
	
	public JSONArray convertToJsonArray(String[] content) {
		int numContents;
		
		for(numContents=0; numContents<content.length; numContents++) {
			taskArr.add(content[numContents]);
		}
		return taskArr;
	}
}

