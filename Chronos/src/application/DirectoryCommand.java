package application;

public class DirectoryCommand extends Command {

	//Unique attributes
	private String _previousPath;
		
	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Set Directory to: %1$s";
	
	protected static final String LOG_SAVED_NEW = "Saved new Directory";
	protected static final String LOG_NO_DIRECTORY = "No Directory specified";
	
	//Instructions
	private static final String PATTERN = "cd (directory)";
	private static final String INSTRUCTION_REQUIRED = "Enter the directory you want to save to. (Ex. C:\\Users\\Folder)";
	private static final String REQUIRED_FIELD_DIRECTORY = "(directory)";
	
	public DirectoryCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() {
		String feedbackString = null;
		if (_content != EMPTY) {
			_previousPath = _store.changeDirectory(_content); 
			log.info(LOG_SAVED_NEW);
			feedbackString = String.format(FEEDBACK_MESSAGE, _content); 
		} else {
			log.warning(LOG_NO_DIRECTORY );
			feedbackString = ERROR_NO_CONTENT;
		}
		return new Feedback(feedbackString);
	}

	@Override
	public Feedback undo() {
		return new DirectoryCommand(_previousPath).execute();
	}

	public static Instruction generateInstruction() {
		Instruction commandInstruction = new Instruction();
		commandInstruction.setCommandPattern(PATTERN);
	    commandInstruction.addToInstructions(INSTRUCTION_REQUIRED);
	    commandInstruction.addToRequiredFields(REQUIRED_FIELD_DIRECTORY);
	    return commandInstruction;
	}
	
	

}
