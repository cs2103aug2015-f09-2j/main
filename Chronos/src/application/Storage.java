package application;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class Storage {
	
	private final String MESSAGE_REQUEST_FILENAME = "Enter the absolute path of where the agenda will be stored: ";
	private final String MESSAGE_INVALID_FILE = "Invalid File. Please try again";
	private final String MESSAGE_FILE_CREATED = "Your agenda will be stored in \"%1$s\"";
	private final String MESSAGE_FILE_OPENED = "Your agenda stored in \"%1$s\" is loaded";

	protected JSONArray entries_;
	protected JSONArray temp_entries_;
	protected String fileDirectory_;
	protected String temp_fileDirectory_;
	
	protected  Storage() {
		entries_ = new JSONArray();
		getFile();
	}
	
	private void getFile(){
		System.out.println(MESSAGE_REQUEST_FILENAME);
		Scanner sc = new Scanner(System.in);
		sc.close();
		fileDirectory_ = sc.nextLine();
		readFile();	
	}
	
	private void readFile(){
		File file = new File(fileDirectory_);
		try {
			if(!file.createNewFile()){
				//Read in the content of an existing file
				getContent();
				System.out.println(String.format(MESSAGE_FILE_OPENED, fileDirectory_));
			}else{
			System.out.println(String.format(MESSAGE_FILE_CREATED, fileDirectory_));
			}
		} catch (IOException e) {
			System.out.println(MESSAGE_INVALID_FILE);
			getFile();
		}
	}
	
	private void getContent(){
		JSONParser jsonParser = new JSONParser();
		try {
			entries_ = (JSONArray)jsonParser.parse(new FileReader(fileDirectory_));
		} catch (IOException | ParseException e) {
			System.out.println(MESSAGE_INVALID_FILE);
			getFile();
		}
	}
	
	
}