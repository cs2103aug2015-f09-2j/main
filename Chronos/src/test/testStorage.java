package test;

import static org.junit.Assert.*;
import application.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class testStorage {
	Storage store;
	JSONArray entries;

	@Before
	public void setUp(){
		CommandCreator creator = new CommandCreator();
		creator.executeInitializeCommand("src/test/testFiles");
		store = Storage.getInstance();
		entries = store.entries_;
	}
	
	@Test
	public void testLength() {
		assertEquals(entries.size(), 4);
	}
	
	@Test
	public void testEntry() {
		String expected = "{\"due date\":\"today\",\"description\":\"buy milk\",\"id\":\"t1\",\"priority\":\"high\",\"category\":\"personal\",\"complete\":false}";
		System.out.println(entries.get(2).toString());
		assertEquals(expected, entries.get(2).toString());
	}
	
	@Test
	public void testValue(){
		JSONObject entry = (JSONObject) entries.get(3);
		assertEquals("sleep", entry.get("description"));
	}

}
