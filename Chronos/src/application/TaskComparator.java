package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class TaskComparator implements Comparator<Task> {
	
	private static final String DEFAULT_END_DATE = "someday";
	private static final String PRIORITY_HIGH = "high";
	private static final String PRIORITY_MED = "med";
	private static final String PRIORITY_LOW = "low";

	@Override
	public int compare(Task task1, Task task2) {
		if (!task1.isTaskComplete() && task2.isTaskComplete()) {
			return -1;
		} else if (task1.isTaskComplete() && !task2.isTaskComplete()) {
			return 1;
		} else {
			if((task1 instanceof Event) && !(task2 instanceof Event)) {
				return -1;
			} else if (!(task1 instanceof Event) && (task2 instanceof Event)) {
				return 1;
			} else {
				return compareDeadline(task1, task2);
			}
		}
	}
	
	private int compareDeadline(Task task1, Task task2) {
		if (task1.getEndDate().equals(DEFAULT_END_DATE) && !task2.getEndDate().equals(DEFAULT_END_DATE)) {
			return 1;
		} else if (!task1.getEndDate().equals(DEFAULT_END_DATE) && task2.getEndDate().equals(DEFAULT_END_DATE)) {
			return -1;
		} else if (task1.getEndDate().equals(DEFAULT_END_DATE) && task2.getEndDate().equals(DEFAULT_END_DATE)) {
			return comparePriority(task1, task2);
		} else {
			Date task1DueDate = getDate(task1.getEndDate());
			Date task2DueDate = getDate(task2.getEndDate());
			assert task1DueDate != null && task2DueDate != null;
			int dateComparison = task1DueDate.compareTo(task2DueDate);
			if (dateComparison < 0) {
				return -1;
			} else if (dateComparison > 0) {
				return 1;
			} else {
				return dateComparison;
			}
		}
	}

	private Date getDate(String endDate) {
		try {
			DateFormat dateFormat = new SimpleDateFormat(Task.DATE_FORMAT); 
			return dateFormat.parse(endDate);
		} catch (ParseException e) {
			System.out.println("Parsing " + endDate + " returns a null date");
			return null;
		}
	}

	private int comparePriority(Task task1, Task task2) {
		int task1Priority = getNumberedPriority(task1.getPriority());
		int task2Priority = getNumberedPriority(task2.getPriority());
		if (task1Priority > task2Priority) {
			return -1;
		} else if (task1Priority < task2Priority) {
			return 1;
		} else {
			return 0;
		}
	}

	private int getNumberedPriority(String priority) {
		switch(priority) {
		
			case PRIORITY_HIGH :
		         return 2;
		         //break;
		    
			case PRIORITY_MED :
				 return 1;
				 //break;
			
			case PRIORITY_LOW :
				 //Fallthrough
			
			default :
				 return 0;
				 //break;
		}
	}

	

}
