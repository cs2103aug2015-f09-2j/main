package application;

import org.json.simple.JSONObject;

public class AddCommand extends Command {
	
	//Unique attributes
	private String _createdItemID;
	
	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Added: %1$s";
	private static final String FEEDBACK_MISSING_DESC = "Error: A task needs a description!";

	public AddCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() {
		String feedbackString;
		try {
			_store.storeTemp();
			//Create Task or event
			Task createdTask = _parse.createItem(_content);
			if(createdTask instanceof Event) {
				createdTask.setId(_store.getEventId());
			} else {
				createdTask.setId(_store.getTaskId());
			}
			_createdItemID = createdTask.getId();
			_store.entries_.add(_parse.convertToJSON(createdTask));
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
		if(_createdItemID.contains("e")){
			_store.decreaseEventID();
		} else {
			_store.decreaseTaskID();
		}
		return undoAdd.execute();
	}

}
