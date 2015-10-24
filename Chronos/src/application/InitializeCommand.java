package application;

public class InitializeCommand extends DirectoryCommand {

	public InitializeCommand(String content) {
		super(content);
	}
	
	@Override
	public Feedback execute() {
		String feedbackString = null;
		if( _content != EMPTY) {
			_store.initialize(_content);
			feedbackString = String.format(FEEDBACK_MESSAGE, _content); 
		} else {
			feedbackString = ERROR_NO_CONTENT;
		}
		return new Feedback(feedbackString);
		
	}

}
