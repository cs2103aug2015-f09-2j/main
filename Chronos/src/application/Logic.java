package application;

import java.util.ArrayList;

public class Logic {
	
	private boolean _isExiting = false;	
	
	public String executeUserCommand(String userInput) {
		Command aCommand = new Command(userInput);
		String feedback = aCommand.execute();
		if(aCommand.isExiting()){
			_isExiting = true;
		}
		return feedback;
	}  
	
	public boolean isProgramExiting(){
		return _isExiting;
	}
}
