package application;
import application.Command;
import application.Feedback;
import application.Task;

public class DoneCommand extends Command {
	
	//Unique Attributes
	private Task _completedTask;

	//Constant Strings
	private static final String FEEDBACK_MESSAGE =  "Marked %1$s as done.";
	private static final String FEEDBACK_COMPLETE = "Task %1$s is already complete.";
	private static final String ERROR_INVALID_ID = "Error: ID doesn't exist.";
	
	private static final String LOG_MESSAGE = "Task %1$s marked as done.";
	
	protected static final String ID_EMPTY = "";
	protected static final String UPDATE_STRING = ", s:";
	
	public DoneCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() {
		String feedbackString = null;
		Feedback feedback;
		if (_content != ID_EMPTY && _parse.isExistingId(_content, _store.entries_)) {
			feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			_completedTask = _parse.retrieveTask(_content, _store.entries_);
			if (_completedTask.isTaskComplete()) {
				feedback = new Feedback(String.format(FEEDBACK_COMPLETE, _content));
			} else {
				_completedTask.markTaskAsDone(true);
				String content = _completedTask.getId() + UPDATE_STRING + _completedTask.isTaskComplete();
				feedback = new UpdateCommand(content).execute(); //_store saving handled by UpdateCommand
				feedback.setMessage(feedbackString);
				log.info(String.format(LOG_MESSAGE, _content));
			}
		} else if (!_parse.isExistingId(_content, _store.entries_)) {
			feedback = new Feedback(ERROR_INVALID_ID);
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
		String content = _completedTask.getId()+ UPDATE_STRING + _completedTask.isTaskComplete();
		Feedback feedback = new UpdateCommand(content).execute();
		return feedback;
	}
	
}
