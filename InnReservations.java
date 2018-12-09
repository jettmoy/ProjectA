/*
   J. Randomgeek
   CSC 365 Project A UI
*/

import java.sql.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.math.*;

// main function. Contains main program loop
public class InnReservations {

   private static Connection conn = null;

   // enter main program loop
   public static void main(String args[]) {

   // eeb: you may want to put various set-up functionality here

   //CHRIS ADDED HERE: START
   try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      System.out.println ("Driver class found and loaded.");
   }
   catch (Exception ex) {
      System.out.println("Driver not found");
      System.out.println(ex);
   };
   Scanner scan = null;
   File f =
      new File("ServerSettings.txt");
   try {
      scan = new Scanner(f);
   } catch(Exception e){
      System.out.println("ERROR: ServerSettings.txt doesn't exist");
   }

   // Now make the mySQL connection
   try{
      conn = DriverManager.getConnection(
        scan.nextLine() ,
        scan.nextLine(), scan.nextLine() );
   }
   catch (Exception ex) {
      System.out.println("Could not open connection");
      System.out.println(ex);
   };
   System.out.println("\nConnected to mySQL\n");

   // Create tables
   // now we're going to create a table and populate it
   // for rooms
   try {
      String table = "CREATE TABLE IF NOT EXISTS ProjectA_rooms LIKE INN.rooms;";
      System.out.println("The CREATE TABLE statement:\n" + table + "\n");

      Statement s1 = conn.createStatement();
      System.out.println("createStatement ok");

      // the next statement executes our CREATE TABLE statement
      s1.executeUpdate(table);

   }
   catch (Exception ee) {
      System.out.println("ee96: " + ee);
   }

   // now we're going to create a table and populate it
   // for reservations
   try {
      String table = "CREATE TABLE IF NOT EXISTS ProjectA_reservations LIKE INN.reservations;";
      System.out.println("The CREATE TABLE statement:\n" + table + "\n");

      Statement s1 = conn.createStatement();
      System.out.println("createStatement ok");

      // the next statement executes our CREATE TABLE statement
      s1.executeUpdate(table);

   }
   catch (Exception ee) {
      System.out.println("ee96: " + ee);
   }

   System.out.println();

   boolean roomsEmpty = checkRoomsEmpty();
   System.out.println("ProjectA_rooms is empty: " + roomsEmpty);

   boolean resEmpty = checkReservationsEmpty();
   System.out.println("ProjectA_reservations is empty: " + resEmpty);

   //CHRIS ADDED HERE: END

   boolean exit = false;
   Scanner input = new Scanner(System.in);

      // clear the screen to freshen up the display
      clearScreen();
      while (!exit) {
	 displayMain();

	 char option = input.nextLine().toLowerCase().charAt(0);

	 switch(option) {
	    case 'a':   adminLoop();
			break;
	    case 'o':   ownerLoop();
			break;
	    case 'g':   guestLoop();
			break;
	    case 'q':   exit = true;
			break;
	 }
      }

      input.close();

   }

   // Main UI display
   private static void displayMain() {
      // Clear the screen
      // clearScreen();

      // Display UI
      System.out.println("Welcome. Please choose your role:\n\n"
         + "- (A)dmin\n"
         + "- (O)wner\n"
         + "- (G)uest\n"
         + "- (Q)uit\n");
   }



   // Program loop for admin subsystem
   private static void adminLoop() {
      boolean exit = false;
      Scanner input = new Scanner(System.in);

      System.out.println("Welcome, Admin.\n");

      while (!exit) {
         displayAdmin();

         String[] tokens = input.nextLine().toLowerCase().split(" ");
         char option = tokens[0].charAt(0);
	 System.out.println("option chosen: " + option);


         switch(option) {
            case 'v':
                        if (tokens.length > 1)
                            displayTable(tokens[1], currentStatus());
                        else
                            System.out.println("Usage: v <table>\n");
                        break;
            case 'c':   System.out.println("\nClearing database...\n");
                        clearDb();
                        break;
            case 'l':   loadDB();
                        break;
            case 'r':   System.out.println("\nRemoving database...\n");
                        removeDB();
                        break;
            case 'b':   exit = true;
                        break;
         }
      }
   }


   // Program loop for owner subsystem
   private static void ownerLoop() {
      boolean exit = false;
      Scanner input = new Scanner(System.in);

      while (!exit) {
         displayOwner();

         String[] tokens = input.nextLine().toLowerCase().split("\\s");
         char option = tokens[0].charAt(0);
         char dataOpt = 0;

         if (tokens.length == 2)
            dataOpt = tokens[1].charAt(0);

         switch(option) {
            case 'o':   System.out.println("occupancyMenu\n");
                        break;
            case 'd':   System.out.println("revenueData\n");
                        break;
            case 's':   System.out.println("browseRes()\n");
                        break;
            case 'r':   System.out.println("viewRooms\n");
                        break;
            case 'b':   exit = true;
                        break;
         }
      }
   }

   // Program loop for guest subsystem
   private static void guestLoop() {
      boolean exit = false;
      Scanner input = new Scanner(System.in);

      while (!exit) {
         displayGuest();

         char option = input.next().toLowerCase().charAt(0);

         switch(option) {
            case 'r':   System.out.println("roomsAndRates\n");
                        break;
            case 's':   System.out.println("viewStays\n");
                        break;
            case 'b':   exit = true;
                        break;
         }
      }
   }

   // Guest UI display
   private static void displayGuest() {
      // Clear the screen
      // clearScreen();

      // Display UI
      System.out.println("Welcome, Guest.\n\n"
         + "Choose an option:\n"
         + "- (R)ooms - View rooms and rates\n"
         + "- (S)tays - View availability for your stay\n"
         + "- (B)ack - Goes back to main menu\n");
   }

   // Clears the console screen when running interactive
   private static void clearScreen() {
      Console c = System.console();
      if (c != null) {

	 // Clear screen for the first time
	 System.out.print("\033[H\033[2J");
	 System.out.flush();
	 //c.writer().print(ESC + "[2J");
	 //c.flush();

	 // Clear the screen again and place the cursor in the top left
	 System.out.print("\033[H\033[1;1H");
	 System.out.flush();
	 //c.writer().print(ESC + "[1;1H");
	 //c.flush();
      }
   }

   // Admin UI display
   private static void displayAdmin() {

      // Clear the screen -- only if it makes sense to do it
      // clearScreen();

      // Display UI
      // add your own information for the state of the database
      Status status = currentStatus();
      System.out.println("Current Status: " + status.status + "\n"
         + "Reservations: " + status.resCount + "\n"
         + "Rooms: " + status.roomCount + "\n\n"
         + "Choose an option:\n"
         + "- (V)iew [table name] - Displays table contents\n"
         + "- (C)lear - Deletes all table contents\n"
         + "- (L)oad - Loads all table contents\n"
         + "- (R)emove - Removes tables\n"
         + "- (B)ack - Goes back to main menu\n");

   }


   // during the display of a database table you may offer the option
   // to stop the display (since there are many reservations):
   //    System.out.print("Type (q)uit to exit: ");
   //    etc.

   // Owner UI display
   private static void displayOwner() {
      // Clear the screen
      // clearScreen();

      // Display UI
      System.out.println("Welcome, Owner.\n\n"
         + "Choose an option:\n"
         + "- (O)ccupancy - View occupancy of rooms\n"
         + "- (D)ata [(c)ounts|(d)ays|(r)evenue] - View data on "
            + "counts, days, or revenue of each room\n"
         + "- (S)tays - Browse list of reservations\n"
         + "- (R)ooms - View list of rooms\n"
         + "- (B)ack - Goes back to main menu\n");
   }


   // Get a date from input
   private static String getDate() {
      Scanner input = new Scanner(System.in);

      String monthName = input.next();
      int month = monthNum(monthName);
      int day = input.nextInt();
      String date = "'2010-" + month + "-" + day + "'";
      return date;
   }

   // Convert month name to month number
   private static int monthNum(String month) {
      switch (month) {
         case "january": return 1;
         case "february": return 2;
         case "march": return 3;
         case "april": return 4;
         case "may": return 5;
         case "june": return 6;
         case "july": return 7;
         case "august": return 8;
         case "september": return 9;
         case "october": return 10;
         case "november": return 11;
         case "december": return 12;
      }

      return 0;
   }

   // ask how many dates will be entered
   private static int getNumDates() {
      Scanner input = new Scanner(System.in);

      System.out.print("Enter number of dates (1 or 2): ");

      int numDates = input.nextInt();
      while (numDates != 1 && numDates != 2) {
         System.out.print("Enter number of dates (1 or 2): ");
         numDates = input.nextInt();
      }
      return numDates;
   }


   // get the room code or a 'q' response to back up the menu
   private static String getRoomCodeOrQ() {
      Scanner input = new Scanner(System.in);
      System.out.print("Enter room code for more details "
	 + "(or (q)uit to exit): ");
      String roomCode = input.next();
      return roomCode;
   }


   // get the reservation code or a 'q' response to back up the menu
   private static String getReservCodeOrQ() {
      Scanner input = new Scanner(System.in);
      System.out.print("Enter reservation code for more details "
	 + "(or (q)uit to exit): ");
      String rvCode = input.next();
      return rvCode;
   }


   // Revenue and volume data subsystem -- option to continue or quit
   private static char revenueData() {
      Scanner input = new Scanner(System.in);
      char opt;
         System.out.print("Type (c)ount, (d)ays, or (r)evenue to view "
            + "different table data (or (q)uit to exit): ");
         opt = input.next().toLowerCase().charAt(0);

	 return opt;
   }



   // potentially useful for Rooms Viewing Subsystem -- gets option to
   // view room code or reservations room code or exit
   private static String viewRooms() {
      Scanner input = new Scanner(System.in);
	 System.out.print("Type (v)iew [room code] or "
	    + "(r)eservations [room code], or (q)uit to exit: ");

	 char option = input.next().toLowerCase().charAt(0);
	 String roomCode = String.valueOf(option);
	 if (option != 'q')
	    roomCode = roomCode + " '" + input.next() + "'";
	 return roomCode;
   }

   // ask user if they wish to quit
   private static char askIfQuit() {
      Scanner input = new Scanner(System.in);

	 System.out.print("Enter (q)uit to quit: ");
	 char go = input.next().toLowerCase().charAt(0);

	 return go;
   }


   // ask user if they wish to go back
   private static char askIfGoBack() {
      Scanner input = new Scanner(System.in);

	 System.out.print("Enter (b)ack to go back: ");
	 char go = input.next().toLowerCase().charAt(0);

	 return go;
   }


   // potentially useful for check availability subsystem
   private static char availabilityOrGoBack() {
      Scanner input = new Scanner(System.in);

      System.out.print("Enter (a)vailability, or "
	 + "(b)ack to go back: ");
      char option = input.next().toLowerCase().charAt(0);

      return option;
   }

   // Check availability subsystem:
   // ask if they want to place reservation or renege
   private static char reserveOrGoBack() {
      Scanner input = new Scanner(System.in);

      System.out.print("Enter (r)eserve to place a reservation, "
	 + "or (b)ack to go back: ");
      char option = input.next().toLowerCase().charAt(0);

      return option;
   }

   // Get the user's first name (for making a reservation)
   private static String getFirstName() {
      Scanner input = new Scanner(System.in);

      System.out.print("Enter your first name: ");
      String firstName = "'" + input.next() + "'";
      return firstName;
   }

   // Get the user's last name (for making a reservation)
   private static String getLastName() {
      Scanner input = new Scanner(System.in);

      System.out.print("Enter your last name: ");
      String lastName = "'" + input.next() + "'";
      return lastName;
   }

   // Get the number of adults for a reservation
   private static int getNumAdults() {
      Scanner input = new Scanner(System.in);

      System.out.print("Enter number of adults: ");
      int numAdults = input.nextInt();
      return numAdults;
   }

   // Get the number of children for a reservation
   private static int getNumChildren() {
      Scanner input = new Scanner(System.in);

      System.out.print("Enter number of children: ");
      int numChildren = input.nextInt();
      return numChildren;
   }

   // get discount for a room reservation
   private static String getDiscount() {
      Scanner input = new Scanner(System.in);

      System.out.print("Enter discount (AAA or AARP, if applicable): ");
      String dsName = input.nextLine().toUpperCase();

      return dsName;
   }

   //CHRIS' METHODS
   private static boolean checkRoomsEmpty(){
      boolean f = true;
      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT * FROM ProjectA_rooms");
         f = result.next();
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }
      return !f;
   }

   private static boolean checkReservationsEmpty(){
      boolean f = true;
      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT * FROM ProjectA_reservations");
         f = result.next();
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }
      return !f;
   }

   // Status Results
   private static class Status {
       String status;
       int resCount, roomCount;

       public Status(String s, int c1, int c2) {
           this.status = s;
           this.roomCount = c1;
           this.resCount = c2;
       }
   }

   // DB Methods
   private static Status currentStatus() {
       try {
           int roomCount, resCount;
           int dbStatus = 0;

           DatabaseMetaData dbm = conn.getMetaData();
            // check if "ProjectA_rooms" table is there and get its count
            ResultSet tables = dbm.getTables(null, null, "ProjectA_rooms", null);
            if (tables.next()) {
                // Table exists
                dbStatus++;
                String getCount = "SELECT * FROM ProjectA_rooms;";
                Statement s1 = conn.createStatement();
                ResultSet rs = s1.executeQuery(getCount);
                // get row count
                roomCount = rs.last() ? rs.getRow() : 0;
            }
            else {
                // Table does not exist
                roomCount = 0;
            }
            // reservation count
            tables = dbm.getTables(null, null, "ProjectA_reservations", null);
            if (tables.next()) {
                // Table exists
                dbStatus++;
                String getCount = "SELECT * FROM ProjectA_reservations;";
                Statement s1 = conn.createStatement();
                ResultSet rs = s1.executeQuery(getCount);
                // get row count
                resCount = rs.last() ? rs.getRow() : 0;
            }
            else {
                resCount = 0;
            }
            if (dbStatus == 0) {
                return new Status("no database", 0, 0);
            }
            else if (resCount == 0 && roomCount == 0) {
                return new Status("empty", 0, 0);
            }
            else {
                return new Status("full", roomCount, resCount);
            }
       }
       catch (Exception ee) {
          System.out.println("ee96: " + ee);
          return new Status("Error", 0, 0);
       }

   }

   private static void displayTable(String table, Status status) {
       String query;
       Statement stmt = null;
       if (table.equals("reservations")) {
           if (status.resCount == 0) {
               System.out.println("\nEmpty set.\n");
               return;
           }
           // System.out.println("Displaying reservations...");
           query = "SELECT * FROM ProjectA_reservations";
           try {
               stmt = conn.createStatement();
               ResultSet rs = stmt.executeQuery(query);
               System.out.println("\nCode" + "\t" + "Room" + "\t" + "checkIn" +
                    "\t\t" + "checkOut" + "\t" + "Rate" + "\t" + "lastName" +
                    "\t" + "firstName" + "\t" + "Adults" + "\t" + "Kids");
               while (rs.next()) {
                   int code = rs.getInt("Code");
                   String room = rs.getString("room");
                   java.sql.Date checkIn = rs.getDate("checkIn");
                   java.sql.Date checkOut = rs.getDate("checkOut");
                   int rate = rs.getInt("rate");
                   String lastName = rs.getString("lastName");
                   String firstName = rs.getString("firstName");
                   int adults = rs.getInt("adults");
                   int kids = rs.getInt("kids");

                   System.out.print(code + "\t" + room +
                                      "\t" + checkIn + "\t" + checkOut +
                                      "\t" + rate + "\t");
                    System.out.printf("%-15s %-15s", lastName, firstName);
                    System.out.println("\t" + adults + "\t" + kids);
               }
               System.out.println();
               if (stmt != null) rs.close();
           } catch (SQLException e) {
               System.out.println("Error" + e);
           }
       }
        else if (table.equals("rooms")) {
            // System.out.println("Displaying rooms...");
            if (status.roomCount == 0) {
                System.out.println("\nEmpty set.\n");
                return;
            }
            query = "SELECT * FROM ProjectA_rooms";
            try {
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                System.out.println("\nRoomId" + "\t" + "RoomName" + "\t\t\t" +
                    "Beds" + "\t" + "BedType" + "\t" + "MaxOcc" + "\t" +
                    "BasePrice" + "\t" + "Decor");
                while (rs.next()) {
                    String roomId = rs.getString("RoomId");
                    String roomName = rs.getString("RoomName");
                    int beds = rs.getInt("Beds");
                    String bedType = rs.getString("BedType");
                    int maxOcc = rs.getInt("MaxOcc");
                    int basePrice = rs.getInt("BasePrice");
                    String decor = rs.getString("Decor");

                    System.out.print(roomId + "\t");
                    System.out.printf("%-30s", roomName);
                    System.out.println("\t" + beds + "\t" + bedType +
                                       "\t" + maxOcc + "\t" + basePrice +
                                       "\t\t" + decor);
                }
                System.out.println();
                if (stmt != null) rs.close();
            } catch (SQLException e) {
                System.out.println("Error" + e);
            }
        }
        else
            System.out.println("Incorrect table name");

   }

   private static void clearDb() {
       try {
          Statement s1 = conn.createStatement();
          s1.executeUpdate("TRUNCATE TABLE ProjectA_rooms");
          Statement s2 = conn.createStatement();
          s2.executeUpdate("TRUNCATE TABLE ProjectA_reservations");
          s1.close();
          s2.close();
       }
       catch (Exception ee) {
          System.out.println("Error: " + ee);
       }
   }

   private static void loadDB() {
       Status status = currentStatus();
       if (status.status.equals("full")) {
           System.out.println("\nDatabase is already populated.\n");
       }
       else if (status.status.equals("empty")) {
           System.out.println("\nPopulating database.\n");
           populateDB();
       }
       else {
           System.out.println("\nInitializing and populating database.\n");
           createDB();
           populateDB();
       }
   }

   private static void populateDB() {
       try {
           Statement s1 = conn.createStatement();
           s1.executeUpdate("INSERT INTO ProjectA_rooms SELECT * FROM INN.rooms;");
           s1.close();
           Statement s2 = conn.createStatement();
           s2.executeUpdate("INSERT INTO ProjectA_reservations SELECT * FROM INN.reservations");
           s2.close();
       } catch (Exception e) {
           System.out.println("Error populating db: " + e);
       }
   }

   private static void createDB() {
       // Create tables
       // now we're going to create a table and populate it
       // for rooms
       try {
          String table = "CREATE TABLE IF NOT EXISTS ProjectA_rooms LIKE INN.rooms;";
          Statement s1 = conn.createStatement();
          s1.executeUpdate(table);
          s1.close();

       // now we're going to create a table and populate it
       // for reservations
          String table2 = "CREATE TABLE IF NOT EXISTS ProjectA_reservations LIKE INN.reservations;";
          Statement s2 = conn.createStatement();
          s2.executeUpdate(table2);
          s2.close();
       }
       catch (Exception ee) {
          System.out.println("Error creating tables: " + ee);
       }
   }

   private static void removeDB() {
       try {
          Statement s1 = conn.createStatement();
          s1.executeUpdate("DROP TABLE ProjectA_rooms");
          Statement s2 = conn.createStatement();
          s2.executeUpdate("DROP TABLE ProjectA_reservations");
          s1.close();
          s2.close();
       }
       catch (Exception ee) {
          System.out.println("Error: " + ee);
       }
   }
}
