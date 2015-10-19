package application;

import org.json.simple.JSONObject;

public class AddCommand extends Command {
	
	//Unique attributes
	private String _createdItemID;
	
	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Added: %1$s";
	private static final String FEEDBACK_MISSING_DESC = "Error: A task needs a description!";
	private static final String ID = "id"; //note: collate JSON strings into just one class for easy referencing

	public AddCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() {
		String feedbackString;
		try {
			_store.storeTemp();
			JSONObject newEntry = _parse.createItem(_content, _store.getId());
			_createdItemID = newEntry.get("id").toString(); //store itemID for undo
			_store.entries_.add(newEntry);
			_store.storeChanges();
			feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			return new Feedback(feedbackString);
		} catch (NullPointerException e) {
			feedbackString = FEEDBACK_MISSING_DESC;
			return new Feedback(feedbackString);
		}
	}

	@Override
	public Feedback undo() {
		DeleteCommand undoAdd = new DeleteCommand(_createdItemID);
		_store.decreaseID();
		return undoAdd.execute();
	}

}
