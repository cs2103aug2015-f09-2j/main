package application;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class NoteCommand extends Command {

	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Added note to %1$s";
	private static final String FEEDBACK_MESSAGE_UNDO =  "Restored %1$s";
	
	//Unique Attributes
	private JSONObject _oldEntry;
	private int _index;
	
	//Instructions
	private static final String PATTERN = "+note (task/event id), (note)";
	private static final String INSTRUCTION_REQUIRED_TASK = "Type the task or event id of the item to add the note to.";
	private static final String INSTRUCTION_REQUIRED_NOTE = "Type the contents of your note";
	private static final String REQUIRED_FIELD_ID = "(task/event id)";
	private static final String REQUIRED_FIELD_NOTE = "(note contents)";
	
	public NoteCommand(String content) {
		super(content);
	}

	//@@author A0126223U
	@Override
	public Feedback execute() {
		String feedbackString = EMPTY;
		String[] noteDetails = _content.split(Parser.CONTENT_SEPARATOR);
		
		_index = findEntry(noteDetails[0]);
		if (_index == Command.FIND_NO_ID) {
			assert _content.equals(EMPTY);
			log.warning(LOG_NO_ID);
			return new Feedback(ERROR_NO_ID);
		} else if (_index == Command.FIND_INVALID_ID) {
			return new Feedback(ERROR_INVALID_ID);
		} else {
			if (noteDetails[1].equals(EMPTY)){
				return new Feedback(ERROR_NO_CONTENT);
			} else {
				JSONObject entry = (JSONObject)_store.entries_.get(_index);
				_oldEntry = (JSONObject) entry.clone();
				return noteProcess(_index, noteDetails);				
			}
		}
	}
	
	private Feedback noteProcess(int id, String[] noteDetails) {
		_store.storeTemp();
		Task aTask = _parse.retrieveTask(noteDetails[0], _store.entries_);
		aTask.addNote(noteDetails[1]); 
		_store.entries_.set(_index, _parse.convertToJSON(aTask));
		_store.storeChanges();
		
		_isSuccessful = true;
		String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
		ArrayList<Task> feedbackData = new ArrayList<Task>();
		feedbackData.add(aTask);
		
		Feedback feedback = new Feedback(feedbackString, feedbackData);
		feedback.setSummaryView(false);
		return feedback;
	}

	@Override
	public Feedback undo() {
		_store.storeTemp();
		JSONObject entry = (JSONObject) _store.entries_.get(_index);
		_store.entries_.set(_index, _oldEntry);
		_store.storeChanges();
		String feedbackString = String.format(FEEDBACK_MESSAGE_UNDO, _content);
		return new Feedback(feedbackString);
	}
	
	public static Instruction generateInstruction() {
		Instruction commandInstruction = new Instruction();
		commandInstruction.setCommandPattern(PATTERN);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED_TASK);
	    commandInstruction.addToRequiredFields(REQUIRED_FIELD_ID);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED_NOTE);
		commandInstruction.addToRequiredFields(REQUIRED_FIELD_NOTE);
		return commandInstruction;
	}
	
}