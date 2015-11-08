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
	protected int _index;
		
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
	
	//Instructions
	private static final String PATTERN = "update (task id), (description), p:(priority), c:(category), e:(end date), b:(start date), s:(complete)"; 
	private static final String INSTRUCTION_REQUIRED = "Enter the id of the item you want to update.";
	private static final String INSTRUCTION_OPTIONAL = "Enter one or more of the fields you want to update.";
	private static final String REQUIRED_FIELD_ID = "(task/event id)";

	
	protected static final int LIMIT_ID = -1;
	
	public UpdateCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() { 
		ArrayList<String> updateDetails = _parse.parseUpdateString(_content);
		String taskID = updateDetails.get(0);
		_index = findEntry(taskID);
		
		if (_index == Command.FIND_NO_ID) {
			assert _content.equals(EMPTY);
			log.warning(LOG_NO_ID);
			return new Feedback(ERROR_NO_ID);
		} else if (_index == Command.FIND_INVALID_ID) {
			return new Feedback(ERROR_INVALID_ID);
		} else {
			_store.storeTemp();
			JSONObject entry = (JSONObject) _store.entries_.get(_index);
			_oldEntry = (JSONObject) entry.clone();
			updateEntry(entry, updateDetails);
			_store.storeChanges();
			String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			return new Feedback(feedbackString);
		}
	}
	
	//@@author A0131496A
	/**
	 * This method allows multiple updates of the fields of the entry
	 * @param entry
	 * @param updateDetails
	 */
	protected void updateEntry(JSONObject entry, ArrayList<String> updateDetails) {
		String field,value;
		Span aSpan;
		DateFormat dateFormat = new SimpleDateFormat(Task.DATE_FORMAT);
		String id = entry.get(Parser.JSON_ID).toString();
		boolean isAlarmChanged = false;
		
		for (int j=1; j<updateDetails.size();j++){
			field = updateDetails.get(j);
			value = updateDetails.get(++j);
			if (field.equals(JSON_END_DATE)||field.equals(JSON_START_DATE)){
				//if there is a change in dates, we have to update the alarm
				isAlarmChanged = true;
				aSpan = Chronic.parse(value);	
				value = dateFormat.format(aSpan.getBeginCalendar().getTime());
			}
			id = updateField(entry, field, value, id);
		}
		//alarm will be updated if there is a need to do so
		updateAlarm(entry, dateFormat, id, isAlarmChanged);
	}

	//@@author A0131496A
	private void updateAlarm(JSONObject entry, DateFormat dateFormat,
			String id, boolean isAlarmChanged) {
		int hoursPrior = checkHoursPrior(entry, dateFormat);
		if (hoursPrior != -1 && isAlarmChanged){
			AlarmCommand setAlarm = new AlarmCommand(String.format(ALARM_COMMAND, id, hoursPrior));
			setAlarm.execute();
		}
	}

	private String updateField(JSONObject entry, String field, String value,
			String id) {
		if(entry.get(JSON_START_DATE) == null &&field.equals(JSON_START_DATE)) {//convert task to event
				Event event = taskToEvent(entry, value);
				id = event.getId();
				DeleteCommand deleteTask = new DeleteCommand(entry.get(_parse.JSON_ID).toString());
				deleteTask.execute();
				_store.entries_.add(_parse.convertToJSON(event));
		}else {			//for event and  updating task as usual
			entry.replace(field,value);
			_store.entries_.set(_index, entry);
		}
		return id;
	}

	//@@author A0131496A
	private int checkHoursPrior(JSONObject entry, DateFormat dateFormat) {
		try{
			//check if the entry has alarm
			if(entry.get(JSON_ALARM) != OFF_ALARM){
				Task aTask = _parse.convertToTask(entry);
				String alarmOffset = aTask.getAlarmOffset();
				Date alarmCalculatedFrom = dateFormat.parse(alarmOffset);
				Date alarm = dateFormat.parse(entry.get(JSON_ALARM).toString());
				//return the number of hours of difference between the alarm and the offset
				return (int)( alarmCalculatedFrom.getTime() - alarm.getTime())/HOUR_TO_MILLI;
			}else{
				return -1;
			}
		}catch (ParseException e){
			log.info(ERROR_PARSING_ALARM);
			return -1;
		}
	}
	
	private Event taskToEvent(JSONObject task, String value) {
		String desc = task.get(_parse.JSON_DESC).toString();
		String endDate = task.get(_parse.JSON_END_DATE).toString();
		String priority = task.get(_parse.JSON_PRIORITY).toString();
		String category = task.get(_parse.JSON_CATEGORY).toString();
		String alarm = task.get(_parse.JSON_ALARM).toString();
		Event event = new Event(EMPTY, desc, EMPTY, endDate, priority, category, alarm);
		event.setStartDate(value);
		event.setId(_store.getEventId());
		return event;
	}

	@Override
	public Feedback undo() {
		_store.storeTemp();
		JSONObject entry = (JSONObject) _store.entries_.get(_index);
		_store.entries_.set(_index, _oldEntry);
		_store.storeChanges();
		String feedbackString = String.format(FEEDBACK_MESSAGE_UNDO, _content);
		return new Feedback(feedbackString);
	}
	
	public static Instruction generateInstruction() {
		Instruction commandInstruction = new Instruction();
		commandInstruction.setCommandPattern(PATTERN);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED);
	    commandInstruction.addToRequiredFields(REQUIRED_FIELD_ID);
	    commandInstruction.addToInstructions(INSTRUCTION_OPTIONAL);
		return commandInstruction;
	}
	
}
