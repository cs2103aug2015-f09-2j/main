package application;

public class ExitCommand extends Command {

	public ExitCommand(Storage store, Parser parse, String content) {
		super(store, parse, content);
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
