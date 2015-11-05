package application;

//@@author A0126223U
public class Note {
	
	private String _content = Command.EMPTY;
	
	public Note(String content) {
		_content = content;
	} 
	
	public String toString(){
		return _content;
	}
}
