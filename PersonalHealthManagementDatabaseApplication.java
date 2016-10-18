import java.util.*;
import java.sql.*;
/**
* Personal HealthManagement Database Application for CSC 540
* Authors: Webb Chawla, Zachary Delong, Akshit Patel, and Kush Mishra
*
*/
public class PersonalHealthManagementDatabaseApplication {
	static Scanner console;

	static final String jdbcURL 
	= "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
	static final String DBuser = "vchawla3";
	static final String DBpassword = "200006054";


	public static void main (String[] args) {
		//SETUP DB CONNECTION HERE
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");

			console = new Scanner (System.in);
			startMenu();
		}catch(Throwable oops){
			oops.printStackTrace();
		}catch(Exception e){
			System.out.println(e.message);
		}
		
	}

	private static void menuUI(){
		System.out.println("Start Menu");
		System.out.println("----------");
		System.out.println("1. Login");
		System.out.println("2. Signup");
		System.out.println("3. Exit");
		System.out.println("----------");
		//extra space
		System.out.println();
	}
	private static void startMenu(){
		menuUI();

		boolean invalid = true;
		do{
			invalid = true;
			String input = console.nextLine();
			switch (input){
				case "1":
					//Try to login
					String uid = loginUI();
					patientMenu(uid);	
					break;
				case "2":
					signupUI();
					break;
				case "3":
					System.exit(0);
				default:
					System.out.println("Invalid input, try again!!!!!");
					menuUI();
					invalid = false;
					break;
			}

		}while(!invalid);
	}

	private static void HealthSupporterUI(String uid){

	}

	private static void checkIfHealthSupporter(String uid){

	}

	private static void signupUI(){
		System.out.println("SignUp Menu");
		System.out.println("----------");
		System.out.println();
	}

	private static void patientUI(){
		System.out.println("Logged in as Patient");
		System.out.println("---------------------");
		System.out.println("1. Profile");
		System.out.println("2. Diagnoses");
		System.out.println("3. Health Indicator");
		System.out.println("4. Alerts");
		System.out.println("5. Health Supporters");
		System.out.println("6. Logout");
		System.out.println("---------------------");
		System.out.println();
	}

	private static void patientMenu(String uid){
		patientUI();
		String input = console.nextLine();
		switch (input){
			case "1":

				break;
			case "2":

				break;
			case "3":

				break;
			case "4":

				break;
			case "5":

				break;
			case "6":
				startMenu();
				break;
		}
	}

	private static String loginUI(){
		System.out.println("Login Menu");
		System.out.println("----------");
		System.out.println("Enter UID");
		String user = console.nextLine();

		System.out.println("Enter Password");
		String pass = console.nextLine();
		System.out.println();
		boolean success;
		do{
			success = loginAction(user,pass);
			if (!success) {
				System.out.println("Login Incorrect");
				System.out.println("Enter UID");
				user = console.nextLine();

				System.out.println("Enter Password");
				pass = console.nextLine();
			}
		}while(!success);
		return user;
	}

	private static boolean loginAction(String user, String pass){
		if (user.equals("1")){
			return true;
		}
		return false;
	}




}