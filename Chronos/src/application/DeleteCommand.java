package application;

import org.json.simple.JSONObject;

public class DeleteCommand extends Command {

	//Unique attributes
	private JSONObject _deletedEntry;
		
	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Deleted item with id: %1$s";
	protected static final String FEEDBACK_MESSAGE_UNDO =  "Restored item with id: %1$s";
	
	//Instructions
	private static final String PATTERN = "delete (task/event id)";
	private static final String INSTRUCTION_REQUIRED = "Type the task or event id that you want to delete.";
	private static final String REQUIRED_FIELD_ID = "(task/event id)";
	
	public DeleteCommand(String content) {
		super(content);
	}

	//@@author A0126223U
	@Override
	public Feedback execute() {
		String feedbackString = null;
		if (_content != EMPTY) {
			_store.storeTemp();
			feedbackString = deleteItem(); 
			_store.storeChanges();
		} else {
			assert _content == null;
			log.warning(LOG_NO_ID);
		}
		return new Feedback(feedbackString); 
	}

	private String deleteItem() {
		int itemIndex = findEntry(_content);
		if (itemIndex == Command.FIND_NO_ID) {
			return ERROR_NO_ID;
		} else if (itemIndex == Command.FIND_INVALID_ID) {
			return ERROR_INVALID_ID;
		} else {
			_deletedEntry = (JSONObject) _store.entries_.remove(itemIndex);
			_isSuccessful = true;
			return String.format(FEEDBACK_MESSAGE, _content);
		}
	}
	
	@Override
	public Feedback undo() {
		_store.storeTemp();
		_store.entries_.add(_deletedEntry);
		_store.storeChanges();
		String feedbackString = String.format(FEEDBACK_MESSAGE_UNDO, _content);
		return new Feedback(feedbackString);
	}
	
	public static Instruction generateInstruction() {
		Instruction commandInstruction = new Instruction();
		commandInstruction.setCommandPattern(PATTERN);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED);
	    commandInstruction.addToRequiredFields(REQUIRED_FIELD_ID);
		return commandInstruction;
	}

}
