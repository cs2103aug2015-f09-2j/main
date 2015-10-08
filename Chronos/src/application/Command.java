package application;

import java.util.ArrayList;
import java.util.Stack;

class Command {
	 
	private static final String MESSAGE_INVALID = "Invalid Command";
	
	//Command Strings
	private static final String COMMAND_ADD_ADD = "add";
	private static final String COMMAND_ADD_PLUS = "+";
	private static final String COMMAND_CD = "cd";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_DELETE_MINUS = "-";
	private static final String COMMAND_DISPLAY = "view";
	private static final String COMMAND_DISPLAY_D = "d";
	private static final String COMMAND_DISPLAY_ALL = "da";
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
	
	
	enum COMMAND_TYPE {
		ADD, CD, DELETE, DISPLAY, EXIT, NOTE, REDO, SEARCH, UNDO, UNKNOWN, UPDATE  
	};
	
	private static Stack<Command> _pastCommands = new Stack<Command>();
	private static Stack<Command> _undoneCommands = new Stack<Command>();
	private static Stack<String> _savedDirectories = new Stack<String>();
	//private static Stack<Item> _deletedItems = new Stack<Item>();
	private static Storage _store = new Storage();
	private static Parser _parser = new Parser();
	
	private COMMAND_TYPE _type;
	private String _content;
	private boolean _isExiting = false;
	
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
				//Fallthrough
				
			case COMMAND_DISPLAY : 
				_type = COMMAND_TYPE.DISPLAY;
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
				
			default : 
				_type = COMMAND_TYPE.UNKNOWN;
				break;

		}
	}

	public String execute () {
		String feedbackString = "";
		
		switch(_type) {
		
			case ADD :
				feedbackString = add(_content); 
				_pastCommands.add(this);
				break;
			
			case DELETE :  
				feedbackString = delete(_content);
				_pastCommands.add(this);
				break;
			
			case DISPLAY : 
				feedbackString = display(_content);
				_pastCommands.add(this);
				break;
				
			case NOTE : 
				feedbackString = note(_content);
				_pastCommands.add(this);
				break;
			
			case UPDATE :
				feedbackString = update(_content);
				_pastCommands.add(this);
				break;
			
			case SEARCH :
				feedbackString = search(_content);
				_pastCommands.add(this);
				break;
			
			case UNDO :
				feedbackString = undo();
				break;
				
			case REDO :
				feedbackString = redo();
				break;
				
			case CD :
				feedbackString = changeDirectory(_content);
				_pastCommands.add(this);
				break;
				
			case EXIT : 
				_isExiting = true;
				break;
				
			case UNKNOWN : 
				feedbackString = String.format(MESSAGE_INVALID);
				break;
		}
		
		return feedbackString;
	}

	private String add(String content) {
		//Item newItem = _parser.createItem(content);
		//_store.writeToFile(newItem);
		return "Adding: " + content;
	}
	
	private String changeDirectory(String newDirectory) {
		//String oldPath = _store.changeDirectory(newDirectory);
		//_savedDirectories.push(oldPath);
		return "Changing Directory to: " + newDirectory;
	}
	
	private String delete(String idToDelete) {
		//Item deletedItem = _store.deleteItemFromFile(idToDelete);
		//_deletedItems.push(deletedItem);
		return "Deleting item with id: " + idToDelete;
	}
	
	private String display(String criteria) {
		//This method should probably return something else
		//ArrayList<Items> filteredItems = _store.filterItems(criteria);
		String feedbackString = "";
		if (criteria.equals("")) {
			feedbackString = "Displaying All";
		} else {
			feedbackString = "Displaying: " + criteria;
		}
		return feedbackString;
	}

	private String note(String noteString) {
		//String itemID = _parser.getID(noteString);
		//Note aNote = new Note(noteString);
		//_store.addNote(itemID, aNote)
		return "Adding note: " + noteString;
	}
	
	public String undo(){
		//Command commandToUndo = _pastCommands.pop();
		//switch(type) to undo commands depending on type
		//return feedbackString
		return "Undoing latest command.";
	}
	
	public String redo () {
		//Command commandToRedo = _undoneCommands.pop();
		//switch(type)... and return feedbackString
		return "Redoing latest command.";
	}
	
	private String search(String searchTerm) {
		//better to have parser to parse the object into more readable form
		for (int i = 0; i < _store.entries_.size(); i++){
			String entry = _store.entries_.get(i).toString();
			if(entry.contains(searchTerm)) {
			    return entry;
			  }
		}
		return "Cannot find "+searchTerm;
	}

	private String update(String updateString) {
		//String itemID = _parser.getID(updateString);
		//_store.addNote(itemID, updateString)
		return "Updating: " + updateString;
	}
	
	public boolean isExiting() {
		return _isExiting;
	}
	
}
