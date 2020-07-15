import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import interfaces.Massageable;
import interfaces.consoleUI;

/**
 * @authors ${Avner Levy & Shahar Raz}
 *
 */

public class Program {
//	public static boolean isOK = false;

	/* delete me if all works well(20-31)*/
//// Global parameters // 
//	public static String flightId;
//	public static String depAirPort;
//	public static String arriveAirPort;
//	public static String depTime;
//	public static String arrTime;
//	public static String brand;
//	public static int terminalNum;
//	public static MyDate date = null;
//	public static int flag;
	// MY LISTS //
	public static List<String> brands = new ArrayList<>();
	public static List<Flight> allFlights = new ArrayList<>();
	private static List<Flight> flightsIn = new ArrayList<>();
	private static List<Flight> flightsOut = new ArrayList<>();
	public static List<String> airPorts = new ArrayList<>();
	// I/O VARIABLES //
	public static Scanner scn; // system.in scanner 
	public static final String FILE_NAME = "Input.txt";
	public static Massageable ui = new consoleUI();

	public static void main(String[] args) throws FileNotFoundException {
		scn = new Scanner(System.in);
		createBrandsSet();
		addAirPorts();
		activition();
		scn.close();
	}

	private static void printMainMenu() {
		for (int i = 0; i < 15; i++) {
			System.out.print("- ");
		}
		
		ui.showMassage("\n1 - Auto-Add new Flight");
		ui.showMassage("2 - Add new Flight");
		ui.showMassage("3 - Sort flights by...");
		ui.showMassage("4 - Save to File");
		ui.showMassage("5 - Load from File");
		ui.showMassage("6 - Print All Flights");
		ui.showMassage("7 - Remove All Flights");
		ui.showMassage("8 - Search Flights by Terms ");
		ui.showMassage("9 - Shuffle Flight Dates");
		ui.showMassage("10 - EXIT");

		for (int i = 0; i < 15; i++) {
			System.out.print("- ");
		}

		System.out.print("\nYour Choice: ");
	}
	// Activate the Menu //
	public static void activition() throws FileNotFoundException {
		ui.showMassage("Welcome\nPlease Choose by entering number:");
		boolean isOK = false;
		try {
			while (!isOK) {
				printMainMenu();
				String userChoice=scn.nextLine();
				
				
				switch (userChoice) {
				case "1":
					AutoAdd();
					ui.showMassage("Flight has been added Successfully");
					isOK = false;
					break;

				case "2":
					addFlight();
					ui.showMassage("Flight has been added Successfully");
					isOK = false;
					break;
				case "3":
					sortFlights();
					isOK = false;
					break;
				case "4":
					try {
						saveToFile();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					ui.showMassage("Flight has been Saved Successfully");
					isOK = false;
					break;
				case "5":
					try {
						loadFromFile(allFlights,flightsIn,flightsOut);
					} catch (Exception e) {
						System.err.println("Error! Something Went Wrong. Try Again!");
						e.printStackTrace();
						break;
					}
					ui.showMassage("Flight has been Loaded Successfully");
					isOK = false;
					break;
				case "6":
					showFlights();
					isOK = false;
					break;
				case "8":
					SearchByTerms(allFlights);// keep in cases needed in future
					isOK = false;
					break;
				case "9":
					Collections.shuffle(allFlights);
					isOK = false;
					break;
				case "10":
					ui.showMassage("goodBye");
					isOK = true;
					break;

				default:
					ui.showMassage("Wrong input try again");
					isOK = false;
				}
			}
		} catch (Exception e) {
			ui.showMassage("Invalid Input...");
		
		}
	}

	private static void saveToFile() throws Exception {
		try (RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "rw")) {
			raf.setLength(0);

			// All Flights //
			for (Flight f : allFlights) {
				// the Parameters //
				raf.writeUTF(f.getBrand());
				raf.writeUTF(f.getDepAirPort());
				raf.writeUTF(f.getArriveAirPort());
				raf.writeUTF(f.getDate().toString());
				raf.writeUTF(f.getDepTime());
				raf.writeUTF(f.getArrTime());
				raf.writeUTF(f.getFlightId());
				raf.writeUTF(f.getTerminalNum() + "");
				raf.writeUTF(f.getFlag() + "");
			}
		}

	}

	// In - > Out //
	protected static void loadFromFile(List<Flight> allFlights1,List<Flight> flightsIn1,
			List<Flight> flightsOut1) throws FileNotFoundException { // General func, move to other class
		MyDate date = null;
		allFlights1.clear();
		flightsIn1.clear();
		flightsOut1.clear();
		String brand1,depAirPort1,arriveAirPort1,depTime1,arrTime1,flightId1;
		int terminalNum1 , flag1;
		try (RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "r")) {
			while (raf.getFilePointer() < raf.length()) {
				brand1 = raf.readUTF();
				depAirPort1 = raf.readUTF();
				arriveAirPort1 = raf.readUTF();
				String theDate = raf.readUTF(); // reading the date as a String //
				String[] split = theDate.split("/");
				date = new MyDate(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
				depTime1 = raf.readUTF();
				arrTime1 = raf.readUTF();
				flightId1 = raf.readUTF();
				terminalNum1 = Integer.parseInt(raf.readUTF());
				flag1 = Integer.parseInt(raf.readUTF());
				if (flag1 == 1)
					allFlights1.add(new FlightIn(brand1, depAirPort1, date, depTime1, arrTime1, flightId1, terminalNum1, 1));
				else
					allFlights1
							.add(new FlightOut(brand1, arriveAirPort1, date, depTime1, arrTime1, flightId1, terminalNum1, 2));

				spreadFlights(allFlights1,flightsIn1,flightsOut1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static void addFlight() {
		ui.showMassage("1) Flight in\n2) Flight Out");
		boolean isOK=false;
		while (!isOK) {
			String inout = scn.nextLine();
			if (inout.compareTo("1") == 0 || inout.compareTo("2") == 0) {
				pushFlight(inout);
				isOK = true;
			} else
				ui.showMassage("Wrong input");
		}
	}

	private static void sortFlights() {
		allFlights.clear();
		ui.showMassage("1) Sort By Departure Time..\n2) Sort By Arriving Time..\nPress any key for main menu");
		char c = scn.nextLine().charAt(0);

		switch (c) {
		case '1':
//			 list 1 //
			Collections.sort(flightsIn, compareByDepDate);

//			 list 2 //
			Collections.sort(flightsOut, compareByDepDate);

			for (Flight f : flightsIn) {
				allFlights.add(f);
			}
			for (Flight f : flightsOut) {
				allFlights.add(f);
			}
			ui.showMassage("Flight has been Sorted by Departure Time Successfully");
			break;

		case '2':
//			 list 1 //
			Collections.sort(flightsIn, compareByArrivalDate);

//			 list 2 //
			Collections.sort(flightsOut, compareByArrivalDate);

			for (Flight f : flightsIn) {
				allFlights.add(f);
			}
			for (Flight f : flightsOut) {
				allFlights.add(f);
			}
			ui.showMassage("Flight has been Sorted by Departure Time Successfully");
			break;

		default:
			ui.showMassage("Going back to main menu");
			break;
		}

	}

	private static void showFlights() {
		flightsIn.clear();
		flightsOut.clear();
		ui.showMassage("num of flights in allFlights: " + allFlights.size());
		if (!allFlights.isEmpty()) {
			allFlights.sort(compareByDepDate);
			spreadFlights(allFlights,flightsIn , flightsOut);
		}
		ui.showMassage(allFlights.size() + " " + flightsIn.size() + " " + flightsOut.size());

		ui.showMassage("\nIncoming Flights: ");
		if (flightsIn.size() == 0)
			ui.showMassage("No Incoming Flights...");
		else
			for (Flight f : flightsIn) {
				ui.showMassage(f.toString());
			}

		ui.showMassage("\nOutcoming Flights: ");
		if (flightsOut.size() == 0)
			ui.showMassage("No Outcoming Flights...");
		else
			for (Flight f : flightsOut) {
				ui.showMassage(f.toString());
			}

	}
	

	public static List<Flight> SearchByTerms(List<Flight> flightsArr) {
	FilterFlights filtered = new FilterFlights(flightsArr,brands);
	return filtered.filterByTerms();
	}


	// HelpFull Methods //
//	public static ArrayList<>

	// Auto add //
	private static void AutoAdd() {
		int hrsIn = (int) (1 + Math.random() * 23);
		int minIn = (int) (1 + Math.random() * 60);
		int hrsOut = (int) (1 + Math.random() * 23);
		int minOut = (int) (1 + Math.random() * 60);
		int mons = (int) (1 + Math.random() * 12);
		int day = (int) (1 + Math.random() * 28);
		int flag1;

		while (hrsOut - hrsIn >= 3) {
			hrsIn = (int) (1 + Math.random() * 23);
			hrsOut = (int) (1 + Math.random() * 23);
		}
		flag1 = (int) (1 + Math.random() * 2);
		Collections.shuffle(airPorts);
		int size = airPorts.size() - 1;
		int p = (int) (1 + Math.random() * size);
		if (flag1 == 1)
			allFlights.add(new FlightIn(brands.get((int) (Math.random() * 4)), airPorts.get(p),
					new MyDate(day, mons, 2020), hrsOut + ":" + minOut, hrsIn + ":" + minIn,
					(100 + Math.random() * 5) + "", (int) (1 + Math.random() * 3), flag1));
		else
			allFlights.add(new FlightOut(brands.get((int) (Math.random() * 4)), airPorts.get(p),
					new MyDate(day, mons, 2020), hrsOut + ":" + minOut, hrsIn + ":" + minIn,
					(100 + Math.random() * 5) + "", (int) (1 + Math.random() * 3), flag1));

	}

	// ask method for detail //
	private static void pushFlight(String n) {
		allFlights.add(askMe(n));
	}
	
	public static MyDate getDateFromUser(Scanner scn) {
		ui.showMassage("enter date 'day' 'month' 'year' (example :23 06 1994)");
		int day = scn.nextInt();
		int month = scn.nextInt();
		int year = scn.nextInt();
		scn.nextLine(); // clear buffer
		return new MyDate(day, month, year);
	}


	public static Flight askMe(String n) { // manual insertion of flight
		String flightId;
		String depAirPort;
		String arriveAirPort;
		String depTime;
		String arrTime;
		String brand;
		int terminalNum;
		MyDate date = null;
		flightId = ui.getString("Insert flight ID: ");
//		System.out.print("Insert flight ID: ");
//		flightId = scn.nextLine();
		ui.showMassage("Insert flight Date: ");
		getDateFromUser(scn);
		depTime =ui.getString("Insert flight Departure Time: ");
		arrTime =ui.getString("Insert flight Arrival Time: ");
		brand =ui.getString("Insert flight brand: ");
		terminalNum = Integer.parseInt(ui.getString("Insert Terminal number: "));

		if (n.equals("1")) { // -->> Flight In ///
			System.out.print("Insert flight Departure Air-Port: ");
			depAirPort = scn.nextLine();
			return new FlightIn(brand, depAirPort, date, depTime, arrTime, flightId, terminalNum, 1);
		} else if (n.equals("2")) { // -->> flight out ///

			System.out.print("Insert flight Arrival AirPort: ");
			arriveAirPort = scn.nextLine();
			return new FlightOut(brand, arriveAirPort, date, depTime, arrTime, flightId, terminalNum, 2);
		}

		return null;
	}

	public static void createBrandsSet() {
		brands.add("Arkia");
		brands.add("El-Al");
		brands.add("Israir");
		brands.add("Wizz");
	}

	// Spreads Flights from AllFlights to FlightIn and FlightOut //

	public static void spreadFlights(List<Flight> allFlights2,List<Flight> flightsIn2,
			List<Flight> flightsOut2) {
		for (Flight f : allFlights2) {
			if (f.getFlag() == 1)
				flightsIn2.add((FlightIn) f);
			else // flag = 2
				flightsOut2.add((FlightOut) f);
		}
	}

	public static void miniShowFlights(List<Flight> l) {
		ui.showMassage("Flights In:");
		for (Flight f : l) {
			if (f.getFlag() == 1)
				ui.showMassage(f.toString());
		}
		ui.showMassage("Flights Out:");
		for (Flight f : l) {
			if (f.getFlag() == 2)
				ui.showMassage(f.toString());
		}
	}
	public static void simpleMiniShowFlights(List<Flight> l) {
		ui.showMassage("Flights:");
		for (Flight f : l) {
				ui.showMassage(f.toString());
		}
	}
	

	public static String addBrand() {
		String brand = "";
		// prints all brands //
		for (int i = 0; i < brands.size(); i++) {
			ui.showMassage((i + 1) + ") " + brands.get(i));
			if (i == brands.size() - 1)
				ui.showMassage((i + 2) + ") Other ...");
		}
		// chooses the brand //
		int res = Integer.parseInt(scn.nextLine());
		while (true) {
			if (res < 0 || res > brands.size() + 1) {
				ui.showMassage("Wrong input! Try Again!");
				res = Integer.parseInt(scn.nextLine());
			} else
				break;
		}
		while (res >= 0 && res <= brands.size() + 1) {
			if (res == brands.size() + 1) {
				ui.showMassage("Whitch Brand?");
				brand = scn.nextLine();
				break;
			} else
				brand = brands.get(res - 1);
			break;
		}
		return brand;

	}

	public static void addAirPorts() {
		airPorts.add("Prague Air-Port, Czech Republic");
		airPorts.add("Paris Air-Port, France");
		airPorts.add("Moscow Air-Port, Russia");
		airPorts.add("Brussel Air-Port, Belguim");
		airPorts.add("Berlin Air-Port, Germany");
		airPorts.add("Devlin Air-Port, Ireland");
		airPorts.add("London Air-Port, UK");
		airPorts.add("NYC Air-Port, USA");
		airPorts.add("New-Deli Air-Port, India");
		airPorts.add("Adiss-Abba Air-Port, Ethipea");
		airPorts.add("Washington DC Air-Port, USA");
		airPorts.add("Helsinki Air-Port, Finland");
		airPorts.add("Bangkok Air-Port, China");
		airPorts.add("Tokyo Air-Port, Japan");
		airPorts.add("Menila Air-Port, The Philippines");
		airPorts.add("Shenghai Air-Port, China");
		airPorts.add("Kiev Air-Port, Ukrain");
		airPorts.add("Odessa Air-Port, Ukrain");
		airPorts.add("Sant-Peternurg Air-Port, Russia");
		airPorts.add("Nairobi Air-Port, Kenya");
		airPorts.add("Oslo Air-Port, Norway");
		airPorts.add("Riga Air-Port, Latvia");
		airPorts.add("Vienna Air-Port, Austria");
		airPorts.add("Wellington Air-Port, New Zealand");
		airPorts.add("Barcelona Air-Port, Spain");
		airPorts.add("Zagreb Air-Port, Croatia");
		airPorts.add("Madrid Air-Port, Spain");
	}

// Inner Comparators for Sorts //
	public static Comparator<Flight> compareByDepDate = new Comparator<Flight>() {
		@Override
		public int compare(Flight o1, Flight o2) {
			String date1 = o1.getDate().toString();
			String date2 = o2.getDate().toString();

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			int res = 0;
			try {
				res = sdf.parse(date1).compareTo(sdf.parse(date2));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (res != 0)
				return res;
			return o1.getDepTime().compareTo(o2.getDepTime());

		}
	};

	public static Comparator<Flight> compareByArrivalDate = new Comparator<Flight>() {
		@Override
		public int compare(Flight o1, Flight o2) {
			String date1 = o1.getDate().toString();
			String date2 = o2.getDate().toString();

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			int res = 0;
			try {
				res = sdf.parse(date1).compareTo(sdf.parse(date2));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (res != 0)
				return res;
			return o1.getArrTime().compareTo(o2.getArrTime());

		}
	};

}
