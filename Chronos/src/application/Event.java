package application;

public class Event extends Task {

	private String _startDate;
	
	public Event(String id, String description, String endDate, String startDate, String priority, String category) {
		super(id, description, endDate, priority, category);
		_startDate = startDate;
	}
	
	public Task toTask(String id) { //get id from parser
		_id = id;
		return new Task(_id, _description, _endDate, _priority, _category);
	}
	
	public String getStartDate() {
		return _startDate;
	}

	void setEndDate(String startDate) {
		_startDate = startDate.trim();
	}

}
