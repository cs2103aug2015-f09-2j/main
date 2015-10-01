package application;

public class Parser {
	
	private static final String ERROR_EMPTY_COMMAND="Command cannot be empty";
	private static final String ADD_COMMAND="add";
	private static final String DELETE_COMMAND="delete";
	private static final String DISPLAY_COMMAND="display";
	private static final String NOTE_COMMAND="note";
	private static final String UPDATE_COMMAND="update";
	private static final String SEARCH_COMMAND="search";
	private static final String UNDO_COMMAND="undo";
	private static final String REDO_COMMAND="redo";
	private static final String CD_COMMAND="cd";
	private static final String EXIT_COMMAND="exit";
	
	enum COMMAND_TYPE {
		DISPLAY, ADD, NOTE, UPDATE, DELETE, UNDO, REDO, SEARCH, CD, EXIT, UNKNOWN
	};
	
	COMMAND_TYPE command;
	
	public Parser(String commandString) {
		command = determineCommandType(commandString);
	}
	
	public COMMAND_TYPE determineCommandType(String commandTypeString) throws Error {
		if (commandTypeString == null) {
			throw new Error (ERROR_EMPTY_COMMAND);
		} else if (commandTypeString.equalsIgnoreCase(ADD_COMMAND)) {
			return COMMAND_TYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase(DISPLAY_COMMAND)) {
			return COMMAND_TYPE.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase(DELETE_COMMAND)) {
			return COMMAND_TYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase(NOTE_COMMAND)) {
			return COMMAND_TYPE.NOTE;
		} else if (commandTypeString.equalsIgnoreCase(UPDATE_COMMAND)) {
			return COMMAND_TYPE.UPDATE;
		} else if (commandTypeString.equalsIgnoreCase(SEARCH_COMMAND)) {
			return COMMAND_TYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase(UNDO_COMMAND)) {
			return COMMAND_TYPE.UNDO;
		} else if (commandTypeString.equalsIgnoreCase(REDO_COMMAND)) {
			return COMMAND_TYPE.REDO;
		} else if (commandTypeString.equalsIgnoreCase(CD_COMMAND)) {
			return COMMAND_TYPE.CD;
		} else if (commandTypeString.equalsIgnoreCase(EXIT_COMMAND)) {
			return COMMAND_TYPE.EXIT;
		} else {
			return COMMAND_TYPE.UNKNOWN;
		}
	}
}

