package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import application.AddCommand;
import application.Command;
import application.CommandCreator;
import application.Feedback;
import application.Logic;
import application.Storage;
import application.Task;
import application.UpdateCommand;

//@@author A0131496A
public class testCommands {
	
	static Logic logic = Logic.getInstance();
	static Storage store;
	static CommandCreator creator = new CommandCreator();
	static final String DEFAULT_PATH= "none";
	static final String PREFS_PATH = "path";
	static final String TEST_FILE = "src/test/testFiles/testSome";
	static String path;
	static Preferences userPrefs = Preferences.userNodeForPackage(Storage.class);

	
	@BeforeClass
	public static void setUp(){
		path = userPrefs.get(PREFS_PATH, DEFAULT_PATH);
		logic.isSavePresent();
		creator.executeInitializeCommand(TEST_FILE);
		store = Storage.getInstance();
	}
	
	@AfterClass
	public static void cleanUp(){
		userPrefs.put(PREFS_PATH, path);
	}

	@Test
	public void testAdd() {
		Command addCmd = new AddCommand("buy milk, Nov 12 10am, p:high, c:personal");
		addCmd.execute();
		JSONObject entry = (JSONObject)store.entries_.get(5);
		assertEquals("buy milk",entry.get("description").toString() );
		addCmd.undo();
	}
	
	@Test
	public void testUpdate(){
		Command updateCmd = new UpdateCommand("e2, c:pet");
		updateCmd.execute();
		JSONObject entry = (JSONObject) store.entries_.get(3);
		assertEquals("pet", entry.get("category") );
		updateCmd.undo();
	}

	/*
	@Test
	public void testSearch(){
		Feedback actual=logic.executeUserCommand("search laundry");
		ArrayList<Task> expected= new ArrayList<Task>();
		expected.add(new Task("t5", "do laundry", "someday", "med", "personal","off"));
		assertEquals(expected.toString(), actual.getData().toString());
	}
*/	
}
