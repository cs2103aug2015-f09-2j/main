package application;

import java.lang.String;

import org.json.simple.JSONArray;

public class Parser {
	
	private JSONArray taskArr;
	
	public Parser() {
		taskArr = new JSONArray();
	}
	
	public JSONArray createItem(String content) {
		int numContents;
		String[] contents = content.split(", ");
		
		for(numContents=0; numContents<contents.length; numContents++) {
			taskArr.add(contents[numContents]);
			/*if((contents[numContents].startsWith("p"))) {
				contents[numContents] = contents[numContents].replaceFirst("p:", "");
			}
			else if((contents[numContents].startsWith("c"))) {
				contents[numContents] = contents[numContents].replaceFirst("c:", "");
			}*/
		}
		return taskArr;
	}
	
	public String getID(String contentString) {
		String[] contents = contentString.split(", ");
		String itemID = contents[0];
		return itemID;
	}
	
}

