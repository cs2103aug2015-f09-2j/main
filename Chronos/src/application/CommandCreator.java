package application;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

public class CommandCreator {
	
	//Messages
	private static final String ERROR_NO_UNDO = "Error: No command to undo.";
	private static final String ERROR_NO_REDO = "Error: No command to redo.";
	
	//Command Strings
	private static final String COMMAND_ADD_ADD = "add";
	private static final String COMMAND_ADD_PLUS = "+";
	private static final String COMMAND_CD = "cd";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_DELETE_MINUS = "-";
	private static final String COMMAND_DISPLAY_D = "d";
	private static final String COMMAND_DISPLAY_ALL = "da";
	private static final String COMMAND_DONE = "done";
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_NOTE = "note";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_REDO_ARROW = ">";
	private static final String COMMAND_SEARCH = "search";
	private static final String COMMAND_SEACH_QUESTION = "?";
	private static final String COMMAND_UNDO = "undo";
	private static final String COMMAND_UNDO_ARROW = "<";
	private static final String COMMAND_UPDATE = "update";
	private static final String COMMAND_UPDATE_U = "u";
	private static final String COMMAND_VIEW = "view";
	private static final String COMMAND_ALARM = "alarm";
	private static final String COMMAND_EXTEND = "extend";
	
	//Command Patterns
	private static final String PATTERN_UPDATE = "update (task id), (description), p:(priority), c:(category), e:(end date), b:(start date), s:(complete)"; 
	private static final String PATTERN_SEARCH = "search (search term OR *), (date), c:(category), p:(priority)";
	private static final String PATTERN_VIEW = "view (task/event id)";
	private static final String PATTERN_UNDO = "Undoes a previous action. Undoable actions: %1$s";
	private static final String PATTERN_REDO = "Redoes an undone action. Redoable actions: %1$s";
	private static final String PATTERN_CD = "cd (directory)";
	private static final String PATTERN_EXIT = "Closes Chronos";
	private static final String PATTERN_ALARM = "alarm (task/event id), (number of hours prior OR off)";
	private static final String PATTERN_UNKNOWN = "Error: Invalid command";
		
	enum COMMAND_TYPE {
		ADD, CD, DELETE, DISPLAY, DONE, EXIT, EXTEND, NOTE, REDO, SEARCH, UNDO, UNKNOWN, UPDATE, VIEW , ALARM 
	};
	
	//Strings for command creation
	public static final int COMMAND_INDEX_COMMAND = 0;
	private static final int COMMAND_INDEX_CONTENT = 1;
	private static final String CONTENT_EMPTY = "";
	
	private static Stack<Command> _pastCommands = new Stack<Command>();
	private static Stack<Command> _undoneCommands = new Stack<Command>();
	private static ArrayList<String> _typedCommandStrings = new ArrayList<String>();
	
	Feedback createAndExecuteCommand(String[] inputs) {
		
		COMMAND_TYPE commandType = determineCommandType(inputs[COMMAND_INDEX_COMMAND]);
		String commandContent = getCommandContent(inputs);
		Command aCommand;
		
		switch(commandType) {
		
			case ADD :
			     aCommand = new AddCommand(commandContent);
			     _pastCommands.add(aCommand);
			     break;
		
			case DELETE :  
				aCommand = new DeleteCommand(commandContent);
				_pastCommands.add(aCommand);
				break;
		
			case DISPLAY : 
				aCommand = new DisplayCommand(commandContent);
				break;
			
			case DONE :
				aCommand = new DoneCommand(commandContent);
				_pastCommands.add(aCommand);
				break;
				
			case EXTEND :
				aCommand = new ExtendCommand(commandContent);
				_pastCommands.add(aCommand);
				break;
			
			case NOTE : 
				aCommand = new NoteCommand(commandContent);
				_pastCommands.add(aCommand);
				break;

			case UPDATE :
				aCommand = new UpdateCommand(commandContent);
				_pastCommands.add(aCommand);
				break;
		
			case SEARCH :
				aCommand = new SearchCommand(commandContent);
				//add content to search history (potential enhancement)
				break;
		
			case VIEW :
				aCommand = new ViewCommand(commandContent);
				break;
			
			case UNDO :
				return undoLatestCommand();
				//break;
			
			case REDO :
				return redoCommand();
				//break;
			
			case CD :
				aCommand = new DirectoryCommand(commandContent);
				_pastCommands.add(aCommand);
				break;
			
			case EXIT : 
				aCommand = new ExitCommand(commandContent);
				break;
				
			case ALARM:
				aCommand = new AlarmCommand(commandContent);
				_pastCommands.add(aCommand);
				break;
				
			case UNKNOWN : 
				//Fallthrough
				
			default :
				aCommand = new UnknownCommand(commandContent);
				break;
		}
		
		if (!(aCommand instanceof UnknownCommand)) {
			//add to typed command stack
			String commandString = inputs[COMMAND_INDEX_COMMAND] + " " +commandContent;
			_typedCommandStrings.add(commandString);
		}
		
		return aCommand.execute();
	}
	
	private Feedback redoCommand() {
		try {
			Command latestCommand = _undoneCommands.pop();
			_pastCommands.push(latestCommand);
			return latestCommand.execute();
		} catch (EmptyStackException e) {
			return new Feedback(ERROR_NO_REDO);
		}
	}

	private Feedback undoLatestCommand() {
		try {
			Command latestCommand = _pastCommands.pop();
			_undoneCommands.push(latestCommand);
			return latestCommand.undo();
		} catch (EmptyStackException e) {
			return new Feedback(ERROR_NO_UNDO);
		}
	}

	private String getCommandContent(String[] inputs) {
		try {
			return inputs[COMMAND_INDEX_CONTENT];
		} catch (ArrayIndexOutOfBoundsException e) {
			return CONTENT_EMPTY;
		}
	}

	private static COMMAND_TYPE determineCommandType(String typeString) {
		
		switch(typeString.toLowerCase()) {
			
			case COMMAND_ADD_PLUS :
				//Fallthrough
			
			case COMMAND_ADD_ADD :
				return COMMAND_TYPE.ADD;
				//break;
			
			case COMMAND_CD :
				return COMMAND_TYPE.CD;
				//break;
			
			case COMMAND_DELETE_MINUS :
				//Fallthrough	
			
			case COMMAND_DELETE :
				return COMMAND_TYPE.DELETE;
				//break;
			
			case COMMAND_DISPLAY_D :
				//Fallthrough
				
			case COMMAND_DISPLAY_ALL :
				return COMMAND_TYPE.DISPLAY;
				//break;
			
			case COMMAND_DONE : 
				return COMMAND_TYPE.DONE;
				//break;
				
			case COMMAND_EXIT : 
				return COMMAND_TYPE.EXIT;
				//break;
				
			case COMMAND_EXTEND : 
				return COMMAND_TYPE.EXTEND;
				//break;
			
			case COMMAND_NOTE : 
				return COMMAND_TYPE.NOTE;
				//break;
			
			case COMMAND_REDO_ARROW :
				//Fallthrough	
				
			case COMMAND_REDO : 
				return COMMAND_TYPE.REDO;
				//break;
			
			case COMMAND_SEACH_QUESTION :
				//Fallthrough	
				
			case COMMAND_SEARCH : 
				return COMMAND_TYPE.SEARCH;
				//break;
				
			case COMMAND_UNDO_ARROW :
				//Fallthrough		
				
			case COMMAND_UNDO : 
				return COMMAND_TYPE.UNDO;
				//break;
			
			case COMMAND_UPDATE_U :
				//Fallthrough		
				
			case COMMAND_UPDATE : 
				return COMMAND_TYPE.UPDATE;
				//break;
			
			case COMMAND_VIEW : 
				return COMMAND_TYPE.VIEW;
				//break;
				
			case COMMAND_ALARM:
				return COMMAND_TYPE.ALARM;
				
			default : 
				return COMMAND_TYPE.UNKNOWN;
				//break;
		}
	}

	public Feedback executeInitializeCommand(String path) {
		return new InitializeCommand(path).execute();
	}

	public static Instruction generateInstructions(String commandString) {
		COMMAND_TYPE commandType = determineCommandType(commandString);
		Instruction commandInstruction = new Instruction();
		switch (commandType) {
			
			case ADD :
			     commandInstruction = AddCommand.generateInstruction();
			     break;
		
			case DELETE : 
				 commandInstruction = DeleteCommand.generateInstruction();
				 break;
		
			case DISPLAY : 
				 commandInstruction = DisplayCommand.generateInstruction();
				 break;
			
			case DONE : 
				 commandInstruction = DoneCommand.generateInstruction();
				 break;
			
			case EXTEND : 
				 commandInstruction = ExtendCommand.generateInstruction();
				 break;
			
			case NOTE : 
				 commandInstruction = NoteCommand.generateInstruction();
				 break;
	
			case UPDATE :
				 commandInstruction.setCommandPattern(PATTERN_UPDATE);
			     commandInstruction.addToInstructions("Enter the id of the item you want to update.");
			     commandInstruction.addToRequiredFields("(task/event id)");
			     commandInstruction.addToInstructions("Enter one or more of the fields you want to update.");
				 break;
		
			case SEARCH :
				 commandInstruction.setCommandPattern(PATTERN_SEARCH);
			     commandInstruction.addToInstructions("Enter a search term, or * if no search term.");
			     commandInstruction.addToRequiredFields("(search term)");
			     commandInstruction.addToInstructions("Optional fields: date or a date range (ex. today to tomorrow), priority, category");
				 break;
		
			case VIEW :
				 commandInstruction.setCommandPattern(PATTERN_VIEW);
			     commandInstruction.addToInstructions("Enter the id of the item you want to view.");
			     commandInstruction.addToRequiredFields("(task/event id)");
				 break;
			
			case UNDO :
				 commandInstruction.setCommandPattern(String.format(PATTERN_UNDO, _pastCommands.size()));
				 break;
			
			case REDO :
				 commandInstruction.setCommandPattern(String.format(PATTERN_REDO, _undoneCommands.size()));
				 break;
			
			case CD :
				 commandInstruction.setCommandPattern(PATTERN_CD);
			     commandInstruction.addToInstructions("Enter the directory you want to save to. (Ex. C:\\Users)");
			     commandInstruction.addToRequiredFields("(directory)");
				 break;
			
			case EXIT : 
				 commandInstruction.setCommandPattern(PATTERN_EXIT);
				 break;
				 
			case ALARM:
				 commandInstruction.setCommandPattern(PATTERN_ALARM);
				 break;
		
			case UNKNOWN : 
				//Fallthrough
				
			default :
				commandInstruction.setCommandPattern(PATTERN_UNKNOWN);
				break;
		}
		
		return commandInstruction;
	}
	
	public static String getTypedCommandString(int commandIndex) {
		try {
			return _typedCommandStrings.get(_typedCommandStrings.size()-commandIndex);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public static boolean isWithinRange(int _commandIndex) {
		int itemIndex = _typedCommandStrings.size() - _commandIndex;
		return (itemIndex >= 0) && (itemIndex < _typedCommandStrings.size());
	}

	public Feedback executeGUIDisplayCommand() {
		GUIDisplayCommand aCommand = new GUIDisplayCommand();
		return aCommand.execute();
	}
	
}
