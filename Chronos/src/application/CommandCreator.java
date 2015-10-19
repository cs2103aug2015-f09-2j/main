package application;

import java.util.Stack;

public class CommandCreator {
	
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
		
	enum COMMAND_TYPE {
		ADD, CD, DELETE, DISPLAY, DONE, EXIT, NOTE, REDO, SEARCH, UNDO, UNKNOWN, UPDATE, VIEW  
	};
	
	//Strings for command creation
	private static final int COMMAND_INDEX_COMMAND = 0;
	private static final int COMMAND_INDEX_CONTENT = 1;
	private static final String CONTENT_EMPTY = "";
	
	private static Stack<Command> _pastCommands = new Stack<Command>();
	private static Stack<Command> _undoneCommands = new Stack<Command>();
	
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
			
			case UNKNOWN : 
				//Fallthrough
				
			default :
				aCommand = new UnknownCommand(commandContent);
				break;
		}
		
		return aCommand.execute();
	}
	
	private Feedback redoCommand() {
		Command latestCommand = _undoneCommands.pop();
		_pastCommands.push(latestCommand);
		return latestCommand.execute();
	}

	private Feedback undoLatestCommand() {
		Command latestCommand = _pastCommands.pop();
		_undoneCommands.push(latestCommand);
		return latestCommand.undo();
	}

	private String getCommandContent(String[] inputs) {
		try{
			return inputs[COMMAND_INDEX_CONTENT];
		} catch (ArrayIndexOutOfBoundsException e) {
			return CONTENT_EMPTY;
		}
	}

	private COMMAND_TYPE determineCommandType(String typeString) {
		
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
				
			default : 
				return COMMAND_TYPE.UNKNOWN;
				//break;
		}
	}

	public Feedback executeInitializeCommand(String path) {
		return new InitializeCommand(path).execute();
	}
	
	
	
}
