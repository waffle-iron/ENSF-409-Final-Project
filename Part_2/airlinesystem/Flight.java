package airlinesystem;
import java.io.Serializable;
import java.text.DecimalFormat;

public class Flight implements Serializable{
	private String flightId;
	private String destination;
	private String source;
	private String dateOfFlight;
	private String timeOfFlight;
	private String durationOfFlight;
	private String totalSeats;
	private String availableSeats;
	private String price;
	static final long serialVersionUID = 2L;
	
	public Flight(String fId, String dest, String src, String dateOF, String timeOF, String durOF, String tS, String aS, String prc){
		flightId = fId;
		destination = dest;
		source = src;
		dateOfFlight = dateOF;
		timeOfFlight = timeOF;
		durationOfFlight = durOF;
		totalSeats = tS;
		availableSeats = aS;
		price = prc;
	}
	public Flight(String dest, String src, String dateOF, String timeOF, String durOF, String tS, String prc){
		destination = dest;
		source = src;
		dateOfFlight = dateOF;
		timeOfFlight = timeOF;
		durationOfFlight = durOF;
		totalSeats = tS;
		price = prc;
	}
	
	public String toDisplay(){
		return flightId + "  " + destination + "  " + source + "  " + "  " + dateOfFlight;
	}
	
	public boolean hasAvailableSeats(){
		if(Integer.getInteger(availableSeats) <= 0)
			return false;
		return true;
	}
	
	public String getFlightId(){
		return flightId;
	}
	public String getDest(){
		return destination;
	}
	public String getSrc(){
		return source;
	}
	public String getDate(){
		return dateOfFlight;
	}
	public String getTime(){
		return timeOfFlight;
	}
	public String getDuration(){
		return durationOfFlight;
	}
	public String getTotalSeats(){
		return totalSeats;
	}
	public String getAvailableSeats(){
		return availableSeats;
	}
	public String getPrice(){
		return price;
	}
	public String getTaxedPrice(){
		Double prc = Double.parseDouble(price);
		prc *= 1.07;
		String toReturn = String.format("%.2f", prc);
		return toReturn;
	}
}
