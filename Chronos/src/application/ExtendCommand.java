package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONObject;

public class ExtendCommand extends UpdateCommand {
	
	public static final String FEEDBACK_MESSAGE =  "Extended %1$s";
	public static final String ERROR_NO_EXTENSION =  "Error: No duration entered";
	public static final String ERROR_CANT_EXTEND =  "Error: Floating tasks can't be extended";
	
	private static final String FIELD_YEAR = "yr";
	private static final String FIELD_MONTH = "mo";
	private static final String FIELD_DAY = "day";
	private static final String FIELD_HOUR = "hr";
	private static final String FIELD_MIN = "min";

	//Instructions
	private static final String PATTERN = "extend (task/event id), (yr OR mo OR day OR hr OR min):(number) OR (someday)";
	private static final String INSTRUCTION_REQUIRED_TASK = "Type the task or event id of item to extend.";
	private static final String INSTRUCTION_REQUIRED_DEADLINE = "Type the duration of your extension. Ex: hr:1, min:30 to extend by 1 hour and 30 minutes";
	private static final String REQUIRED_FIELD_ID = "(task/event id)";
	private static final String REQUIRED_FIELD_DEADLINE = "(someday)";

	
	public ExtendCommand(String content) {
		super(content);
	}
	
	//@@author A0126223U
	@Override
	public Feedback execute() { 
		ArrayList<String> extendDetails = _parse.parseExtendString(_content);
		String taskID = extendDetails.remove(0);
		_index = findEntry(taskID);
		
		if (_index == Command.FIND_NO_ID) {
			assert _content.equals(EMPTY);
			log.warning(LOG_NO_ID);
			return new Feedback(ERROR_NO_ID);
		} else if (_index == Command.FIND_INVALID_ID) {
			return new Feedback(ERROR_INVALID_ID);
		} else {
			if (extendDetails.size() == 0) {
				return new Feedback(ERROR_NO_EXTENSION);
			} else { 
				JSONObject entry = (JSONObject) _store.entries_.get(_index);
				_oldEntry = (JSONObject) entry.clone();
				if(_oldEntry.get(JSON_END_DATE).equals("someday")) {
					return new Feedback(ERROR_CANT_EXTEND);
				} else { 
					_store.storeTemp();
					Task taskToExtend = extendTask(taskID, extendDetails);
					_store.entries_.set(_index, _parse.convertToJSON(taskToExtend));
					_store.storeChanges();
					_isSuccessful = true;
					String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
					return new Feedback(feedbackString);
				}
			}
		}
	}

	private Task extendTask(String taskID, ArrayList<String> extendDetails) {
		Task taskToExtend = _parse.retrieveTask(taskID, _store.entries_);
		if(extendDetails.size()==1 && extendDetails.get(0).equals("someday")) {
			taskToExtend.setEndDate("someday");
		} else {
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(getTaskEndDate(taskToExtend));
			for(String extendDetail:extendDetails){
				String[] details = extendDetail.split(":");
				int field = determineField(details[0]);
				int amount = Integer.parseInt(details[1]); //amount should be >0
				if(field > 0) {
					endDate.add(field, amount);
				}
			}
			DateFormat dateFormat = new SimpleDateFormat(Task.DATE_FORMAT);
			taskToExtend.setEndDate(dateFormat.format(endDate.getTime()));
		}
		return taskToExtend;
	}

	private int determineField(String string) {
		switch(string) {
		
			case FIELD_YEAR :
			     return Calendar.YEAR;
			     //break;
			     
			case FIELD_MONTH :
				 return Calendar.MONTH;
				 //break;
			
			case FIELD_DAY :
				 return Calendar.DATE;
				 //break;
			
			case FIELD_HOUR :
				 return Calendar.HOUR;
				 //break;
			
			case FIELD_MIN :
				 return Calendar.MINUTE;
				 //break;
			
			default :
				return -1;
		}
	}

	private Date getTaskEndDate(Task taskToExtend) {
		DateFormat dateFormat = new SimpleDateFormat(Task.DATE_FORMAT);
		try {
			return dateFormat.parse(taskToExtend.getEndDate());
		} catch (ParseException e) {
			return null;
		}
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
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED_DEADLINE);
		commandInstruction.addToRequiredFields(REQUIRED_FIELD_DEADLINE);
		return commandInstruction;
	}
	
	
	
}
