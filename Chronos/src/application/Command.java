package application;

import java.util.logging.Logger;

public abstract class Command {

	//Messages
	protected static String FEEDBACK_MESSAGE;
		
	protected static final String ERROR_NO_CONTENT = "Error: No content entered.";
	protected static final String ERROR_INVALID_ID = "Error: Invalid ID entered.";
		
	protected static final String LOG_NO_ID = "No id specified";
	
	protected static final String EMPTY = "";
	
	//Class Attributes
	protected Storage _store;
	protected Parser _parse;
	protected String _content;
	
	protected static Logger log = Logger.getLogger("CommandLog");
	
	public Command(String content) {
		_store = Storage.getInstance();
		_parse = Parser.getInstance();
		_content = content; 
	}
	
	public abstract Feedback execute();
	
	public abstract Feedback undo();
	
	

}
