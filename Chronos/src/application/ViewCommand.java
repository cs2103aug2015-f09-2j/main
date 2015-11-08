package application;

import java.util.ArrayList;

public class ViewCommand extends Command {
	
	private static final String FEEDBACK_MESSAGE = "Retrieving Task: %1$s";
	
	//Instructions
	private static final String PATTERN = "view (task/event id)";
	private static final String INSTRUCTION_REQUIRED = "Enter the id of the item you want to view.";
	private static final String REQUIRED_FIELD_ID = "(description)";

	public ViewCommand(String content) {
		super(content);
	}

	//@@author A0126223U
	@Override
	public Feedback execute() {
		int index = findEntry(_content);
		if (index == Command.FIND_NO_ID) {
			assert _content.equals(EMPTY);
			log.warning(LOG_NO_ID);
			return new Feedback(ERROR_NO_ID);
		} else if (index == Command.FIND_INVALID_ID) {
			return new Feedback(ERROR_INVALID_ID);
		} else {
			Task selectedTask = _parse.retrieveTask(_content, _store.entries_);
			ArrayList<Task> data = new ArrayList<Task>();
			data.add(selectedTask);
			String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			Feedback feedback = new Feedback(feedbackString, data);
			feedback.setSummaryView(false);
			return feedback;
		}
	}

	@Override
	public Feedback undo() {
		return null; 
	}

	public static Instruction generateInstruction() {
		Instruction commandInstruction = new Instruction();
		commandInstruction.setCommandPattern(PATTERN);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED);
	    commandInstruction.addToRequiredFields(REQUIRED_FIELD_ID);
	    return commandInstruction;
	}
	
	
}
