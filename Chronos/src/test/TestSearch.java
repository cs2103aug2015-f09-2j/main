package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import application.Event;
import application.Feedback;
import application.SearchCommand;
import application.Task;

//@@author A0131496A
public class TestSearch extends TestCommands{

	SearchCommand searchCmd;
	
	/**
	 * Partitions for searchCommand:
	 * 1. search a specific keyword in all fields
	 * 2. search a specific keyword in certain field
	 * 3. search all results in certain field
	 */
	
	@Test
	//partition 1
	public void testSearchKeyword(){
	    searchCmd = new SearchCommand("with");
		Feedback searchFeedback = searchCmd.execute();
		ArrayList<Task> actual = searchFeedback.getData();
		ArrayList<Task> expected= new ArrayList<Task>();
		expected.add(new Event("e1", "meeting with boss", "07 Nov 2015 09:00", "07 Nov 2015 10:00", "high", "work","off"));
		expected.add(new Task("t3","hangout with friends", "07 Nov 2015 12:00", "med","friends","off"));
		assertEquals(expected.toString(), actual.toString());
	}
	
	@Test
	//partition 2
	public void testSearchKeywordConstraint(){
		searchCmd = new SearchCommand("with, c:work");
		Feedback searchFeedback = searchCmd.execute();
		ArrayList<Task> actual = searchFeedback.getData();
		ArrayList<Task> expected= new ArrayList<Task>();
		expected.add(new Event("e1", "meeting with boss", "07 Nov 2015 09:00", "07 Nov 2015 10:00", "high", "work","off"));
		assertEquals(expected.toString(), actual.toString());
	}
}
