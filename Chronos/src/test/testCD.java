package test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.prefs.Preferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import application.Command;
import application.Parser;
import application.Storage;

public class testCD {
	Parser parse = new Parser(Preferences.userNodeForPackage(this.getClass()));
	Storage store = new Storage("src/test/testFiles/cd");
	@Before
	public void setUp() {
		store.storeTemp();
	}

	@After
	public void tearDown()  {
		store.swapFile();
	}

	@Test
	public void test() {
		Command cmd= new Command("cd src/test/testFiles/temp", store, parse);
		cmd.execute();
		File file = new File("src/test/testFiles/temp\\chronos_storage.txt");
		assertTrue(file.exists());
	}

}
