package application;

import java.util.logging.Logger;

//@@author A0126223U
/**
 * This class is used as a format for several Command classes within the package.
 */
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
	public static Instruction commandInstruction;
	
	protected static Logger log = Logger.getLogger("CommandLog");
	
	public Command(String content) {
		_store = Storage.getInstance();
		_parse = Parser.getInstance();
		_content = content; 
	}
	
	public abstract Feedback execute();
	
	public abstract Feedback undo();

}
