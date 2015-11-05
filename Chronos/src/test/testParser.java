package test;


import static org.junit.Assert.assertEquals;

import java.util.prefs.Preferences;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import application.Parser;
import application.Task;
import application.Storage;
import application.AddCommand;
import application.CommandCreator;
import application.DeleteCommand;
import application.UpdateCommand;
import application.Event;

import java.text.ParseException;
import java.util.ArrayList;

public class testParser {
	Storage store = Storage.getInstance();
	Task task;
	ArrayList<Task> taskArr = new ArrayList<Task>();
	JSONObject entry = new JSONObject();
	JSONArray entries = store.entries_;
	AddCommand add;
	UpdateCommand update;
	DeleteCommand delete;
	static Parser parser;
	static Preferences userPrefs = Preferences.userNodeForPackage(Storage.class);
	static final String DEFAULT_PATH= "none";
	static final String PREFS_PATH = "path";
	static String path;
	static CommandCreator creator = new CommandCreator();
	
	@BeforeClass
	public static void setUpBeforeClass(){
		parser = Parser.getInstance();
		path = userPrefs.get(PREFS_PATH, DEFAULT_PATH);
		creator.executeInitializeCommand("src/test/testFiles/testParser");
	}
	
	@AfterClass
	public static void cleanUp(){
		userPrefs.put(PREFS_PATH, path);
	}

	@Test
	//This is a boundary case for the creating task partition
	public void testCreateItem1() throws Exception {
		Task createdTask = parser.createItem("submit report, tomorrow, c:work, p:high");
		String actual = createdTask.toString();
		String expected = "null. submit report 30/10/15 12:00 PM high work";
		assertEquals(expected, actual);
	}
	
	@Test
	//This is a boundary case for the creating event partition
	public void testCreateItem2() throws Exception {
		Event createdEvent = (Event) parser.createItem("submit report, 23/10/2015 2:00 pm to 23/10/2015 3:00 pm, c:work, p:high");
		String actual = createdEvent.toString();
		String expected = "null. submit report 23/10/15 2:00 PM 23/10/15 3:00 PM high work";
		assertEquals(expected, actual);
	}
	
	
	/**
	 *  Boundary Case for creating task partition
	 *  Equivalence Partition: [any string] 
	 *  [null]
	 *  Boundary Values: Non-empty String, a String of at least length of one.
	 * @throws ParseException 
	 */
	@Test
	public void testCreateItem() throws Exception {
		int item = 0;
		task = parser.createItem("buy paper, today, c:Work, p:MED");
		taskArr.add(task);
		task = parser.createItem("buy milk, today, c:Personal, p:MED");
		taskArr.add(task);
		task = parser.createItem("buy toy for son, today, c:Personal, p:MED");
		taskArr.add(task);
		assertEquals("buy paper", ("Work"), taskArr.get(item).getCategory());
		assertEquals("buy paper", ("med"), taskArr.get(item).getPriority());
		//assertEquals("buy paper", ("30/10/15 12:00 AM"), taskArr.get(item).getEndDate());
		assertEquals("buy paper", ("buy paper"), taskArr.get(item).getDescription());
		assertEquals("buy paper", (false), taskArr.get(item).isTaskComplete());
		taskArr.get(++item).markTaskAsDone(true);
		assertEquals("buy milk", ("Personal"), taskArr.get(item).getCategory());
		assertEquals("buy milk", ("med"), taskArr.get(item).getPriority());
		//assertEquals("buy milk", ("30/10/15 12:00 AM"), taskArr.get(item).getEndDate());
		assertEquals("buy milk", ("buy milk"), taskArr.get(item).getDescription());
		assertEquals("buy milk", (true), taskArr.get(item).isTaskComplete());
		taskArr.get(++item).markTaskAsDone(true);
		assertEquals("buy toy for son", ("Personal"), taskArr.get(item).getCategory());
		assertEquals("buy toy for son", ("med"), taskArr.get(item).getPriority());
		//assertEquals("buy toy for son", ("30/10/15 12:00 AM"), taskArr.get(item).getEndDate());
		assertEquals("buy toy for son", ("buy toy for son"), taskArr.get(item).getDescription());
		assertEquals("buy toy for son", (true), taskArr.get(item).isTaskComplete());
	}
	
	@Test
	public void testConvertToJSON() throws Exception {
		task = parser.createItem("buy paper, today, c:Work, p:MED");
		entry = parser.convertToJSON(task);
		assertEquals("description", ("buy paper"), entry.get("description"));
		//assertEquals("end date", ("30/10/15 12:00 AM"), entry.get("due date"));
		assertEquals("category", ("Work"), entry.get("category"));
		assertEquals("priority", ("med"), entry.get("priority"));
		assertEquals("complete", (false), entry.get("complete"));
		task = parser.createItem("buy milk, today, c:Work");
		entry = parser.convertToJSON(task);
		assertEquals("description", ("buy milk"), entry.get("description"));
		//assertEquals("end date", ("30/10/15 12:00 AM"), entry.get("due date"));
		assertEquals("category", ("Work"), entry.get("category"));
		assertEquals("priority", ("med"), entry.get("priority"));
		assertEquals("complete", (false), entry.get("complete"));
	}
	
	@Test
	public void testConvertToTaskArray() {
		add = new AddCommand("buy paper, d:today, c:personal, p:MED");
		add.execute();
		taskArr = parser.convertToTaskArray(entries);
		int item = taskArr.size()-1;
		assertEquals("buy paper", ("personal"), taskArr.get(item).getCategory());
		assertEquals("buy paper", ("med"), taskArr.get(item).getPriority());
		//assertEquals("buy paper", ("30/10/15 12:00 AM"), taskArr.get(item).getEndDate());
		assertEquals("buy paper", ("buy paper"), taskArr.get(item).getDescription());
		assertEquals("buy paper", (false), taskArr.get(item).isTaskComplete());
		add = new AddCommand("buy paper");
		add.execute();
		taskArr = parser.convertToTaskArray(entries);
		taskArr.get(++item).markTaskAsDone(true);
		assertEquals("buy paper", ("none"), taskArr.get(item).getCategory());
		assertEquals("buy paper", ("med"), taskArr.get(item).getPriority());
		assertEquals("buy paper", ("someday"), taskArr.get(item).getEndDate());
		assertEquals("buy paper", ("buy paper"), taskArr.get(item).getDescription());
		assertEquals("buy paper", (true), taskArr.get(item).isTaskComplete());
	}
	
	@Test
	public void testRetrieveTask() {
		String taskId;
		taskArr = parser.convertToTaskArray(entries);
		int item = taskArr.size()-1;
		add = new AddCommand("buy paper, d:today, c:Work, p:MED");
		add.execute();
		add = new AddCommand("buy milk, d:today, c:Personal, p:MED");
		add.execute();
		taskArr = parser.convertToTaskArray(entries);
		//int item = taskArr.size()-2;
		taskId = taskArr.get(item).getId();
		task = parser.retrieveTask(taskId, entries);


		assertEquals("buy paper", ("buy paper"), task.getDescription());

		System.out.println(task.getDescription());
		assertEquals("buy paper", ("buy paper"), task.getDescription());

		/*
		System.out.println(task.getDescription());
		assertEquals("buy paper", ("buy paper"), task.getDescription());
		*/
		assertEquals("buy paper", ("someday"), task.getEndDate());
		assertEquals("buy paper", ("none"), task.getCategory());
		assertEquals("buy paper", ("med"), task.getPriority());
		assertEquals("buy paper", (0), task.getNotesNo());
		taskId = taskArr.get(++item).getId();
		update = new UpdateCommand(taskId + ", p:HIGH");
		update.execute();
		taskArr = parser.convertToTaskArray(entries);
		task = parser.retrieveTask(taskId, entries);
		assertEquals("buy milk", ("buy paper"), task.getDescription());
		assertEquals("buy milk", ("HIGH"), task.getPriority());
		assertEquals("buy milk", (0), task.getNotesNo());
	}
	
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
	public void testParseUpdateString() {
		String taskId;
		int item = 0;
		ArrayList<String> updatedTask;
		add = new AddCommand("buy paper, d:today, c:Work, p:MED");
		add.execute();
		add = new AddCommand("buy milk, d:today, c:Personal, p:MED");
		add.execute();
		taskArr = parser.convertToTaskArray(entries);
		taskId = taskArr.get(item).getId();
		updatedTask = parser.parseUpdateString(taskId + ", d:tomorrow");
		assertEquals("buy paper", (taskId), updatedTask.get(item));
		assertEquals("buy paper", ("description"), updatedTask.get(++item));
		assertEquals("buy paper", ("d:tomorrow"), updatedTask.get(++item));
		item = 0;
		delete = new DeleteCommand(taskId);
		delete.execute();
		taskArr = parser.convertToTaskArray(entries);
		taskId = taskArr.get(item).getId();
		updatedTask = parser.parseUpdateString(taskId + ", c:None");
		assertEquals("buy paper", (taskId), updatedTask.get(item));
		assertEquals("buy paper", ("category"), updatedTask.get(++item));
		assertEquals("buy paper", ("None"), updatedTask.get(++item));
	}
	
	/**
	 *  Boundary Case for checking for existing id partition
	 *  Equivalence Partition: [taskId is an existing task id number] 
	 *  [taskId is a non-existing task id number] [null]
	 *  Boundary Values: Non-empty String, a String of at least length of one.
	 */
	@Test
	public void testIsExistingId() {
		String taskId;
		int item = 0;
		add = new AddCommand("buy paper, d:today, c:Work, p:MED");
		add.execute();
		add = new AddCommand("buy milk, d:today, c:Personal, p:MED");
		add.execute();
		assertEquals("check id", (false), parser.isExistingId("0", entries));
		taskArr = parser.convertToTaskArray(entries);
		taskId = taskArr.get(item).getId();
		assertEquals("check id", (true), parser.isExistingId(taskId, entries));
	}
	
}
