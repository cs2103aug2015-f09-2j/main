package application;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.json.simple.JSONObject;

public class Logic {
	
	private static final String MESSAGE_SET_PATH = "Setting save path to: %1$s";

	//contants
	private static final String PREFS_PATH = "path";
	private static final String PREFS_COUNT = "count";
	
	private static final String DEFAULT_VALUE = "none";
	private static final int  DEFAULT_COUNT = 0;
	
	private boolean _isExiting = false;
	private boolean _isInSummaryView = true;
	private static Storage _store;
	private static Parser _parse;
	private static Preferences _userPrefs;
	
	public Logic(){
		_userPrefs = Preferences.userNodeForPackage(this.getClass());
	}
	
	public Feedback executeUserCommand(String userInput) {
		Command aCommand = new Command(userInput, _store, _parse);
		Feedback feedback = aCommand.execute();
		_isInSummaryView = aCommand.isInSummaryView();
		//Check for boolean values
		if (aCommand.isExiting()) {
			_isExiting = true;
		}
		return feedback;
	}

	public boolean isProgramExiting() {
		return _isExiting;
	}
	
	public boolean isSavePresent() {
		String savedPath = _userPrefs.get(PREFS_PATH, DEFAULT_VALUE);
		if (savedPath.equals(DEFAULT_VALUE)) {
			return false;
		} else { //there is a saved path
			_store = new Storage(savedPath);
			_parse = new Parser(_userPrefs);
			return true;
		}
	}

	public Feedback setSavePath(String path) {
		_userPrefs.put(PREFS_PATH, path);
		_userPrefs.putInt(PREFS_COUNT, DEFAULT_COUNT);
		_store = new Storage(path);
		_parse = new Parser(_userPrefs);
		String feedbackString = String.format(MESSAGE_SET_PATH, _userPrefs.get(PREFS_PATH, DEFAULT_VALUE));
		return new Feedback(feedbackString);
	}
	
	/*
	public ArrayList<Task> getTasks(){
		ArrayList<Task> entries = new ArrayList<Task>();
		JSONObject entry;
		for (int i = 0; i<_store.entries_.size(); i++){
			entry = (JSONObject)_store.entries_.get(i);
			entries.add(new Task(EMPTY_STRING,(String)entry.get("content"), "", "", ""));
		}
		return entries;
	}
	*/
	
	public boolean isInSummaryView() {
		return _isInSummaryView;
	}
}
