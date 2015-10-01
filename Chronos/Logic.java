import java.util.Scanner;
import java.util.Collections;

import java.lang.System;

public class Logic {
	Scanner sc = new Scanner(System.in);
	
	private static final String INVALID_MESSAGE = "Invaid Command\n";
	
	
	private void userTypeCommand() {
		String commandString;
		while (true) {
			commandString = sc.next();
			executeUserCommand(commandString);
		}
	}
	
	public void executeUserCommand(String userCommand) {
		String result="";
		if (userCommand.trim().equals("")) {
			println(String.format(INVALID_MESSAGE));
		} else {
			Parser parse = new Parser(userCommand);
			result=executeCommand(parse.command);
			print(result);
		}
	}  
	
	private static String executeCommand(Parser.COMMAND_TYPE command) throws Error {
		String result;
		switch (command) {
			case ADD:  
				//result=addCommand(sc.next());
				//return result;
			case DISPLAY: 
				//result=displayCommand();
				//return result;
			case DELETE:  
				//result=deleteCommand();
				//return result;
			case NOTE: 
				//result=clearCommand();
				//return result;
			case UPDATE:
				//result=sortCommand();
				//return result;
			case SEARCH:
				//result=searchCommand(sc.next().trim());
				//return result;
			case UNDO:
				
				
			case REDO:
				
				
			case CD:
				
				
			case EXIT: 
				System.exit(0);
				//Fallthrough
			default: 
				throw new Error (INVALID_MESSAGE);
				//Fallthrough
		}		
	}
	
	/**
	 * This operation prints the input with newline.
	 * This is done to enable SLAP.
	 * @param input		input is string which needs to be printed to user
	 */
	public static void println(String input) {
		System.out.println(input);
	}
	
	/**
	 * This operation prints the input with no newline.
	 * This is done to enable SLAP.
	 * @param input		input is string which needs to be printed to user
	 */
	public static void print(String input) {
		System.out.print(input);
	}
	
}
