package application;
import application.Command;
import application.Feedback;
import application.Task;

public class DoneCommand extends Command {
	
	//Unique Attributes
	private Task _completedTask;

	//Constant Strings
	private static final String FEEDBACK_MESSAGE =  "Marked %1$s as done.";
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
		String feedbackString = null;
		Feedback feedback;
		if (_content != EMPTY) {
			feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			_completedTask = _parse.retrieveTask(_content, _store.entries_);
			_completedTask.markTaskAsDone(true);
			String content = _completedTask.getId() + UPDATE_STRING + _completedTask.isTaskComplete();
			feedback = new UpdateCommand(content).execute(); //_store.Temp done in Update Command
			feedback.setMessage(feedbackString);
			log.info(String.format(LOG_MESSAGE, _content));
		} else {
			log.warning(LOG_NO_ID);
			feedbackString = ERROR_NO_CONTENT;
			feedback = new Feedback(feedbackString);
		}
		return feedback;
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
