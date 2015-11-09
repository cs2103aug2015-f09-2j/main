package test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.utils.Span;

import application.Task;

//@@author A0126223U
public class TestTask {
	
	@Before
	public void setUp() {
	}
	
	@Test //Partition: No Deadline
	public void testOnlyDescription() throws ParseException {
		String[] contents = {"buy milk"};
		Task testTask = new Task(contents);
		String expectedDescription = "buy milk";
		String expectedEndDate = "someday";
		String expectedCategory = "none";
		String expectedPriority = "low";
		assertEquals(expectedDescription, testTask.getDescription());
		assertEquals(expectedEndDate, testTask.getEndDate());
		assertEquals(expectedCategory, testTask.getCategory());
		assertEquals(expectedPriority, testTask.getPriority());
	}
	
	@Test //Partition: deadline - "today"
	public void testWithToday() throws ParseException {
		String[] contents = {"buy milk", "today"};
		Task testTask = new Task(contents);
		String expectedDescription = "buy milk";
		DateFormat dateFormat = new SimpleDateFormat(Task.DATE_FORMAT);
		Span aSpan = Chronic.parse(contents[1]);
		String expectedEndDate = dateFormat.format(aSpan.getBeginCalendar().getTime());
		String expectedCategory = "none";
		String expectedPriority = "low";
		assertEquals(expectedDescription, testTask.getDescription());
		assertEquals(expectedEndDate, testTask.getEndDate());
		assertEquals(expectedCategory, testTask.getCategory());
		assertEquals(expectedPriority, testTask.getPriority());
	}
	
	@Test //Partition: deadline - "tomorrow"
	public void testWithTomorrow() throws ParseException {
		String[] contents = {"buy milk", "tomorrow"};
		Task testTask = new Task(contents);
		String expectedDescription = "buy milk";
		DateFormat dateFormat = new SimpleDateFormat(Task.DATE_FORMAT);
		Span aSpan = Chronic.parse(contents[1]);
		String expectedEndDate = dateFormat.format(aSpan.getBeginCalendar().getTime());
		String expectedCategory = "none";
		String expectedPriority = "low";
		assertEquals(expectedDescription, testTask.getDescription());
		assertEquals(expectedEndDate, testTask.getEndDate());
		assertEquals(expectedCategory, testTask.getCategory());
		assertEquals(expectedPriority, testTask.getPriority());
	}
	
	@Test //Partition: deadline - specified date
	public void testWithDate() throws ParseException {
		String[] contents = {"buy milk", "25/12/2015"};
		Task testTask = new Task(contents);
		String expectedDescription = "buy milk";
		String expectedEndDate = "25 Dec 2015 12:00";
		String expectedCategory = "none";
		String expectedPriority = "low";
		assertEquals(expectedDescription, testTask.getDescription());
		assertEquals(expectedEndDate, testTask.getEndDate());
		assertEquals(expectedCategory, testTask.getCategory());
		assertEquals(expectedPriority, testTask.getPriority());
	}
	
	@Test //Partition: deadline - specified date and time
	public void testWithDateAndTime() throws ParseException {
		String[] contents = {"buy milk", "25/12/2015 5:00 am"};
		Task testTask = new Task(contents);
		String expectedDescription = "buy milk";
		String expectedEndDate = "25 Dec 2015 05:00";
		String expectedCategory = "none";
		String expectedPriority = "low";
		assertEquals(expectedDescription, testTask.getDescription());
		assertEquals(expectedEndDate, testTask.getEndDate());
		assertEquals(expectedCategory, testTask.getCategory());
		assertEquals(expectedPriority, testTask.getPriority());
	}
	
	@Test(expected = ParseException.class) //Partition: deadline - invalid date
	public void testWithInvalidDate() throws ParseException {
		String[] contents = {"buy milk", "bluh"};
		Task testTask = new Task(contents);
	}
	
	@Test //Testing Class Passing Methods
	public void testWithIntId() {
		Task testTask = new Task(1, "buy milk", "23/10/2015", "high", "personal","off");
		String expectedId = "t1";
		String expectedDescription = "buy milk";
		String expectedEndDate = "23/10/2015";
		String expectedCategory = "personal";
		String expectedPriority = "high";
		assertEquals(expectedId, testTask.getId());
		assertEquals(expectedDescription, testTask.getDescription());
		assertEquals(expectedEndDate, testTask.getEndDate());
		assertEquals(expectedCategory, testTask.getCategory());
		assertEquals(expectedPriority, testTask.getPriority());
	}
	
	@Test //Testing Class Passing Methods
	public void testWithStringId() {
		Task testTask = new Task("t1", "buy milk", "23/10/2015", "high", "personal","off");
		String expectedId = "t1";
		String expectedDescription = "buy milk";
		String expectedEndDate = "23/10/2015";
		String expectedCategory = "personal";
		String expectedPriority = "high";
		assertEquals(expectedId, testTask.getId());
		assertEquals(expectedDescription, testTask.getDescription());
		assertEquals(expectedEndDate, testTask.getEndDate());
		assertEquals(expectedCategory, testTask.getCategory());
		assertEquals(expectedPriority, testTask.getPriority());
	}
	
	@After
	public void tearDown() { 
	}
	
}
