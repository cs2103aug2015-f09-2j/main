package test;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.util.prefs.Preferences;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import application.CommandCreator;
import application.Logic;
import application.Storage;

//@@author A0131496A
//Fix the running order of the test methods
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

/**
 * This is a series of consecutive system testing. 
 * Except for the first testInitialisation method where it confirms the file is read in correctly,
 * all other test methods take in user commands as they are and run them in black box.
 * The commands run consecutively to simulate a user's work flow.
 * A storage file with 20 entries at the start is used for testing
 */
public class testSystem {
	
	static Storage store;
	static Logic logic = Logic.getInstance();
	static JSONArray entries;
	static CommandCreator creator = new CommandCreator();
	static Preferences userPrefs = Preferences.userNodeForPackage(Storage.class);
	static final String DEFAULT_PATH= "none";
	static final String PREFS_PATH = "path";
	static final String TEST_DIR = "src/test/testFiles/testSystem";
	static final String TEST_FILE = "src/test/testFiles/testSystem/chronos_storage.txt";
	//Backup file is a copy of the original test file
	static final String BACKUP_FILE = "src/test/testFiles/temp/testSystem.txt";
	static String path;

	@BeforeClass
	//Store the file path in user preference
	public static void setUpBeforeClass() throws Exception {
		path = userPrefs.get(PREFS_PATH, DEFAULT_PATH);
	}

	@AfterClass
	//Restore the file path in user preference
	//Since the content in the test file has been changed, rewrite it with the content of the backup file
	//to ensure the test cases' reproducibility
	public static void tearDownAfterClass() throws Exception {
		userPrefs.put(PREFS_PATH, path);
		File from = new File(BACKUP_FILE);
		File to = new File(TEST_FILE);
		//delete the changed test file first
		to.delete();
		Files.copy(from.toPath(), to.toPath());
	}
	
	@Test
	//Test by random sampling if the file is read in properly
	public void a_testInitialisation() {
		creator.executeInitializeCommand(TEST_DIR);
		store = Storage.getInstance();
		entries = store.entries_;
		logic.isSavePresent();
		assertEquals(20,entries.size());
		String expected = "{\"due date\":\"08 Nov 2015 22:00\",\"alarm\":\"off\",\"description\":\"call mom\",\"id\":\"t9\",\"priority\":\"high\",\"category\":\"none\",\"complete\":false}";
		assertEquals(expected, entries.get(14).toString());
		JSONObject entry = (JSONObject) entries.get(2);
		assertEquals("walk the dog", entry.get("description"));
		entry = (JSONObject) entries.get(7);
		assertEquals("15 Nov 2015 22:00", entry.get("due date"));
		entry = (JSONObject) entries.get(19);
		assertEquals("none", entry.get("category"));
		entry = (JSONObject) entries.get(0);
		assertEquals(false, entry.get("complete"));
		entry = (JSONObject) entries.get(5);
		assertEquals("low", entry.get("priority"));
		entry = (JSONObject) entries.get(12);
		assertEquals("09 Nov 2015 19:00", entry.get("start date"));
	}
	
	@Test
	public void b_testAddTask(){
		logic.executeUserCommand("add buy milk, november 20 9am, p:high, c:personal");
		//check if an entry is added
		assertEquals(21,entries.size());
		String expected = "{\"due date\":\"20 Nov 2015 09:00\",\"alarm\":\"off\",\"description\":\"buy milk\",\"id\":\"t11\",\"priority\":\"high\",\"category\":\"personal\",\"complete\":false}";
		//check if the added entry is the correct one
		assertEquals(expected, entries.get(20).toString());
	}
	
	
	@Test
	public void c_testAddEvent(){
		logic.executeUserCommand("+ go shopping, Nov 21 3pm to Nov 21 5pm, p:high, c:personal");
		String expected = "{\"start date\":\"21 Nov 2015 15:00\",\"due date\":\"21 Nov 2015 17:00\",\"alarm\":\"off\",\"description\":\"go shopping\",\"id\":\"e11\",\"priority\":\"high\",\"category\":\"personal\",\"complete\":false}";
		assertEquals(22,entries.size());
		assertEquals(expected, entries.get(21).toString());
	}
	
	@Test
	public void d_testDelete(){
		logic.executeUserCommand("delete t8");
		assertEquals(21,entries.size());
		//t8 was originally entries[13]. After its deletion, t9 should now be entries[13].
		String expected = "{\"due date\":\"08 Nov 2015 22:00\",\"alarm\":\"off\",\"description\":\"call mom\",\"id\":\"t9\",\"priority\":\"high\",\"category\":\"none\",\"complete\":false}";
		assertEquals(expected, entries.get(13).toString());
	}
	
	@Test
	public void e_testUndo(){
		logic.executeUserCommand("undo");
		assertEquals(22,entries.size());
		String expected = "{\"due date\":\"11 Nov 2015 12:00\",\"alarm\":\"off\",\"description\":\"submit report\",\"id\":\"t8\",\"priority\":\"high\",\"category\":\"none\",\"complete\":false}";
		//t8 is re-inserted into the JSONArray at the end
		assertEquals(expected, entries.get(21).toString());
	}
	
	@Test
	public void f_testRedo(){
		logic.executeUserCommand("redo");
		assertEquals(21,entries.size());
		String expected = "{\"start date\":\"21 Nov 2015 15:00\",\"due date\":\"21 Nov 2015 17:00\",\"alarm\":\"off\",\"description\":\"go shopping\",\"id\":\"e11\",\"priority\":\"high\",\"category\":\"personal\",\"complete\":false}";
		//t8 is re-deleted, so the last entry should be e11
		assertEquals(expected, entries.get(20).toString());
	}
	
	@Test
	public void g_testUpdate(){
		logic.executeUserCommand("update e1, b:Nov 10 10am");
		//Start date is updated;alarm is also changed since it is set to be 2 hours before the start date
		String expected = "{\"start date\":\"10 Nov 2015 10:00\",\"due date\":\"10 Nov 2015 11:00\",\"alarm\":\"10 Nov 2015 09:00\",\"description\":\"meeting with boss\",\"id\":\"e1\",\"priority\":\"high\",\"category\":\"work\",\"complete\":false}";
		assertEquals(expected, entries.get(0).toString());
	}
	
	@Test
	public void h_testDone(){
		logic.executeUserCommand("done e1");
		String expected = "{\"start date\":\"10 Nov 2015 10:00\",\"due date\":\"10 Nov 2015 11:00\",\"alarm\":\"10 Nov 2015 09:00\",\"description\":\"meeting with boss\",\"id\":\"e1\",\"priority\":\"high\",\"category\":\"work\",\"complete\":\"true\"}";
		assertEquals(expected, entries.get(0).toString());
	}
	
	@Test
	public void i_testCd(){
		logic.executeUserCommand("cd src/test/testFiles/temp");
		File newFile = new File("src/test/testFiles/temp/chronos_storage.txt");
		File oldFile = new File("src/test/testFiles/testSystem/chronos_storage.txt");
		assertTrue(newFile.exists());
		//File at the old directory should have been deleted
		assertFalse(oldFile.exists());
		//Undo cd command so it will be easier for the AfterClass method to clean up
		logic.executeUserCommand("undo");
	}
	
	@Test
	public void j_testNote(){
		logic.executeUserCommand("note t9, tell her the good news");
		String expected = "{\"due date\":\"08 Nov 2015 22:00\",\"notes\":[{\"note\":\"tell her the good news\"}],\"alarm\":\"off\",\"description\":\"call mom\",\"id\":\"t9\",\"priority\":\"high\",\"category\":\"none\",\"complete\":false}";
		assertEquals(expected, entries.get(13).toString());
	}
	
	@Test
	public void k_testAlarm(){
		logic.executeUserCommand("alarm t11, 1");
		//alarm is set to be 1 hour before the due time
		String expected = "{\"due date\":\"20 Nov 2015 09:00\",\"alarm\":\"20 Nov 2015 08:00\",\"description\":\"buy milk\",\"id\":\"t11\",\"priority\":\"high\",\"category\":\"personal\",\"complete\":false}";
		assertEquals(expected, entries.get(19).toString());
	}
}
