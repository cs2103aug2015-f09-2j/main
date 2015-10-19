package application;

import java.util.prefs.Preferences;

public class Logic {
	
	private static CommandCreator _commandCreator;
	private static Storage _store;
	private static Parser _parse;
	
	public Logic(){
		_commandCreator = new CommandCreator();
	}
	
	public boolean isSavePresent() {
		_store = Storage.getInstance();
		_parse = Parser.getInstance();
		return _store.isSavePresent();
	}
	
	//happens if there's no save present
	public Feedback setSavePath(String path) { 
		return _commandCreator.executeInitializeCommand(path);
	}
	
	public Feedback executeUserCommand(String userInput) {
		String[] inputs = _parse.parseUserContent(userInput);
		return _commandCreator.createAndExecuteCommand(inputs, _store, _parse);
	}
}
