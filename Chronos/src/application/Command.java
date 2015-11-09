package application;

import java.util.logging.Logger;

import org.json.simple.JSONObject;

//@@author A0126223U
/**
 * This class is used as a format for several Command classes within the package.
 */
public abstract class Command {

	//Messages
	protected static String FEEDBACK_MESSAGE;
		
	protected static final String ERROR_NO_CONTENT = "Error: No content entered.";
	protected static final String ERROR_INVALID_ID = "Error: Invalid ID entered.";
	protected static final String ERROR_NO_ID = "Error: No ID entered.";
		
	protected static final String LOG_NO_ID = "No id specified";
	
	protected static final String EMPTY = "";
	
	public static final int FIND_NO_ID = -2;
	public static final int FIND_INVALID_ID = -1;
	
	//Class Attributes
	protected Storage _store;
	protected Parser _parse;
	protected String _content;
	protected boolean _isSuccessful = false;
	public static Instruction commandInstruction;
	
	protected static Logger log = Logger.getLogger("CommandLog");
	
	public Command(String content) {
		_store = Storage.getInstance();
		_parse = Parser.getInstance();
		_content = content; 
	}
	
	public abstract Feedback execute();
	
	public abstract Feedback undo();
	
	/**
	 * Finds the index of an item in the JSONArray based on its ID.
	 *
	 * @param id   The id of the desired item. Starts with either an 'e' or a 't'.
	 * @return     The index of the item in the Array. Returns -1 if it is not found, or -2 if there is no id passed.
	 */
	public int findEntry (String id) {
		if(id.equals(EMPTY)){
			return FIND_NO_ID;
		} else {
			for (int i = 0; i < _store.entries_.size(); i++) {
				JSONObject currentEntry = (JSONObject) _store.entries_.get(i);
				if (currentEntry.get(Parser.JSON_ID).equals(id)) {
					return i;
				}
			}
			return FIND_INVALID_ID;
		}
	}
	
	public boolean isSuccessful() {
		return _isSuccessful;
	}

}
