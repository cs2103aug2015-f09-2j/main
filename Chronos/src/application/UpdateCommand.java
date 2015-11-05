package application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import org.json.simple.JSONObject;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;

public class UpdateCommand extends Command {

	//Unique attributes
	protected JSONObject _oldEntry;
	protected int _id;
		
	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Updated %1$s";
	protected static final String FEEDBACK_MESSAGE_UNDO =  "Restored %1$s";
	static final String JSON_START_DATE = "start date";
	static final String JSON_END_DATE = "due date";
	static final String JSON_ALARM = "alarm";
	static final String OFF_ALARM = "off";
	static final String ALARM_COMMAND = "%1$s, %2$s";
	static final String ERROR_PARSING_ALARM = "failed to parse dates for alarm";
	static final int HOUR_TO_MILLI = 1000*60*60;
	
	protected static final int LIMIT_ID = -1;
	
	public UpdateCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() { 
		ArrayList<String> updateDetails = _parse.parseUpdateString(_content);
		String taskID = updateDetails.get(0);
		_id = findEntry(taskID);
		if (_id > LIMIT_ID) {
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

	protected int findEntry(String id) {
		for (int i = 0; i < _store.entries_.size(); i++) {
			JSONObject currentEntry = (JSONObject) _store.entries_.get(i);
			if (currentEntry.get(Parser.JSON_ID).equals(id)) {
				_oldEntry = (JSONObject) currentEntry.clone();
				return i;
			}
		}
		return -1;
	}
	
	protected void updateEntry(JSONObject entry, ArrayList<String> updateDetails) {
		String field,value;
		Span aSpan;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
		int offset = -1;
		String id = entry.get(_parse.JSON_ID).toString();
		boolean isChanged = false;
		try{
			if(entry.get(JSON_ALARM) != OFF_ALARM){
				Date alarmFrom;
				if(entry.get(JSON_START_DATE)!=null){
					alarmFrom = dateFormat.parse(entry.get(JSON_START_DATE).toString());
				}else{
					alarmFrom = dateFormat.parse(entry.get(JSON_END_DATE).toString());
				}
				Date alarm = dateFormat.parse(entry.get(JSON_ALARM).toString());
				offset = (int)( alarmFrom.getTime() - alarm.getTime())/HOUR_TO_MILLI;
				System.out.println(offset);
			}
		}catch (ParseException e){
			log.info(ERROR_PARSING_ALARM);
		}
		for (int j=1; j<updateDetails.size();j++){
			field = updateDetails.get(j);
			value = updateDetails.get(++j);
			if (field.equals(JSON_END_DATE)||field.equals(JSON_START_DATE)){
				isChanged = true;
				aSpan = Chronic.parse(value);	
				value = dateFormat.format(aSpan.getBeginCalendar().getTime());
			}
			if(entry.get(JSON_START_DATE) == null) {	//for tasks
				if(field.equals(JSON_START_DATE)) {		//convert task to event
					Event event = taskToEvent(entry, value);
					id = event.getId();
					DeleteCommand deleteTask = new DeleteCommand(entry.get(_parse.JSON_ID).toString());
					deleteTask.execute();
					_store.entries_.add(_parse.convertToJSON(event));
				}
				else {		//for updating task as usual
					entry.replace(field,value);
					_store.entries_.set(_id, entry);
				}
			}
			else {			//for event
				entry.replace(field,value);
				_store.entries_.set(_id, entry);
			}
		}
		if (isChanged){
			AlarmCommand setAlarm = new AlarmCommand(String.format(ALARM_COMMAND, id, offset));
			setAlarm.execute();
		}
	}
	
	private Event taskToEvent(JSONObject task, String value) {
		String desc = task.get(_parse.JSON_DESC).toString();
		String endDate = task.get(_parse.JSON_END_DATE).toString();
		String priority = task.get(_parse.JSON_PRIORITY).toString();
		String category = task.get(_parse.JSON_CATEGORY).toString();

		//Event event = new Event(EMPTY, desc, EMPTY, endDate, priority, category, null);

		String alarm = task.get(_parse.JSON_ALARM).toString();
		Event event = new Event(EMPTY, desc, EMPTY, endDate, priority, category,alarm);
		event.setStartDate(value);
		event.setId(_store.getEventId());
		return event;
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
