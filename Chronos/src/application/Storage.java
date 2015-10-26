package application;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class Storage {

	private final String MESSAGE_INVALID_FILE = "Invalid File.";
	private final String MESSAGE_FILE_CREATED = "Your agenda will be stored in \"%1$s\"";
	private final String MESSAGE_FILE_OPENED = "Your agenda stored in \"%1$s\" is loaded";
	private final String MESSAGE_FILE_SWAPPED = "content of %1$s moved to %2$s";
	private final String MESSAGE_ERROR_DELETE = "old file %1$s not deleted";
	private final String MESSAGE_TEMP_SWAPPED = "swapped entries_ with temp_entries";
	//constants
	private static final String PREFS_PATH = "path";
	private static final String PREFS_TASK_COUNT = "task count";
	private static final String PREFS_EVENT_COUNT = "event count";
	private static final String DEFAULT_DIRECTORY = "/chronos_storage.txt";
	private static final String DEFAULT_VALUE = "none";
	private static  int  DEFAULT_TASK_COUNT = 0;
	private static  int  DEFAULT_EVENT_COUNT = 0;
	
	private static Logger log = Logger.getLogger("StorageLog");
	
	public JSONArray entries_;
	private JSONArray temp_entries_;
	private String temp_fileDirectory_;
	private String fileDirectory_;
	private static Preferences _userPrefs;
	private boolean isStoredTemp = false;
	private boolean _isSavePresent = false;
	
	private static Storage _theStorage;
	
	private Storage() { 
		entries_ = new JSONArray();
		_userPrefs = Preferences.userNodeForPackage(this.getClass());
		String savedPath = _userPrefs.get(PREFS_PATH, DEFAULT_VALUE);
		if (!savedPath.equals(DEFAULT_VALUE)) { 
			//There's a path, so open it.
			getFile(savedPath);
			_isSavePresent = true;
		} 
	}
	
	public static Storage getInstance() {
		if (_theStorage == null) {
			_theStorage = new Storage();
		}
		return _theStorage;
	}
	
	void initialize(String path) { 
		//Initialize prefs
		_userPrefs.put(PREFS_PATH, path);
		//check if there's already chronos_storage in it and get the maximum id's
		_userPrefs.putInt(PREFS_TASK_COUNT, DEFAULT_TASK_COUNT);
		_userPrefs.putInt(PREFS_EVENT_COUNT, DEFAULT_EVENT_COUNT);
		getFile(path);
	}
	
	private void getFile(String filePath){
		fileDirectory_ = filePath;
		readFile();	
	}
	
	private void readFile(){
		File file = new File(fileDirectory_ + DEFAULT_DIRECTORY );
		try {
			if(!file.createNewFile()){ 
				//Read in the content of an existing file
				getContent();
				getMaxId();
				log.info(String.format(MESSAGE_FILE_OPENED, fileDirectory_));

			}else{
				log.info(String.format(MESSAGE_FILE_CREATED, fileDirectory_));
			}
		} catch (IOException e) {
			log.warning(MESSAGE_INVALID_FILE);
		}
	}
	
	private void getMaxId(){
		String id;
		int taskId, eventId;
		JSONObject anEntry;
		for (int i = 0; i<entries_.size();i++){
			anEntry = (JSONObject)entries_.get(i);
			id = (String) anEntry.get("id");
			if (id.charAt(0)=='t'){
				taskId = Integer.parseInt(id.substring(1));
				if (taskId>DEFAULT_TASK_COUNT){
					DEFAULT_TASK_COUNT = taskId;
				}
			}else{
				eventId = Integer.parseInt(id.substring(1));
				if (eventId>DEFAULT_EVENT_COUNT){
					DEFAULT_EVENT_COUNT = eventId;
				}
			}
		}
		_userPrefs.putInt(PREFS_TASK_COUNT, DEFAULT_TASK_COUNT);
		_userPrefs.putInt(PREFS_EVENT_COUNT, DEFAULT_EVENT_COUNT);
	}
	
	private void getContent(){
		JSONParser jsonParser = new JSONParser();
		try {
			entries_ = (JSONArray)jsonParser.parse(new FileReader(fileDirectory_+DEFAULT_DIRECTORY ));
		} catch (IOException | ParseException e) {
			log.warning(MESSAGE_INVALID_FILE);
		}
	}
	
	//to be called before an add, delete, update i.e. commands that will
	//change the content of the file
	public void storeTemp(){
		temp_entries_ = (JSONArray) entries_.clone();
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
		log.info(MESSAGE_TEMP_SWAPPED);
	}
	
	public String changeDirectory(String newDirectory){
		temp_fileDirectory_ = fileDirectory_;
		fileDirectory_ = newDirectory;
		writeToFile();
		File oldFile = new File(temp_fileDirectory_+DEFAULT_DIRECTORY );
		//Check if file is deleted
		if (!oldFile.delete()) {
			log.warning(String.format(MESSAGE_ERROR_DELETE, temp_fileDirectory_));
		} else {
			log.info(String.format(MESSAGE_FILE_SWAPPED, temp_fileDirectory_,fileDirectory_));
		}
		return temp_fileDirectory_;
	}
	
	//to be called by undo/redo commands that undo/redo cd commands
	//Do we still need this?
	public void swapFile(){
		changeDirectory(temp_fileDirectory_);
	}
	
	private void writeToFile(){
		try{
			FileWriter file = new FileWriter(fileDirectory_+DEFAULT_DIRECTORY );
			file.write(entries_.toJSONString());
			file.flush();
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public boolean isSavePresent() {
		return _isSavePresent;
	}

	public int getTaskId() { 
		int id = _userPrefs.getInt(PREFS_TASK_COUNT, DEFAULT_TASK_COUNT);
		_userPrefs.putInt(PREFS_TASK_COUNT, ++id);
		return id;
	}

	void decreaseTaskID() {
		int id = _userPrefs.getInt(PREFS_TASK_COUNT, DEFAULT_TASK_COUNT);
		_userPrefs.putInt(PREFS_TASK_COUNT, --id);
	}
	
	public int getEventId() { 
		int id = _userPrefs.getInt(PREFS_EVENT_COUNT, DEFAULT_EVENT_COUNT);
		_userPrefs.putInt(PREFS_EVENT_COUNT, ++id);
		return id;
	}

	void decreaseEventID() {
		int id = _userPrefs.getInt(PREFS_EVENT_COUNT, DEFAULT_EVENT_COUNT);
		_userPrefs.putInt(PREFS_EVENT_COUNT, --id);
	}
}