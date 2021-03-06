package airlinesystem;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Passenger {
    protected PrintWriter ticketWriter;
    protected Socket socket;
    protected ObjectOutputStream outputStream;
    protected ObjectInputStream inputStream;
    public ArrayList<Flight> receivedFlights;


    /**
     * The Passenger class
     * @param serverName The host name/IP address
     * @param portNumber The port number
     */
    public Passenger(String serverName, int portNumber) {
        try {
            socket = new Socket(serverName, portNumber);
            System.out.println("Trying to connect to output");
			outputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Trying to connect to Input");
            inputStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
        	System.out.println("Something went wrong.");
            System.err.println(e.getStackTrace());
        }
    }

    /**
     *Function to search for flights
     * @param param The paramater being searched for
     * @param key The key used for the search
     * @return The object searched for
     */
    public String searchFlights(String param, String key) {
        String response = "";
        String format = checkFormatFlightSearch(param, key);
        if(format.contains("error"))
        	return format;
        try {
            outputStream.writeObject((String)"SEARCHFLIGHT_" + param + "_" + key);
            receivedFlights = (ArrayList<Flight>)inputStream.readObject();
            if(receivedFlights.isEmpty())
            {
            	 return "The flight you were looking for could not be found";
            }
            else
            	return "GOOD";
        }
        catch (IOException e) {
            System.err.println(e.getStackTrace());
            return "An IO Exception occured...";
        }
        catch (ClassNotFoundException e) {
            System.err.println(e.getStackTrace());
            return "A ClassNotFound Exception occured";
        }
    }

    /**
     * The function to get the list of flights
     * @return "GOOD" when flights exist, No flights could be found when no flights are found
     */
    public String getFlights() {
        String response = "";
        try {
            outputStream.writeObject((String)"GETFLIGHTS_");
            response = (String)inputStream.readObject();
            if (response.equals("GOOD")) {
                receivedFlights = (ArrayList<Flight>)inputStream.readObject();
                return "GOOD";
            }
            else {
                return "No flights could be found";
            }
        }
        catch (IOException e) {
            System.err.println(e.getStackTrace());
            return "An IO Exception occured...";
        }
        catch (ClassNotFoundException e) {
            System.err.println(e.getStackTrace());
            return "A ClassNotFound Exception occured";
        }
    }

    /**
     * Function used to book a ticket
     * @param firstName The passenger's first name
     * @param lastName The passenger's last name
     * @param flightNumber The flight number of the flight they wish to book
     * @return
     */
    public String bookTicket(String firstName, String lastName, String flightNumber) {
        String response = "";
        String format = checkFormatTicketAdd(firstName, lastName);
        if(format.contains("ERROR"))
        	return format;
        try {
            outputStream.writeObject((String)"BOOK_" + flightNumber + "_" + firstName + "_" + lastName);
            response = (String)inputStream.readObject();
            if (response.equals("GOOD")) {
              //  inputStream = new ObjectInputStream(socket.getInputStream());
            	Thread.sleep(10);
                Ticket printedTicket = (Ticket)inputStream.readObject();
                printTicket(printedTicket);
                return "Ticket Successfully Booked!";
            }
            else {
                return "Error booking ticket, please refresh to ensure seats are available";
            }
        }
        catch (IOException e) {
            System.err.println(e.getStackTrace());
            return "An IO Exception occured...";
        }
        catch (ClassNotFoundException e) {
            System.err.println(e.getStackTrace());
            return "A ClassNotFound Exception occured";
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        	return "An exception occured";
        }
    }

    /**
     * Function used to print the ticket
     * @param ticketA The ticket used to be printed
     */
    public void printTicket(Ticket ticketA) {

        try {
            ticketWriter = new PrintWriter("Ticket" + ticketA.getTicketId() + ".txt");
            ticketWriter.println("Your Ticket Information");
            ticketWriter.println("Ticket #: " + ticketA.getFlightId()); 
            ticketWriter.println("First Name: " + ticketA.getFirstName());
            ticketWriter.println("Last Name: " + ticketA.getLastName());
            ticketWriter.println("Flight #: " + ticketA.getFlightId());
			ticketWriter.println("Starting Destination: " + ticketA.getSrc());
			ticketWriter.println("Final Destination: " + ticketA.getDest());
			ticketWriter.println("Date of Departure: " + ticketA.getDate());
			ticketWriter.println("Time of Departure: " + ticketA.getTime());
			ticketWriter.println("Duration of Flight: " + ticketA.getDuration());
			ticketWriter.println("Price of Flight (+ tax): " + ticketA.getTaxedPrice());
            ticketWriter.println("Thank you for choosing Air Yeezy");
            ticketWriter.close();
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getStackTrace());
        }
    }

    /**
     * Checks if a string is a number
     * @param toTest String to parse and test
     * @return true when it is, false if not
     */
    public boolean isNum(String toTest){
		 try { 
		        Integer.parseInt(toTest); 
		    } catch(NumberFormatException e) { 
		        return false; 
		    }
		    return true;
	}

    /**
     * Function to check if date entered is valid
     * @param date The date checked by the system
     * @return True when a valid date, false when not
     */
    public boolean isValidDate(String date){
		//code obtained and edited from http://stackoverflow.com/questions/15491894/regex-to-validate-date-format-dd-mm-yyyy
		if(!date.matches("^(?=\\d{2}([/])\\d{2}\\1\\d{4}$)(?:0[1-9]|1\\d|[2][0-8]|29(?!.02.(?!(?!(?:[02468][1-35-79]|[13579]"
				+ "[0-13-57-9])00)\\d{2}(?:[02468][048]|[13579][26])))|30(?!.02)|31(?=.(?:0[13578]|10|12))).(?:0[1-9]|1[012]).\\d{4}$"))
			return false;
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date currententDate = new Date();
		try{
			Date flightDate = format.parse(date);
			if(!flightDate.after(currententDate))
				return false;
		}catch (Exception e){
			return false;
		}
		return true;
	}

    /**
     * Checks the format of the flight search
     * @param param The parameter searched for
     * @param key The key searched for
     * @return Nothing if good, the error(s) if found
     */
    public String checkFormatFlightSearch(String param, String key){
    	String toReturn = "";
    	switch (param){
    	case "flightnumber":
    		if(!isNum(key) || key.length() > 4)
    			toReturn = "Search field format error, please ensure field is an integer of 4 digits or less";
    		break;
    	case "destlocation":
    		if(key.length() > 45 || key.contains("_"))
    			toReturn = "Search field format error, please ensure field does not contain underscores or exceed 45 characters";
    		break;
    	case "sourcelocation":
    		if(key.length() > 45 || key.contains("_"))
    			toReturn = "Search field format error, please ensure field does not contain underscores or exceed 45 characters";
    		break;
    	case "date":
    		if(!isValidDate(key))
    			toReturn = "Search field format error, please ensure field follows dd/MM/yyyy format";
    		break;
    	default:
    		toReturn = "Unrecognized Parameter error...";
    		break;
    	}
    	return toReturn;	
    }

    /**
     * Checks for the format of the ticket
     * @param firstName The first name of the passenger
     * @param lastName The last name of the passennger
     * @return "GOOD FORMAT" when format is good or the error whe  not
     */
    public String checkFormatTicketAdd(String firstName, String lastName) {
        String toReturn="";
		boolean badFormat = false;
		;
		if(firstName.length() > 45 || firstName.isEmpty() || firstName.contains("_")){
			badFormat = true;
			toReturn += "First Name format Error, please ensure field is not empty, does not contain underscores and does not exceed 45 characters_";
		}
		
		if(lastName.length() > 45 || lastName.isEmpty() || lastName.contains("_")){
			badFormat = true;
			toReturn += "Last Name format Error, please ensure field is not empty, does not contain underscores and does not exceed 45 characters_";
		}
		
		if(badFormat){
			return "ERROR_" + toReturn;
		}
		return "GOOD FORMAT";
    }

    /**
     * Used when closing the GUI, closes all streams
     */
    public void quitServer()
    {
        try{
            outputStream.writeObject((String)"QUIT_");
            socket.close();
            outputStream.close();
            inputStream.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
