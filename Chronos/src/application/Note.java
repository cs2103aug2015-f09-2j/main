package application;

public class Note {
	
	private static final String CONTENT_EMPTY = "";
	
	private String _content = CONTENT_EMPTY;
	
	public Note(String content) {
		_content = content;
	} 
	
	public String toString(){
		return _content;
	}
}
