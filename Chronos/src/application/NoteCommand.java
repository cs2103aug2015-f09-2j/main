package application;

public class NoteCommand extends Command {

	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Added note to %1$s";
	
	public NoteCommand(Storage store, Parser parse, String content) {
		super(store, parse, content);
	}

	@Override
	public Feedback execute() {
		_store.storeTemp();
		//add note to entries: similar to Update
		_store.storeChanges();
		String feedbackString =  String.format(FEEDBACK_MESSAGE, _content);
		return new Feedback(feedbackString);
	}

	@Override
	public Feedback undo() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*JSONObject entry;
	String[] noteDetails = noteString.split(", ");
	for (int i = 0; i<_store.entries_.size(); i++){
		entry = (JSONObject) _store.entries_.get(i);
		String id = noteDetails[0];
		if (entry.get("id").equals(id)) {	
			entry.put("note", noteDetails[1]);
			break;
		}
	}*/
	
}
