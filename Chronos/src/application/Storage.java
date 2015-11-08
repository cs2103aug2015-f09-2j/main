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

//@@author A0131496A
public class Storage {

	private final String MESSAGE_INVALID_FILE = "Invalid File.";
	private final String MESSAGE_FILE_CREATED = "Your agenda will be stored in \"%1$s\"";
	private final String MESSAGE_FILE_OPENED = "Your agenda stored in \"%1$s\" is loaded";
	private final String MESSAGE_FILE_SWAPPED = "content of %1$s moved to %2$s";
	private final String MESSAGE_ERROR_DELETE = "old file %1$s not deleted";
	private final String MESSAGE_TEMP_SWAPPED = "swapped entries_ with temp_entries";
	private static final String PREFS_PATH = "path";
	private static final String PREFS_TASK_COUNT = "task count";
	private static final String PREFS_EVENT_COUNT = "event count";
	private static final String DEFAULT_DIRECTORY = "/chronos_storage.txt";
	private static final String DEFAULT_PATH = "none";
	private static final String[] ESSENTIAL_FIELDS = {"id","due date","description","priority","category","complete","alarm"};
	private static final char TASK_PREFIX = 't';
	private static final char EVENT_PREFIX = 'e';
	private static final int ERROR_TYPE_ID = 0;
	private static  int  DEFAULT_TASK_COUNT = 0;
	private static  int  DEFAULT_EVENT_COUNT = 0;
	
	private static Logger log = Logger.getLogger("StorageLog");
	
	public JSONArray entries_;
	private JSONArray tempEntries_;
	private String tempFileDirectory_;
	private String fileDirectory_;
	private static Preferences userPrefs_;
	private boolean isStoredTemp_ = false;
	private boolean isSavePresent_ = false;
	
	private static Storage theStorage_;
	
	private Storage() { 
		entries_ = new JSONArray();
		userPrefs_ = Preferences.userNodeForPackage(this.getClass());
		String savedPath = userPrefs_.get(PREFS_PATH, DEFAULT_PATH);
		if (!savedPath.equals(DEFAULT_PATH)) { 
			//If user has specified where to save, just open it
			getFile(savedPath);
			isSavePresent_ = true;
		} 
	}
	
	public static Storage getInstance() {
		if (theStorage_ == null) {
			theStorage_ = new Storage();
		}
		return theStorage_;
	}
	
	void initialize(String path) { 
		//store user specified directory into user preference
		userPrefs_.put(PREFS_PATH, path);
		//check if there's already chronos_storage in it and get the maximum id's
		userPrefs_.putInt(PREFS_TASK_COUNT, DEFAULT_TASK_COUNT);
		userPrefs_.putInt(PREFS_EVENT_COUNT, DEFAULT_EVENT_COUNT);
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
				getContent(fileDirectory_);
				checkValidFormat();
				getMaxId();
				log.info(String.format(MESSAGE_FILE_OPENED, fileDirectory_));
			}else{
				log.info(String.format(MESSAGE_FILE_CREATED, fileDirectory_));
			}
		} catch (IOException | ParseException e) {
			log.warning(MESSAGE_INVALID_FILE);
		}
	}
	
	//throws exception if the JSON format is incorrect i.e. does not contain the essential fields
	public void checkValidFormat() throws ParseException{
		JSONObject anEntry;
		for (int i = 0; i<entries_.size();i++){
			anEntry = (JSONObject)entries_.get(i);
			String key;
			//"id" field will be tested in getMaxId method, so there is no need to check it here
			for(int j = 1; j<ESSENTIAL_FIELDS.length; j++){
				key = ESSENTIAL_FIELDS[j];
				if(anEntry.get(key)==null){
					throw new ParseException(j);
				}
			}			
		}
	}
	
	private void getMaxId() throws ParseException{
		String idString;
		int maxTaskId = 0, maxEventId = 0;
		JSONObject anEntry;
		for (int i = 0; i<entries_.size();i++){
			anEntry = (JSONObject)entries_.get(i);
			idString = (String) anEntry.get("id");
			switch (idString.charAt(0)){
				case TASK_PREFIX:
					maxTaskId = compareId(idString, maxTaskId);
					break;
				case EVENT_PREFIX:
					maxEventId = compareId(idString, maxEventId);
					break;
				default:
					throw new ParseException(ERROR_TYPE_ID);
			}
		}
		DEFAULT_TASK_COUNT = maxTaskId;
		DEFAULT_EVENT_COUNT = maxEventId;
		userPrefs_.putInt(PREFS_TASK_COUNT, DEFAULT_TASK_COUNT);
		userPrefs_.putInt(PREFS_EVENT_COUNT, DEFAULT_EVENT_COUNT);
	}

	private int compareId(String idString, int maxId) {
		int id;
		id = Integer.parseInt(idString.substring(1));
		if (id>maxId){
			maxId = id;
		}
		return maxId;
	}
	
	//throws exception if the file is not in JSON format
	public void getContent(String fileDirectory) throws  ParseException, IOException{
		JSONParser jsonParser = new JSONParser();
		entries_ = (JSONArray)jsonParser.parse(new FileReader(fileDirectory+DEFAULT_DIRECTORY ));
	}
	
	//to be called before an add, delete, update i.e. commands that will change the content of the file
	public void storeTemp(){
		tempEntries_ = (JSONArray) entries_.clone();
		isStoredTemp_ = true;	
	}
	
	//to be called after changes to the content of the file
	public void storeChanges(){
		//the entries have to be stored before making changes
		assert isStoredTemp_ == true;
		isStoredTemp_ = false;
		writeToFile();
	}
	
	//to be called by undo/redo commands that undo/redo add/delete/update commands
	public void swapTemp(){
		JSONArray placeHolder = entries_;
		entries_ = tempEntries_;
		tempEntries_ = placeHolder;
		writeToFile();	
		log.info(MESSAGE_TEMP_SWAPPED);
	}
	
	public String changeDirectory(String newDirectory){
		tempFileDirectory_ = fileDirectory_;
		fileDirectory_ = newDirectory;
		writeToFile();
		File oldFile = new File(tempFileDirectory_+DEFAULT_DIRECTORY );
		//Check if file is deleted
		if (!oldFile.delete()) {
			log.warning(String.format(MESSAGE_ERROR_DELETE, tempFileDirectory_));
		} else {
			log.info(String.format(MESSAGE_FILE_SWAPPED, tempFileDirectory_,fileDirectory_));
		}
		//update user preference
		userPrefs_.put(PREFS_PATH, newDirectory);
		return tempFileDirectory_;
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

	public boolean checkIsSavePresent() {
		return isSavePresent_;
	}

	public int getTaskId() { 
		int id = userPrefs_.getInt(PREFS_TASK_COUNT, DEFAULT_TASK_COUNT);
		userPrefs_.putInt(PREFS_TASK_COUNT, ++id);
		return id;
	}

	void decreaseTaskID() {
		int id = userPrefs_.getInt(PREFS_TASK_COUNT, DEFAULT_TASK_COUNT);
		userPrefs_.putInt(PREFS_TASK_COUNT, --id);
	}
	
	public int getEventId() { 
		int id = userPrefs_.getInt(PREFS_EVENT_COUNT, DEFAULT_EVENT_COUNT);
		userPrefs_.putInt(PREFS_EVENT_COUNT, ++id);
		return id;
	}

	void decreaseEventID() {
		int id = userPrefs_.getInt(PREFS_EVENT_COUNT, DEFAULT_EVENT_COUNT);
		userPrefs_.putInt(PREFS_EVENT_COUNT, --id);
	}
}