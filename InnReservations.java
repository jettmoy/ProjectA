/*
   J. Randomgeek
   CSC 365 Project A UI
*/

import java.sql.*;
import java.time.*;
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
                        occupancyMenuLoop();
                        break;
            case 'd':   System.out.println("revenueData\n");
                        revenueMenuLoop();
                        break;
            case 's':   System.out.println("browseRes()\n");
                        reservationMenuLoop();
                        break;
            case 'r':   System.out.println("viewRooms\n");
                        roomMenuLoop();
                        break;
            case 'b':   exit = true;
                        break;
         }
      }
   }

   // CHRIS MAKING METHODS FOR OWNER LOOP: START
   // OR-1: START
   // Logic for menu when (O)wner -> (O)ccupancy is selected
   private static void occupancyMenuLoop(){
      boolean exit = false;
      Scanner input = new Scanner(System.in);

      while (!exit) {
         displayOccupancyMenu();

         String[] tokens = input.nextLine().toLowerCase().split(" ");

         char option = tokens[0].charAt(0);
         System.out.println("option chosen: " + option);


         switch(option) {
         case '1':   occupancy1();
                     break;
         case '2':   occupancy2();
                     break;
         case 'b':   exit = true;
                     break;
         }
      }
   }

   // Owner occupancy UI display
   private static void displayOccupancyMenu() {

      // Display UI
      System.out.println("Occupancy Menu.\n\n"
         + "Choose an option:\n"
         + "- (1) date (MM-DD)\n"
         + "- (2) dates (MM-DD:MM-DD)\n"
         + "- (B)ack - Goes back to main menu\n");
   }


   // 1 date option select
   private static void occupancy1(){
      Scanner input = new Scanner(System.in);

      System.out.println("Enter 1 Date:\n");
      String[] tokens = input.nextLine().toLowerCase().split("-");

      // Checking if date is entered properly
      if(tokens.length != 2 || tokens[0].length() != 2 || tokens[1].length() != 2){
         System.out.println("Improper date format... Returning to Owner menu");
         return;
      }
      try{
         Integer.parseInt(tokens[0]);
         Integer.parseInt(tokens[1]);
      }
      catch(NumberFormatException nfe){
         System.out.println("Improper date units... Returning to Owner menu");
         return;
      }

      // Making mysql query to display room OccupationStatus
      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT rm.RoomName, rm.RoomId, "
         + "IF(SUM(IF(CheckIn <= '2010-" + tokens[0] + "-" + tokens[1]
         + "' AND CheckOut > '2010-" + tokens[0] + "-" + tokens[1]
         + "', 1, 0)) =1, 'Occupied', 'Empty') AS OccupationStatus "
         + "FROM ProjectA_rooms rm "
         + "INNER JOIN ProjectA_reservations res "
         + "ON rm.RoomId = res.Room "
         + "GROUP BY rm.RoomName, rm.RoomId "
         + "ORDER BY rm.RoomId;");
         System.out.println("Room availability:");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            System.out.println(str1 + ", " + str2 + ", " + str3);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

      System.out.println("Select a room code to view reservation conflicts:\n");
      String[] roomChoice = input.nextLine().toLowerCase().split(" ");

      String option = roomChoice[0].substring(0, 3);
      System.out.println("option chosen: " + option);

      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT * "
         + "FROM ProjectA_reservations res "
         + "WHERE res.ROOM = '" + option + "' AND CheckIn <= '2010-" + tokens[0] + "-" + tokens[1]
         + "' AND CheckOut > '2010-" + tokens[0] + "-" + tokens[1]
         + "';");
         System.out.println("Conflicting Reservation:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            String str5 = result.getString(5);
            String str6 = result.getString(6);
            String str7 = result.getString(7);
            String str8 = result.getString(8);
            String str9 = result.getString(9);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4
            + ", " + str5 + ", " + str6 + ", " + str7 + ", " + str8 + ", " + str9);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

   }

   // 2 date option select
   private static void occupancy2(){
      Scanner input = new Scanner(System.in);

      System.out.println("Enter 2 Dates:\n");
      String[] tokens = input.nextLine().toLowerCase().split(":");

      // Check if proper amount of dates entered
      if(tokens.length !=2){
         System.out.println("Improper amount of dates... Returning to Owner menu");
         return;
      }

      String[] date1 = tokens[0].split("-");
      String[] date2 = tokens[1].split("-");

      // Checking if dates are entered properly
      if(date1.length != 2 || date1[0].length() != 2 || date1[1].length() != 2){
         System.out.println("Improper date format... Returning to Owner menu");
         return;
      }
      if(date2.length != 2 || date2[0].length() != 2 || date2[1].length() != 2){
         System.out.println("Improper date format... Returning to Owner menu");
         return;
      }

      try{
         Integer.parseInt(date1[0]);
         Integer.parseInt(date1[1]);
         Integer.parseInt(date2[0]);
         Integer.parseInt(date2[1]);
      }
      catch(NumberFormatException nfe){
         System.out.println("Improper date units... Returning to Owner menu");
         return;
      }

      // Making mysql query to show OccupationStatus
      try {
         Statement s = conn.createStatement();

         // I don't think this covers situation when room is fully occupied by 2 separate parties
         ResultSet result = s.executeQuery("SELECT rm.RoomName, rm.RoomId, "
         + "IF(SUM(CASE "
         + "WHEN (CheckIn <= '2010-" + tokens[0] + "') AND (CheckOut > '2010-" + tokens[1] + "') THEN 1 "
         + "WHEN (CheckIn <= '2010-" + tokens[0] + "' AND CheckOut <= '2010-" + tokens[1] + "' AND CheckOut > '2010-" + tokens[0]
         + "') OR (CheckIn > '2010-" + tokens[0] + "' AND CheckIn < '2010-" + tokens[1] + "' AND CheckOut > '2010-" + tokens[1]
         + "') OR (CheckIn >= '2010-" + tokens[0] + "' AND CheckOut <= '2010-" + tokens[1] + "') THEN 0.01 "
         + "ELSE 0 "
         + "END) = 0, 'Empty', IF(SUM(CASE "
         + "WHEN (CheckIn <= '2010-" + tokens[0] + "') AND (CheckOut > '2010-" + tokens[1] + "') THEN 1 "
         + "WHEN (CheckIn <= '2010-" + tokens[0] + "' AND CheckOut <= '2010-" + tokens[1] + "' AND CheckOut > '2010-" + tokens[0]
         + "') OR (CheckIn > '2010-" + tokens[0] + "' AND CheckIn < '2010-" + tokens[1] + "' AND CheckOut > '2010-" + tokens[1]
         + "') OR (CheckIn >= '2010-" + tokens[0] + "' AND CheckOut <= '2010-" + tokens[1] + "') THEN 0.01 "
         + "ELSE 0 "
         + "END) = 1, 'Fully Occupied', 'Partially Occupied') ) AS OccupationStatus "
         + "FROM ProjectA_rooms rm "
         + "INNER JOIN ProjectA_reservations res "
         + "ON rm.RoomId = res.Room "
         + "GROUP BY rm.RoomName, rm.RoomId "
         + "ORDER BY rm.RoomId;");
         System.out.println("Room availability:");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            System.out.println(str1 + ", " + str2 + ", " + str3);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

      // Making mysql query to reservation conflicts
      System.out.println("Select a room code to view reservation conflicts:\n");
      String[] roomChoice = input.nextLine().toLowerCase().split(" ");

      String option = roomChoice[0].substring(0, 3);
      System.out.println("option chosen: " + option);

      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT Code, Room, CheckIn, CheckOut "
         + "FROM ProjectA_reservations res "
         + "WHERE res.ROOM = '" + option + "' AND ((CheckIn <= '2010-" + tokens[0] + "' AND CheckOut > '2010-" + tokens[1]
         + "') OR ((CheckIn <= '2010-" + tokens[0] + "' AND CheckOut <= '2010-" + tokens[1] + "' AND CheckOut > '2010-" + tokens[0]
         + "') OR (CheckIn > '2010-" + tokens[0] + "' AND CheckIn < '2010-" + tokens[1] + "' AND CheckOut > '2010-" + tokens[1]
         + "') OR (CheckIn >= '2010-" + tokens[0] + "' AND CheckOut <= '2010-" + tokens[1] + "')))"
         + ";");
         System.out.println("Conflicting Reservations:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

      // mysql query to show all info for reservation
      System.out.println("\nSelect a reservation number to view full reservation:\n");
      String[] resChoice = input.nextLine().toLowerCase().split(" ");

      option = resChoice[0];
      System.out.println("option chosen: " + option);

      try{
         Integer.parseInt(option);
      }
      catch(NumberFormatException nfe){
         System.out.println("Not a number... Returning");
         return;
      }

      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT * "
         + "FROM ProjectA_reservations "
         + "WHERE Code = " + option + ";");
         System.out.println("Full Conflicting Reservation:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            String str5 = result.getString(5);
            String str6 = result.getString(6);
            String str7 = result.getString(7);
            String str8 = result.getString(8);
            String str9 = result.getString(9);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4
            + ", " + str5 + ", " + str6 + ", " + str7 + ", " + str8 + ", " + str9);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

   }

   // OR-1: END

   // OR-2: START

   // Logic for menu when (O)wner -> (D)ata is selected
   private static void revenueMenuLoop(){
      boolean exit = false;
      Scanner input = new Scanner(System.in);

      while (!exit) {
         displayRevenueMenu();

         String[] tokens = input.nextLine().toLowerCase().split(" ");

         char option = tokens[0].charAt(0);
         System.out.println("option chosen: " + option);


         switch(option) {
         case 'c':   revReservations();
                     break;
         case 'd':   revDays();
                     break;
         case 'r':   revRevenue();
                     break;
         case 'b':   exit = true;
                     break;
         }
      }
   }

   // Revenue UI display
   private static void displayRevenueMenu() {

      // Display UI
      System.out.println("Data Menu.\n\n"
         + "Choose an option:\n"
         + "- (C)ounts,\n"
         + "- (D)ays,\n"
         + "- (R)evenue,\n"
         + "- (B)ack - Goes back to main menu\n");
   }

   // Dispay number of Reservations by month
   private static void revReservations() {

      // Making mysql query to display Dispay number of Reservations by month
      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT rm.RoomName, SUM(IF(MONTH(CheckOut) = 1, 1, 0)) AS january, "
         + "SUM(IF(MONTH(CheckOut) = 2, 1, 0)) AS february, "
         + "SUM(IF(MONTH(CheckOut) = 3, 1, 0)) AS march, "
         + "SUM(IF(MONTH(CheckOut) = 4, 1, 0)) AS april, "
         + "SUM(IF(MONTH(CheckOut) = 5, 1, 0)) AS may, "
         + "SUM(IF(MONTH(CheckOut) = 6, 1, 0)) AS june, "
         + "SUM(IF(MONTH(CheckOut) = 7, 1, 0)) AS july, "
         + "SUM(IF(MONTH(CheckOut) = 8, 1, 0)) AS august, "
         + "SUM(IF(MONTH(CheckOut) = 9, 1, 0)) AS september, "
         + "SUM(IF(MONTH(CheckOut) = 10, 1, 0)) AS october, "
         + "SUM(IF(MONTH(CheckOut) = 11, 1, 0)) AS november, "
         + "SUM(IF(MONTH(CheckOut) = 12, 1, 0)) AS december, "
         + "COUNT(*) AS total "
         + "FROM ProjectA_rooms rm "
         + "INNER JOIN ProjectA_reservations res "
         + "ON rm.RoomId = res.Room "
         + "GROUP BY rm.RoomName;");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            String str5 = result.getString(5);
            String str6 = result.getString(6);
            String str7 = result.getString(7);
            String str8 = result.getString(8);
            String str9 = result.getString(9);
            String str10 = result.getString(10);
            String str11 = result.getString(11);
            String str12 = result.getString(12);
            String str13 = result.getString(13);
            String str14 = result.getString(14);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4
            + ", " + str5 + ", " + str6 + ", " + str7 + ", " + str8 + ", " + str9 + ", "
            + str10 + ", " + str11 + ", " + str12 + ", " + str13 + ", " + str14);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }
   }

   private static void revDays() {
      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT rm.RoomName, SUM(IF(MONTH(CheckOut) = 1, DATEDIFF(CheckOut, CheckIn), 0)) AS january, "
         + "SUM(IF(MONTH(CheckOut) = 2, DATEDIFF(CheckOut, CheckIn), 0)) AS february, "
         + "SUM(IF(MONTH(CheckOut) = 3, DATEDIFF(CheckOut, CheckIn), 0)) AS march, "
         + "SUM(IF(MONTH(CheckOut) = 4, DATEDIFF(CheckOut, CheckIn), 0)) AS april, "
         + "SUM(IF(MONTH(CheckOut) = 5, DATEDIFF(CheckOut, CheckIn), 0)) AS may, "
         + "SUM(IF(MONTH(CheckOut) = 6, DATEDIFF(CheckOut, CheckIn), 0)) AS june, "
         + "SUM(IF(MONTH(CheckOut) = 7, DATEDIFF(CheckOut, CheckIn), 0)) AS july, "
         + "SUM(IF(MONTH(CheckOut) = 8, DATEDIFF(CheckOut, CheckIn), 0)) AS august, "
         + "SUM(IF(MONTH(CheckOut) = 9, DATEDIFF(CheckOut, CheckIn), 0)) AS september, "
         + "SUM(IF(MONTH(CheckOut) = 10, DATEDIFF(CheckOut, CheckIn), 0)) AS october, "
         + "SUM(IF(MONTH(CheckOut) = 11, DATEDIFF(CheckOut, CheckIn), 0)) AS november, "
         + "SUM(IF(MONTH(CheckOut) = 12, DATEDIFF(CheckOut, CheckIn), 0)) AS december, "
         + "SUM(DATEDIFF(CheckOut, CheckIn)) AS total "
         + "FROM ProjectA_rooms rm "
         + "INNER JOIN ProjectA_reservations res "
         + "ON rm.RoomId = res.Room "
         + "GROUP BY rm.RoomName;");
         boolean f = result.next();
         while (f) {
               String str1 = result.getString(1);
               String str2 = result.getString(2);
               String str3 = result.getString(3);
               String str4 = result.getString(4);
               String str5 = result.getString(5);
               String str6 = result.getString(6);
               String str7 = result.getString(7);
               String str8 = result.getString(8);
               String str9 = result.getString(9);
               String str10 = result.getString(10);
               String str11 = result.getString(11);
               String str12 = result.getString(12);
               String str13 = result.getString(13);
               String str14 = result.getString(14);
               System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4
               + ", " + str5 + ", " + str6 + ", " + str7 + ", " + str8 + ", " + str9 + ", "
               + str10 + ", " + str11 + ", " + str12 + ", " + str13 + ", " + str14);
               f=result.next();
            }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }
   }

   private static void revRevenue() {
      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT rm.RoomName, SUM(IF(MONTH(CheckOut) = 1, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS january, "
         + "SUM(IF(MONTH(CheckOut) = 2, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS february, "
         + "SUM(IF(MONTH(CheckOut) = 3, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS march, "
         + "SUM(IF(MONTH(CheckOut) = 4, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS april, "
         + "SUM(IF(MONTH(CheckOut) = 5, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS may, "
         + "SUM(IF(MONTH(CheckOut) = 6, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS june, "
         + "SUM(IF(MONTH(CheckOut) = 7, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS july, "
         + "SUM(IF(MONTH(CheckOut) = 8, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS august, "
         + "SUM(IF(MONTH(CheckOut) = 9, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS september, "
         + "SUM(IF(MONTH(CheckOut) = 10, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS october, "
         + "SUM(IF(MONTH(CheckOut) = 11, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS november, "
         + "SUM(IF(MONTH(CheckOut) = 12, DATEDIFF(CheckOut, CheckIn) * res.rate, 0)) AS december, "
         + "SUM(DATEDIFF(CheckOut, CheckIn) * res.rate) AS total "
         + "FROM ProjectA_rooms rm "
         + "INNER JOIN ProjectA_reservations res "
         + "ON rm.RoomId = res.Room "
         + "GROUP BY rm.RoomName;");
         boolean f = result.next();
         while (f) {
               String str1 = result.getString(1);
               String str2 = result.getString(2);
               String str3 = result.getString(3);
               String str4 = result.getString(4);
               String str5 = result.getString(5);
               String str6 = result.getString(6);
               String str7 = result.getString(7);
               String str8 = result.getString(8);
               String str9 = result.getString(9);
               String str10 = result.getString(10);
               String str11 = result.getString(11);
               String str12 = result.getString(12);
               String str13 = result.getString(13);
               String str14 = result.getString(14);
               System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4
               + ", " + str5 + ", " + str6 + ", " + str7 + ", " + str8 + ", " + str9 + ", "
               + str10 + ", " + str11 + ", " + str12 + ", " + str13 + ", " + str14);
               f=result.next();
            }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }
   }

   // OR-2: END

   // OR-3: START
   // Logic for menu when (O)wner -> (S)tays is selected
   private static void reservationMenuLoop(){
      boolean exit = false;
      Scanner input = new Scanner(System.in);

      while (!exit) {
         displayReservationMenu();

         String[] tokens = input.nextLine().toLowerCase().split(" ");
         String[] datesOrRoom = tokens[0].split(":");

         int option = tokens.length;

         if(tokens[0].toLowerCase().equals("b")){
            return;
         }

         switch(option) {
         case 1:   if(datesOrRoom.length == 2){
                     resDates(datesOrRoom[0], datesOrRoom[1]);
                  } else if(datesOrRoom.length == 1){
                     resRoom(datesOrRoom[0]);
                  } else {
                     System.out.println("Wrong number of inputs");
                  };
                     break;
         case 2:   resDatesAndRoom(datesOrRoom[0], datesOrRoom[1], tokens[1]);
                     break;
         case 'b':   exit = true;
                     break;
         }
      }
   }

   // Reservation UI display
   private static void displayReservationMenu() {

      // Display UI
      System.out.println("Data Menu.\n\n"
         + "Enter range of dates (MM-DD:MM-DD) and/or room code:\n"
         + "- (B)ack - Goes back to main menu\n");
   }

   private static void resDatesAndRoom(String date1, String date2, String room){
      Scanner input = new Scanner(System.in);

      String[] date1arr = date1.split("-");
      String[] date2arr = date2.split("-");

      // Checking if dates are entered properly
      if(date1arr.length != 2 || date1arr[0].length() != 2 || date1arr[1].length() != 2){
         System.out.println("Improper date format... Returning to Owner menu");
         return;
      }
      if(date2arr.length != 2 || date2arr[0].length() != 2 || date2arr[1].length() != 2){
         System.out.println("Improper date format... Returning to Owner menu");
         return;
      }
      try{
         Integer.parseInt(date1arr[0]);
         Integer.parseInt(date1arr[1]);
         Integer.parseInt(date2arr[0]);
         Integer.parseInt(date2arr[1]);
      }
      catch(NumberFormatException nfe){
         System.out.println("Improper date units... Returning to Owner menu");
         return;
      }

      try {
         Statement s = conn.createStatement();

         ResultSet result = s.executeQuery("SELECT Code, Room, CheckIn, CheckOut "
         + "FROM ProjectA_reservations res "
         + "WHERE CheckIn >= '2010-" + date1 + "' AND CheckIn < '2010-" + date2 + "' AND Room = '" + room + "';");
         System.out.println("Reservations based on search:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

      // mysql query to show all info for reservation
      System.out.println("\nSelect a reservation number to view full reservation:\n");
      String[] resChoice = input.nextLine().toLowerCase().split(" ");

      String option = resChoice[0];
      System.out.println("option chosen: " + option);

      try{
         Integer.parseInt(option);
      }
      catch(NumberFormatException nfe){
         System.out.println("Not a number... Returning");
         return;
      }

      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT * "
         + "FROM ProjectA_reservations "
         + "WHERE Code = " + option + ";");
         System.out.println("Full Conflicting Reservation:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            String str5 = result.getString(5);
            String str6 = result.getString(6);
            String str7 = result.getString(7);
            String str8 = result.getString(8);
            String str9 = result.getString(9);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4
            + ", " + str5 + ", " + str6 + ", " + str7 + ", " + str8 + ", " + str9);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }
   }

   private static void resRoom(String room){
      Scanner input = new Scanner(System.in);

      try {
         Statement s = conn.createStatement();

         ResultSet result = s.executeQuery("SELECT Code, Room, CheckIn, CheckOut "
         + "FROM ProjectA_reservations res "
         + "WHERE Room = '" + room + "';");
         System.out.println("Reservations based on search:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

      // mysql query to show all info for reservation
      System.out.println("\nSelect a reservation number to view full reservation:\n");
      String[] resChoice = input.nextLine().toLowerCase().split(" ");

      String option = resChoice[0];
      System.out.println("option chosen: " + option);

      try{
         Integer.parseInt(option);
      }
      catch(NumberFormatException nfe){
         System.out.println("Not a number... Returning");
         return;
      }

      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT * "
         + "FROM ProjectA_reservations "
         + "WHERE Code = " + option + ";");
         System.out.println("Full Conflicting Reservation:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            String str5 = result.getString(5);
            String str6 = result.getString(6);
            String str7 = result.getString(7);
            String str8 = result.getString(8);
            String str9 = result.getString(9);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4
            + ", " + str5 + ", " + str6 + ", " + str7 + ", " + str8 + ", " + str9);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }
   }

   private static void resDates(String date1, String date2){
      Scanner input = new Scanner(System.in);

      String[] date1arr = date1.split("-");
      String[] date2arr = date2.split("-");

      // Checking if dates are entered properly
      if(date1arr.length != 2 || date1arr[0].length() != 2 || date1arr[1].length() != 2){
         System.out.println("Improper date format... Returning to Owner menu");
         return;
      }
      if(date2arr.length != 2 || date2arr[0].length() != 2 || date2arr[1].length() != 2){
         System.out.println("Improper date format... Returning to Owner menu");
         return;
      }
      try{
         Integer.parseInt(date1arr[0]);
         Integer.parseInt(date1arr[1]);
         Integer.parseInt(date2arr[0]);
         Integer.parseInt(date2arr[1]);
      }
      catch(NumberFormatException nfe){
         System.out.println("Improper date units... Returning to Owner menu");
         return;
      }

      try {
         Statement s = conn.createStatement();

         ResultSet result = s.executeQuery("SELECT Code, Room, CheckIn, CheckOut "
         + "FROM ProjectA_reservations res "
         + "WHERE CheckIn >= '2010-" + date1 + "' AND CheckIn < '2010-" + date2 + "';");
         System.out.println("Reservations based on search:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

      // mysql query to show all info for reservation
      System.out.println("\nSelect a reservation number to view full reservation:\n");
      String[] resChoice = input.nextLine().toLowerCase().split(" ");

      String option = resChoice[0];
      System.out.println("option chosen: " + option);

      try{
         Integer.parseInt(option);
      }
      catch(NumberFormatException nfe){
         System.out.println("Not a number... Returning");
         return;
      }

      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT * "
         + "FROM ProjectA_reservations "
         + "WHERE Code = " + option + ";");
         System.out.println("Full Conflicting Reservation:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            String str5 = result.getString(5);
            String str6 = result.getString(6);
            String str7 = result.getString(7);
            String str8 = result.getString(8);
            String str9 = result.getString(9);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4
            + ", " + str5 + ", " + str6 + ", " + str7 + ", " + str8 + ", " + str9);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }


   }
   // OR-3: END

   // OR-4: START
   // Logic for menu when (O)wner -> (R)ooms is selected
   private static void roomMenuLoop(){
      boolean exit = false;
      Scanner input = new Scanner(System.in);

      while (!exit) {
         displayRoomMenu();

         // display list of rooms
         try {
            Statement s = conn.createStatement();
            ResultSet result = s.executeQuery("SELECT RoomId, RoomName "
            + "FROM ProjectA_rooms;");
            System.out.println("Room List:\n");
            boolean f = result.next();
            while (f) {
               String str1 = result.getString(1);
               String str2 = result.getString(2);
               System.out.println(str1 + ", " + str2);
               f=result.next();
            }
            System.out.println("\n");
         }
         catch (Exception ee) {
            System.out.println("ee129: " + ee);
         }

         String[] tokens = input.nextLine().toLowerCase().split(" ");

         char option = tokens[0].charAt(0);
         System.out.println("option chosen: " + option);


         switch(option) {
         case '1':   roomRoom(tokens[1]);
                     break;
         case '2':   roomReservations(tokens[1]);
                     break;
         case 'b':   exit = true;
                     break;
         }
      }
   }

   // Room UI display
   private static void displayRoomMenu() {

      // Display UI
      System.out.println("Occupancy Menu.\n\n"
         + "Choose an option (add desired room code after choice):\n"
         + "- (1) Room Info\n"
         + "- (2) Reservation Info\n"
         + "- (B)ack - Goes back to main menu\n");
   }

   // Option 1 of displayOccupancyMenu
   private static void roomRoom(String room){
      Scanner input = new Scanner(System.in);

      try {
         Statement s = conn.createStatement();

         ResultSet result = s.executeQuery("SELECT DISTINCT rm.RoomId, rm.RoomName, rm.Beds, rm.BedType, rm.MaxOcc, rm.BasePrice, rm.Decor "
         + "FROM ProjectA_rooms rm "
         + "INNER JOIN ProjectA_reservations res "
         + "ON rm.RoomId = res.Room "
         + "WHERE Room = '" + room + "';");
         System.out.println("Room based on search:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            String str5 = result.getString(5);
            String str6 = result.getString(6);
            String str7 = result.getString(7);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4 + ", " + str5 + ", " + str6 + ", " + str7);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

      // • total number of nights of occupancy for the room in 2010
      try {
         Statement s = conn.createStatement();

         ResultSet result = s.executeQuery("SELECT SUM(IF(YEAR(CheckOut) = 2010, DATEDIFF(CheckOut, CheckIn), DATEDIFF('2010-12-31', CheckIn))) AS totalNights "
         + "FROM ProjectA_rooms rm "
         + "INNER JOIN ProjectA_reservations res "
         + "ON rm.RoomId = res.Room "
         + "WHERE Room = '" + room + "' AND YEAR(CheckIn) = 2010 "
         + "GROUP BY rm.RoomName;");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            System.out.println(str1);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

      // percent of time the room is occupied
      try {
         Statement s = conn.createStatement();

         ResultSet result = s.executeQuery("SELECT SUM(IF(YEAR(CheckOut) = 2010, DATEDIFF(CheckOut, CheckIn), DATEDIFF('2010-12-31', CheckIn))) * 100 / 365 AS totalNights "
         + "FROM ProjectA_rooms rm "
         + "INNER JOIN ProjectA_reservations res "
         + "ON rm.RoomId = res.Room "
         + "WHERE Room = '" + room + "' AND YEAR(CheckIn) = 2010 "
         + "GROUP BY rm.RoomName;");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            System.out.println(str1);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

      // total revenue the room has generated in 2010
      try {
         Statement s = conn.createStatement();

         ResultSet result = s.executeQuery("SELECT SUM(IF(YEAR(CheckOut) = 2010, DATEDIFF(CheckOut, CheckIn) * res.Rate, DATEDIFF('2010-12-31', CheckIn) * res.Rate )) AS totalNights "
         + "FROM ProjectA_rooms rm "
         + "INNER JOIN ProjectA_reservations res "
         + "ON rm.RoomId = res.Room "
         + "WHERE Room = '" + room + "' AND YEAR(CheckIn) = 2010 "
         + "GROUP BY rm.RoomName;");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            System.out.println(str1);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

   // percent of the overall 2010 revenue generated by the room
   try {
      Statement s = conn.createStatement();
      ResultSet result = s.executeQuery("SELECT SUM(IF(YEAR(CheckOut) = 2010, DATEDIFF(CheckOut, CheckIn) * res.Rate, DATEDIFF('2010-12-31', CheckIn) * res.Rate )) * 100 "
      + "/ (SELECT SUM(IF(YEAR(CheckOut) = 2010, DATEDIFF(CheckOut, CheckIn) * res.Rate, DATEDIFF('2010-12-31', CheckIn) * res.Rate )) AS totalNights "
      + "FROM ProjectA_rooms rm "
      + "INNER JOIN ProjectA_reservations res ON rm.RoomId = res.Room WHERE YEAR(CheckIn) = 2010) "
      + "FROM ProjectA_rooms rm "
      + "INNER JOIN ProjectA_reservations res "
      + "ON rm.RoomId = res.Room "
      + "WHERE Room = '" + room + "' AND YEAR(CheckIn) = 2010 "
      + "GROUP BY rm.RoomName;");
      boolean f = result.next();
      while (f) {
         String str1 = result.getString(1);
         System.out.println(str1);
         f=result.next();
      }
   }
   catch (Exception ee) {
      System.out.println("ee129: " + ee);
   }

}

   private static void roomReservations(String room){
      Scanner input = new Scanner(System.in);

      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT Code, Room, CheckIn, CheckOut "
         + "FROM ProjectA_reservations res "
         + "WHERE res.ROOM = '" + room + "' ORDER BY CheckIn;");
         System.out.println("Reservations for room:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }

      // mysql query to show all info for reservation
      System.out.println("\nSelect a reservation number to view full reservation:\n");
      String[] resChoice = input.nextLine().toLowerCase().split(" ");

      String option = resChoice[0];
      System.out.println("option chosen: " + option);

      try{
         Integer.parseInt(option);
      }
      catch(NumberFormatException nfe){
         System.out.println("Not a number... Returning");
         return;
      }

      try {
         Statement s = conn.createStatement();
         ResultSet result = s.executeQuery("SELECT * "
         + "FROM ProjectA_reservations "
         + "WHERE Code = " + option + ";");
         System.out.println("Full Reservation:\n");
         boolean f = result.next();
         while (f) {
            String str1 = result.getString(1);
            String str2 = result.getString(2);
            String str3 = result.getString(3);
            String str4 = result.getString(4);
            String str5 = result.getString(5);
            String str6 = result.getString(6);
            String str7 = result.getString(7);
            String str8 = result.getString(8);
            String str9 = result.getString(9);
            System.out.println(str1 + ", " + str2 + ", " + str3 + ", " + str4
            + ", " + str5 + ", " + str6 + ", " + str7 + ", " + str8 + ", " + str9);
            f=result.next();
         }
      }
      catch (Exception ee) {
         System.out.println("ee129: " + ee);
      }
   }



   // OR-4: END

   // CHRIS MAKING METHODS FOR OWNER LOOP: END

   // Program loop for guest subsystem
   private static void guestLoop() {
      boolean exit = false;
      Scanner input = new Scanner(System.in);

      while (!exit) {
         displayGuest();

         char option = input.next().toLowerCase().charAt(0);

         switch(option) {
            case 'r':   roomsAndRates();
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

   // Guest Methods
   private static void roomsAndRates() {
       String usage = "Usage: <checkIn> <checkOut>" + "\nDate Format: yyyy-mm-dd";
       boolean done = false;
       LocalDate start, end;
       start = end = null;
       String[] input = null;
       displayTable("rooms", currentStatus());
       System.out.print("Enter room code: ");
       Scanner sc = new Scanner(System.in);
       String room = sc.nextLine();
        while (!done) {
            System.out.print("Enter dates: ");
            input = sc.nextLine().toLowerCase().split(" ");
            for (String s : input) System.out.println(s);
            if (input.length != 2) {
                System.out.println(usage);
            }
            else {
                try {
                    String[] s = input[0].split("-");
                    start.of(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                    String[] s2 = input[1].split("-");
                    start.of(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]), Integer.parseInt(s2[2])) ;
                    done = true;
                    System.out.println(start.toString());
                    System.out.println(end.toString());
                } catch (Exception e) {
                    System.out.println(usage);
                }
            }
        }
       checkAvailability(start, end);

   }

   private static void checkAvailability(LocalDate start, LocalDate end) {
       System.out.println("Checking availability...");
       System.out.println("START: " + start + "\nEND: " + end);
       while (!start.isAfter(end)) {
           System.out.println(start);
           start.plusDays(1);
       }
       // try {
       //    Statement s1 = conn.createStatement();
       //    s1.executeUpdate("DROP TABLE ProjectA_rooms");
       //    s1.close();
       // }
       // catch (Exception ee) {
       //    System.out.println("Error: " + ee);
       // }
       //
       //  for (int i = 0; i < dates; i++) {
       //
       //  }
       //  String sql = "SELECT * Employees set age=? WHERE id=?";
       //  stmt = conn.prepareStatement(sql);
       //
       //  //Bind values into the parameters.
       //  stmt.setInt(1, 35);  // This would set age
       //  stmt.setInt(2, 102); // This would set ID
       //
       //  // Let us update age of the record with ID = 102;
       //  int rows = stmt.executeUpdate();
       //  System.out.println("Rows impacted : " + rows );
       //
       //  // Let us select all the records and display them.
       //  sql = "SELECT id, first, last, age FROM Employees";
       //  ResultSet rs = stmt.executeQuery(sql);
   }
}
