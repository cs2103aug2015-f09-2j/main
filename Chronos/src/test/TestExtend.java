package test;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

import application.AddCommand;
import application.Command;
import application.ExtendCommand;
import application.Feedback;
import application.Parser;

//@@author A0131496A
public class TestExtend extends TestCommands{

	ExtendCommand extendCmd;
	
	/**
	 * Partitions for extend:
	 * 1. extend by year/month/day/hour/minutes
	 * 2. invalid format
	 * 3. extend floating task is not allowed
	 */

	@Test
	//Test for partition 1, boundary case: all time units present
	public void testExtendMultiple(){
		extendCmd = new ExtendCommand("t1, yr:1, mo:1, day:1, hr:1, min:30");
		extendCmd.execute();
		JSONObject entry= (JSONObject) store.entries_.get(0);
		String expected = "09 Dec 2016 13:30";
		assertEquals(expected,  entry.get(Parser.JSON_END_DATE).toString());
		extendCmd.undo();
		expected = "08 Nov 2015 12:00";
		entry= (JSONObject) store.entries_.get(0);
		assertEquals(expected,  entry.get(Parser.JSON_END_DATE).toString());
	}
	
	@Test
	//Test for partition 1, boundary case: time unit value overflow to the next time unit
	public void testExtendOverflow(){
		extendCmd = new ExtendCommand("t1, hr:30, min:90");
		extendCmd.execute();
		JSONObject entry= (JSONObject) store.entries_.get(0);
		String expected = "09 Nov 2015 19:30";
		assertEquals(expected,  entry.get(Parser.JSON_END_DATE).toString());
		extendCmd.undo();
	}
	
	@Test
	//Test for partition 2, boundary case:
	public void testExtendInvalid(){
		extendCmd = new ExtendCommand("t19, hr:30, min:90");
		Feedback msg = extendCmd.execute();
		assertEquals(Command.ERROR_INVALID_ID, msg.getMessage());
	}
	
	@Test
	//Test for partition 3
	public void testExtentSomeday(){
		//add a floating task first
		AddCommand addFloating = new AddCommand("travel");
		addFloating.execute();
		JSONObject entry = (JSONObject) store.entries_.get(5);
		String id = entry.get(Parser.JSON_ID).toString();
		extendCmd = new ExtendCommand(id+", someday");
		Feedback msg = extendCmd.execute();
		assertEquals(ExtendCommand.ERROR_CANT_EXTEND, msg.getMessage());
		addFloating.undo();
	}

}
