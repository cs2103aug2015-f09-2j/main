package test;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

import application.Command;
import application.Feedback;
import application.Parser;
import application.UpdateCommand;

//@@author A0131496A
public class TestUpdate extends TestCommands{

	UpdateCommand updateCmd;
	
	/**
	 * Partitions for updateCommand:
	 * 1. update description
	 * 2. update time (without changing task to event)
	 * 3. update priority
	 * 4. update category
	 * 5. update task to event
	 * 6. invalid format
	 * 
	 * combine 1 to 4 as testUpdateMultiple
	 */
	
	@Test
	//update description, start time, end time, category, priority
	public void testUpdateMultiple(){
		updateCmd = new UpdateCommand("t4, get a dog, e:13 Nov 2015 5pm, c:pet, p:low");
		updateCmd.execute();
		JSONObject entry = (JSONObject) store.entries_.get(4);
		assertEquals("pet", entry.get(Parser.JSON_CATEGORY));
		assertEquals("get a dog",entry.get(Parser.JSON_DESC).toString());
		assertEquals("13 Nov 2015 17:00", entry.get(Parser.JSON_END_DATE).toString());
		assertEquals("low", entry.get(Parser.JSON_PRIORITY).toString());
		updateCmd.undo();
		entry = (JSONObject) store.entries_.get(4);
		assertEquals("work", entry.get(Parser.JSON_CATEGORY));
		assertEquals("submit report",entry.get(Parser.JSON_DESC).toString());
		assertEquals("10 Nov 2015 12:00", entry.get(Parser.JSON_END_DATE).toString());
		assertEquals("high", entry.get(Parser.JSON_PRIORITY).toString());
	}
	
	@Test
	//update task to event
	public void testUpdateToEvent(){
		updateCmd = new UpdateCommand("t4, b:10 Nov 2015 10:00");
		updateCmd.execute();
		JSONObject entry = (JSONObject) store.entries_.get(4);
		assertEquals("e", entry.get(Parser.JSON_ID).toString().substring(0,1));
		assertEquals("10 Nov 2015 10:00", entry.get(Parser.JSON_START_DATE).toString());
		updateCmd.undo();
		entry = (JSONObject) store.entries_.get(4);
		assertEquals("t4", entry.get(Parser.JSON_ID).toString());
		assertEquals(null, entry.get(Parser.JSON_START_DATE));
	}

	@Test
	//update invalid format, boundary case: invalid id
	public void testUpdateInvalid(){
		updateCmd = new UpdateCommand("t14, call mom");
		Feedback msg = updateCmd.execute();
		assertEquals(Command.ERROR_INVALID_ID, msg.getMessage());
	}

}
