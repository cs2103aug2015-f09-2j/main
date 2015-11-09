package application;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class NoteUpdateCommand extends Command {

	//Unique Attributes
	private JSONObject _oldEntry;
	private int _index;
	
	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Updated note from %1$s";
	private static final String FEEDBACK_MESSAGE_UNDO =  "Restored note from %1$s";
	
	protected static final String ERROR_NO_NOTE_ID = "Error: No Note ID entered.";
	protected static final String ERROR_INVALID_NOTE_ID = "Error: Invalid Note ID entered.";
	protected static final String ERROR_NO_NOTE_CONTENT = "Error: No updated content entered.";
	
	//Instructions
	private static final String PATTERN = "unote (task/event id), (note id), (updated note)";
	private static final String INSTRUCTION_REQUIRED_TASK = "Type the task or event id of the item to delete a note from.";
	private static final String INSTRUCTION_REQUIRED_NOTE_ID = "Type the id of the note";
	private static final String INSTRUCTION_REQUIRED_NOTE = "Type updated note";
	private static final String REQUIRED_FIELD_ID = "(task/event id)";
	private static final String REQUIRED_FIELD_NOTE_ID = "(note id)";
	private static final String REQUIRED_FIELD_NOTE = "(updated note)";
	
	public NoteUpdateCommand(String content) {
		super(content);
	}
	
	@Override
	public Feedback execute() {
		String[] noteDetails = _content.split(Parser.CONTENT_SEPARATOR);
		
		_index = findEntry(noteDetails[0]);
		if (_index == Command.FIND_NO_ID) {
			assert _content.equals(EMPTY);
			log.warning(LOG_NO_ID);
			return new Feedback(ERROR_NO_ID);
		} else if (_index == Command.FIND_INVALID_ID) {
			return new Feedback(ERROR_INVALID_ID);
		} else {
			if (noteDetails.length == 1 || noteDetails[1].equals(EMPTY)){
				return new Feedback(ERROR_NO_NOTE_ID);
			} else if (noteDetails.length == 2 || noteDetails[2].equals(EMPTY)) {
				return new Feedback(ERROR_NO_NOTE_CONTENT);
			} else {
				JSONObject entry = (JSONObject)_store.entries_.get(_index);
				_oldEntry = (JSONObject) entry.clone();
				return updateNote(_index, noteDetails);				
			}
		}
	}
	
	private Feedback updateNote(int itemID, String[] noteDetails) {
		
		Task aTask = _parse.retrieveTask(noteDetails[0], _store.entries_);
	
		int noteID = Integer.parseInt(noteDetails[1]);
		if (aTask.hasNote(noteID)) {
			_store.storeTemp();
			aTask.updateNote(noteID, noteDetails[2]);
			_store.entries_.set(_index, _parse.convertToJSON(aTask));
			_store.storeChanges();
			
			_isSuccessful = true;
			String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
			ArrayList<Task> feedbackData = new ArrayList<Task>();
			feedbackData.add(aTask);
			
			Feedback feedback = new Feedback(feedbackString, feedbackData);
			feedback.setSummaryView(false);
			return feedback;
		} else {
			return new Feedback(ERROR_INVALID_NOTE_ID);
		}
		
	}

	@Override
	public Feedback undo() {
		_store.storeTemp();
		JSONObject entry = (JSONObject) _store.entries_.get(_index);
		_store.entries_.set(_index, _oldEntry);
		_store.storeChanges();
		String feedbackString = String.format(FEEDBACK_MESSAGE_UNDO, _content);
		ArrayList<Task> feedbackData = new ArrayList<Task>();
		Task aTask = _parse.convertToTask(entry);
		feedbackData.add(aTask);
		Feedback feedback =  new Feedback(feedbackString);
		feedback.setSummaryView(false);
		return feedback;
	}

	public static Instruction generateInstruction() {
		Instruction commandInstruction = new Instruction();
		commandInstruction.setCommandPattern(PATTERN);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED_TASK);
	    commandInstruction.addToRequiredFields(REQUIRED_FIELD_ID);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED_NOTE_ID);
		commandInstruction.addToRequiredFields(REQUIRED_FIELD_NOTE_ID);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED_NOTE);
		commandInstruction.addToRequiredFields(REQUIRED_FIELD_NOTE);
		return commandInstruction;
	}

}
