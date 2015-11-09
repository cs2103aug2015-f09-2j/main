package test;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

import application.AlarmCommand;
import application.Parser;

//@@author A0131496A
public class TestAlarm extends TestCommands {
	
	AlarmCommand alarmCmd;
	
	/**
	 * Partitions for AlarmCommand:
	 * 1. add alarm
	 * 2. turn off alarm
	 */
	@Test
	public void testAddAlarm(){
		alarmCmd = new AlarmCommand("e1, 1");
		alarmCmd.execute();
		JSONObject entry= (JSONObject) store.entries_.get(1);
		String expected = "07 Nov 2015 08:00";
		assertEquals(expected, entry.get(Parser.JSON_ALARM));
		alarmCmd.undo();
		entry= (JSONObject) store.entries_.get(1);
		assertEquals("off", entry.get(Parser.JSON_ALARM));
	}
	
	@Test
	public void testOffAlarm(){
		alarmCmd = new AlarmCommand("e1, 1");
		alarmCmd.execute();
		alarmCmd = new AlarmCommand("e1, off");
		alarmCmd.execute();
		String expected = "off";
		JSONObject entry= (JSONObject) store.entries_.get(1);
		assertEquals(expected, entry.get(Parser.JSON_ALARM));
	}
	
}
