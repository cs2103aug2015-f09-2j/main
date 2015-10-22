package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import application.CommandCreator;
import application.Feedback;
import application.Logic;
import application.Storage;
import application.Task;

public class testCommands {
	//Remember to undo after testing CRUD commands
	Logic logic = new Logic();
	Storage store;
	JSONArray entries;
	CommandCreator creator = new CommandCreator();
	
	@Before
	public void setUp(){
		logic.isSavePresent();
		creator.executeInitializeCommand("src/test/testFiles/test");
		store = Storage.getInstance();
	}

	@Test
	public void testAdd() {
		logic.executeUserCommand("add buy milk, p:high, c:personal, today");
		entries = store.entries_;
		JSONObject entry = (JSONObject) entries.get(4);
		assertEquals("buy milk",entry.get("description").toString() );
		logic.executeUserCommand("undo");
	}
	
	@Test
	public void testUpdate(){
		logic.executeUserCommand("update t4, p:high");
		entries = store.entries_;
		JSONObject entry = (JSONObject) entries.get(2);
		assertEquals("high", entry.get("priority") );
		logic.executeUserCommand("undo");
	}

	@Test
	public void testDelete(){
		logic.executeUserCommand("delete t4");
		entries = store.entries_;
		assertEquals(3, entries.size());
		logic.executeUserCommand("undo");
	}
	
	@Test
	public void testSearch(){
		Feedback actual=logic.executeUserCommand("search laundry");
		ArrayList<Task> expected= new ArrayList<Task>();
		expected.add(new Task("t5", "do laundry", "someday", "med", "personal"));
		assertEquals(expected.toString(), actual.getData().toString());
	}
	
}
