package application;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.json.simple.JSONObject;

public class AlarmCommand extends UpdateCommand {
	private static final String FEEDBACK_INVALID_COMMAND = "Error: Invalid Command";
	private static final String ALARM_OFF = "off";
	private static final String JSON_ALRAM = "alarm";
	private static final String JSON_START_DATE = "start date";
	private static final String JSON_END_DATE = "due date";
	
	public AlarmCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() {
		try{
		String[] alarmDetails = _parse.parseAlarmString(_content);
		if (alarmDetails.length != 2){
			return new Feedback(FEEDBACK_INVALID_COMMAND);
		}
		String taskID = alarmDetails[0];
		_id = findEntry(taskID);
		if (_id > LIMIT_ID) {
			_store.storeTemp();
			JSONObject entry = (JSONObject) _store.entries_.get(_id);
			updateAlarm(entry, alarmDetails);
			_store.storeChanges();
			String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			return new Feedback(feedbackString);
		} else {
			return new Feedback(ERROR_INVALID_ID);
		}
		}catch (NumberFormatException | ParseException e) {
			return new Feedback(FEEDBACK_INVALID_COMMAND);
		}
	}
	
	private void updateAlarm(JSONObject entry, String[] alarmDetails) throws NumberFormatException, ParseException{
		if(alarmDetails[1].equals(ALARM_OFF)){
			entry.replace(JSON_ALRAM,ALARM_OFF);
		}else{
			int hours = Integer.parseInt(alarmDetails[1]);
			String alarmOffset;
			if (entry.containsKey(JSON_START_DATE)){
				alarmOffset = entry.get(JSON_START_DATE).toString();
			}else{
				alarmOffset = entry.get(JSON_END_DATE).toString();
			}
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa"); 
			Date offset = dateFormat.parse(alarmOffset);
			Calendar cal = Calendar.getInstance();
			cal.setTime(offset);
			cal.add(Calendar.HOUR,hours*(-1));
			Date alarm = cal.getTime();
			String alarmString = dateFormat.format(alarm);
			entry.replace(JSON_ALRAM, alarmString);
		}
	}

}
