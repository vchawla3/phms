package phms.main;
import java.util.Scanner;
//import phms.model.*;
//import phms.dao.*;
import java.sql.*;
/**
* Personal HealthManagement Database Application for CSC 540
* Authors: Webb Chawla, Zachary Delong, Akshit Patel, and Kush Mishra
*
*/
public class PersonalHealthManagementDatabaseApplication {
	static Scanner console;
	static PHMSDao dao;

	public static void main (String[] args) {
		//SETUP DB CONNECTION HERE
		dao = new PHMSDao();
		
		//dao.test();
		
		console = new Scanner (System.in);
		startMenu();
	}

	private static void menuUI(){
		System.out.println("Start Menu");
		System.out.println("----------");
		System.out.println("1. Login as Patient");
		System.out.println("2. Login as Health Supporter");
		System.out.println("3. Signup");
		System.out.println("4. Exit");
		System.out.println("----------");
		//extra space
		System.out.println();
	}
	private static void startMenu(){
		menuUI();
		boolean invalid = true;
		do{
			invalid = true;
			int input;
			try{
				input = Integer.parseInt(console.nextLine());
			} catch (NumberFormatException e){
				input = 0;
			}
			switch (input){
				case 1:
					//sign in as patient
					String uid = loginUI();
					patientMenu(uid);	
					break;
				case 2:
					//sign in as health supporter
					break;
				case 3:
					signUp();
					break;
				case 4:
					System.exit(0);
				case 0:
					System.out.println("Invalid input, try again!!!!!");
					menuUI();
					invalid = false;
					break;
			}

		}while(!invalid);
	}

	private static void signUp() {
		System.out.println("New Patient Menu");
		System.out.println("----------");
		//Patient p = new Patient();
		System.out.println("Enter SSN: ");
		long ssn = Long.parseLong(console.nextLine());
		
		System.out.println("Enter First Name: ");
		String fname = console.nextLine();
		System.out.println("Enter Last Name: ");
		String lname = console.nextLine();
		
		System.out.println("Enter DOB Month (1-12): ");
		String month = console.nextLine();
		System.out.println("Enter DOB Day: ");
		String day = console.nextLine();
		System.out.println("Enter DOB Year: ");
		String year = console.nextLine();
		
		System.out.println("Enter Address: ");
		String address = console.nextLine();
		System.out.println("Enter PhoneNumber: ");
		String phone = console.nextLine();
		System.out.println("Enter Sex: ");
		String sex = console.nextLine();
		System.out.println("Enter Password: ");
		String pass = console.nextLine();
		
		System.out.println("Sick (Y/N)? ");
		String yn = console.nextLine();
		
		while(!yn.equalsIgnoreCase("y") && !yn.equalsIgnoreCase("n")){
			System.out.println("Enter Y or N? ");
			yn = console.nextLine();
		}
		
		Patient p = new Patient();
		p.setSsn(ssn);
		p.setFname(fname);
		p.setLname(lname);
		//Date dob = new Date(0, 0, 0);
		p.setAddress(address);
		p.setPhoneNum(phone);
		p.setSex(sex);
		p.setPassword(pass);
		if (yn.equalsIgnoreCase("y")){
			p.setSick(1);
		} else if (yn.equalsIgnoreCase("n")){
			p.setSick(0);
		}
		
		
	}

	private static void HealthSupporterUI(String uid){

	}

	private static void checkIfHealthSupporter(String uid){

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
		int input = Integer.parseInt(console.nextLine());
		switch (input){
			case 1:

				break;
			case 2:

				break;
			case 3:

				break;
			case 4:

				break;
			case 5:

				break;
			case 6:
				startMenu();
				break;
		}
	}

	private static String loginUI(){
		System.out.println("Login Menu");
		System.out.println("----------");
		System.out.println("Enter SSN");
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
		return dao.login(user,pass);
	}




}