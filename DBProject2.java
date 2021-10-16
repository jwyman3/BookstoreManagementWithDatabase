import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.util.*;
import java.time.*;
import java.util.Date;

public class DBProject2 {
    public static void main(String args[]) throws Exception{
        int choice = 0;
        Scanner scan = new Scanner(System.in);
        do {
        System.out.println("Please Make a selection \n 1) User \n 2) Admin \n 3) Exit \n Selection: ");
        choice = scan.nextInt();

        switch (choice) {
            case 1:
                user();
                break;
            case 2:
                admin();
                break;
            case 3:
                break;
            default:
                System.out.println("Invalid Selection");
        }
        }while(choice != 3);
        }//main

        public static Connection getConnection() throws Exception{
        try{
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost/BookStore";
            String uid = "root";
            String pwd = "bobcat33";
            Connection conn=DriverManager.getConnection(  url, uid, pwd);
            //System.out.println("Successfully Connected to Database!");
            return conn;
        }catch(Exception e){System.out.println(e);}
        return null;
        }

        public static void user(){
            int choice = 0;
            int uid = 0;
            Scanner scan = new Scanner(System.in);
            scan.useDelimiter("\n");
            do {
                System.out.println("Enter User Id:");
                uid = scan.nextInt();
                System.out.println("Please Make a selection \n 1) Search \n 2) Checkout (Rent and Buy) \n 3) Return \n 4) Check Balance and Late Fees \n 5) Exit \n Selection:");
                choice = scan.nextInt();

                switch (choice) {
                    case 1:
                        search(scan);
                        break;
                    case 2:
                        checkout(scan, uid);
                        break;
                    case 3:
                        returnBook(scan, uid);
                        break;
                    case 4:
                        balance(scan, uid);
                        break;
                    case 5:
                        break;
                    default:
                        System.out.println("Invalid Selection");
                }
            }while(choice == 0);

        }//user

        public static void search(Scanner scan){
        String lookup;
        char c = '"';

            try{
                Connection conn = getConnection();
                System.out.println("Enter a Book Title:");
                lookup = scan.next();
                PreparedStatement booksearch = conn.prepareStatement("SELECT * FROM search where BK_TITLE = " + c + lookup + c );
                ResultSet result = booksearch.executeQuery();
                System.out.println("ID \t |Title \t\t |ISBN \t |Format \t |Rent or Sell \t |Location");
                while(result.next()){
                    System.out.println(result.getInt("BK_ID")+ "\t" + result.getString("BK_TITLE") + "\t" +result.getString("BK_ISBN") + "\t" +result.getString("BK_FORMAT")+ "\t" + result.getString("BK_RENT_SELL") + "\t" + result.getString("BK_LOCATION"));
                }

            }catch (Exception e){
                System.out.println("An error has occurred! " + e);
            }
            System.out.println();
        }//search by title only



        public static void checkout(Scanner scan, int uid){
        int bkID;
        int type;
        int cc;

                try{
                    Connection conn = getConnection();
                    System.out.println("Enter Book ID:");
                    bkID = scan.nextInt();
                    PreparedStatement bookCost = conn.prepareStatement("SELECT * FROM book_inventory where BK_ID = " + bkID + " LIMIT 1");
                    ResultSet result = bookCost.executeQuery();
                    while(result.next()) {
                        System.out.println("Cost of Book: $" + result.getInt("BK_PRICE"));
                    }
                    System.out.println("Enter CC Number: ");
                    cc = scan.nextInt();
                    PreparedStatement statement = conn.prepareStatement("INSERT INTO INVOICE (BK_ID, USE_CODE, INV_DATE, INV_TOTAL, INV_AMTPAID, INV_CARD_NUMBER, INV_TYPE) values (" + bkID + " , " + uid + " , " + java.time.LocalDate.now() + " , " + " BK_PRICE from book_sell where " + bkID +" = BK_ID , " + " BK_PRICE from book_sell where " + bkID +" = BK_ID , " + cc + ", Rent");
                    statement.executeUpdate();
                }catch (Exception e){
                    System.out.println("An error has occurred! " + e);
                }
        }//checkout

        public static void returnBook(Scanner scan, int uid){
            int bkID;
            System.out.println("Enter Book ID:");
            bkID = scan.nextInt();

            System.out.println("Late Fee Applied to Account");

        }//returnbook

        public static void balance(Scanner scan, int uid){
            try {
                Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT * from USER where USE_CODE = " + uid + " LIMIT 1");
                //add SQL statement to find balance for user UID
                ResultSet result = statement.executeQuery();
                 while(result.next()) {
                     System.out.println("Balance: $" + result.getInt("USE_BALANCE"));
                     System.out.println("Late Fees: $" + result.getInt("USE_LATE_FEES") + "\n");
                 }
            } catch(Exception e){
                System.out.println("An Error has occurred! " + e);
            }
        }//balance

        public static void admin(){
            int choice = 0;
            Scanner scan = new Scanner(System.in);
            do {
                System.out.println("Please Make a selection \n 1) Locate Title \n 2) Update Inventory \n 3) Check User Balances and Late Fees \n 4) Generate Reports \n Selection:");
                choice = scan.nextInt();

                switch (choice) {
                    case 1:
                        locate(scan);
                        break;
                    case 2:
                        updateInventory(scan);
                        break;
                    case 3:
                        adminCheckBalances(scan);
                        break;
                    case 4:
                        reports(scan);
                        break;
                    default:
                        System.out.println("Invalid Selection");
                }
            }while(choice == 0);
        }//admin

        public static void locate(Scanner scan){
            int bkID;
            System.out.println("Enter Book ID:");
            bkID = scan.nextInt();
            try{
                Connection conn = getConnection();
                PreparedStatement locate = conn.prepareStatement("Select * from book_inventory where book_inventory.BK_ID = " + bkID);
                ResultSet result = locate.executeQuery();
                System.out.println("Location: ");
                while(result.next()){
                    System.out.println(result.getString("BK_LOCATION"));
                }
            }catch (Exception e){
                System.out.println("An error has occurred! " + e);
            }

        }//locate

        public static void updateInventory(Scanner scan) {
            int selection = 0;
            do {
                System.out.println("Please Make a selection \n 1) Add new Book Title\n 2) Add new Book Inventory");
                selection = scan.nextInt();
                scan.useDelimiter("\n");
                switch (selection) {
                    case 1:
                        String isbn;
                        String title;
                        String pubID;
                        String bkRelease;
                        String format;
                        boolean bkDisplay;

                        System.out.println("Enter a ISBN-13: ");
                        isbn = scan.next();

                        System.out.println("Enter the Book Title");
                        title = scan.next();

                        System.out.println("Enter the Publisher ID: ");
                        pubID = scan.next();
                        int pubIDINT = Integer.parseInt(pubID);

                        System.out.println("Enter the book release date: (Format yyyy-mm-dd)");
                        bkRelease = scan.next();

                        System.out.println("Enter the Format: (PaperBack, HardCover, EBook, AudioBook, Large Print)");
                        format = scan.next();

                        bkDisplay = true;
                        try{
                            Connection conn = getConnection();
                            PreparedStatement addBook = conn.prepareStatement("Insert into book_title (BK_ISBN, BK_TITLE, PUB_ID, BK_RELEASE, BK_FORMAT) values ('" + isbn + "', '" + title + "', '" + pubIDINT + "', '" + bkRelease + "', '" + format + "')");
                            addBook.executeUpdate();
                        }catch (Exception e){
                            System.out.println("An error has occurred! " + e);
                        }
                        break;
                    case 2:
                        String BK_ISBN;
                        String BK_STATUS = "Available";
                        float BK_PRICE;
                        String BK_CONDITION;
                        LocalDate BK_INV_DATE = LocalDate.now();
                        String BK_RENT_SELL;
                        String BK_LOCATION;

                        System.out.println("Enter a ISBN-13: ");
                        BK_ISBN = scan.next();

                        System.out.println("Enter a price: (ex: 19.99");
                        BK_PRICE = scan.nextFloat();

                        System.out.println("Enter the Book Condition: (New or Used)");
                        BK_CONDITION = scan.next();

                        System.out.println("Enter for Rent or Sell: (Rent or Sell) ");
                        BK_RENT_SELL = scan.next();

                        System.out.println("Enter Book Location: ");
                        BK_LOCATION = scan.next();

                        try{
                            Connection conn = getConnection();
                            PreparedStatement addBook = conn.prepareStatement("Insert into book_inventory (BK_ISBN, BK_PRICE, BK_STATUS, BK_CONDITION, BK_INV_DATE, BK_RENT_SELL, BK_LOCATION) values ('" + BK_ISBN + "', " + BK_PRICE +", '" + BK_STATUS + "', '" + BK_CONDITION + "', '" + BK_INV_DATE + "', '" + BK_RENT_SELL +"', '" + BK_LOCATION + "')");
                            addBook.executeUpdate();
                        }catch (Exception e){
                            System.out.println("An error has occurred! " + e);
                        }
                        break;
                    default:
                        System.out.println("Invalid Selection");
                }
            }while (selection == 0) ;

        }//updateInventory

        public static void adminCheckBalances(Scanner scan){
            try {
                Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT * from USER");
                //add SQL statement to find balance for user UID
                ResultSet result = statement.executeQuery();
               //ArrayList<String> array = new ArrayList<String>();
                System.out.println("First| Last| Balance| Late Fees");
               while(result.next()){
                   System.out.println(result.getString("USE_FNAME") + "\t" + result.getString("USE_LNAME") + "\t $" + result.getFloat("USE_BALANCE") + result.getFloat("USE_LATE_FEES"));
               }
            } catch(Exception e){
                System.out.println("An Error has occurred! " + e);
            }
        }//adminCheckBalances

        public static void reports(Scanner scan){
            String dateOne;
            String dateTwo;

            System.out.println("Enter beginning report date: (dd-mm-yyy)");
            dateOne = scan.next();
            System.out.println("Enter ending report date: (dd-mm-yyyy)");
            dateTwo = scan.next();

        //Start of Revenue Report
            try {
                Connection conn = getConnection();
                PreparedStatement revenue = conn.prepareStatement("SELECT DISTINCT INV_NUMBER, FORMAT(sum(INV_TOTAL), 2) AS \"INV Total\" FROM INVOICE as I WHERE\t" + dateOne +" > I.INV_DATE >= " + dateTwo);
                ResultSet resultR = revenue.executeQuery();
                PreparedStatement amtPaid = conn.prepareStatement("SELECT DISTINCT INV_NUMBER, FORMAT(sum(INV_AMTPAID), 2) AS \"AMTPAID Total\" FROM INVOICE as A WHERE\t" + dateOne +" > A.INV_DATE >= " + dateTwo);
                ResultSet resultA = amtPaid.executeQuery();
                while(resultR.next()) {
                    System.out.println("Total Invoice: $" + resultR.getFloat("INV Total"));
                }
                while(resultA.next()) {
                    System.out.println("Total Amount Paid: $" + resultA.getFloat("AMTPAID Total"));
                }

            } catch(Exception e){
                System.out.println("An Error has occurred! " + e);
            }
        // End of Revenue Report

        }//reports


    }
