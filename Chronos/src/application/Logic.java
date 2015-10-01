package application;

import java.util.Collections;

import java.lang.System;

public class Logic {
	
	private static final String MESSAGE_INVALID = "Invalid Command";
	private static final String MESSAGE_TYPE_STUB = "Executing an %1$s command.";
	
	private boolean isExiting = false;
	
	public String executeUserCommand(String userCommand) {
		if (userCommand.trim().equals("")) {
			return MESSAGE_INVALID;
		} else {
			Parser parse = new Parser(userCommand);
			return executeCommand(parse.command);
		}
	}  
	
	private String executeCommand(Parser.COMMAND_TYPE command) throws Error {
		String result = "";
		switch (command) {
			case ADD:  
				//result=addCommand(sc.next());
				result = String.format(MESSAGE_TYPE_STUB, "ADD");
				break;
			case DISPLAY: 
				//result=displayCommand();
				result = String.format(MESSAGE_TYPE_STUB, "DISPLAY");
				break;
			case DELETE:  
				//result=deleteCommand();
				result = String.format(MESSAGE_TYPE_STUB, "DELETE");
				break;
			case NOTE: 
				//result=clearCommand();
				result = String.format(MESSAGE_TYPE_STUB, "DISPLAY");
				break;
			case UPDATE:
				//result=sortCommand();
				result = String.format(MESSAGE_TYPE_STUB, "UPDATE");
				break;
			case SEARCH:
				//result=searchCommand(sc.next().trim());
				result = String.format(MESSAGE_TYPE_STUB, "SEARCH");
				break;
			case UNDO:
				result = String.format(MESSAGE_TYPE_STUB, "UNDO");
				break;
				
			case REDO:
				result = String.format(MESSAGE_TYPE_STUB, "REDO");
				break;
				
			case CD:
				result = String.format(MESSAGE_TYPE_STUB, "CHANGE DIRECTORY");
				break;
				
			case EXIT: 
				isExiting = true;
				break;
				
			default: 
				result = MESSAGE_INVALID;
				break;
		}
		return result;
	}
	
	boolean isProgramExiting(){
		return isExiting;
	}
}
