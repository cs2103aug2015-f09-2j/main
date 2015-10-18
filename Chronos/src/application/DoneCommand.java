package application;
import application.Command;
import application.Feedback;
import application.Parser;
import application.Storage;
import application.Task;

public class DoneCommand extends Command {
	
	//Unique Attributes
	private Task _completedTask;

	//Constant Strings
	private static final String FEEDBACK_MESSAGE =  "Marked %1$s as done.";
	
	private static final String LOG_MESSAGE = "Task %1$s marked as done.";
	
	protected static final String UPDATE_STRING = ", s:";
	
	public DoneCommand(Storage store, Parser parse, String content) {
		super(store, parse, content);
	}

	@Override
	public Feedback execute() {
		String feedbackString = null;
		Feedback feedback;
		if(_content!="") {
			feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			_completedTask = _parse.retrieveTask(_content, _store.entries_);
			_completedTask.markTaskAsDone(true);
			_store.storeTemp();
			String content = _completedTask.getId()+ UPDATE_STRING + _completedTask.isTaskComplete();
			feedback = new UpdateCommand(_store, _parse, content).execute();
			feedback.setMessage(feedbackString);
			_store.storeChanges();
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
		_store.storeTemp();
		String content = _completedTask.getId()+ UPDATE_STRING + _completedTask.isTaskComplete();
		Feedback feedback = new UpdateCommand(_store, _parse, content).execute();
		_store.storeChanges();
		return feedback;
	}
	
}
