//@@author A0131496A
package test;

import static org.junit.Assert.*;

import java.util.prefs.Preferences;

import application.CommandCreator;
import application.Storage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestStorage {
	
	Storage store;
	JSONArray entries;
	CommandCreator creator = new CommandCreator();
	static Preferences userPrefs = Preferences.userNodeForPackage(Storage.class);
	static final String DEFAULT_PATH= "none";
	static final String PREFS_PATH = "path";
	static final String TEST_FILE_EMPTY = "src/test/testFiles/testEmpty";
	static final String TEST_FILE_SOME = "src/test/testFiles/testSome";
	static final String TEST_FILE_MANY = "src/test/testFiles/testMany";
	static final String TEST_FILE_NOT_JSON = "src/test/testFiles/testInvalid";
	static final String TEST_FILE_WRONG_JASON = "src/test/testFiles/testIncorrect";
	
	static String path;

	@BeforeClass
	//Store the file path in user preference before testing
	public static void setUp(){
		path = userPrefs.get(PREFS_PATH, DEFAULT_PATH);
	}
	
	@AfterClass
	//Restore the file path in user preference
	public static void cleanUp(){
		userPrefs.put(PREFS_PATH, path);
	}

	/*
	 * Test reading in of files
	 * partitions: valid, boundaries: no content, some content, lots of content
	 * 				   invalid, boundaries: not JSON format, incorrect JSON format
	 */
	@Test
	//test boundary case of "no content" 
	public void testReadEmpty(){
		creator.executeInitializeCommand(TEST_FILE_EMPTY);
		store = Storage.getInstance();
		entries = store.entries_;
		assertEquals(entries.size(), 0);
	}
	
	@Test
	//test boundary case of "some content" 
	public void testReadSome() {	
		creator.executeInitializeCommand(TEST_FILE_SOME);
		store = Storage.getInstance();
		entries = store.entries_;
		assertEquals(entries.size(), 5);
		String expected = "{\"start date\":\"07 Nov 2015 09:00\",\"due date\":\"07 Nov 2015 10:00\",\"alarm\":\"off\",\"description\":\"meeting with boss\",\"id\":\"e1\",\"priority\":\"high\",\"category\":\"work\",\"complete\":false}";
		assertEquals(expected, entries.get(1).toString());
		JSONObject entry = (JSONObject) entries.get(3);
		assertEquals("walk the dog", entry.get("description"));
	}
	
	@Test
	//test boundary case of "lots of content"
	public void testReadMany(){
		creator.executeInitializeCommand(TEST_FILE_MANY);
		store = Storage.getInstance();
		entries = store.entries_;
		assertEquals(200, entries.size());
		String expected = "{\"start date\":\"07 Nov 2015 09:00\",\"due date\":\"07 Nov 2015 10:00\",\"alarm\":\"off\",\"description\":\"meeting with boss\",\"id\":\"e1\",\"priority\":\"high\",\"category\":\"work\",\"complete\":false}";
		assertEquals(expected, entries.get(60).toString());
		JSONObject entry = (JSONObject) entries.get(40);
		assertEquals("meeting with boss", entry.get("description"));
	}
	
	@Test
	//test boundary case of "not JSON format" 
	public void testInvalid(){
		Throwable caught = null;
		store = Storage.getInstance();
		try {
			store.getContent(TEST_FILE_NOT_JSON);
		} catch (Throwable e) {
			caught = e;
		}
		assertSame(org.json.simple.parser.ParseException.class,caught.getClass());	
	}
	
	@Test
	//test boundary case of "incorrect JSON format"
	public void testIncorrect(){
		Throwable caught = null;
		store = Storage.getInstance();
		try {
			store.getContent(TEST_FILE_WRONG_JASON);
			store.checkValidFormat();
		} catch (Throwable e) {
			caught = e;
		}
		assertSame(org.json.simple.parser.ParseException.class,caught.getClass());	
	}
}
