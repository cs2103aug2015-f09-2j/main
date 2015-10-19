package application;

public class ExitCommand extends Command {

	public ExitCommand(String content) {
		super(content);
	}

	@Override
	public Feedback execute() {
		return new Feedback();
	}

	@Override
	public Feedback undo() {
		return null;
	}

}
