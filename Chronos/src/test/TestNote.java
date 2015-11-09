package test;

import static org.junit.Assert.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import application.NoteCommand;
import application.Parser;

//@@author A0131496A
public class TestNote extends TestCommands {
	
	/**
	 * Partitions for commands related with notes
	 * 1. add a note
	 * 2. delete a note
	 * 3. update a note
	 */
	@Test
	public void testNote(){
		NoteCommand addNoteCmd = new NoteCommand("e1, bring documents");
		addNoteCmd.execute();
		JSONObject entry= (JSONObject) store.entries_.get(1);
		JSONArray notes = (JSONArray)entry.get(Parser.JSON_NOTES);
		JSONObject note = (JSONObject) notes.get(0);
		String expected = "bring documents";
		String actual = note.get(JSON_NOTE).toString();
		assertEquals(expected, actual);
		addNoteCmd.undo();
	}

}
