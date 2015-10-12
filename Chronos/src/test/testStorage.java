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
		store = new Storage("src/test/testFiles/test");
		entries = store.entries_;
	}
	
	@Test
	public void testLength() {
		assertEquals(entries.size(), 4);
	}
	
	@Test
	public void testEntry() {
		String expected = "{\"description\":\"sleep\",\"id\":\"t4\"}";
		assertEquals(expected, entries.get(2).toString());
	}
	
	@Test
	public void testValue(){
		JSONObject entry = (JSONObject) entries.get(3);
		assertEquals("do laundry", entry.get("description"));
	}

}
