package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import application.Command;
import application.Feedback;
import application.Parser;
import application.Storage;
import application.Task;

public class testCommands {
	Parser parse = new Parser(Preferences.userNodeForPackage(this.getClass()));
	Storage store = new Storage("src/test/testFiles/test");
	JSONArray entries = store.entries_;
	@Before
	public void setUp(){
		store.storeTemp();
	}
	@After
	public void CleanUp(){
		store.swapTemp();
	}
	@Test
	public void testAdd() {
		Command cmd = new Command("add buy milk, p:high, c:personal, d:today", store, parse);
		cmd.execute();
		JSONObject entry = (JSONObject) entries.get(4);
		assertEquals("buy milk",entry.get("description").toString() );
	}
	
	@Test
	public void testUpdate(){
		Command cmd = new Command("update t4, p:high", store, parse);
		cmd.execute();
		JSONObject entry = (JSONObject) entries.get(2);
		assertEquals("high", entry.get("priority") );
	}

	@Test
	public void testDelete(){
		Command cmd = new Command("delete t4", store, parse);
		cmd.execute();
		assertEquals(3, entries.size());
	}
	
	@Test
	public void testSearch(){
		Command cmd= new Command("search laundry", store, parse);
		Feedback actual = cmd.execute();
		ArrayList<Task> expected= new ArrayList<Task>();
		expected.add(new Task("t5", "do laundry", "none", "low", "personal"));
		assertEquals(expected.toString(), actual.getData().toString());
	}
	
	@Test
	public void testNote(){
		Command cmd= new Command("note t4, sleep early", store, parse);
		cmd.execute();
		JSONObject entry = (JSONObject) entries.get(2);
		assertEquals("sleep early", entry.get("note") );
	}
}
