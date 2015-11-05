package application;

public class Logic {
	
	private static CommandCreator _commandCreator;
	private static Storage _store;
	private static Parser _parse;
	private static Logic _theLogic = null;
	private static final String ALARM_OFF = "off";
	private static final String ALARM_OFF_COMMAND = "alarm %1$s, off";
	
	private Logic(){
		_commandCreator = new CommandCreator();
	}
	
	public static Logic getInstance() {
		if (_theLogic == null) {
			_theLogic = new Logic();
		}
		return _theLogic;
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
		return _commandCreator.createAndExecuteCommand(inputs);
	}
	
	public boolean checkForClashes(Task taskToCheck) {
		return _parse.checkForClashes(taskToCheck, _store.entries_);
	}

	public static Instruction getCommandInstruction(String commandString) {
		return CommandCreator.generateInstructions(commandString);
	}
	
	public void switchOffAlarm(Task aTask){
		aTask.setAlarm(ALARM_OFF);
		String id = aTask.getId();
		executeUserCommand(String.format(ALARM_OFF_COMMAND, id));
	}
}
