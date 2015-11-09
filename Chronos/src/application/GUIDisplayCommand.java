package application;

import java.util.ArrayList;
import java.util.Collections;

public class GUIDisplayCommand extends DisplayCommand {

	private static final String MESSAGE_DISPLAY_GUI = "Displayed 30 most urgent items";
	
	private static final int TASK_LIMIT = 30;
	
	public GUIDisplayCommand() {
		super(EMPTY);
	}
	
	@Override
	public Feedback execute() {
		ArrayList<Task> filteredTasks = new ArrayList<Task>();
		String feedbackString = MESSAGE_DISPLAY_GUI;
		filteredTasks = moderateTasks(_parse.convertToTaskArray(_store.entries_));
		Feedback feedback = new Feedback(feedbackString, filteredTasks);
		feedback.setSummaryView(true);
		return feedback;
	}
	
	private ArrayList<Task> moderateTasks(ArrayList<Task> tasks) {
		Collections.sort(tasks, new TaskComparator());
		while (tasks.size() > TASK_LIMIT) {
			tasks.remove(tasks.size() - 1); //remove the last element
		}
		return tasks;
	}

}
