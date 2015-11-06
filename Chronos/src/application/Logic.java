package application;


public class Logic {
	
	private static CommandCreator _commandCreator;
	private static Storage _store;
	private static Parser _parse;
	private static Logic _theLogic = null;
	private static final String ALARM_OFF = "off";
	private static final String ALARM_OFF_COMMAND = "alarm %1$s, off";
	private static final int INDEX_DEFAULT = 0;
	private static int _commandIndex;
	
	//@@author A0126223U
	private Logic(){
		_commandCreator = new CommandCreator();
	}
	
	//@@author A0126223U
	public static Logic getInstance() {
		if (_theLogic == null) {
			_theLogic = new Logic();
			_commandIndex = INDEX_DEFAULT;
		}
		return _theLogic;
	}
	
	//@@author A0126223U
	public boolean isSavePresent() {
		_store = Storage.getInstance();
		_parse = Parser.getInstance();
		return _store.isSavePresent();
	}
	
	//@@author A0126223U
	//happens if there's no save present
	public Feedback setSavePath(String path) { 
		return _commandCreator.executeInitializeCommand(path);
	}
	
	//@@author A0126223U
	public Feedback executeUserCommand(String userInput) {
		String[] inputs = _parse.parseUserContent(userInput);
		return _commandCreator.createAndExecuteCommand(inputs);
	}
	
	//@@author A0126223U
	public boolean checkForClashes(Task taskToCheck) {
		return _parse.checkForClashes(taskToCheck, _store.entries_);
	}
	
	//@@author A0126223U
	public static Instruction getCommandInstruction(String commandString) {
		return CommandCreator.generateInstructions(commandString);
	}
	

	public void switchOffAlarm(Task aTask){
		aTask.setAlarm(ALARM_OFF);
		String id = aTask.getId();
		executeUserCommand(String.format(ALARM_OFF_COMMAND, id));
	}
	
	//@@author A0126223U
	public static String getPreviouslyTypedCommand() {
		_commandIndex++;
		if(CommandCreator.isWithinRange(_commandIndex)) {
			return CommandCreator.getTypedCommandString(_commandIndex);
		} else {
			_commandIndex--;
			return CommandCreator.getTypedCommandString(_commandIndex);
		}
	}

	//@@author A0126223U
	public static String getNextTypedCommand() {
		_commandIndex--;
		if(CommandCreator.isWithinRange(_commandIndex)) {
			return CommandCreator.getTypedCommandString(_commandIndex);
		} else {
			_commandIndex++;
			return CommandCreator.getTypedCommandString(_commandIndex);
		}
	}
	
	//@@author A0126223U
	public Feedback updateDisplay() {
		return _commandCreator.executeGUIDisplayCommand();
	}
}
