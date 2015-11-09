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
import application.DeleteCommand;
import application.ExtendCommand;
import application.Feedback;
import application.Logic;
import application.NoteCommand;
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
	static final String CD_PATH = "src/test/testFiles";
	static final String JSON_ID = "id";
	static final String JSON_DES = "description";
	static final String JSON_DUE_DATE = "due date";
	static final String JSON_START_DATE = "start date";
	static final String JSON_PRIORITY = "priority";
	static final String JSON_CAT = "category";
	static final String JSON_NOTES = "notes";
	static final String JSON_ALARM = "alarm";
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
		Command addCmd = new AddCommand("buy milk, Nov 12 10am, p:high, c:personal");
		addCmd.execute();
		JSONObject entry = (JSONObject)store.entries_.get(5);
		assertEquals("buy milk",entry.get(JSON_DES).toString());
		assertEquals("12 Nov 2015 10:00", entry.get(JSON_DUE_DATE).toString());
		assertEquals("high", entry.get(JSON_PRIORITY).toString());
		assertEquals("personal", entry.get(JSON_CAT).toString());
		addCmd.undo();
		assertEquals(5,store.entries_.size());
	}
	
	@Test
	//test partition 2 and 4
	public void testAddEvent(){
		Command addCmd = new AddCommand("give tuition, Nov 12 10am to Nov 12 11am");
		addCmd.execute();
		JSONObject entry = (JSONObject)store.entries_.get(5);
		assertEquals("give tuition",entry.get(JSON_DES).toString());
		assertEquals("12 Nov 2015 10:00", entry.get(JSON_START_DATE).toString());
		assertEquals("12 Nov 2015 11:00", entry.get(JSON_DUE_DATE).toString());
		assertEquals("low", entry.get(JSON_PRIORITY).toString());
		assertEquals("none", entry.get(JSON_CAT).toString());
		addCmd.undo();
		assertEquals(5,store.entries_.size());
	}
	
	@Test
	//test partition 5
	public void testInvalidFormat(){
		Command addCmd = new AddCommand("give tuition, invalid date");
		Feedback msg = addCmd.execute();
		assertEquals(AddCommand.FEEDBACK_WRONG_DATE, msg.getMessage());
	}
	
	@Test
	public void testUpdate(){
		Command updateCmd = new UpdateCommand("e2, c:pet");
		updateCmd.execute();
		JSONObject entry = (JSONObject) store.entries_.get(3);
		assertEquals("pet", entry.get(JSON_CAT) );
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
	public void testNote(){
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
		assertEquals(expected, entry.get(JSON_ALARM));
		alarmCmd.undo();
	}
	
	@Test
	public void testDelete(){
		Command deleteCmd = new DeleteCommand("t4");
		deleteCmd.execute();
		assertEquals(4, store.entries_.size());
		JSONObject entry= (JSONObject) store.entries_.get(3);
		String expected = "e2";
		assertEquals(expected, entry.get(JSON_ID).toString());
		deleteCmd.undo();
	}
	
	@Test
	public void testExtend(){
		Command extendCmd = new ExtendCommand("t1, hr:1, min:30");
		extendCmd.execute();
		JSONObject entry= (JSONObject) store.entries_.get(0);
		String actual = entry.get(JSON_DUE_DATE).toString();
		String expected = "08 Nov 2015 13:30";
		assertEquals(expected, actual);
		extendCmd.undo();
	}

}
