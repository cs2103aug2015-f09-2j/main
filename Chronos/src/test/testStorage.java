package test;

import static org.junit.Assert.assertEquals;
import application.CommandCreator;
import application.Storage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class testStorage {
	Storage store;
	JSONArray entries;
	CommandCreator creator = new CommandCreator();

	@Before
	public void setUp(){
	
	}
	
	/*
	 * Test reading in of files
	 * partitions: no content, some content, lots of content
	 */
	@Test
	//test boundary case for the "no content" partition
	public void testReadEmpty(){
		creator.executeInitializeCommand("src/test/testFiles/testEmpty");
		store = Storage.getInstance();
		entries = store.entries_;
		assertEquals(entries.size(), 0);
	}
	@Test
	//test boundary case for the "some content" partition
	public void testReadSome() {	
		creator.executeInitializeCommand("src/test/testFiles");
		store = Storage.getInstance();
		entries = store.entries_;
		assertEquals(entries.size(), 4);
		String expected = "{\"due date\":\"today\",\"description\":\"buy milk\",\"id\":\"t1\",\"priority\":\"high\",\"category\":\"personal\",\"complete\":false}";
		assertEquals(expected, entries.get(2).toString());
		JSONObject entry = (JSONObject) entries.get(3);
		assertEquals("sleep", entry.get("description"));
	}
	@Test
	//test boundary case for the "lots of content" partition
	public void testReadMany(){
		creator.executeInitializeCommand("src/test/testFiles/testMany");
		store = Storage.getInstance();
		entries = store.entries_;
		assertEquals(100, entries.size());
		String expected = "{\"due date\":\"someday\",\"description\":\"sleep\",\"id\":\"t4\",\"priority\":\"high\",\"category\":\"none\",\"complete\":false}";
		assertEquals(expected, entries.get(60).toString());
		JSONObject entry = (JSONObject) entries.get(40);
		assertEquals("sleep", entry.get("description"));
	}
	

}
