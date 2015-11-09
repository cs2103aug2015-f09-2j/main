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
import application.AlarmCommand;
import application.Command;
import application.CommandCreator;
import application.Feedback;
import application.Logic;
import application.NoteCommand;
import application.Parser;
import application.SearchCommand;
import application.Storage;
import application.Task;
import application.UpdateCommand;

//@@author A0131496A
/**
 * Unit tests for the different commands.
 * All tests are performed on the same storage file. 
 * If commands change the content of the storage, call undo() to restore the original storage file.
 **/
public class testCommands {
	
	static Logic logic = Logic.getInstance();
	static Storage store;
	static CommandCreator creator = new CommandCreator();
	static final String DEFAULT_PATH= "none";
	static final String PREFS_PATH = "path";
	static final String TEST_FILE = "src/test/testFiles/testSome";
	static final String JSON_NOTES = "notes";
	static final String JSON_NOTE = "note";
	static String path;
	static Preferences userPrefs = Preferences.userNodeForPackage(Storage.class);

	
	@BeforeClass
	//Store the file path in user preference in String path
	public static void setUp(){
		path = userPrefs.get(PREFS_PATH, DEFAULT_PATH);
		logic.isSavePresent();
		creator.executeInitializeCommand(TEST_FILE);
		store = Storage.getInstance();
	}
	
	@AfterClass
	//Restore the user preferred file path
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

	@Test
	public void testSearch(){
	    Command searchCmd = new SearchCommand("buy");
		Feedback searchFeedback = searchCmd.execute();
		ArrayList<Task> actual = searchFeedback.getData();
		ArrayList<Task> expected= new ArrayList<Task>();
		expected.add(new Task("t1", "buy grocery", "08 Nov 2015 12:00", "low", "none","off"));
		assertEquals(expected.toString(), actual.toString());
	}
	
	@Test
	public void testAddNote(){
		Command addNoteCmd = new NoteCommand("e1, bring documents");
		addNoteCmd.execute();
		JSONObject entry= (JSONObject) store.entries_.get(1);
		JSONArray notes = (JSONArray)entry.get(JSON_NOTES);
		JSONObject note = (JSONObject) notes.get(0);
		String expected = "bring documents";
		String actual = note.get(JSON_NOTE).toString();
		assertEquals(expected, actual);
		addNoteCmd.undo();
	}
	
	@Test
	public void testAlarm(){
		Command alarmCmd = new AlarmCommand("e1, 1");
		alarmCmd.execute();
		JSONObject entry= (JSONObject) store.entries_.get(1);
		String expected = "07 Nov 2015 08:00";
		assertEquals(expected, entry.get(Parser.JSON_ALARM));
		alarmCmd.undo();
	}
	
}
