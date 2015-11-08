package application;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class ExtendCommand extends UpdateCommand {
	
	protected static final String FEEDBACK_MESSAGE =  "Extended %1$s";

	//Instructions
	private static final String PATTERN = "extend (task/event id), (new deadline)";
	private static final String INSTRUCTION_REQUIRED_TASK = "Type the task or event id of item to extend.";
	private static final String INSTRUCTION_REQUIRED_DEADLINE = "Type the new deadline.";
	private static final String REQUIRED_FIELD_ID = "(task/event id)";
	private static final String REQUIRED_FIELD_DEADLINE = "(new end date)";

	
	public ExtendCommand(String content) {
		super(content);
	}
	
	@Override
	public Feedback execute() { 
		ArrayList<String> extendDetails = _parse.parseExtendString(_content);
		String taskID = extendDetails.get(0);
		_id = findEntry(taskID);
		if (_id > LIMIT_ID) {
			_store.storeTemp();
			JSONObject entry = (JSONObject) _store.entries_.get(_id);
			updateEntry(entry, extendDetails);
			_store.storeChanges();
			String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			return new Feedback(feedbackString);
		} else {
			return new Feedback(ERROR_INVALID_ID);
		}
	}

	public static Instruction generateInstruction() {
		Instruction commandInstruction = new Instruction();
		commandInstruction.setCommandPattern(PATTERN);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED_TASK);
	    commandInstruction.addToRequiredFields(REQUIRED_FIELD_ID);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED_DEADLINE);
		commandInstruction.addToRequiredFields(REQUIRED_FIELD_DEADLINE);
		return commandInstruction;
	}
	
	
	
}
