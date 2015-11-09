package test;


import java.util.prefs.Preferences;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import application.CommandCreator;
import application.Logic;
import application.Storage;

//@@author A0131496A
/**
 * Unit tests for different commands share the variables initialized in this class, BeforeClass and AfterClass.
 * All tests are performed on the same storage file. 
 * If commands change the content of the storage, call undo() to restore the original storage file.
 **/

public class TestCommands {
	
	static Logic logic = Logic.getInstance();
	static Storage store;
	static CommandCreator creator = new CommandCreator();
	static final String DEFAULT_PATH= "none";
	static final String PREFS_PATH = "path";
	static final String TEST_FILE = "src/test/testFiles/testSome";
	static final String CD_PATH = "src/test/testFiles";
	static final String JSON_NOTE = "note";
	static final String JSON_STATUS = "complete";
	
	static String path;
	static Preferences userPrefs = Preferences.userNodeForPackage(Storage.class);

	
	@BeforeClass
	//Store the file path in user preference in String path
	public static void setUp(){
		path = userPrefs.get(PREFS_PATH, DEFAULT_PATH);
		logic.isSavePresent();
		creator.executeInitializeCommand(TEST_FILE);
		store = Storage.getInstance();
	}
	
	@AfterClass
	//Restore the user preferred file path
	public static void cleanUp(){
		userPrefs.put(PREFS_PATH, path);
	}

}
