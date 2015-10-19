package application;

public class InitializeCommand extends DirectoryCommand {

	public InitializeCommand(Storage store, Parser parse, String content) {
		super(store, parse, content);
	}
	
	@Override
	public Feedback execute() {
		String feedbackString = null;
		if(_content!="") {
			_store.initialize(_content);
			feedbackString = String.format(FEEDBACK_MESSAGE, _content); 
		} else {
			feedbackString = ERROR_NO_CONTENT;
		}
		return new Feedback(feedbackString);
		
	}

}
