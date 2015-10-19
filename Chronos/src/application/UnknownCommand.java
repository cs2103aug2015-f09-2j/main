package application;

public class UnknownCommand extends Command {

	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Invalid Command";
	
	public UnknownCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() {
		return new Feedback(FEEDBACK_MESSAGE);
	}

	@Override
	public Feedback undo() {
		return null;
	}

}
