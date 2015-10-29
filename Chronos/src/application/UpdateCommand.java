package application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;

import org.json.simple.JSONObject;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;

public class UpdateCommand extends Command {

	//Unique attributes
	private JSONObject _oldEntry;
	private int _id;
		
	//Constant Strings
	private static final String FEEDBACK_MESSAGE =  "Updated %1$s";
	private static final String FEEDBACK_MESSAGE_UNDO =  "Restored %1$s";
	static final String JSON_START_DATE = "start date";
	static final String JSON_END_DATE = "due date";
	
	public UpdateCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() { 
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
			if (currentEntry.get(Parser.JSON_ID).equals(id)) {
				_oldEntry = (JSONObject) currentEntry.clone();
				return i;
			}
		}
		return -1;
	}
	
	private void updateEntry(JSONObject entry, ArrayList<String> updateDetails) {
		String field,value;
		Span aSpan;
		DateFormat dateFormat = new SimpleDateFormat(); 
		for (int j=1; j<updateDetails.size();j++){
			field = updateDetails.get(j);
			value = updateDetails.get(++j);
			if (field.equals(JSON_END_DATE)||field.equals(JSON_START_DATE)){
				aSpan = Chronic.parse(value);	
				value = dateFormat.format(aSpan.getBeginCalendar().getTime());
			}
			entry.replace(field,value );
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
