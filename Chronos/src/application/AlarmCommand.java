package application;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.simple.JSONObject;

//@@author A0131496A
public class AlarmCommand extends UpdateCommand {
	
	private static final String FEEDBACK_INVALID_COMMAND = "Error: Invalid Command";
	private static final String ALARM_OFF = "off";
	private static final String JSON_ALRAM = "alarm";
	private static final String JSON_START_DATE = "start date";
	private static final String JSON_END_DATE = "due date";
	private static final int FIELDS_NUM = 2;
	
	/**
	 * Alarm command should have 2 fields, the id to identify task/event, and
	 * 	 either "off" to denote turning off the alarm
	 *   or the number of hours before the event starts or the task finishes that the alarm should go off
	 * @param content
	 */
	public AlarmCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() {
		try{
		String[] alarmDetails = _parse.parseAlarmString(_content);
		if (alarmDetails.length != FIELDS_NUM){
			return new Feedback(FEEDBACK_INVALID_COMMAND);
		}
		String taskID = alarmDetails[0];
		//call parent method
		_index = findEntry(taskID);
		if (_index > LIMIT_ID) {
			performUpdate(alarmDetails);
			String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			return new Feedback(feedbackString);
		} else {
			return new Feedback(ERROR_INVALID_ID);
		}
		}catch (NumberFormatException | ParseException e) {
			return new Feedback(FEEDBACK_INVALID_COMMAND);
		}
	}

	private void performUpdate(String[] alarmDetails) throws ParseException {
		_store.storeTemp();
		JSONObject entry = (JSONObject) _store.entries_.get(_index);
		updateAlarm(entry, alarmDetails);
		_store.storeChanges();
	}
	
	//Suppress the warnings from JSON syntax, no effect on how the code works
	@SuppressWarnings("unchecked")
	private void updateAlarm(JSONObject entry, String[] alarmDetails) throws NumberFormatException, ParseException{
		if(alarmDetails[1].equals(ALARM_OFF)){
			entry.replace(JSON_ALRAM,ALARM_OFF);
		}else{
			String alarmOffset = findAlarmOffset(entry);
			DateFormat dateFormat = new SimpleDateFormat(Task.DATE_FORMAT); 
			Date offset = dateFormat.parse(alarmOffset);
			int hoursPrior = Integer.parseInt(alarmDetails[1]);
			Date newAlarm = getNewAlarm(hoursPrior, offset);
			String alarmString = dateFormat.format(newAlarm);
			entry.replace(JSON_ALRAM, alarmString);
		}
	}

	private Date getNewAlarm(int hoursPrior, Date offset) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(offset);
		//The new absolute alarm time should be hoursPrior before the offset
		cal.add(Calendar.HOUR, hoursPrior*(-1));
		Date alarm = cal.getTime();
		return alarm;
	}

	private String findAlarmOffset(JSONObject entry) {
		String alarmOffset;
		if (entry.containsKey(JSON_START_DATE)){
			//if it is event, alarm should be calculated from the start time
			alarmOffset = entry.get(JSON_START_DATE).toString();
		}else{
			//else it is task, alarm should be calculated from the end time
			alarmOffset = entry.get(JSON_END_DATE).toString();
		}
		return alarmOffset;
	}

}
