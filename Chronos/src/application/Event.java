package application;

import java.text.ParseException;

public class Event extends Task {

	private static final String ID_HEADER = "e";	
	
	private String _startDate;
	
	public Event(String[] contents) throws ParseException {
		super();
		_description = contents[0];
		for (int i = 1; i < contents.length; i++) {
			if (contents[i].contains("p:")) {
				_priority = contents[i].substring(2).toLowerCase();
			} else if (contents[i].contains("c:")) {
				_category = contents[i].substring(2);
			} else { //date manipulation
				String[] dates = contents[i].split(" to ");
				_startDate = manipulateDate(dates[0]);
				_endDate = manipulateDate(dates[1]);
			}
		}
	}
	
	public Task toTask(int id) { //get id from storage
		return new Task(id, _description, _endDate, _priority, _category);
	}
	
	@Override
	public String toString(){
		return  _id + ". " + _description + " " +_startDate+" "+ _endDate + " " + _priority + " " + _category;
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
