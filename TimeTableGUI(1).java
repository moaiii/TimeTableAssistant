/**
 * TO DO:
 * 1. Wednesday PM check - is it sufficient to throw an information message?
 * 2. How to print out both report & text file with one method, instead of 2?
 * 3. (Optional) Add scroll pane? JTextArea may need to be formed in a different way then (instead of one string, append one at a time; specify how much rows/columns area will have)  
 * 4. (For consideration) Position buttons better? E.g.:
 * 
 * |timetable&inputs				 |		report			 |
 * |								 |		area			 |
 * |Label BUTTON					 |						 |
 * |Label BUTTON					 |						 |
 * |_________________________________|_______________________|
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class TimeTableGUI extends JFrame {
	private JFrame frame; // creates a frame
	private JLabel[][] grid; // array of labels that represents timetable display
	private JLabel saveLabel, exitLabel;
	private JComboBox<String> timeDropdown, roomDropdown;
	private JButton saveButton, exitButton;
	private JTextField codeField; // where user enters the class code
	private JTextArea report; // holds report
	private ModulesList timetable; // class that will hold/manipulate all the modules specified in the text file
	
	private final int FRAME_WIDTH = 850;
	private final int FRAME_HEIGHT = 600;
	
	// Declaring & initializing class constants
	private final String[] timeslot = {"MonAM","MonPM","TueAM","TuePM", "WedAM","WedPM","ThuAM","ThuPM","FriAM","FriPM"};
	public final static String[] ROOMS = {"A","B","C","D","E","F","G","H"};
	public final static int[] ROOM_SIZE = {100,100,60,60,60,30,30,30};
	
	// Constructor
	public TimeTableGUI() 
	{ 
		/**Establish the main properties of JFrame*/
        setTitle("Timetable Assistant");
	    setSize(FRAME_WIDTH,FRAME_HEIGHT);
		setLocation(350, 250); 
		setResizable(false); // doesn't allow the frame to be resized
		
		/*	Sets JFrame to be like this:
		 *  _________________
		 *	|		|		|
		 *	|		|		|
		 *	|_______|_______| 
		 */
		setLayout(new GridLayout(1,2));
	
		
		/**Panel that takes care of the timetable widgets is created & configured*/
		JPanel timetablePanel = new JPanel(); // timetable related widgets
		timetablePanel.setLayout(new FlowLayout()); // adds one after another, in a row
													// if there's enough horizontal space
		// Add the timetable
		JPanel timetableGrid = gridLay();
		timetablePanel.add(timetableGrid);
		
		// Add the input fields
		JPanel inputFields = createInputs();
		timetablePanel.add(inputFields);
		
		// Add the save & exit buttons
		JPanel buttons = createButtons();
		timetablePanel.add(buttons);
		
		// Add timetable panel to the left side of the JFrame (GridLayout adds from left to right)
		add(timetablePanel); 
		
		// Read the text file, construct array of modules, Timetable object.
		// Must be called after creation of grids/etc. Otherwise, NullPointerException will be thrown.
		initTimetable();
	
		/**Panel taking care of report is created & configured*/
		JPanel report = report(); // creates a modules' report display
		add(report); // adds to the right side of the JFrame (2nd column)
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // stops program from running in the background
		setVisible(true);
	}
	
	
	/**
	 * Reads the given text file, puts all the lines in an array list. This is passed to the Timetable object,
	 * which in turn creates a Module object for each string in the array list. That is, information in the 
	 * text file determines the values of Module object. List is used to make account for uncertain number of lines.
	 * At the end of the method, timetable grid is updated with the information from the file. 
	 */
	private void initTimetable()
	{
		// Store strings from text file. Since size may not be 24, array list is useful.
		ArrayList<String> modulesList = new ArrayList<String>();
		String filePath = "ModulesIn.txt";
		
		try 
		{
			FileReader textFile = new FileReader(filePath);
			Scanner scan = new Scanner(textFile);
			while (scan.hasNextLine())	// while there're more lines in the file, add it to the list
				modulesList.add(scan.nextLine()); 	// adds to the list of strings that represent modules
			scan.close();							// which are later parsed for relevant info
			textFile.close(); 
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "No modules list found.", "I/O Exception", JOptionPane.ERROR_MESSAGE);
			System.exit(0); // if no file is found, exit the program - it would do anything anyway
		}
		
		timetable = new ModulesList(modulesList); // initialise timetable object with ArrayList of modules
		fillTimetable(); // fill in timetable with information from the modules that already exist
	}
	
	
	/**
	 * Fills in the timetable grid frame with information of modules that are already set up.
	 * Goes through each module to check if it has valid information. 
	 * If it does, sets text in appropriate grid.
	 */
	private void fillTimetable()
	{
		Module[] modules = timetable.returnModules();
		for(int index = 0; index < modules.length; index++)
		{
			int roomIndex = findRoomIndex(modules[index].getRoom());
			int timeIndex = findTimeIndex(modules[index].getTimeslot());
			if (timeIndex != -1 && roomIndex != -1)
			{
				String moduleCode = modules[index].getCode();	// get module code
				grid[timeIndex+2][roomIndex+1].setText(moduleCode); // print it on the grid
			}
		}
	}
	
	
	/**
	 * Derives x position on the grid by finding a specified room in the array of all available rooms.   
	 * @param room which room is to be found
	 * @return either index of the room in the array or an indication that it was not found (-1). 
	 */
	private int findRoomIndex(String room)
	{
		for (int index = 0; index < ROOMS.length; index++)
			if (room.equals(ROOMS[index]))
				return index;
		return -1; // no matching room
	}
	
	
	/**
	 * Derives y position on the grid by finding a specified time slot in the array of all available times.
	 * @param time which time slot is to be found
	 * @return either index of the time slot in the array or an indication that it was not found (-1). 
	 */
	private int findTimeIndex(String time)
	{
		for (int index = 0; index < timeslot.length; index++)
			if (time.equals(timeslot[index]))
				return index;
		return -1; // no matching time
	}
	
	
    /**
     * Determines what the 'Save' button does. Checks whether:
     * 1) anything was entered in the code text field 
     * 2) such module exists
     * 2) the room is big enough
     * 3) a module from the same programme is happening at the same time
     * 4) a module from a different programme is scheduled at the same time & room
     * 5) WedPM is selected
     * If all of these return false (with exception of WedPM), module's info is modified, grid updated.
     * If the module is already scheduled at some time, clears that time slot first.    
     */
    private class SaveListener implements ActionListener 
    {
    	public void actionPerformed(ActionEvent event) 
    	{
    		String code = codeField.getText(); // code that was entered
    		String room = (String) roomDropdown.getSelectedItem(); // room selected
    		String time = (String) timeDropdown.getSelectedItem(); // time selected
    		
    		
    		if (code.trim().equals(""))
    			JOptionPane.showMessageDialog(frame, "No input provided.");
    		else if (timetable.findModule(code) == null) 
    		{
    			JOptionPane.showMessageDialog(null, "Such module does not exist.", 
    					"Module does not exist", JOptionPane.ERROR_MESSAGE);
    			codeField.setText("");   			
    		}
    		else if (!timetable.checkRoomSize(room, code))	// is room big enough?
    			JOptionPane.showMessageDialog(null, "Selected room is too small for this module.", 
    					"Room is too small", JOptionPane.ERROR_MESSAGE);
    		else if (!timetable.checkTime(code, time)) // is the timeslot free for particular programme?
    			JOptionPane.showMessageDialog(null, "Another module from the same programme is happening at this time.", 
    					"Timetable clash", JOptionPane.ERROR_MESSAGE);
    		else if (!timetable.roomAvailable(code, time, room)) // any other modules in this room at this time?
    			JOptionPane.showMessageDialog(null, "Room is already taken by another module.", 
    					"Timetable clash", JOptionPane.ERROR_MESSAGE);
    		else // time slot is free and does not clash with other modules  
    		{
    			// notification for the user
    			if (time.equals("WedPM"))
    				JOptionPane.showMessageDialog(null, "You have scheduled a module on Wednesday afternoon. "
	    					+ "\nPlease keep in mind that ideally it is reserved for sports.", 
	    					"Wednesday afternoon selected", JOptionPane.INFORMATION_MESSAGE);
    			
    			Module module = timetable.moduleExists(code); // get the module that is to be scheduled
    			if (module != null) // check if it already had time scheduled and if it did, clear the timetable grid
    				grid[findTimeIndex(module.getTimeslot()) + 2][findRoomIndex(module.getRoom()) + 1].setText("");	    			
    			
    			timetable.setModuleInfo(code, time, room); // set new room and time for the module
	    		grid[findTimeIndex(time) + 2][findRoomIndex(room) + 1].setText(code); // update timetable grid
	    		report.setText(timetable.printReport()); // update report
	    		writeFile(); // update file
	    		
	    		// reset the text field to default option  
	    		codeField.setText("");
	    		
	    		// Drop down menus are not reset for usability: in case a user wants to fill in the particular room.
	    		// To reset the fields, uncomment the following two lines: 
	    		// timeDropdown.setSelectedIndex(0);
	    		// roomDropdown.setSelectedIndex(0);
    		}
    	}
    }


    /**
     * Saves the current module information and exits the program 
     */
    private class ExitListener implements ActionListener 
    {
    	public void actionPerformed(ActionEvent event) 
    	{
    		writeFile();
   			System.exit(0);
    	}
    }
    
    
    /**
     * Writes the current information int he modules to text file.
     */
    private void writeFile()
    {
		String outputPath = "ModulesOut.txt";
		try
		{
			FileWriter output = new FileWriter(outputPath);
			output.write(timetable.printModules());
			output.close();
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "Error writing the output file.", "I/O Exception", JOptionPane.ERROR_MESSAGE);
		}
    }
    
    /**
     * Forms a report. 
     * @return panel object, containing formed report
     */
    private JPanel report() 
    {
    	report = new JTextArea();
		Border reportBorder = BorderFactory.createLineBorder(Color.BLACK);
		report.setBorder(BorderFactory.createCompoundBorder(reportBorder,
				BorderFactory.createEmptyBorder(5, 5, 5, 5))); // create a border around the report
		report.setFont(new Font("Courier", Font.PLAIN, 12)); // set font for letters to be of same size
		report.setText(timetable.printReport()); // populate the text area with info from modules
		report.setPreferredSize(new Dimension(FRAME_WIDTH/2-50, FRAME_HEIGHT-50)); // ~1/2 of frame width, ~ full height
		report.setEditable(false); // user should not be able to edit the text area
		    	   
		JPanel reportPanel = new JPanel();
		reportPanel.add(report);
		return reportPanel;
	}

    
    /**
     * Forms a timetable display depending on how many time slots & rooms there are.
     * @return fully formed JPanel object, containing the timetable display
     */
	private JPanel gridLay()
	{
		int height = timeslot.length + 2; // 2 upper rows (rooms + sizes) must be accommodated
		int width = ROOMS.length + 1; // leftmost column is for time slots
		
		JPanel gridPanel = new JPanel(); // main panel inside which everything is placed
		gridPanel.setLayout(new GridLayout(height,width)); // grid that represents timetable
		grid = new JLabel[height][width]; // allocate the size of grid
		
		// Build the grid matrix with reference numbers
		for(int yAxis = 0; yAxis < height; yAxis++) 
			for(int xAxis = 0; xAxis < width; xAxis++)
			{
				grid[yAxis][xAxis] = new JLabel(); // creates new label
				gridPanel.add(grid[yAxis][xAxis]); // adds label to grid
			}
          
		// Set the cell borders
		Border border = LineBorder.createGrayLineBorder();
        for(int i = 2; i <= 11; i++)
        	for(int j = 1; j <= 8; j++)
        		grid[i][j].setBorder(border);
           
        // Set left hand corner blank cells (formatting)
        grid[0][0].setText(" ");
        grid[1][0].setText(" ");
        
        // Column A day times
        for(int i = 2; i <= 11; i++) // i = 2 as first two rows need to be blank (room & its size rows)
        	grid[i][0].setText(timeslot[i - 2]); 
        
        // Row 1 class characters
        for(int i = 1; i <= 8; i++) // i = 1 as first column needs to be blank (time slot column) 
        	grid[0][i].setText(ROOMS[i - 1]); 

        // Row 2 class room sizes
        for(int i = 1; i <= 8; i++) // i = 1 as first column needs to be blank (time slot column) 
        	grid[1][i].setText(Integer.toString(ROOM_SIZE[i - 1]));
           
        return gridPanel;
	}
       
	
	/**
	 * Creates a panel that contains input fields.
	 * @return JPanel with input fields 
	 */
	private JPanel createInputs() 
	{
		/**Create JPanel (module) that will contain input fields*/
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout()); // widgets are placed one after the other
		
		/**Create class code input box*/
		JLabel codeLabel = new JLabel("Code: "); // label describing text field
		panel.add(codeLabel);
		codeField = new JTextField(6);
		panel.add(codeField);
		
		/**Create drop down list for time slots*/
		JLabel timeLabel = new JLabel("Select time: "); // label describing time dropdown list
		panel.add(timeLabel);
		timeDropdown = new JComboBox<String>();
		
		// Populate the drop down list with the daytimes 
		for(int i=0; i<timeslot.length; i++) 
			timeDropdown.addItem(timeslot[i]);
		panel.add(timeDropdown);
		
		/**Create drop down list for rooms*/
		JLabel roomLabel = new JLabel("Select room: "); //label for room drop down list
		panel.add(roomLabel);
    	roomDropdown = new JComboBox<String>();
		
    	// Populate the drop down list with selections
		for(int i=0; i<ROOMS.length; i++) 
			roomDropdown.addItem(ROOMS[i]);
		panel.add(roomDropdown);
		
		// Return the fully formed panel
		return panel;
	}
      
	
	/**
	 * Creates a panel that contains buttons to save and exit the program.
	 * @return JPanel that contains save & exit buttons.
	 */
    private JPanel createButtons()
    {
    	JPanel panel = new JPanel();
    	
    	/**Create save button, add action listener to it*/
    	saveLabel = new JLabel("Save changes");
    	panel.add(saveLabel);
    	saveButton = new JButton("Save");
    	panel.add(saveButton);
    	ActionListener saveListener = new SaveListener();
    	saveButton.addActionListener(saveListener);
    	   
    	/**Create exit button, add action listener to it*/
    	exitLabel = new JLabel("Save and exit");
    	panel.add(exitLabel);
    	exitButton = new JButton("Exit");
    	panel.add(exitButton);
    	ActionListener exitListener = new ExitListener();
    	exitButton.addActionListener(exitListener);
    	   
    	return panel;
    }
}