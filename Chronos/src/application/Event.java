package application;

import java.text.ParseException;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;

public class Event extends Task {

	static final String ID_HEADER = "e";	
	
	private String _startDate;
	
	private static final String DATE_SEPARATOR = " to ";
	private static final String PRIORITY_HEADER = "p:";
	private static final String CATEGORY_HEADER = "c:";
	private static final String EMPTY_SPACE = " ";
	private static final String ID_DISPLAY = ". ";
	
	public Event(String[] contents) throws ParseException {
		super();
		_description = contents[0];
		for (int i = 1; i < contents.length; i++) {
			if (contents[i].contains(PRIORITY_HEADER)) {
				_priority = contents[i].substring(2).toLowerCase();
			} else if (contents[i].contains(CATEGORY_HEADER)) {
				_category = contents[i].substring(2);
			} else { //date manipulation
				String[] dates = contents[i].split(DATE_SEPARATOR);
				_startDate = manipulateDate(Chronic.parse(dates[0]).getBeginCalendar());
				_endDate = manipulateDate(Chronic.parse(dates[1]).getEndCalendar());
			}
		}
	}
	
	public Event(String id, String description, String startDate, String endDate, String priority, String category) {
		_id = id;
		_description = description;
		_startDate = startDate;
		_endDate = endDate;
		_priority = priority;
		_category = category;
	}

	public Task toTask(int id) { //get id from storage
		return new Task(id, _description, _endDate, _priority, _category);
	}
	
	@Override
	public String toString(){
		return  _id + ID_DISPLAY + _description + EMPTY_SPACE + _startDate+ EMPTY_SPACE + _endDate + EMPTY_SPACE + _priority + EMPTY_SPACE + _category;
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
