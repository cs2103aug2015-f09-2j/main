package application;

import java.util.ArrayList;
import java.util.Stack;

public class Command {
	 
	private static final String MESSAGE_INVALID = "Invalid Command";
	
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
	public static final String COMMAND_START = "startup";
	
	
	
	enum COMMAND_TYPE {
		ADD, CD, DELETE, DISPLAY, DONE, EXIT, NOTE, REDO, SEARCH, START, UNDO, UNKNOWN, UPDATE, VIEW  
	};
	
	private static Stack<Command> _pastCommands = new Stack<Command>();
	private static Stack<Command> _undoneCommands = new Stack<Command>();
	private static Stack<String> _savedDirectories = new Stack<String>();
	//private static Stack<Item> _deletedItems = new Stack<Item>();
	//private static Storage _store = new Storage();
	private static Parser _parser = new Parser();
	
	private COMMAND_TYPE _type;
	private String _content;
	private boolean _isExiting = false;
	private boolean _isInDetailView = false;
	private boolean _isInSummaryView = true;
	
	public Command (String inputString) {
		try {
			String[] inputs = inputString.split(" ", 2);
			determineCommandType(inputs[0]);
			_content = inputs[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			_content = "";
		}
	}
	
	private void determineCommandType(String typeString) {
		switch(typeString.toLowerCase()) {
			
			case COMMAND_ADD_PLUS :
				//Fallthrough
			
			case COMMAND_ADD_ADD :
				_type = COMMAND_TYPE.ADD;
				break;
			
			case COMMAND_CD :
				_type = COMMAND_TYPE.CD;
				break;
			
			case COMMAND_DELETE_MINUS :
				//Fallthrough	
			
			case COMMAND_DELETE :
				_type = COMMAND_TYPE.DELETE;
				break;
			
			case COMMAND_DISPLAY_D :
				//Fallthrough
				
			case COMMAND_DISPLAY_ALL :
				_type = COMMAND_TYPE.DISPLAY;
				break;
			
			case COMMAND_DONE : 
				_type = COMMAND_TYPE.DONE;
				break;
				
			case COMMAND_EXIT : 
				_type = COMMAND_TYPE.EXIT;
				break;
			
			case COMMAND_NOTE : 
				_type = COMMAND_TYPE.NOTE;
				break;
			
			case COMMAND_REDO_ARROW :
				//Fallthrough	
				
			case COMMAND_REDO : 
				_type = COMMAND_TYPE.REDO;
				break;
			
			case COMMAND_SEACH_QUESTION :
				//Fallthrough	
				
			case COMMAND_SEARCH : 
				_type = COMMAND_TYPE.SEARCH;
				break;
			
			case COMMAND_START : 
				_type = COMMAND_TYPE.START;
				break;
				
			case COMMAND_UNDO_ARROW :
				//Fallthrough		
				
			case COMMAND_UNDO : 
				_type = COMMAND_TYPE.UNDO;
				break;
			
			case COMMAND_UPDATE_U :
				//Fallthrough		
				
			case COMMAND_UPDATE : 
				_type = COMMAND_TYPE.UPDATE;
				break;
			
			case COMMAND_VIEW : 
				_type = COMMAND_TYPE.VIEW;
				break;
				
			default : 
				_type = COMMAND_TYPE.UNKNOWN;
				break;

		}
	}

	public Feedback execute () {
		Feedback feedback = null;
		
		switch(_type) {
		
			case ADD :
				feedback = add(_content); 
				_pastCommands.add(this);
				break;
			
			case DELETE :  
				feedback = delete(_content);
				_pastCommands.add(this);
				break;
			
			case DISPLAY : 
				feedback = display(_content);
				_pastCommands.add(this);
				break;
				
			case DONE :
				feedback = markAsDone(_content);
				_pastCommands.add(this);
				break;
				
			case NOTE : 
				feedback = note(_content);
				_pastCommands.add(this);
				break;
			
			case UPDATE :
				feedback = update(_content);
				_pastCommands.add(this);
				break;
			
			case SEARCH :
				feedback = search(_content);
				_pastCommands.add(this);
				break;
			
			case START :
				feedback = startup();
				break;
			
			case VIEW :
				feedback = retrieveTask(_content);
				_pastCommands.add(this);
				break;
				
			case UNDO :
				feedback = undo();
				break;
				
			case REDO :
				feedback = redo();
				break;
				
			case CD :
				feedback = changeDirectory(_content);
				_pastCommands.add(this);
				break;
				
			case EXIT : 
				feedback = null;
				_isExiting = true;
				break;
				
			case UNKNOWN : 
				feedback = new Feedback(String.format(MESSAGE_INVALID), null);
				break;
		}
		
		return feedback;
	}

	private Feedback startup() {
		String feedbackString = "Welcome to Chronos V0.1";
		return new Feedback(feedbackString, null);
	}

	private Feedback retrieveTask(String taskID) {
		// TODO Auto-generated method stub
		String feedbackString = "Retrieving Task: " + taskID;
		return new Feedback(feedbackString, null);
	}

	private Feedback markAsDone(String taskID) {
		// TODO Auto-generated method stub
		String feedbackString = "Marking as Done: " + taskID;
		return new Feedback(feedbackString, null);
	}

	private Feedback add(String content) {
		//Task newTask = _parser.createTask(content);
		//_store.writeToFile(newItem);
		String feedbackString = "Adding: " + content;
		return new Feedback(feedbackString, null);
	}
	
	private Feedback changeDirectory(String newDirectory) {
		//String oldPath = _store.changeDirectory(newDirectory);
		//_savedDirectories.push(oldPath);
		String feedbackString = "Changing Directory to: " + newDirectory;
		return new Feedback(feedbackString, null);
	}
	
	private Feedback delete(String idToDelete) {
		//Item deletedItem = _store.deleteItemFromFile(idToDelete);
		//_deletedItems.push(deletedItem);
		String feedbackString = "Deleting item with id: " + idToDelete;
		return new Feedback(feedbackString, null); // replace null with deletedItem
	}
	
	private Feedback display(String criteria) {
		//This method should probably return something else
		//ArrayList<Items> filteredItems = _store.filterItems(criteria);
		String feedbackString = "";
		if (criteria.equals("")) {
			feedbackString = "Displaying All";
		} else {
			feedbackString = "Displaying: " + criteria;
		}
		return new Feedback(feedbackString, null); // filteredItems
	}

	private Feedback note(String noteString) {
		//String itemID = _parser.getID(noteString);
		//Note aNote = new Note(noteString);
		//_store.addNote(itemID, aNote)
		String feedbackString = "Adding note: " + noteString;
		return new Feedback(feedbackString, null);
	}
	
	public Feedback undo(){
		//Command commandToUndo = _pastCommands.pop();
		//switch(type) to undo commands depending on type
		//return feedbackString
		String feedbackString = "Undoing latest command.";
		return new Feedback(feedbackString, null);
	}
	
	public Feedback redo () {
		//Command commandToRedo = _undoneCommands.pop();
		//switch(type)... and return feedbackString
		String feedbackString = "Redoing latest command.";
		return new Feedback(feedbackString, null);
	}
	
	private Feedback search(String searchTerm) {
		//This method should probably return something else
		//ArrayList<Items> filteredItems = _store.filterItems(criteria);
		/*
		for (int i = 0; i < _store.entries_.size(); i++){
			String entry = _store.entries_.get(i).toString();
			if(entry.contains(searchTerm)) {
			    return entry;
			  }
		}
		return "Cannot find "+searchTerm;
		*/
		String feedbackString = "Searching for: " + searchTerm;
		return new Feedback(feedbackString, null); //searchREsults
	}

	private Feedback update(String updateString) {
		//String itemID = _parser.getID(updateString);
		//_store.addNote(itemID, updateString)
		String feedbackString = "Updating: " + updateString;
		return new Feedback(feedbackString, null);
	}
	
	public boolean isExiting() {
		return _isExiting;
	}
	
}
