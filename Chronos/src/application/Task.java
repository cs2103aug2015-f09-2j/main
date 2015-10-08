package application;

import java.util.ArrayList;

public class Task {
	
	private String id;
	private String description;
	private String endDate;
	private String priority;
	private String category;
	private ArrayList<Note> notes;
	
	
	public Task(String id, String description, String endDate, String priority, String category) {
		this.id = id;
		this.description = description;
		this.endDate = endDate;
		this.priority = priority;
		this.category = category;
	}

	public String getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}

	void setDescription(String _description) {
		this.description = _description;
	}

	public String getEndDate() {
		return endDate;
	}

	void setEndDate(String _endDate) {
		this.endDate = _endDate;
	}

	public String getPriority() {
		return priority;
	}

	void setPriority(String _priority) {
		this.priority = _priority;
	}

	public String getCategory() {
		return category;
	}

	void setCategory(String _category) {
		this.category = _category;
	}

	public ArrayList<Note> getNotes() {
		return notes;
	}
	
	@Override
	public String toString() {
		return id + " " + description + " " + endDate + " " + priority + " " + category;
	}
	
}
