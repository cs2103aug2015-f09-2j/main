package application;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class DisplayCommand extends Command {

	private static final String MESSAGE_DISPLAY_ALL = "Displaying All";
	
	private static final String CONTENT_EMPTY = "";
	
	public DisplayCommand(Storage store, Parser parse, String content) {
		super(store, parse, content);
	}

	@Override
	public Feedback execute() {
		ArrayList<Task> filteredTasks = new ArrayList<Task>();
		String feedbackString = CONTENT_EMPTY;
		if (_content.equals(CONTENT_EMPTY)) {
			feedbackString = MESSAGE_DISPLAY_ALL;
			filteredTasks = _parse.convertToTaskArray(_store.entries_);
			log.info("Display all items");
		} 
		else {
			feedbackString = "Displaying: " + _content;
			String condition = null;
			String[] criteria = _content.split(", ");
			for(int index=0; index<criteria.length; index++) {
				condition = criteria[index].substring(2);
			}
			for(int index=0; index<_store.entries_.size(); index++) {
				String entry = _store.entries_.get(index).toString();
				JSONObject entryObject = (JSONObject) _store.entries_.get(index);
				if(entry.contains(condition)) {
					filteredTasks.add(_parse.retrieveTask(entryObject.get("id").toString(),_store.entries_));
				}
			}
			log.info("Display selected items");
		}
		Feedback feedback = new Feedback(feedbackString, filteredTasks);
		feedback.setSummaryView(true);
		return feedback; 
	}

	@Override
	public Feedback undo() {
		return null;
	}

}
