package application;

import java.util.ArrayList;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Command {
	 
	private static final String MESSAGE_INVALID = "Invalid Command";
	
	//Command Strings
	private static final String COMMAND_ADD_ADD = "add";
	private static final String COMMAND_ADD_PLUS = "+";
	private static final String COMMAND_CD = "cd";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_DELETE_MINUS = "-";
	public static final String COMMAND_DISPLAY_D = "d";
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
	
	//Messages
	private static final String MESSAGE_RETRIEVING = "Retrieving Task: %1$s";
	private static final String MESSAGE_MARKING = "Marking as Done: %1$s";
	private static final String MESSAGE_ADDING = "Adding: %1$s";
	private static final String MESSAGE_CHANGEDIR = "Changing Directory to: %1$s";
	private static final String MESSAGE_DELETING = "Deleting item with id: %1$s";
	private static final String MESSAGE_DISPLAY_ALL = "Displaying All";
	private static final String MESSAGE_ADDING_NOTE = "Adding note: %1$s";
	private static final String MESSAGE_UNDO = "Undoing latest command.";
	private static final String MESSAGE_REDO = "Redoing latest command.";
	private static final String MESSAGE_CANT_REDO = "Nothing to redo.";
	private static final String MESSAGE_SEARCHING = "Searching for: %1$s";
	private static final String MESSAGE_UPDATING = "Updating: %1$s";
	
	//Constants
	private static final String COMMAND_SEPARATOR = " ";
	private static final int COMMAND_ARG_COUNT = 2;
	private static final int COMMAND_INDEX_COMMAND = 0;
	private static final int COMMAND_INDEX_CONTENT = 1;
	private static final String CONTENT_EMPTY = "";
	
	private static Stack<Command> _pastCommands = new Stack<Command>();
	//private static Stack<Command> _undoneCommands = new Stack<Command>();
	private static Stack<String> _savedDirectories = new Stack<String>();
	//private static Stack<Item> _deletedItems = new Stack<Item>();
	private static Storage _store;
	private static Parser _parser;
	private static boolean _isRedoable = false;
	
	private COMMAND_TYPE _type;
	private String _content;
	private boolean _isExiting = false;
	private boolean _isInSummaryView = true;
	
	public Command (String inputString, Storage store, Parser parse) {
		try {
			_store = store;
			_parser = parse;
			String[] inputs = inputString.split(COMMAND_SEPARATOR, COMMAND_ARG_COUNT);
			determineCommandType(inputs[COMMAND_INDEX_COMMAND ]);
			_content = inputs[COMMAND_INDEX_CONTENT];
		} catch (ArrayIndexOutOfBoundsException e) {
			_content = CONTENT_EMPTY;
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
			
			case VIEW :
				feedback = retrieveTask(_content);
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
				feedback = new Feedback(String.format(MESSAGE_INVALID));
				break;
		}
		
		return feedback;
	}

	private Feedback retrieveTask(String taskID) {
		_isInSummaryView = false;
		ArrayList<Task> data = new ArrayList<Task>();
		Task selectedTask = _parser.retrieveTask(taskID, _store.entries_);
		data.add(selectedTask);
 		String feedbackString = String.format(MESSAGE_RETRIEVING, taskID);
		return new Feedback(feedbackString, data);
	}

	private Feedback markAsDone(String taskID) {
		// TODO Auto-generated method stub
		String feedbackString = String.format(MESSAGE_MARKING, taskID);
		return new Feedback(feedbackString, null);
	}

	private Feedback add(String content) {
		_store.storeTemp();
		JSONObject newEntry = _parser.createItem(content);
		_store.entries_.add(newEntry);
		_store.storeChanges();
		String feedbackString = String.format(MESSAGE_ADDING, content);
		return new Feedback(feedbackString);
	}
	
	private Feedback changeDirectory(String newDirectory) {
		String oldPath = _parser.changeDirectory(newDirectory);
		_savedDirectories.push(oldPath);
		_store.changeDirectory(newDirectory);
		String feedbackString = String.format(MESSAGE_CHANGEDIR, newDirectory); 
		return new Feedback(feedbackString);
	}
	
	private Feedback delete(String idToDelete) {
		_store.storeTemp();
		JSONObject entry;
		for (int i = 0; i < _store.entries_.size(); i++) {
			entry = (JSONObject) _store.entries_.get(i);
			if (entry.get("id").equals(idToDelete)) {
				_store.entries_.remove(i);
				break;
			}
		}
		String feedbackString = String.format(MESSAGE_DELETING, idToDelete);
		_store.storeChanges();
		return new Feedback(feedbackString, _parser.convertToTaskArray(_store.entries_)); 
	}
	
	private Feedback display(String criteria) {
		_isInSummaryView = true;
		ArrayList<Task> filteredTasks = new ArrayList<Task>();
		String feedbackString = CONTENT_EMPTY;
		if (criteria.equals(CONTENT_EMPTY)) {
			feedbackString = MESSAGE_DISPLAY_ALL;
			filteredTasks = _parser.convertToTaskArray(_store.entries_);
		} 
		/*else {
			feedbackString = "Displaying: " + criteria;
			//search for thing, then entries -> Array<Task>
			filteredTasks = new ArrayList<Task>();
		}*/
		return new Feedback(feedbackString, filteredTasks); 
	}

	private Feedback note(String noteString) {
		//String itemID = _parser.getID(noteString);
		//Note aNote = new Note(noteString);
		//_store.addNote(itemID, aNote)
		_store.storeTemp();
		JSONObject entry;
		String[] noteDetails = noteString.split(", ");
		for (int i = 0; i<_store.entries_.size(); i++){
			entry = (JSONObject) _store.entries_.get(i);
			String id = noteDetails[0];
			if (entry.get("id").equals(id)) {	
				entry.put("note", noteDetails[1]);
				break;
			}
		}
		_store.storeChanges();
		String feedbackString =  String.format(MESSAGE_ADDING_NOTE, noteString);
		return new Feedback(feedbackString);
	}
	
	public Feedback undo(){
		_isRedoable = true;
		Command latestCommand = _pastCommands.peek();
		if (latestCommand._type == COMMAND_TYPE.CD) {
			_store.swapFile();
		} else {
			_store.swapTemp();
		}
		String feedbackString = MESSAGE_UNDO;
		return new Feedback(feedbackString);
	}
	
	public Feedback redo () {
		String feedbackString;
		if (isRedoable()) {
			undo();
			_isRedoable = false;
			feedbackString = MESSAGE_REDO;
			return new Feedback(feedbackString);
		} else {
			feedbackString = MESSAGE_CANT_REDO;
			return new Feedback(feedbackString);
		}
	}
	
	private Feedback search(String searchTerm) {
		ArrayList<Task> filteredTasks = new ArrayList<Task>();
		
		for (int i = 0; i < _store.entries_.size(); i++) {
			String entry = _store.entries_.get(i).toString();
			JSONObject entryObject = (JSONObject) _store.entries_.get(i);
			if (entry.contains(searchTerm)) {
			    filteredTasks.add(_parser.retrieveTask(entryObject.get("id").toString(),_store.entries_));
			}
		}
		
		String feedbackString = String.format(MESSAGE_SEARCHING, searchTerm);
		return new Feedback(feedbackString, filteredTasks); 
	}

	private Feedback update(String updateString) {
		_store.storeTemp();
		JSONObject entry;
		ArrayList<String> updateDetails = _parser.parseUpdateString(updateString);
		for (int i = 0; i < _store.entries_.size(); i++) {
			entry = (JSONObject) _store.entries_.get(i);
			String id = updateDetails.get(0);
			if (entry.get("id").equals(id)) {
				for (int j=1; j<updateDetails.size();j++){
				entry.replace(updateDetails.get(j), updateDetails.get(++j));
				}
				_store.entries_.set(i, entry);
				break;
			}
		}
		_store.storeChanges();
		String feedbackString = String.format(MESSAGE_UPDATING, updateString);
		return new Feedback(feedbackString);
	}
	
	public boolean isExiting() {
		return _isExiting;
	}
	
	public boolean isInSummaryView() {
		return _isInSummaryView;
	}
	
	public boolean isRedoable(){
		return _isRedoable;
	}
	
}
