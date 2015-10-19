package application;

public class Event extends Task {

	private static final String ID_HEADER = "e";	
	
	private String _startDate;
	
	public Event(String[] contents) {
		super(contents);
		for (int i = 1; i < contents.length; i++) {
			if (contents[i].contains("p:")) {
				_priority = contents[i].substring(2);
			} else if (contents[i].contains("c:")) {
				_category = contents[i].substring(2);
			} else { //date manipulation
				String[] dates = contents[i].split(" to ");
				System.out.println(dates);
				_startDate = dates[0];
				_endDate = dates[1];
			}
		}
	}
	
	public Task toTask(int id) { //get id from storage
		return new Task(id, _description, _endDate, _priority, _category);
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
