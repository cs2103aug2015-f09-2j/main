package test;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

import application.AddCommand;
import application.Feedback;
import application.Parser;

//@@author A0131496A
public class TestAdd extends TestCommands {
	
	AddCommand addCmd;

	/**
	 * Partitions for AddCommand: 
	 * 1. add task 
	 * 2. add event 
	 * 3. add with complete details
	 * 4. add with incomplete details
	 * 5. add invalid time format
	 * 6. add event end time before start time
	 * 7. add empty
	 * 
	 * Combine 1 and 3 as testAddTask, 2 and 4 as testAddEvent
	 */
	
	@Test
	//test partition 1 and 3
	public void testAddTask() {
		addCmd = new AddCommand("buy milk, Nov 12 10am, p:high, c:personal");
		addCmd.execute();
		JSONObject entry = (JSONObject)store.entries_.get(5);
		assertEquals("buy milk",entry.get(Parser.JSON_DESC).toString());
		assertEquals("12 Nov 2015 10:00", entry.get(Parser.JSON_END_DATE).toString());
		assertEquals("high", entry.get(Parser.JSON_PRIORITY).toString());
		assertEquals("personal", entry.get(Parser.JSON_CATEGORY).toString());
		addCmd.undo();
		assertEquals(5,store.entries_.size());
	}
	
	@Test
	//test partition 2 and 4
	public void testAddEvent(){
		addCmd = new AddCommand("give tuition, Nov 12 10am to Nov 12 11am");
		addCmd.execute();
		JSONObject entry = (JSONObject)store.entries_.get(5);
		assertEquals("give tuition",entry.get(Parser.JSON_DESC).toString());
		assertEquals("12 Nov 2015 10:00", entry.get(Parser.JSON_START_DATE).toString());
		assertEquals("12 Nov 2015 11:00", entry.get(Parser.JSON_END_DATE).toString());
		assertEquals("low", entry.get(Parser.JSON_PRIORITY).toString());
		assertEquals("none", entry.get(Parser.JSON_CATEGORY).toString());
		addCmd.undo();
		assertEquals(5,store.entries_.size());
	}
	
	@Test
	//test partition 5
	public void testAddInvalidTime(){
		addCmd = new AddCommand("give tuition, invalid date");
		Feedback msg = addCmd.execute();
		assertEquals(AddCommand.FEEDBACK_WRONG_DATE, msg.getMessage());
	}
	
	@Test
	//test partition 6
	public void testAddInvalidEvent(){
		addCmd = new AddCommand("give tuition, Nov 12 10pm to Nov 12 8pm");
		Feedback msg = addCmd.execute();
		assertEquals(AddCommand.FEEDBACK_WRONG_END_DATE, msg.getMessage());
	}
	
	@Test
	//test partition 7, boundary case: lots of spaces
	public void testAddEmpty(){
		addCmd = new AddCommand("       ");
		Feedback msg = addCmd.execute();
		assertEquals(AddCommand.FEEDBACK_MISSING_DESC, msg.getMessage());
	}

}
