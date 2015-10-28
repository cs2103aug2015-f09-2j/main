package application;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class NoteCommand extends Command {

	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Added note to %1$s";
	private static final String FEEDBACK_MESSAGE_UNDO =  "Restored %1$s";
	private static final String CONTENT_EMPTY = "";
	
	//Unique Attributes
	private JSONObject _oldEntry;
	private int _id;
	
	public NoteCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() {
		String feedbackString = CONTENT_EMPTY;
		String[] noteDetails = _content.split(_parse.CONTENT_SEPARATOR);
		_id = findEntry(noteDetails[0]);
		if (_id > -1) {
			feedbackString = noteProcess(_id, noteDetails);
		} else { 
			assert _content == CONTENT_EMPTY;
			log.warning(LOG_NO_ID);
			feedbackString = LOG_NO_ID;
		}
		
		return new Feedback(feedbackString);
	}

	private int findEntry(String id) {
		for (int i = 0; i < _store.entries_.size(); i++) {
			JSONObject currentEntry = (JSONObject) _store.entries_.get(i);
			if (currentEntry.get(Parser.JSON_ID).equals(id)) {
				_oldEntry = (JSONObject) currentEntry.clone();
				return i;
			}
		}
		return -1;
	}
	
	private String noteProcess(int id, String[] noteDetails) {
		_store.storeTemp();
		Task aTask = _parse.retrieveTask(noteDetails[0], _store.entries_);
		aTask.addNote(noteDetails[1]); //defend this
		_store.entries_.set(_id, _parse.convertToJSON(aTask));
		_store.storeChanges();
		return String.format(FEEDBACK_MESSAGE, _content);
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