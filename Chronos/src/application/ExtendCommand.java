package application;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class ExtendCommand extends UpdateCommand {
	
	protected static final String FEEDBACK_MESSAGE =  "Extended %1$s";

	public ExtendCommand(String content) {
		super(content);
	}
	
	@Override
	public Feedback execute() { 
		ArrayList<String> extendDetails = _parse.parseExtendString(_content);
		String taskID = extendDetails.get(0);
		_id = findEntry(taskID);
		if (_id > LIMIT_ID) {
			_store.storeTemp();
			JSONObject entry = (JSONObject) _store.entries_.get(_id);
			updateEntry(entry, extendDetails);
			_store.storeChanges();
			String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			return new Feedback(feedbackString);
		} else {
			return new Feedback(ERROR_INVALID_ID);
		}
	}

}
