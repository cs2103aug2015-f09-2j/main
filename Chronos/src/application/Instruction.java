package application;

import java.util.ArrayList;

//@@author A0126223U
public class Instruction {

	private static final int ZERO = 0;
	
	private int _numberOfSteps = ZERO;
	private ArrayList<String> _instructions;
	private ArrayList<String> _requiredFields;
	private String _commandPattern;	
	
	public Instruction() {
		_instructions = new ArrayList<String>();
		_requiredFields = new ArrayList<String>();
	}
	
	public void addToRequiredFields(String fieldString) {
		_requiredFields.add(fieldString);
	}
	
	public void addToInstructions(String instructionString) {
		_instructions.add(instructionString);
	}
	
	public void nextStep() {
		if(_numberOfSteps < _instructions.size()) { 
			_numberOfSteps++;
		} 
	}
	
	public void previousStep() {
		if(_numberOfSteps >= ZERO) {
			_numberOfSteps--;
		}
	}
	
	public boolean isFinished() {
		return _numberOfSteps == _instructions.size();
	}
	
	public String getNextInstruction() {
			return _instructions.get(_numberOfSteps);
	}
	
	public String getNextRequiredField() {
		try {
			return _requiredFields.get(_numberOfSteps);
		} catch (IndexOutOfBoundsException e) {
			return Command.EMPTY;
		}
	}
	
	public void setCommandPattern (String commandPattern) {
		_commandPattern = commandPattern;
	}
	
	public String getCommandPattern () {
		return _commandPattern;
	}

	public boolean hasInstructions() {
		return _instructions.size() > ZERO;
	}
	
	
}
