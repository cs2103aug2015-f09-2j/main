package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import application.CommandCreator;
import application.Feedback;
import application.Logic;
import application.Storage;
import application.Task;

public class testCommands {
	static //Remember to undo after testing CRUD commands
	Logic logic = Logic.getInstance();
	static Storage store;
	JSONArray entries;
	static CommandCreator creator = new CommandCreator();
	static final String DEFAULT_PATH= "none";
	static final String PREFS_PATH = "path";
	static String path;
	static Preferences userPrefs = Preferences.userNodeForPackage(Storage.class);

	
	@BeforeClass
	public static void setUp(){
		path = userPrefs.get(PREFS_PATH, DEFAULT_PATH);
		logic.isSavePresent();
		creator.executeInitializeCommand("src/test/testFiles/testCommands");
		store = Storage.getInstance();
	}
	
	@AfterClass
	public static void cleanUp(){
		userPrefs.put(PREFS_PATH, path);
	}

	@Test
	public void testAdd() {
		logic.executeUserCommand("add buy milk, p:high, c:personal, today");
		entries = store.entries_;
		JSONObject entry = (JSONObject) entries.get(5);
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
	public void testSearch(){
		Feedback actual=logic.executeUserCommand("search laundry");
		ArrayList<Task> expected= new ArrayList<Task>();
		expected.add(new Task("t5", "do laundry", "someday", "med", "personal","off"));
		assertEquals(expected.toString(), actual.getData().toString());
	}
	
}
