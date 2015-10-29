package application;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class DisplayCommand extends Command {

	private static final String MESSAGE_DISPLAY_ALL = "Displayed all items";
	private static final String MESSAGE_DISPLAY =  "Displayed: %1$s";
	private static final String MESSAGE_DISPLAY_SELECTED = "Displayed selected items";
	static final String CONTENT_SEPARATOR = ", ";
	
	public DisplayCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() {
		ArrayList<Task> filteredTasks = new ArrayList<Task>();
		String feedbackString = EMPTY;
		if (_content.equals(EMPTY)) {
			feedbackString = MESSAGE_DISPLAY_ALL;
			filteredTasks = _parse.convertToTaskArray(_store.entries_);
			log.info(MESSAGE_DISPLAY_ALL);
		} else {
			feedbackString = String.format(MESSAGE_DISPLAY, _content);
			String condition = EMPTY;
			String[] criteria = _content.split(CONTENT_SEPARATOR);
			filteredTasks = displaySelectedItems(condition, criteria);
		}
		Feedback feedback = new Feedback(feedbackString, filteredTasks);
		feedback.setSummaryView(true);
		return feedback; 
	}

	//TODO: Check for correctness
	private ArrayList<Task> displaySelectedItems(String condition, String[] criteria) {
		
		for(int index=0; index<criteria.length; index++) {
			condition = criteria[index].substring(2);
		}
		
		ArrayList<Task> filteredTasks = new ArrayList<Task>();
		for(int index=0; index<_store.entries_.size(); index++) {
			String entry = _store.entries_.get(index).toString();
			JSONObject entryObject = (JSONObject) _store.entries_.get(index);
			if(entry.contains(condition)) {
				filteredTasks.add((Task)_parse.retrieveTask(entryObject.get(Parser.JSON_ID).toString(),_store.entries_));
			}
		}
		log.info(MESSAGE_DISPLAY_SELECTED);
		return filteredTasks;
	}
	
	@Override
	public Feedback undo() {
		return null;
	}

}
