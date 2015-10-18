package application;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class SearchCommand extends Command {

	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Searching for: %1$s";
	private static final String ID = "id"; //note: collate JSON strings into just one class for easy referencing
	
	public SearchCommand(Storage store, Parser parse, String content) {
		super(store, parse, content);
	}

	@Override
	public Feedback execute() {
		ArrayList<Task> filteredTasks = new ArrayList<Task>();
		
		for (int i = 0; i < _store.entries_.size(); i++) {
			String entry = _store.entries_.get(i).toString();
			JSONObject entryObject = (JSONObject) _store.entries_.get(i);
			if (entry.contains(_content)) {
			    filteredTasks.add(_parse.retrieveTask(entryObject.get("id").toString(),_store.entries_));
			}
		}
		
		String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
		return new Feedback(feedbackString, filteredTasks); 
	}

	@Override
	public Feedback undo() {
		return null;
	}
	
}
