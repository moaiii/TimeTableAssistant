import java.util.Scanner;

public class Module {
	/**Instance variables*/
	public String code, title, timeslot;  
	public String room;
	public int size; 
	
    /**Constructor*/
	public Module (String inputline)
	{    
		Scanner in = new Scanner(inputline);
	    while (in.hasNext())
	    {
			code = in.next();
			title = in.next();
			timeslot = in.next();
			room = in.next();
			size = in.nextInt(); 
	    }
	    in.close();
	}
	
	/**Getters*/
	public String getCode(){return code;}
//	public String getTitle(){return title;} // not used in this version
	public String getTimeslot(){return timeslot;}
	public String getRoom(){return room;}
	public int getSize(){return size;}

	/**
	 * Returns the programme code & year information
	 * @return String containing first three characters of the programme. 
	 */
	public String getProgramme() 
	{
		return getCode().substring(0, 3);
	}
    
    /**Setters*/
	public void setCode(String Code) {code = Code;}
//	public void setTitle(String Title) {title = Title;} // not used in this version
	public void setTimeSlot(String Timeslot) {timeslot = Timeslot;}
	public void setRoom(String Room) {room = Room;}
//	public void setSize(int Size) {size = Size;} // not used in this version
	
	/**
	 * Prints full information of the module.
	 * @return String containing code, title, time slot, room and its size.
	 */
	public String outp()
    {
    	String outp = String.format("%s %s %s %s %d %n", code, title, timeslot, room, size);
    	return outp;
    }
	
	/**
	 * Used for printing out the essential information of the module
	 * @return String that contains essential information about module: code, time slot, room and its size. 
	 */
	public String printEssential()
    {
    	return String.format("%9s %10s %10s %12d %n", code, timeslot, room, size);
    }
}