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
		ArrayList<Task> data = null;
		String feedbackString = EMPTY;
		if(_content != EMPTY) {
			data = new ArrayList<Task>();
			Task selectedTask = _parse.retrieveTask(_content, _store.entries_);
			data.add(selectedTask);
			feedbackString = String.format(FEEDBACK_MESSAGE, _content);
		} else {
			log.warning(LOG_NO_ID);
			feedbackString = ERROR_NO_CONTENT;
		}
		Feedback feedback = new Feedback(feedbackString, data);
		feedback.setSummaryView(false);
		return feedback;
	}

	@Override
	public Feedback undo() {
		return null; //Alternative: return new DisplayCommand(null).execute();
	}

	public static Instruction generateInstruction() {
		Instruction commandInstruction = new Instruction();
		commandInstruction.setCommandPattern(PATTERN);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED);
	    commandInstruction.addToRequiredFields(REQUIRED_FIELD_ID);
	    return commandInstruction;
	}
	
	
}
