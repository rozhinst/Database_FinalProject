import com.mysql.cj.protocol.Resultset;

import java.sql.*;
import java.util.Scanner;

public class Main {
    static int id = -1;
    static boolean signedIn = false;
    static  boolean loop = true;


    public static void signUp(Scanner sc, Connection con) throws SQLException {
        System.out.println("Username:");
        String username = sc.nextLine();
        System.out.println("First Name:");
        String firstName = sc.nextLine();
        System.out.println("Last Name:");
        String lastName = sc.nextLine();
        System.out.println("Address:");
        String address = sc.nextLine();
        System.out.println("Phone:");
        String phone = sc.nextLine();
        System.out.println("Password:");
        String password = sc.nextLine();
        System.out.println("Category:");
        String category = sc.nextLine();
        CallableStatement st = con.prepareCall("{call sign_up(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
        st.setString(1,username);
        st.setString(2,firstName);
        st.setString(3,lastName);
        st.setString(4,address);
        st.setString(5,phone);
        st.setString(6,password);
        st.setString(7, category);
        st.registerOutParameter(8, Types.VARCHAR);
        st.registerOutParameter(9, Types.VARCHAR);
        st.registerOutParameter(10, Types.VARCHAR);
        st.registerOutParameter(11, Types.VARCHAR);
        st.registerOutParameter(12, Types.VARCHAR);
        st.registerOutParameter(13, Types.VARCHAR);

        st.execute();
        if(!st.getString(8).equals("")){
            System.out.println(st.getString(8));
        }
        if(!st.getString(9).equals("")){
            System.out.println(st.getString(9));
        }
        if(!st.getString(10).equals("")){
            System.out.println(st.getString(10));
        }
        if(!st.getString(11).equals("")){
            System.out.println(st.getString(11));
        }
        if(!st.getString(12).equals("")){
            System.out.println(st.getString(12));
        }
        if(!st.getString(13).equals("")){
            System.out.println(st.getString(13));
        }

    }

    public static void signIn(Scanner sc, Connection con) throws SQLException {
        System.out.println("Username:");
        String username = sc.nextLine();
        System.out.println("Password:");
        String pass = sc.nextLine();

        CallableStatement st = con.prepareCall("{call sign_in(?,?,?,?)}");
        st.setString(1,username);
        st.setString(2,pass);
        st.registerOutParameter(3, Types.VARCHAR);
        st.registerOutParameter(4, Types.INTEGER);
        st.execute();
        System.out.println(st.getString(3));
        id = st.getInt(4);
        if(st.getString(3).equals("logged in successfully")){
            signedIn = true;
            loop = true;
        }
    }

    public static void  getMyDetails(Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call get_my_details(?)}");
        st.setInt(1,id);
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        while(set.next()) {
            System.out.println("UserId: " + set.getInt("UserId"));
            System.out.println("FirstName: " + set.getString("FirstName"));
            System.out.println("LastName: " + set.getString("LastName"));
            System.out.println("Phone: " + set.getString("Phone"));
            System.out.println("Address: " + set.getString("Address"));
            System.out.println("Username: " + set.getString("Username"));
            System.out.println("Balance: " + set.getDouble("Balance"));
            System.out.println("Status: " + set.getString("accountSatus"));
            System.out.println("CreationDate: " + set.getDate("CreationDate"));
            if (set.getString("ProhibitionDate") != null) {
                System.out.println("Prohibition Date: " + set.getString("ProhibitionDate"));
            }
        }
    }

    public static void printBookDetails(ResultSet set) throws SQLException {
        while(set.next()) {
            System.out.println("BookId: " + set.getInt("BookID"));
            System.out.println("Title: " + set.getString("Title"));
            System.out.println("Author: " + set.getString("PublisherName"));
            System.out.println("Genre: " + set.getString("Genre"));
            System.out.println("Edition: " + set.getInt("Edition"));
            System.out.println("Published Date: " + set.getDate("PublishedDate"));
            System.out.println("Page Number: " + set.getInt("PageNumber"));
            System.out.println("Tag: " + set.getString("type"));
            System.out.println("Price: " + set.getDouble("Price"));
            System.out.println("...................................................................");
        }
    }

    public static void searchByName(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon
        CallableStatement st = con.prepareCall("{call search_by_name(?)}");
        System.out.println("Enter the book's name:");
        st.setString(1,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);

    }

    public static void searchByEdition(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon
        CallableStatement st = con.prepareCall("{call search_by_edition(?)}");
        System.out.println("Enter the book's edition:");
        st.setString(1,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByPublishedDate(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon
        CallableStatement st = con.prepareCall("{call search_by_published_date(?)}");
        System.out.println("Enter the book's published date:");
        st.setString(1,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByPublisher(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon
        CallableStatement st = con.prepareCall("{call search_by_publisher(?)}");
        System.out.println("Enter the book's publisher's name:");
        st.setString(1,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByPublisherTitle(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon
        CallableStatement st = con.prepareCall("{call search_by_publisher_title(?,?)}");
        System.out.println("Enter the book's name:");
        st.setString(1,sc.nextLine());
        System.out.println("Enter the book's publisher's name:");
        st.setString(2,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByPublisherEdition(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon and def check
        CallableStatement st = con.prepareCall("{call search_by_publisher_edition(?,?)}");
        System.out.println("Enter the book's edition:");
        st.setInt(1,sc.nextInt());
        sc.nextLine();
        System.out.println("Enter the book's publisher's name:");
        st.setString(2,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByPublisherDate(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon and def check
        CallableStatement st = con.prepareCall("{call search_by_publisher_date(?,?)}");
        System.out.println("Enter the book's published date:");
        st.setString(1, sc.nextLine());//?
        System.out.println("Enter the book's publisher's name:");
        st.setString(2,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByTitleDate(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon and def check
        CallableStatement st = con.prepareCall("{call search_by_title_date(?,?)}");
        System.out.println("Enter the book's title:");
        st.setString(1, sc.nextLine());
        System.out.println("Enter the book's published date:");
        st.setString(2,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByTitleEdition(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon and def check
        CallableStatement st = con.prepareCall("{call search_by_title_edition(?,?)}");
        System.out.println("Enter the book's title:");
        st.setString(1, sc.nextLine());
        System.out.println("Enter the book's edition:");
        st.setInt(2,sc.nextInt());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByEditionDate(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon and def check
        CallableStatement st = con.prepareCall("{call search_by_edition_date(?,?)}");
        System.out.println("Enter the book's published date:");
        st.setString(1, sc.nextLine());
        System.out.println("Enter the book's edition:");
        st.setInt(2,sc.nextInt());
        sc.nextLine();
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByTitleEditionDate(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon and def check
        CallableStatement st = con.prepareCall("{call search_by_title_edition_date(?,?,?)}");
        System.out.println("Enter the book's name:");
        st.setString(1, sc.nextLine());
        System.out.println("Enter the book's edition:");
        st.setInt(2,sc.nextInt());
        sc.nextLine();
        System.out.println("Enter the book's published date:");
        st.setString(3,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByTitleEditionPublisher(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon and def check
        CallableStatement st = con.prepareCall("{call search_by_title_edition_publisher(?,?,?)}");
        System.out.println("Enter the book's name:");
        st.setString(1, sc.nextLine());
        System.out.println("Enter the book's edition:");
        st.setInt(2,sc.nextInt());
        sc.nextLine();
        System.out.println("Enter the book's publisher's name:");
        st.setString(3,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByTitleDatePublisher(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon and def check
        CallableStatement st = con.prepareCall("{call search_by_title_date_publisher(?,?,?)}");
        System.out.println("Enter the book's name:");
        st.setString(1, sc.nextLine());
        System.out.println("Enter the book's published date:");
        st.setString(2,sc.nextLine());
        System.out.println("Enter the book's publisher's name:");
        st.setString(3,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByDateEditionPublisher(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon and def check
        CallableStatement st = con.prepareCall("{call search_by_date_edition_publisher(?,?,?)}");
        System.out.println("Enter the book's published date:");
        st.setString(1,sc.nextLine());
        System.out.println("Enter the book's edition:");
        st.setInt(2, sc.nextInt());
        sc.nextLine();
        System.out.println("Enter the book's publisher's name:");
        st.setString(3,sc.nextLine());
        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void searchByTotal(Scanner sc, Connection con) throws SQLException {//chantayi ro search kon and def check
        CallableStatement st = con.prepareCall("{call search_by_title_edition_publisher_date(?,?,?,?)}");
        System.out.println("Enter the book's name");
        st.setString(1,sc.nextLine());
        System.out.println("Enter the book's edition:");
        st.setInt(2, sc.nextInt());
        sc.nextLine();
        System.out.println("Enter the book's publisher's name:");
        st.setString(3,sc.nextLine());
        System.out.println("Enter the book's published date:");
        st.setString(4,sc.nextLine());

        ResultSet set = st.executeQuery();
        System.out.println("Details:");
        printBookDetails(set);
    }

    public static void borrow(Scanner sc,Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call borrow(?,?,?)}");
        st.setInt(1,id);
        System.out.println("Enter the book's id");
        st.setInt(2,sc.nextInt());
        sc.nextLine();
        st.registerOutParameter(3, Types.VARCHAR);
        st.execute();
        System.out.println(st.getString(3));
    }

    public static void giveBookBack(Scanner sc,Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call give_book_back(?,?,?)}");
        st.setInt(1,id);
        System.out.println("Enter the book's id");
        st.setInt(2,sc.nextInt());
        sc.nextLine();
        st.registerOutParameter(3, Types.VARCHAR);
        st.execute();
        System.out.println(st.getString(3));
    }

    public static void increaseBalance(Scanner sc,Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call increase_balance(?,?,?)}");
        st.setInt(1,id);
        System.out.println("Enter the amount");
        st.setDouble(2,sc.nextDouble());
        st.registerOutParameter(3, Types.VARCHAR);
        st.execute();
        System.out.println(st.getString(3));
    }

    public static void addBook(Scanner sc,Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call  add_book(?,?,?,?,?,?,?,?,?,?)}");
        st.setInt(1,id);
        System.out.println("Enter the edition");
        st.setInt(2,sc.nextInt());
        sc.nextLine();
        System.out.println("Enter the price");
        st.setDouble(3,sc.nextDouble());
        System.out.println("Enter the page number");
        st.setInt(4,sc.nextInt());
        sc.nextLine();
        System.out.println("Enter book's title");
        st.setString(5,sc.nextLine());
        System.out.println("Enter Genre");
        st.setString(6,sc.nextLine());
        System.out.println("Enter the Tag");
        st.setString(7,sc.nextLine());
        System.out.println("Enter the published date");
        st.setString(8,sc.nextLine());
        System.out.println("Enter the publisher's name");
        st.setString(9,sc.nextLine());
        st.registerOutParameter(10, Types.VARCHAR);
        st.execute();
        System.out.println(st.getString(10));
    }

    public static void getSuccessList(Scanner sc,Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call  get_success_list(?,?,?)}");
        st.setInt(1,id);
        System.out.println("Enter page number (starts from 0)");
        st.setInt(2,sc.nextInt());
        sc.nextLine();
        st.registerOutParameter(3, Types.VARCHAR);
        st.executeQuery();
        System.out.println(st.getString(3));
        if(st.getString(3).equals("successfull")){
            ResultSet set = st.executeQuery();
            System.out.println("Details:");
            st.getMoreResults();
            set = st.getResultSet();
            while(set.next()) {
                System.out.println("Id: " + set.getInt("Id"));
                System.out.println("Message: " + set.getString("Message"));
                System.out.println("Date: " + set.getDate("CreationDate"));
                System.out.println("................................................................................................");
            }
        }
    }

    public static void printUserDetails(ResultSet set) throws SQLException {
        System.out.println("Details:");
        while(set.next()) {
            System.out.println("Id: " + set.getInt("UserId"));
            System.out.println("Username: " + set.getString("Username"));
            System.out.println("FirstName: " + set.getString("FirstName"));
            System.out.println("LastName: " + set.getString("LastName"));
            System.out.println("Address: " + set.getString("Address"));
            System.out.println("Phone: " + set.getString("Phone"));
            System.out.println("Status: " + set.getString("accountSatus"));
            System.out.println("Creation Date: " + set.getDate("CreationDate"));
            if(set.getString("ProhibitionDate")!= null){
                System.out.println("Prohibition Date: " + set.getDate("ProhibitionDate"));
            }
            System.out.println("................................................................................................");
        }
    }

    public static void adminSearchByUsername(Scanner sc,Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call search_by_username(?,?,?,?)}");
        st.setInt(1,id);
        System.out.println("Enter username");
        st.setString(2,sc.nextLine());
        System.out.println("Enter page number (starts from 0)");
        st.setInt(3,sc.nextInt());
        sc.nextLine();
        st.registerOutParameter(4, Types.VARCHAR);
        ResultSet set = st.executeQuery();
        System.out.println(st.getString(4));
        st.getMoreResults();
        set = st.getResultSet();
        if(st.getString(4).equals("successfull")){
            System.out.println("Details:");
            while(set.next()) {
                System.out.println("Id: " + set.getInt("Id"));
                System.out.println("Username: " + set.getString("Username"));
                System.out.println("FirstName: " + set.getString("FirstName"));
                System.out.println("LastName: " + set.getString("LastName"));
                System.out.println("Address: " + set.getString("Address"));
                System.out.println("Phone: " + set.getString("Phone"));
                System.out.println("Status: " + set.getString("accountSatus"));
                System.out.println("Creation Date: " + set.getDate("CreationDate"));
                if(set.getString("ProhibitionDate")!= null){
                    System.out.println("Prohibition Date: " + set.getDate("ProhibitionDate"));
                }
                System.out.println("................................................................................................");
            }
        }
    }
    public static void adminSearchByLastname(Scanner sc,Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call search_by_lastname(?,?,?,?)}");
        st.setInt(1,id);
        System.out.println("Enter lastname");
        st.setString(2,sc.nextLine());
        System.out.println("Enter page number (starts from 0)");
        st.setInt(3,sc.nextInt());
        sc.nextLine();
        st.registerOutParameter(4, Types.VARCHAR);
        ResultSet set = st.executeQuery();
        st.getMoreResults();
        set = st.getResultSet();
        System.out.println(st.getString(4));
        if(st.getString(4).equals("successfull")){
            printUserDetails(set);
        }
    }

    public static void searchHistory(Scanner sc,Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call search_history(?,?,?)}");
        st.setInt(1,id);
        System.out.println("Enter customer id");
        st.setInt(2,sc.nextInt());
        sc.nextLine();
        st.registerOutParameter(3, Types.VARCHAR);
        ResultSet set = st.executeQuery();
        String message;
        message = st.getString(3);
        System.out.println(message);
        st.getMoreResults();
        set = st.getResultSet();
        while(set.next()) {
            if(message.equals("successfull")){
                System.out.println("Id: " + set.getString("Id"));
                System.out.println("BorrowID: " + set.getString("BorrowID"));
                System.out.println("UserId: " + set.getString("UserId"));
                System.out.println("result: " + set.getString("result"));
                System.out.println("Prohibition Date: " + set.getString("ProhibitionDate"));
                System.out.println("Creation Date: " + set.getString("CreationDate"));
                System.out.println("...............................................................................");
            }
        }
    }

    public static void deleteUser(Scanner sc,Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call delete_user(?,?,?)}");
        st.setInt(1,id);
        System.out.println("Enter customer id");
        st.setInt(2,sc.nextInt());
        sc.nextLine();
        st.registerOutParameter(3, Types.VARCHAR);
        ResultSet set = st.executeQuery();
        System.out.println(st.getString(3));
    }

    public static void getPassedBooks(Scanner sc,Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call get_passed_books(?,?)}");
        st.setInt(1,id);
        st.registerOutParameter(2, Types.VARCHAR);
        ResultSet set = st.executeQuery();
        st.getMoreResults();
        set = st.getResultSet();
        System.out.println(st.getString(2));
        if(st.getString(2).equals("successfull")){
            while(set.next()) {
                System.out.println("Title: " + set.getString("Title"));
                System.out.println("................................................................................................");
            }
        }
    }

    public static void signOut(Connection con) throws SQLException {
        CallableStatement st = con.prepareCall("{call sign_out(?)}");
        st.setInt(1,id);

        st.execute();
        id = st.getInt(1);
    }

//    public static void getSameBooks(Scanner sc,Connection con) throws SQLException {
//        CallableStatement st = con.prepareCall("{call get_same_books(?,?)}");
//        st.setInt(1,id);
//        System.out.println("Enter book id");
//        st.setInt(1,sc.nextInt());
//        st.registerOutParameter(3, Types.VARCHAR);
//        ResultSet set = st.executeQuery();
//        st.getMoreResults();
//        set = st.getResultSet();
//        System.out.println(st.getString(3));
//        if(st.getString(3).equals("successfull")){
//            while(set.next()) {
//                System.out.println("Id: " + set.getInt("Id"));
//                System.out.println("BorrowId: " + set.getInt("BorrowID"));
//                System.out.println("UserId: " + set.getInt("UserId"));
//                System.out.println("result: " + set.getInt("Id"));
//                System.out.println("................................................................................................");
//            }
//        }
//    }

    public static void main(String[] args) {
	try{
	    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library","root", "");
        Scanner sc = new Scanner(System.in);
        while(true) {
            while (!signedIn) {
                System.out.println("Welcome to the Library.Unless you have an account, sign up\n 1- sign up \n 2- sign in");
                String option = sc.nextLine();
                if (option.equals("1")) {
                    signUp(sc, conn);
                } else {
                    signIn(sc, conn);

                }
            }

            while (loop) {
                System.out.println("What so you want to do?");
                System.out.println("1-Get Details\n2-Search Books\n3-Search Users\n4-Borrow Book\n5-Give Book Back\n6-Increase Balance\n7-Add Book\n8-Get Success List\n9-Search History\n10-Delete User\n11-Get Passed Books\n12- Sign Out");
                String choice = sc.nextLine();
                switch (choice) {
                    case "1":
                        getMyDetails(conn);
                        break;
                    case "2":
                        System.out.println("How do you want to search?");
                        System.out.println("1-Search by title\n2-Search by edition\n3-Search by publisher\n4-Search by published Date\n5-Search by title and edition");
                        System.out.println("6-Search by title and publisher\n7-Search by title and published date\n8-Search by edition and publisher\n9-Search by edition and published date");
                        System.out.println("10-Search by publisher and published date\n11-Search by title and edition and publisher\n12-Search by title and edition and published date");
                        System.out.println("13-Search by edition and publisher and published date\n14-Search by title and published date and publisher\n15-Search by all four");
                        String ch = sc.nextLine();
                        switch (ch) {
                            case "1":
                                searchByName(sc, conn);
                                break;
                            case "2":
                                searchByEdition(sc, conn);
                                break;
                            case "3":
                                searchByPublisher(sc, conn);
                                break;
                            case "4":
                                searchByPublishedDate(sc, conn);
                                break;
                            case "5":

                                searchByTitleEdition(sc, conn);
                                break;
                            case "6":
                                searchByPublisherTitle(sc, conn);
                                break;
                            case "7":
                                searchByTitleDate(sc, conn);
                                break;
                            case "8":
                                searchByPublisherEdition(sc, conn);
                                break;
                            case "9":
                                searchByEditionDate(sc, conn);
                                break;
                            case "10":
                                searchByPublisherDate(sc, conn);
                                break;
                            case "11":
                                searchByTitleEditionPublisher(sc, conn);
                                break;
                            case "12":
                                searchByTitleEditionDate(sc, conn);
                                break;
                            case "13":
                                searchByDateEditionPublisher(sc, conn);
                                break;
                            case "14":
                                searchByTitleDatePublisher(sc, conn);
                                break;
                            case "15":
                                searchByTotal(sc, conn);
                                break;
                        }
                        break;
                    case "3":
                        System.out.println("How do you want to search?");
                        System.out.println("1-Search by username\n2-Search by lastname");
                        String tmp = sc.nextLine();
                        if (tmp.equals("1")) {
                            adminSearchByUsername(sc, conn);
                        } else {
                            adminSearchByLastname(sc, conn);
                        }
                        break;
                    case "4":
                        borrow(sc, conn);
                        break;
                    case "5":
                        giveBookBack(sc, conn);
                        break;
                    case "6":
                        increaseBalance(sc, conn);
                        break;
                    case "7":
                        addBook(sc, conn);
                        break;
                    case "8":
                        getSuccessList(sc, conn);
                        break;
                    case "9":
                        searchHistory(sc, conn);
                        break;
                    case "10":
                        deleteUser(sc, conn);
                        break;
                    case "11":
                        getPassedBooks(sc, conn);
                        break;
                    case "12":
                        signOut(conn);
                        loop = false;
                        signedIn = false;
                        break;
                }
            }
        }

    }catch(Exception e){
	    e.printStackTrace();
    }
    }

}
