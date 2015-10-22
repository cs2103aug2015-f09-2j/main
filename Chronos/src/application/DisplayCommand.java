package application;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class DisplayCommand extends Command {

	private static final String MESSAGE_DISPLAY_ALL = "Displaying All";
	
	private static final String CONTENT_EMPTY = "";
	
	public DisplayCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() {
		ArrayList<Task> filteredTasks = new ArrayList<Task>();
		String feedbackString = CONTENT_EMPTY;
		if(_content.equals(CONTENT_EMPTY)) {
			//displayAll(filteredTasks, feedbackString); Note: this method does not pass the data back to execute()
			feedbackString = MESSAGE_DISPLAY_ALL;
			filteredTasks = _parse.convertToTaskArray(_store.entries_);
			log.info("Display all items");
		} else {
			feedbackString = "Displaying: " + _content;
			String condition = null;
			String[] criteria = _content.split(", ");
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
				filteredTasks.add((Task)_parse.retrieveTask(entryObject.get("id").toString(),_store.entries_));
			}
		}
		log.info("Display selected items");
		return filteredTasks;
	}
	
	@Override
	public Feedback undo() {
		return null;
	}

}
