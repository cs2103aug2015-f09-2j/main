package test;

import static org.junit.Assert.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import application.NoteCommand;
import application.NoteDeleteCommand;
import application.NoteUpdateCommand;
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
	//Test for partition 1
	public void testAddNote(){
		NoteCommand addNoteCmd = new NoteCommand("e1, bring documents");
		addNoteCmd.execute();
		JSONObject entry= (JSONObject) store.entries_.get(1);
		JSONArray notes = (JSONArray)entry.get(Parser.JSON_NOTES);
		JSONObject note = (JSONObject) notes.get(0);
		String expected = "bring documents";
		String actual = note.get(JSON_NOTE).toString();
		assertEquals(expected, actual);
		addNoteCmd.undo();
		entry= (JSONObject) store.entries_.get(1);
		assertEquals(null, entry.get(Parser.JSON_NOTES));
	}
	
	@Test
	//Test for partition 2
	public void testDeleteNote(){
		//add the note first
		NoteCommand addNoteCmd = new NoteCommand("e1, bring documents");
		addNoteCmd.execute();
		NoteDeleteCommand deleteNoteCmd = new NoteDeleteCommand("e1, 1");
		deleteNoteCmd.execute();
		JSONObject entry= (JSONObject) store.entries_.get(1);
		assertEquals(null, entry.get(Parser.JSON_NOTES));
	}

	@Test
	//Test for partition 3
	public void testUpdateNote(){
		//add the note first
		NoteCommand addNoteCmd = new NoteCommand("e1, bring documents");
		addNoteCmd.execute();
		NoteUpdateCommand updateNoteCmd = new NoteUpdateCommand("e1, 1, bring report");
		updateNoteCmd.execute();
		JSONObject entry= (JSONObject) store.entries_.get(1);
		JSONArray notes = (JSONArray)entry.get(Parser.JSON_NOTES);
		JSONObject note = (JSONObject) notes.get(0);
		String expected = "bring report";
		String actual = note.get(JSON_NOTE).toString();
		assertEquals(expected, actual);
		updateNoteCmd.undo();
		addNoteCmd.undo();
	}
}
