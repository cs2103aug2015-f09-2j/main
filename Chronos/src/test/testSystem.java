package test;

import static org.junit.Assert.*;

import java.io.File;
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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testSystem {
	static Storage store;
	static Logic logic = Logic.getInstance();
	static JSONArray entries;
	static CommandCreator creator = new CommandCreator();
	static Preferences userPrefs = Preferences.userNodeForPackage(Storage.class);
	static final String DEFAULT_PATH= "none";
	static final String PREFS_PATH = "path";
	static String path;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		path = userPrefs.get(PREFS_PATH, DEFAULT_PATH);
		creator.executeInitializeCommand("src/test/testFiles/testSystem");
		store = Storage.getInstance();
		entries = store.entries_;
		logic.isSavePresent();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		userPrefs.put(PREFS_PATH, path);
	}
	@Test
	//test by random sampling if the file is read in properly
	public void a_testInitialisation() {
		assertEquals(20,entries.size());
		String expected = "{\"start date\":\"10\\/30\\/15 6:00 PM\",\"due date\":\"10\\/30\\/15 7:00 PM\",\"description\":\"consult tutor\",\"id\":\"e5\",\"priority\":\"high\",\"category\":\"cs2106\",\"complete\":\"false\"}";
		assertEquals(expected, entries.get(14).toString());
		JSONObject entry = (JSONObject) entries.get(2);
		assertEquals("tutorial", entry.get("description"));
		entry = (JSONObject) entries.get(7);
		assertEquals("someday", entry.get("due date"));
		entry = (JSONObject) entries.get(19);
		assertEquals("none", entry.get("category"));
		entry = (JSONObject) entries.get(0);
		assertEquals("false", entry.get("complete"));
		entry = (JSONObject) entries.get(5);
		assertEquals("high", entry.get("priority"));
		entry = (JSONObject) entries.get(2);
		assertEquals("10/30/15 9:00 AM", entry.get("start date"));
	}
	
	@Test
	public void b_testAddTask(){
		store.storeTemp();
		logic.executeUserCommand("add buy milk, 11/1, p:high, c:personal");
		store.storeChanges();
		String expected = "{\"due date\":\"01\\/11\\/2015 12:00 PM\",\"description\":\"buy milk\",\"id\":\"t11\",\"priority\":\"high\",\"category\":\"personal\",\"complete\":\"false\"}";
		assertEquals(21,entries.size());
		assertEquals(expected, entries.get(20).toString());
		logic.executeUserCommand("undo");
		assertEquals(20,entries.size());
	}
	
	
	@Test
	public void c_testAddEvent(){
		store.storeTemp();
		logic.executeUserCommand("add buy milk, 11/1/15 10am to 11/1/15 11am, p:high, c:personal");
		store.storeChanges();
		String expected = "{\"start date\":\"01\\/11\\/2015 10:00 AM\",\"due date\":\"01\\/11\\/2015 11:00 AM\",\"description\":\"buy milk\",\"id\":\"e11\",\"priority\":\"high\",\"category\":\"personal\",\"complete\":\"false\"}";
		assertEquals(expected, entries.get(20).toString());
		logic.executeUserCommand("undo");
		assertEquals(20,entries.size());
	}
	
	
	@Test
	public void d_testDelete(){
		store.storeTemp();
		logic.executeUserCommand("delete e10");
		store.storeChanges();
		assertEquals(19,entries.size());
		String expected = "{\"start date\":\"10\\/31\\/15 9:00 AM\",\"due date\":\"10\\/31\\/15 12:00 PM\",\"description\":\"CIP\",\"id\":\"e9\",\"priority\":\"high\",\"category\":\"none\",\"complete\":\"false\"}";
		assertEquals(expected, entries.get(18).toString());
		logic.executeUserCommand("undo");
		assertEquals(20,entries.size());
	}
	
	@Test
	public void e_testUpdate(){
		store.storeTemp();
		logic.executeUserCommand("update e1, p:high");
		store.storeChanges();
		String expected = "{\"start date\":\"10\\/30\\/15 12:00 AM\",\"due date\":\"10\\/30\\/15 8:00 AM\",\"description\":\"sleep\",\"id\":\"e1\",\"priority\":\"high\",\"category\":\"personal\",\"complete\":\"false\"}";
		assertEquals(expected, entries.get(1).toString());
		logic.executeUserCommand("undo");
		expected = "{\"start date\":\"10\\/30\\/15 12:00 AM\",\"due date\":\"10\\/30\\/15 8:00 AM\",\"description\":\"sleep\",\"id\":\"e1\",\"priority\":\"med\",\"category\":\"personal\",\"complete\":\"false\"}";
		assertEquals(expected, entries.get(1).toString());
	}
	
	@Test
	public void f_testDone(){
		store.storeTemp();
		logic.executeUserCommand("done e1");
		store.storeChanges();
		String expected = "{\"start date\":\"10\\/30\\/15 12:00 AM\",\"due date\":\"10\\/30\\/15 8:00 AM\",\"description\":\"sleep\",\"id\":\"e1\",\"priority\":\"med\",\"category\":\"personal\",\"complete\":\"true\"}";
		assertEquals(expected, entries.get(1).toString());
		logic.executeUserCommand("undo");
		expected = "{\"start date\":\"10\\/30\\/15 12:00 AM\",\"due date\":\"10\\/30\\/15 8:00 AM\",\"description\":\"sleep\",\"id\":\"e1\",\"priority\":\"med\",\"category\":\"personal\",\"complete\":\"false\"}";
		assertEquals(expected, entries.get(1).toString());
	}
	
	@Test
	public void g_testCd(){
		store.storeTemp();
		logic.executeUserCommand("cd src/test/testFiles/temp");
		store.storeChanges();
		File newFile = new File("src/test/testFiles/temp/chronos_storage.txt");
		File oldFile = new File("src/test/testFiles/testSystem/chronos_storage.txt");
		assertTrue(newFile.exists());
		//assertFalse(oldFile.exists());
		logic.executeUserCommand("undo");
		assertTrue(oldFile.exists());
		assertFalse(newFile.exists());
	}
	
	@Test
	public void h_testNote(){
		store.storeTemp();
		logic.executeUserCommand("note e1, sleep tight");
		store.storeChanges();
		String expected = "{\"start date\":\"10\\/30\\/15 12:00 AM\",\"due date\":\"10\\/30\\/15 8:00 AM\",\"notes\":[{\"note\":\"sleep tight\"}],\"description\":\"sleep\",\"id\":\"e1\",\"priority\":\"med\",\"category\":\"personal\",\"complete\":\"false\"}";
		assertEquals(expected, entries.get(1).toString());
		logic.executeUserCommand("undo");
		expected = "{\"start date\":\"10\\/30\\/15 12:00 AM\",\"due date\":\"10\\/30\\/15 8:00 AM\",\"description\":\"sleep\",\"id\":\"e1\",\"priority\":\"med\",\"category\":\"personal\",\"complete\":\"false\"}";
		assertEquals(expected, entries.get(1).toString());
	}
	
}
