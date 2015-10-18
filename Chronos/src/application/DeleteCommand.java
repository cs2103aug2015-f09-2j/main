package application;

import org.json.simple.JSONObject;

public class DeleteCommand extends Command {

	//Unique attributes
	private JSONObject _deletedEntry;
		
	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Deleted item with id: %1$s";
	protected static final String FEEDBACK_MESSAGE_UNDO =  "Restored item with id: %1$s";
	
	public DeleteCommand(Storage store, Parser parse, String content) {
		super(store, parse, content);
	}

	@Override
	public Feedback execute() {
		String feedbackString = null;
		if(_content!="") {
			_store.storeTemp();
			for (int i = 0; i < _store.entries_.size(); i++) {
				JSONObject entry = (JSONObject) _store.entries_.get(i);
				if (entry.get("id").equals(_content)) {
					_store.entries_.remove(i);
					break;
				}
			}
			feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			_store.storeChanges();
		} else {
			assert _content == null;
			log.warning(LOG_NO_ID);
		}
		return new Feedback(feedbackString, _parse.convertToTaskArray(_store.entries_)); 
	}

	@Override
	public Feedback undo() {
		_store.storeTemp();
		_store.entries_.add(_deletedEntry);
		_store.storeChanges();
		String feedbackString = String.format(FEEDBACK_MESSAGE_UNDO, _content);
		return new Feedback(feedbackString);
	}

}
