package application;

public class DirectoryCommand extends Command {

	//Unique attributes
	private String _previousPath;
		
	//Constant Strings
	protected static final String FEEDBACK_MESSAGE =  "Changing Directory to: %1$s";
	
	public DirectoryCommand(Storage store, Parser parse, String content) {
		super(store, parse, content);
	}

	@Override
	public Feedback execute() {
		String feedbackString = null;
		if(_content!="") {
			//TODO: merge two changeDirectory methods
			_previousPath = _parse.changeDirectory(_content);
			_store.changeDirectory(_content);
			log.info("replacing old directory with new directory");
			feedbackString = String.format(FEEDBACK_MESSAGE, _content); 
		} else {
			log.warning("No new directory");
			feedbackString = ERROR_NO_CONTENT;
		}
		return new Feedback(feedbackString);
	}

	@Override
	public Feedback undo() {
		return new DirectoryCommand(_store,_parse,_previousPath).execute();
	}
	
	

}
