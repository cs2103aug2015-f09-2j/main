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
		String expected = "{\"note\":\"sleep early\",\"due date\":\"none\",\"description\":\"sleep\",\"id\":\"t4\",\"priority\":\"high\",\"category\":\"none\",\"complete\":\"false\"}";
		System.out.println(entries.get(2).toString());
		assertEquals(expected, entries.get(2).toString());
	}
	
	@Test
	public void testValue(){
		JSONObject entry = (JSONObject) entries.get(3);
		assertEquals("do laundry", entry.get("description"));
	}

}
