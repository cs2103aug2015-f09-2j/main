package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.simple.JSONObject;

import com.mdimension.jchronic.Chronic;

public class SearchCommand extends Command {

	//Constant Strings
	protected static final String FEEDBACK_MESSAGE = "Searching for: %1$s";
	private static final String PRIORITY_HEADER = "p:";
	private static final String CATEGORY_HEADER = "c:";
	private static final String WILD_CARD = "*";
	
	//Instructions
	private static final String PATTERN = "search (search term OR *), (date), c:(category), p:(priority)";
	private static final String INSTRUCTION_REQUIRED = "Enter a search term, or * if no search term.";
	private static final String INSTRUCTION_OPTIONAL = "Optional fields: date or a date range (ex. today to tomorrow), priority, category";
	private static final String REQUIRED_FIELD_SEARCH = "(search term)";
	
	public SearchCommand(String content) {
		super(content);
	}

	//@@author A0126223U
	@Override
	public Feedback execute() {
		ArrayList<Task> filteredTasks = new ArrayList<Task>();
		String[] searchCriteria = _parse.getContentArray(_content);
		for (int i = 0; i < _store.entries_.size(); i++) {
			String entry = _store.entries_.get(i).toString();
			JSONObject entryObject = (JSONObject) _store.entries_.get(i);
			Task entryItem = _parse.convertToTask(entryObject);
			if (isMatchingEntry(entryItem, searchCriteria)) {
			    filteredTasks.add(_parse.retrieveTask(entryObject.get(_parse.JSON_ID).toString(),_store.entries_));
			}
		}
		String feedbackString = String.format(FEEDBACK_MESSAGE, _content);
		return new Feedback(feedbackString, filteredTasks); 
	}

	private boolean isMatchingEntry(Task entryItem, String[] searchCriteria) {
		//If it's not a star and doesnt have the description, boot it out
		String keyword = searchCriteria[0];
		if(!keyword.equals(WILD_CARD) && !entryItem.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
			return false;
		}
		
		for(int i = 1; i < searchCriteria.length; i++) {
			String searchHeader = searchCriteria[i].substring(0, 2);
			String searchContent = searchCriteria[i].substring(2);
			
			if (searchHeader.equals(PRIORITY_HEADER) && !isSamePriority(entryItem, searchContent)) {
				return false;
			} 
			
			if (searchHeader.equals(CATEGORY_HEADER) && !isSameCategory(entryItem, searchContent)) {
				return false;
			}
			
			if(!(searchHeader.equals(PRIORITY_HEADER)||(searchHeader.equals(CATEGORY_HEADER))) && !isDateFound(entryItem, searchCriteria[i])) {
				return false;
			}
		}
		
		return true;
	}

	private boolean isDateFound(Task entryItem, String searchDate) {
		try {
			return searchDates(searchDate, entryItem);
		} catch (ParseException e) {
			return false;
		} 
	}

	private boolean isSameCategory(Task entryItem, String searchContent) {
		return entryItem.getCategory().equalsIgnoreCase(searchContent);
	}

	private boolean isSamePriority(Task entryItem, String searchContent) {
		return entryItem.getPriority().equalsIgnoreCase(searchContent);
	}
	
	

	private boolean searchDates(String dateString, Task entryItem) throws ParseException, NullPointerException {		
		DateFormat dateFormat = new SimpleDateFormat(Task.DATE_FORMAT);
		if (dateString.equalsIgnoreCase(entryItem.DEFAULT_END_DATE)) {
			return dateString.equals(entryItem.getEndDate());
		} else {
			if(entryItem.getEndDate().equals(entryItem.DEFAULT_END_DATE)) {
				return false;
			} else { 
				Calendar searchEndDate = Chronic.parse(dateString).getBeginCalendar();
				Calendar endDate = Calendar.getInstance();
				endDate.setTime(dateFormat.parse(entryItem.getEndDate()));
				if (isSameDay(searchEndDate, endDate)) {
					return true;
				} else if (entryItem instanceof Event) {
					Calendar startDate = Calendar.getInstance();
					startDate.setTime(dateFormat.parse(((Event) entryItem).getStartDate()));
					return isSameDay(searchEndDate, startDate);
				} else {
					return false;
				}
			}
			
		}
	}

	private boolean isSameDay(Calendar date1, Calendar date2) {
		return (date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)) && (date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR));
	}

	@Override
	public Feedback undo() {
		return null;
	}
	
	public static Instruction generateInstruction() {
		Instruction commandInstruction = new Instruction();
		commandInstruction.setCommandPattern(PATTERN);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED);
	    commandInstruction.addToRequiredFields(REQUIRED_FIELD_SEARCH);
	    commandInstruction.addToInstructions(INSTRUCTION_OPTIONAL);
		return commandInstruction;
	}
	
}
