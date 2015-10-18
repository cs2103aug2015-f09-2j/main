package application;

import java.util.logging.Logger;

public abstract class Command {

	//Class Attributes
	protected Storage _store;
	protected Parser _parse;
	protected String _content;
	
	//Messages
	protected static String FEEDBACK_MESSAGE;
	
	protected static final String ERROR_NO_CONTENT = "Error: No content entered.";
	protected static final String ERROR_INVALID_ID = "Error: Invalid ID entered.";
	
	protected static final String LOG_NO_ID = "No id specified";
	
	protected static Logger log = Logger.getLogger("CommandLog");
	
	public Command(Storage store, Parser parse, String content) {
		_store = store;
		_parse = parse;
		_content = content;
	}
	
	public abstract Feedback execute();
	
	public abstract Feedback undo();
}
