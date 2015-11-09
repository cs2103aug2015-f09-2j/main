package test;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

import application.Command;
import application.DeleteCommand;
import application.Feedback;
import application.Parser;

//@@author A0131496A
public class TestDelete extends TestCommands{
	
	DeleteCommand deleteCmd;

	/**
	 * Partitions for DeleteCommand:
	 * 1. delete existing id
	 * 2. delete non-existing id
	 */
	
	@Test
	//Test partition 1
	public void testDelete(){
		deleteCmd = new DeleteCommand("t4");
		deleteCmd.execute();
		assertEquals(4, store.entries_.size());
		JSONObject entry= (JSONObject) store.entries_.get(3);
		String expected = "e2";
		assertEquals(expected, entry.get(Parser.JSON_ID).toString());
		deleteCmd.undo();
		assertEquals(5, store.entries_.size());
	}
	
	@Test
	//Test partition 2
	public void testDeleteInvalid(){
		deleteCmd = new DeleteCommand("t21");
		Feedback msg = deleteCmd.execute();
		assertEquals(Command.ERROR_INVALID_ID, msg.getMessage());
	}
}
