package application;

import java.text.ParseException;
import java.util.Calendar;

import com.mdimension.jchronic.Chronic;

public class Event extends Task {

	static final String ID_HEADER = "e";	
	
	private String _startDate;
	
	private static final String DATE_SEPARATOR = " to ";
	private static final String PRIORITY_HEADER = "p:";
	private static final String CATEGORY_HEADER = "c:";
	private static final String EMPTY_SPACE = " ";
	private static final String ID_DISPLAY = ". ";
	private static final String MESSAGE_INVALID_END = "End date < start date";
	
	//@@author A0126223U
	public Event(String[] contents) throws ArithmeticException, ParseException {
		super();
		_description = contents[0];
		for (int i = 1; i < contents.length; i++) {
			if (contents[i].contains(PRIORITY_HEADER)) {
				_priority = contents[i].substring(2).toLowerCase();
			} else if (contents[i].contains(CATEGORY_HEADER)) {
				_category = contents[i].substring(2);
			} else { //date manipulation
				String[] dates = contents[i].split(DATE_SEPARATOR);
				Calendar start = Chronic.parse(dates[0]).getBeginCalendar();
				Calendar end = Chronic.parse(dates[1]).getEndCalendar();
				if (end.getTime().compareTo(start.getTime())<0){
					throw new ArithmeticException(MESSAGE_INVALID_END);
				}
				_startDate = manipulateDate(start);
				_endDate = manipulateDate(end);
			}
		}
	}
	
	public Event(String id, String description, String startDate, String endDate, String priority, String category, String alarm) {
		_id = id;
		_description = description;
		_startDate = startDate;
		_endDate = endDate;
		_priority = priority;
		_category = category;
		_alarm = alarm;
	}

	public Task toTask(int id) { 
		return new Task(id, _description, _endDate, _priority, _category, _alarm);
	}
	
	@Override
	public String toString(){
		return  _id + ID_DISPLAY + _description + EMPTY_SPACE + _startDate+ DATE_SEPARATOR + _endDate + EMPTY_SPACE + _priority + EMPTY_SPACE + _category;
	}
	
	@Override
	public String getAlarmOffset(){
		return _startDate;
	}
	
	public String getStartDate() {
		return _startDate;
	}

	void setStartDate(String startDate) {
		_startDate = startDate.trim();
	}
	
	void setId(int id) {
		_id = ID_HEADER + Integer.toString(id);
	}

}
