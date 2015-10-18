package application;

import java.util.prefs.Preferences;

public class Logic {
	
	private static final String MESSAGE_SET_PATH = "Setting save path to: %1$s";

	//contants
	private static final String PREFS_PATH = "path";
	private static final String PREFS_COUNT = "count";
	
	private static final String DEFAULT_VALUE = "none";
	private static final int  DEFAULT_COUNT = 0;
	
	private static CommandCreator _commandCreator;
	private static Storage _store;
	private static Parser _parse;
	private static Preferences _userPrefs;
	
	public Logic(){
		_userPrefs = Preferences.userNodeForPackage(this.getClass());
		_commandCreator = new CommandCreator();
	}
	
	public Feedback executeUserCommand(String userInput) {
		//Command aCommand = new Command(userInput, _store, _parse);
		String[] inputs = _parse.parseUserContent(userInput);
		Feedback feedback = _commandCreator.createAndExecuteCommand(inputs, _store, _parse);
		return feedback;
	}
	
	public boolean isSavePresent() {
		String savedPath = _userPrefs.get(PREFS_PATH, DEFAULT_VALUE);
		if (savedPath.equals(DEFAULT_VALUE)) {
			return false;
		} else { //load saved settings onto storage and parser
			_store = new Storage(savedPath);
			_parse = new Parser(_userPrefs);
			return true;
		}
	}
	
	//refactor into cd Command
	public Feedback setSavePath(String path) { 
		_userPrefs.put(PREFS_PATH, path);
		_userPrefs.putInt(PREFS_COUNT, DEFAULT_COUNT);
		_store = new Storage(path);
		_parse = new Parser(_userPrefs);
		String feedbackString = String.format(MESSAGE_SET_PATH, _userPrefs.get(PREFS_PATH, DEFAULT_VALUE));
		return new Feedback(feedbackString);
	}
	
}
