import java.util.*;

public class ModulesList 
{
	/**Declaring & initializing instance variables*/
	private Module[] modulesList; // array (since the length of the list is known) of all modules
	private int roomAmount = TimeTableGUI.ROOMS.length; // number of rooms 
	private int moduleAmount; // number of modules in the file - used for iterations
	private Module module;
	
	/**
	 * Constructor. Initializes an array of modules (size derived from ArrayList) with input from a text file.
	 * @param textInput: ArrayList of strings that were derived from the text file.
	 */
	public ModulesList(ArrayList<String> textInput)
	{
		modulesList = new Module[textInput.size()];
		for (int index = 0; index < textInput.size(); index++)
			modulesList[index] = new Module(textInput.get(index));
		
		moduleAmount = modulesList.length;
	}	
	
	
	/**
	 * Finds module with the specified code.
	 * @param codeName the code of the module.
	 * @return Module object (if found) or null (if not found). 
	 */
	public Module findModule(String codeName)
	{
		for	(int i = 0; i < moduleAmount; i++)	
			if (codeName.equals(modulesList[i].getCode()))
				return modulesList[i];
		return null;
	}
	
	
	/**
	 * Checks whether the class can fit in the specified room.
	 * Finds the capacity of specified room by finding a matching room in a room list.
	 * Since room lists/capacity never changes, gets the appropriate room size using room's index. 
	 * @param roomName room in which the module is to be scheduled.
	 * @param codeName module's code that is to be scheduled in a specified room.
	 * @return boolean: 'true' if room is big enough, 'false' if it is not.
	 */
	public boolean checkRoomSize(String roomName, String codeName)
	{
		int roomCap = 0;
		int requiredCap = findModule(codeName).getSize();
		moduleAmount = modulesList.length;	 

		for (int i = 0; i <roomAmount; i++)	//find room capacity
			if(roomName.equals(TimeTableGUI.ROOMS[i]))
				roomCap = TimeTableGUI.ROOM_SIZE[i];				
		
		if (requiredCap <= roomCap)	// check if students fit in the room		
			return true; // room is big enough
		
		return false; // room is too small
	}
	
	
	/**
	 * Checks if the module already exists in the timetable. If it does, returns it.
	 * @param codeName module to be searched
	 * @return Module object (if found) or null (if not found).
	 */
	public Module moduleExists(String codeName)
	{
		module = findModule(codeName);
		if (module != null && !module.getRoom().equals("?")) // if module has a room, it is scheduled
			return module; // module is on a timetable	
		return null; // module is not on a timetable
	}
	
	
	/**
	 * Checks whether the specified time is available for the module of a particular programme.
	 * Goes through every module, checks what time does it take place. If time is the same as specified,
	 * only then checks whether it is from the same programme - so as to avoid unnecessary calculations.
	 * Exception: same module. This implies movement of the module, rather than scheduling of a new one.
	 * @param code: code of the module that is to be (re)scheduled.
	 * @param proposedTime: new time the module is to be (re)scheduled at.
	 * @return: can the module be scheduled at the specified time?
	 */
	public boolean checkTime(String code, String proposedTime)
	{
		String inputProgramme = code.substring(0, 3); // first 3 letters of the to-be-scheduled module
		for(int index = 0; index < moduleAmount; index++)
		{
			String iterationTime = modulesList[index].getTimeslot(); // get time for each module in the list
			if (proposedTime.equals(iterationTime)) // if the time is the same as one on the module's list  
			{
				// Checks if programme's students have another lecture at that time
				String iterationProgramme = modulesList[index].getProgramme(); // get year & programme
				if (inputProgramme.equals(iterationProgramme) && !code.equals(modulesList[index].getCode()))
					return false; // scheduling is impossible
			}
		}
		return true; // scheduling is possible
	}
	
	
	/**
	 * Checks whether the room is available at a particular time. Ignores the modules with exactly the same code,
	 * as this means the user reschedules the class at the same time. Thus, program simply overwrites it.
	 * @param code: code of the module that is to be (re)scheduled.
	 * @param proposedTime: time of the module that is to be (re)scheduled.
	 * @param proposedRoom: room that the module that is to be (re)scheduled should take place in.
	 * @return: is the room available at the particular time?
	 */
	public boolean roomAvailable(String code, String proposedTime, String proposedRoom)
	{
		for (int index = 0; index < moduleAmount; index++) // iterate through array of modules
			if (proposedTime.equals(modulesList[index].getTimeslot()) // if time is the same
					&& proposedRoom.equals(modulesList[index].getRoom()) // and room is the same
					&& !code.equals(modulesList[index].getCode())) // (exactly the same module does not count)
					return false; // room is unavailable
		return true; // room is available
	}

	
	/**
	 * Sets/changes module's time and room. Should only be used when all checks are true.
	 * @param code module that is to be edited
	 * @param time time to which it is rescheduled
	 * @param room room to which it is rescheduled
	 */
	public void setModuleInfo(String code, String time, String room)
	{
		module = findModule(code);
		module.setTimeSlot(time);
		module.setRoom(room);		
	}

	
	/**
	 * Method to return all the modules. For other modules to derive information from it.
	 * @return: ArrayList containing all the modules.
	 */
	public Module[] returnModules() {return modulesList;}

	
	/**
	 * Prints each module's info in a format that is desirable for text field.
	 */
	public String printModules()
	{
		String output = "";
		for (int index = 0; index < moduleAmount; index++)
			output += modulesList[index].outp();
		return output;
	}
	
	
	/**
	 * Prints each module's info in a format that is desirable for the report.
	 */
	public String printReport()
	{
		String output = "";
		output += String.format("%8s %10s %14s %9s %n", "Code", "Time", "Room", "Size");
		for (int index = 0; index < moduleAmount; index++)
			output += modulesList[index].printEssential();
		return output;
	}
}