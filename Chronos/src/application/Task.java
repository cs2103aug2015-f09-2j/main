package application;

import java.util.ArrayList;

public class Task {
	
	private String id;
	private String description;
	private String endDate;
	private String priority;
	private String category;
	private String note;
	
	
	public Task(String id, String description, String endDate, String priority, String category, String note) {
		this.id = id.trim();
		this.description = description.trim();
		this.endDate = endDate.trim();
		this.priority = priority.trim();
		this.category = category.trim();
		this.note = note.trim();
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

	public String getNote() {
		return note;
	}
	
	@Override
	public String toString() {
		return id + " " + description + " " + endDate + " " + priority + " " + category + " "+note;
	}
	
	/*public String getNoteString(){
		String noteString = "";
		
		if(notes.size() == 0) {
			return "No notes available.";
		} 
		
		for(int i=0; i<notes.size(); i++){
			String oneNote = i + ". " + notes.get(i).toString() + "\n";
			noteString += oneNote;
		}
		
		return noteString;
	}*/
	
}
