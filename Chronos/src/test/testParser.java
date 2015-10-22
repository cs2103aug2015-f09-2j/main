package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import application.Event;
import application.Parser;
import application.Task;

public class testParser {
	static Parser parser;
	@BeforeClass
	public static void setUpBeforeClass(){
		parser = Parser.getInstance();
	}

	@Test
	//This is a boundary case for the creating task partition
	public void testCreateItem1() {
		Task createdTask = parser.createItem("submit report, tomorrow, c:work, p:high");
		String actual = createdTask.toString();
		String expected = "null. submit report tomorrow high work";
		assertEquals(expected, actual);
	}
	
	@Test
	//This is a boundary case for the creating event partition
	public void testCreateItem2() {
		Event createdEvent = (Event) parser.createItem("submit report, 2pm to 3pm, c:work, p:high");
		String actual = createdEvent.toString();
		String expected = "null. submit report 2pm 3pm high work";
		assertEquals(expected, actual);
	}

}
