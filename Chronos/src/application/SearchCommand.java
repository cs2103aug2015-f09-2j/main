package application;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.simple.JSONObject;

import com.mdimension.jchronic.Chronic;

public class SearchCommand extends Command {

	//Constant Strings
	protected static final String FEEDBACK_MESSAGE = "Searching for: %1$s";
	private static final String PRIORITY_HEADER = "p:";
	private static final String CATEGORY_HEADER = "c:";
	
	public SearchCommand(String content) {
		super(content);
	}

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
		if (entryItem.getDescription().contains(searchCriteria[0]) && !searchCriteria[0].equals("*")) {
			return true;
		}
		for (int i = 1; i < searchCriteria.length; i++) {
			if (searchCriteria[i].contains(PRIORITY_HEADER)) {
				return entryItem.getPriority().equals(searchCriteria[i].substring(2));
			} else if (searchCriteria[i].contains(CATEGORY_HEADER)) {
				return entryItem.getCategory().contains(searchCriteria[i].substring(2));
			} else { 
				return searchDates(searchCriteria[i], entryItem);
			}
		}
		return false;
	}

	private boolean searchDates(String dateString, Task entryItem) {		
		if (dateString.equalsIgnoreCase(entryItem.DEFAULT_END_DATE)) {
			return dateString.equals(entryItem.getEndDate());
		} else {
			if(entryItem.getEndDate().equals(entryItem.DEFAULT_END_DATE)) {
				return false;
			} else { 
				Calendar searchEndDate = Chronic.parse(dateString).getBeginCalendar();
				Calendar endDate = Chronic.parse(entryItem.getEndDate()).getBeginCalendar();
				if (isSameDay(searchEndDate, endDate)) {
					return true;
				} else if (entryItem instanceof Event) {
					Calendar startDate = Chronic.parse(((Event) entryItem).getStartDate()).getBeginCalendar();
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
	
}
