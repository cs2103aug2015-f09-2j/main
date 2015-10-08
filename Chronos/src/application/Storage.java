package application;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class Storage {

	private final String MESSAGE_REQUEST_FILENAME = "Enter the absolute path of where the agenda will be stored: ";
	private final String MESSAGE_INVALID_FILE = "Invalid File. Please try again";
	private final String MESSAGE_FILE_CREATED = "Your agenda will be stored in \"%1$s\"";
	private final String MESSAGE_FILE_OPENED = "Your agenda stored in \"%1$s\" is loaded";
	
	private static Logger log = Logger.getLogger("StorageLog");
	
	public JSONArray entries_;
	private JSONArray temp_entries_;
	private String temp_fileDirectory_;
	private String fileDirectory_;
	private boolean isStoredTemp = false;
	
	public  Storage(String filePath) {
		entries_ = new JSONArray();
		getFile(filePath);
	}
	
	private void getFile(String filePath){
		fileDirectory_ = filePath + "\\chronos_storage.txt";
		readFile();	
	}
	
	private void readFile(){
		File file = new File(fileDirectory_);
		try {
			if(!file.createNewFile()){ //Read in the content of an existing file
				getContent();
				log.info(String.format("read file %1$s", fileDirectory_));
				//System.out.println(String.format(MESSAGE_FILE_OPENED, fileDirectory_));
			}else{
				log.info(String.format("created file %1$s", fileDirectory_));
				//System.out.println(String.format(MESSAGE_FILE_CREATED, fileDirectory_));
			}
		} catch (IOException e) {
			log.warning("cannot get file");
			//System.out.println(MESSAGE_INVALID_FILE);
			//getFile();
		}
	}
	
	private void getContent(){
		JSONParser jsonParser = new JSONParser();
		try {
			entries_ = (JSONArray)jsonParser.parse(new FileReader(fileDirectory_));
		} catch (IOException | ParseException e) {
			//System.out.println(MESSAGE_INVALID_FILE);
			//getFile();
		}
	}
	
	//to be called before an add, delete, update i.e. commands that will
	//change the content of the file
	public void storeTemp(){
		temp_entries_ = entries_;
		isStoredTemp = true;
	}
	
	//to be called after changes to the content of the file
	public void storeChanges(){
		//the entries have to be stored before making changes
		assert isStoredTemp == true;
		isStoredTemp = false;
		writeToFile();
	}
	
	//to be called by undo/redo commands that undo/redo add/delete/update commands
	public void swapTemp(){
		JSONArray placeHolder = entries_;
		entries_ = temp_entries_;
		temp_entries_ = placeHolder;
		writeToFile();	
		log.info("swapped entries_ with temp_entries");
	}
	
	public void changeDirectory(String newDirectory){
		temp_fileDirectory_ = fileDirectory_;
		fileDirectory_ = newDirectory + "\\chronos_storage.txt";
		writeToFile();
		File oldFile = new File(temp_fileDirectory_);
		if(!oldFile.delete()){
			log.warning(String.format("old file %1$s not deleted", temp_fileDirectory_));
		}
		log.info(String.format("content of %1$s moved to %2$s", temp_fileDirectory_,fileDirectory_));
	}
	
	//to be called by undo/redo commands that undo/redo cd commands
	public void swapFile(){
		changeDirectory(temp_fileDirectory_);
	}
	
	private void writeToFile(){
		try{
			FileWriter file = new FileWriter(fileDirectory_);
			file.write(entries_.toJSONString());
			file.flush();
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}