package gui;

public class Item {

	public String ID;
	public String time;
	public String title;
	public String category;

	public Item(String fID, String fTime, String fTitle, String fCategory) {
		this.ID = fID;
		this.time = fTime;
		this.title = fTitle;
		this.category = fCategory;
		//System.out.println(category);
	}

	public String getID() {
		return ID;
	}

	public String getTime() {
		return time;
	}

	public String getTitle() {
		return title;
	}

	public String getCategory() {
		return category;
	}
}
