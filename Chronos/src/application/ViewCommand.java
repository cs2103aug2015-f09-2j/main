package application;

import java.util.ArrayList;

public class ViewCommand extends Command {
	
	private static final String FEEDBACK_MESSAGE = "Retrieving Task: %1$s";

	public ViewCommand(Storage store, Parser parse, String content) {
		super(store, parse, content);
	}

	@Override
	public Feedback execute() {
		ArrayList<Task> data = null;
		String feedbackString = null;
		if(_content!="") {
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
}
