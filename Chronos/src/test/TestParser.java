package test;


import static org.junit.Assert.assertEquals;

import java.util.prefs.Preferences;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runners.MethodSorters;

import application.Parser;
import application.Task;
import application.Storage;
import application.AddCommand;
import application.CommandCreator;
import application.Event;
import application.Logic;

import java.text.ParseException;
import java.util.ArrayList;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@@author A0125424N
public class TestParser {
	static final String CONTENT_SEPARATOR = ", ";
	static Logic logic = Logic.getInstance();
	AddCommand add;
	static Parser parser;
	static Storage store = Storage.getInstance();
	JSONArray entries = store.entries_;
	static Preferences userPrefs = Preferences.userNodeForPackage(Storage.class);
	static final String DEFAULT_PATH = "none";
	static final String PREFS_PATH = "path";
	static final String TEST_FILE = "src/test/testFiles/testParser";
	static String path;
	static CommandCreator creator = new CommandCreator();
	
	@BeforeClass
	public static void setUp(){
		parser = Parser.getInstance();
		path = userPrefs.get(PREFS_PATH, DEFAULT_PATH);
		logic.isSavePresent();
		creator.executeInitializeCommand(TEST_FILE);
		
	}
	
	
	@AfterClass
	public static void cleanUp(){
		userPrefs.put(PREFS_PATH, path);
	}
	
	
	/**
	 *  Boundary Case for creating task partition
	 *  Equivalence Partition: [any string] 
	 *  [null]
	 *  Boundary Values: Non-empty String, a String of at least length of one.
	 * @throws Exception 
	 */
	@Test
	public void a_testCreateItem() throws Exception {
		int item = 0;
		Task task;
		ArrayList<Task> taskArr = new ArrayList<Task>();
		task = parser.createItem("buy paper, Nov 11 12pm, c:Work, p:MED");
		taskArr.add(task);
		task = parser.createItem("buy milk, Nov 12 12pm, c:Personal, p:MED");
		taskArr.add(task);
		task = parser.createItem("buy toy for son, 11/11/2015 12:00 PM, c:Personal, p:MED");
		taskArr.add(task);
		task = parser.createItem("decorate house, 12/11/2015 10:00 PM");
		taskArr.add(task);
		assertEquals("buy paper", taskArr.get(item).getDescription());
		assertEquals("11 Nov 2015 12:00", taskArr.get(item).getEndDate());
		assertEquals("Work", taskArr.get(item).getCategory());
		assertEquals("med", taskArr.get(item).getPriority());
		assertEquals("med", taskArr.get(++item).getPriority());
		assertEquals("Personal", taskArr.get(item).getCategory());
		assertEquals("11 Nov 2015 12:00", taskArr.get(++item).getEndDate());
		assertEquals("low", taskArr.get(++item).getPriority());
		assertEquals("none", taskArr.get(item).getCategory());
	}
	
	
	
	@Test
	public void b_testConvertToJSON() throws Exception {
		Task task;
		JSONObject entry = new JSONObject();
		task = parser.createItem("buy paper, 11/11/2015 12:00 PM, c:Work, p:MED");
		entry = parser.convertToJSON(task);
		assertEquals("buy paper", entry.get("description"));
		assertEquals("11 Nov 2015 12:00", entry.get("due date"));
		assertEquals("Work", entry.get("category"));
		assertEquals("med", entry.get("priority"));
		assertEquals(false, entry.get("complete"));
		task = parser.createItem("buy milk, 11/18/2015 9am");
		entry = parser.convertToJSON(task);
		assertEquals("18 Nov 2015 09:00", entry.get("due date"));
		assertEquals("none", entry.get("category"));
		assertEquals("low", entry.get("priority"));
	}
	
	
	@Test
	public void c_testConvertToTaskArray() throws ArithmeticException, NullPointerException, ParseException {
		ArrayList<Task> taskArr = new ArrayList<Task>();
		add = new AddCommand("buy paper, 11/11/2015 12:00 PM, c:personal, p:MED");
		add.execute();
		taskArr = parser.convertToTaskArray(entries);
		int item = taskArr.size()-1;
		assertEquals("personal", taskArr.get(item).getCategory());
		assertEquals("med", taskArr.get(item).getPriority());
		assertEquals("11 Nov 2015 12:00", taskArr.get(item).getEndDate());
		assertEquals("buy paper", taskArr.get(item).getDescription());
		assertEquals(false, taskArr.get(item).isTaskComplete());
		add.undo();
		add = new AddCommand("buy paper");
		add.execute();
		taskArr = parser.convertToTaskArray(entries);
		taskArr.get(item).markTaskAsDone(true);
		assertEquals("none", taskArr.get(item).getCategory());
		assertEquals("low", taskArr.get(item).getPriority());
		assertEquals("someday", taskArr.get(item).getEndDate());
		assertEquals(true, taskArr.get(item).isTaskComplete());
		add.undo();
	}
	
	
	@Test
	public void d_testRetrieveTask() {
		String taskId;
		Task task;
		ArrayList<Task> taskArr = new ArrayList<Task>();
		add = new AddCommand("buy paper, 11/11/2015 12:00 PM, c:Work, p:MED");
		add.execute();
		taskArr = parser.convertToTaskArray(entries);
		int item = taskArr.size()-1;
		taskId = taskArr.get(item).getId();
		task = parser.retrieveTask(taskId, entries);
		assertEquals("buy paper", task.getDescription());
		assertEquals("11 Nov 2015 12:00", task.getEndDate());
		assertEquals("Work", task.getCategory());
		assertEquals("med", task.getPriority());
		assertEquals(false, task.isTaskComplete());
		assertEquals(0, task.getNotesNo());
		add.undo();
		add = new AddCommand("buy milk, p:MED");
		add.execute();
		taskArr = parser.convertToTaskArray(entries);
		taskId = taskArr.get(item).getId();
		task = parser.retrieveTask(taskId, entries);
		task.markTaskAsDone(true);
		assertEquals("someday", task.getEndDate());
		assertEquals("none", task.getCategory());
		assertEquals(true, task.isTaskComplete());
		add.undo();
	}
	
	@Test
	public void e_testConvertToTask() {
		JSONObject entry = new JSONObject();
		String taskId;
		Task task;
		Event event;
		ArrayList<Task> taskArr = new ArrayList<Task>();
		add = new AddCommand("buy paper, 11/11/2015 12:00 PM, c:Work, p:MED");
		add.execute();
		int item = entries.size()-1;
		task = parser.convertToTask((JSONObject)entries.get(item));
		assertEquals("buy paper", task.getDescription());
		assertEquals("11 Nov 2015 12:00", task.getEndDate());
		assertEquals("Work", task.getCategory());
		assertEquals("med", task.getPriority());
		assertEquals(false, task.isTaskComplete());
		assertEquals(0, task.getNotesNo());
		add.undo();
		add = new AddCommand("celebrate christmas with family, Dec 25 1pm to Dec 25 8pm, c:Family, p:MED");
		add.execute();
		task = parser.convertToTask((JSONObject)entries.get(item));
		event = (Event)task;
		assertEquals("25 Dec 2015 20:00", event.getEndDate());
		assertEquals("25 Dec 2015 13:00", event.getStartDate());
		add.undo();
	}
	
	//@@author A0125424N
	/**
	 *  Boundary case for updating field partition
	 *  Equivalence Partition: [any task id + ", d:" + any date] 
	 *  [any task id + ", c:" + any string]
	 *  [any task id + ", p:HIGH"]
	 *  [any task id + ", p:LOW"]
	 *  [any task id + ", p:MED"]
	 *  [any task id + ", " + any String]
	 *  [any other string] [null]
	 *  Boundary Values: Non-empty String, a String of at least length of one.
	 */
	@Test
	public void f_testParseUpdateString() {
		String taskId, updatedStr;
		ArrayList<Task> taskArr = new ArrayList<Task>();
		ArrayList<String> updatedTask;
		add = new AddCommand("buy paper, 11/11/2015 12:00 PM, c:Work, p:MED");
		add.execute();
		taskArr = parser.convertToTaskArray(entries);
		int item = 2;
		int taskItem = taskArr.size() - 1;
		taskId = taskArr.get(taskItem).getId();
		updatedStr = "p:HIGH";
		updatedTask = parser.parseUpdateString(taskId + CONTENT_SEPARATOR + updatedStr);
		assertEquals("HIGH", updatedTask.get(item));
		add.undo();
		add = new AddCommand("buy milk, 11/06/2015 12:00 PM, c:Personal, p:MED");
		add.execute();
		taskArr = parser.convertToTaskArray(entries);
		taskId = taskArr.get(taskItem).getId();
		updatedStr = "e:11/17/2015 5:00 PM";
		updatedTask = parser.parseUpdateString(taskId + CONTENT_SEPARATOR + updatedStr);
		assertEquals("11/17/2015 5:00 PM", updatedTask.get(item));
		add.undo();
	}
	
	
	/**
	 *  Boundary Case for checking for existing id partition
	 *  Equivalence Partition: [taskId is an existing task id number] 
	 *  [taskId is a non-existing task id number] [null]
	 *  Boundary Values: Non-empty String, a String of at least length of one.
	 */
	@Test
	public void g_testIsExistingId() {
		add = new AddCommand("buy paper, 11/06/2015 12:00 PM, c:Work, p:MED");
		add.execute();
		assertEquals(false, parser.isExistingId("0", entries));
		add.undo();
	}
	
	@Test
	public void h_testCheckForClashes() {
		add = new AddCommand("buy paper, 11/11/2015 12:00 PM, c:Work, p:MED");
		add.execute();
		add = new AddCommand("buy milk, 11/11/2015 12:00 PM, c:Personal, p:MED");
		add.execute();
		int item = 1;
		Task task = parser.convertToTask((JSONObject)entries.get(item));
		assertEquals(true, parser.checkForClashes(task, entries));
		add.undo();
		add.undo();
	}
	
	@Test
	public void i_testParseExtendString() {
		ArrayList<String> extend = new ArrayList<String>();
		add = new AddCommand("buy paper, 11/11/2015 12:00 PM, c:Work, p:MED");
		add.execute();
		String taskId = parser.convertToTask((JSONObject)entries.get(0)).getId();
		extend = parser.parseExtendString(taskId + CONTENT_SEPARATOR + "11/11/2015 2:00 PM");
		assertEquals("11/11/2015 2:00 PM", extend.get(1));
		add.undo();
	}
	
}
