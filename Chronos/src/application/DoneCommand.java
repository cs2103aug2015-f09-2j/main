package application;
import org.json.simple.JSONObject;

import application.Command;
import application.Feedback;
import application.Task;

public class DoneCommand extends Command {
	
	//Unique Attributes
	private Task _completedTask;

	//Constant Strings
	private static final String FEEDBACK_MESSAGE =  "Marked %1$s as done.";
	private static final String FEEDBACK_ALREADY_DONE = "Error: %1$s is already done.";
	private static final String LOG_MESSAGE = "Task %1$s marked as done.";
	protected static final String UPDATE_STRING = ", s:";
	
	//Instructions
	private static final String PATTERN = "done (task/event id)";
	private static final String INSTRUCTION_REQUIRED = "Type the task or event id of the completed item.";
	private static final String REQUIRED_FIELD_ID = "(task/event id)";

	
	public DoneCommand(String content) {
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
			String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			_completedTask = _parse.retrieveTask(_content, _store.entries_);
			if (_completedTask.isTaskComplete()) {
				return new Feedback(String.format(FEEDBACK_ALREADY_DONE, _content));
			} else {
				_completedTask.markTaskAsDone(true);
				String content = _completedTask.getId() + UPDATE_STRING + _completedTask.isTaskComplete();
				Feedback feedback = new UpdateCommand(content).execute();
				feedback.setMessage(feedbackString);
				return feedback;
			}
		}
	}

	@Override
	public Feedback undo() {
		_completedTask.markTaskAsDone(false); 
		String content = _completedTask.getId() + UPDATE_STRING + _completedTask.isTaskComplete();
		Feedback feedback = new UpdateCommand(content).execute();
		return feedback;
	}
	
	public static Instruction generateInstruction() {
		Instruction commandInstruction = new Instruction();
		commandInstruction.setCommandPattern(PATTERN);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED);
	    commandInstruction.addToRequiredFields(REQUIRED_FIELD_ID);
		return commandInstruction;
	}
	
	
}
