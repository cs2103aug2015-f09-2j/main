package application;

public class Parser {
	
	public Parser() {}
	
	public String[] createItem(String content) {
		//int numContents;
		String[] contents = content.split(", ");
		
		/*for(numContents=0; numContents<contents.length; numContents++) {
			contents[numContents]=contents[numContents].trim();
			if((contents[numContents].startsWith("p"))) {
				contents[numContents] = contents[numContents].replaceFirst("p:", "");
			}
			else if((contents[numContents].startsWith("c"))) {
				contents[numContents] = contents[numContents].replaceFirst("c:", "");
			}
		}*/
		return contents;
	}
	
	public String getID(String contentString) {
		String[] contents = contentString.split(", ");
		String itemID = contents[0];
		return itemID;
	}
	
	
}

