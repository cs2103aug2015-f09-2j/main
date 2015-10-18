package application;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class UpdateCommand extends Command {

	//Unique attributes
	private JSONObject _oldEntry;
	private int _id;
		
	//Constant Strings
	private static final String FEEDBACK_MESSAGE =  "Updated %1$s";
	private static final String FEEDBACK_MESSAGE_UNDO =  "Restored %1$s";
	private static final String ID = "id"; //note: collate JSON strings into just one class for easy referencing
	
	public UpdateCommand(Storage store, Parser parse, String content) {
		super(store, parse, content);
	}

	@Override
	public Feedback execute() { //TODO: Refactor, too nested
		ArrayList<String> updateDetails = _parse.parseUpdateString(_content);
		String taskID = updateDetails.get(0);
		_id = findEntry(taskID);
		if (_id > -1) {
			_store.storeTemp();
			JSONObject entry = (JSONObject) _store.entries_.get(_id);
			updateEntry(entry, updateDetails);
			_store.storeChanges();
			String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			return new Feedback(feedbackString);
		} else {
			return new Feedback(ERROR_INVALID_ID);
		}
	}

	private int findEntry(String id) {
		for (int i = 0; i < _store.entries_.size(); i++) {
			JSONObject currentEntry = (JSONObject) _store.entries_.get(i);
			if (currentEntry.get(ID).equals(id)) { //updateEntry(entry, updateDetails);
				_oldEntry = currentEntry;
				return i;
			}
		}
		return -1;
	}
	
	private void updateEntry(JSONObject entry, ArrayList<String> updateDetails) {
		for (int j=1; j<updateDetails.size();j++){
			entry.replace(updateDetails.get(j), updateDetails.get(++j));
		}
		_store.entries_.set(_id, entry);
	}

	@Override
	public Feedback undo() {
		_store.storeTemp();
		JSONObject entry = (JSONObject) _store.entries_.get(_id);
		_store.entries_.set(_id, _oldEntry);
		_store.storeChanges();
		String feedbackString = String.format(FEEDBACK_MESSAGE_UNDO, _content);
		return new Feedback(feedbackString);
	}
	
}
