package phms.main;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;
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
		
		//System.out.println(dao.test());
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
		System.out.println("5. Implemented Queries");
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
			} catch (NoSuchElementException e) {
				input = 0;
				System.exit(0);
			}
			switch (input){
				case 1:
					//sign in as patient
					Patient user = loginUI();
					if (user == null){
						System.out.println("Login Incorrect");
						menuUI();
						invalid = false;
					} else if (user.isSick() == 1) {
						//check if user has at least 1 HS
						patientMenu(user);
					} else {
						patientMenu(user);	
					}
					break;
				case 2:
					//sign in as health supporter
					HealthSupporter hsuser = loginHSUI();
					if (hsuser == null){
						System.out.println("Login Incorrect");
						menuUI();
						invalid = false;
					} else {
						hsMenu(hsuser);	
					}
					break;
				case 3:
					signUp();
					break;
				case 4:
					System.exit(0);
				case 5:
					queries();
					break;
				case 0:
					System.out.println("Invalid input, try again!!!!!");
					menuUI();
					invalid = false;
					break;
			}

		}while(!invalid);
	}

	private static void queries() {
		System.out.println();
		System.out.println("Report Queries");
		System.out.println("----------");
		System.out.println("1. Find patients who belong to more than one Sick Patient class");
		System.out.println("2. Find all Sick patients whose Health Supporters are also Sick patients");
		System.out.println("3. Find patients who have two Health Supporters");
		System.out.println("4. For each patient and each month of 2015, list all the alerts that were generated");
		System.out.println("5. For each month of 2015, list the patients with the most alerts.");
		System.out.println("----------");
		System.out.println();
		int input;
		try{
			input = Integer.parseInt(console.nextLine());
		} catch (NumberFormatException e){
			input = 0;
		}
		switch(input){
			case 0:
				System.out.println("Invalid input, back to Start Menu");
				break;
			case 1:
				dao.patientsWithMoreThanOneDiagnosis();
				break;
			case 2:
				dao.sickPatientsWithHSAlsoSick();
				break;
			case 3:
				dao.patientsWithTwoHS();
				break;
			case 4:
				dao.alertsEachMonthEachPatient();
				break;
			case 5:
				dao.mostAlertsPerMonth();
				break;
		}
		System.out.println("----------");
		startMenu();
	}
	
	private static Patient enterInfo(){ 
		System.out.println("Enter SSN: ");
		long ssn = Long.parseLong(console.nextLine());
		
		System.out.println("Enter First Name: ");
		String fname = console.nextLine();
		System.out.println("Enter Last Name: ");
		String lname = console.nextLine();
		
		System.out.println("Enter DOB Month (1-12): ");
		int month = Integer.parseInt(console.nextLine());
		System.out.println("Enter DOB Day: ");
		int day = Integer.parseInt(console.nextLine());
		System.out.println("Enter DOB Year: ");
		int year = Integer.parseInt(console.nextLine());
		
		System.out.println("Enter Address: ");
		String address = console.nextLine();
		System.out.println("Enter PhoneNumber: ");
		String phone = console.nextLine();
		System.out.println("Enter Sex (m/f): ");
		String sex = console.nextLine();
		System.out.println("Enter Password: ");
		String pass = console.nextLine();
		
//		Sick is set Automatically by DB
//		System.out.println("Sick (Y/N)? ");
//		String yn = console.nextLine();
//		
//		while(!yn.equalsIgnoreCase("y") && !yn.equalsIgnoreCase("n")){
//			System.out.println("Enter Y or N? ");
//			yn = console.nextLine();
//		}
//		
		Patient p = new Patient();
		p.setSsn(ssn);
		p.setFname(fname);
		p.setLname(lname);
		
		
		String date = year + "-" + month + "-" + day;
		java.sql.Date dat = java.sql.Date.valueOf(date);
		p.setDOB(dat);
		
		p.setAddress(address);
		p.setPhoneNum(phone);
		p.setSex(sex);
		p.setPassword(pass);
		return p;
	}

	private static void signUp() {
		System.out.println("New Account Menu");
		System.out.println("----------");
		System.out.println("1. Add new Patient ");
		System.out.println("2. Add new Health Supporter");
		System.out.println("----------");
		int input;
		try{
			input = Integer.parseInt(console.nextLine());
		} catch (NumberFormatException e){
			input = 0;
		} 
		
		switch(input){
			case 0:
				System.out.println("Invalid input, Back to Start Menu");
				break;
			case 1:
				System.out.println("New Patient Menu");
				System.out.println("----------");
				Patient p = enterInfo();
				dao.addNewPatient(p);
				System.out.println("New Patient Added!!");
				break;
			case 2:
				System.out.println("New Health Supporter Menu");
				System.out.println("----------");
				Patient p1 = enterInfo();
				dao.addNewPerson(p1);
				System.out.println("New Health Supporter Added!! Make sure your patient adds you!!");
				break;
		}
		startMenu();
	}
	
	private static void hsUI(){
		System.out.println("Logged in as Health Supporter");
		System.out.println("---------------------");
		System.out.println("1. Profile");
		System.out.println("2. Diagnoses");
		System.out.println("3. Health Indicator");
		System.out.println("4. Alerts");
		System.out.println("5. Recomendations");
		System.out.println("6. Logout");
		System.out.println("---------------------");
		System.out.println();
	}
	
	private static void hsMenu(HealthSupporter h){
		hsUI();
		boolean invalid;
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
					profile(h);
					break;
				case 2:
					diagnoses(h);
					break;
				case 3:
					healthInd(h);
					break;
				case 4:
					alerts(h);
					break;
				case 5:
					recs(h);
					break;
				case 6:
					startMenu();
					break;
				case 0:
					System.out.println("Invalid input, try again!!!!!");
					hsUI();
					invalid = false;
					break;
			}
		} while(!invalid);
	}
	
	private static void recs(HealthSupporter h){
		System.out.println("View/Add your patients Recommendations");
		System.out.println("---------------------");
		Patient p = selectPatient(h);
		viewRecs(h,p);
	}
	
	private static void viewRecs(HealthSupporter h, Patient p){
		ArrayList<HealthObservationType> hot = dao.getAllRecomendationsforPatient(p);
		int size = hot.size();
		for(int i = 0; i < size; i++){
			HealthObservationType ho = hot.get(i);
			System.out.println("Recomendation #" + (i+1));
			System.out.print("Name: ");
			System.out.println(ho.getName());
			System.out.print("Disease: ");
			System.out.println(ho.getDisease());
			System.out.print("Upper Limit: ");
			System.out.println(ho.getUpper());
			System.out.print("Lower Limit: ");
			System.out.println(ho.getLower());
			System.out.print("Freq: ");
			System.out.println(ho.getFreq());
			System.out.println("---------------------");
		}
		
		System.out.println("Add a Recommendation (Y/N)?");
		String input = console.nextLine();
		if (input.equalsIgnoreCase("y")){
			addRec(h,p);
		} else if (input.equalsIgnoreCase("n")){
			hsMenu(h);
		} else {
			System.out.println("Invalid Input, back to Health Supporter Menu");
			hsMenu(h);
		}
	}
	private static void addRec(HealthSupporter h, Patient p) {
		System.out.println("---------------------");
		System.out.println("New Recommendation");
		//TODO add rec
	}

	private static void alerts(HealthSupporter h){
		System.out.println("View/Edit your patients Alerts");
		System.out.println("---------------------");
		Patient p = selectPatient(h);
		viewAlerts(h,p);
	}
	
	private static void healthInd(HealthSupporter h){
		System.out.println("View your patients Health Indicators");
		System.out.println("---------------------");
		Patient p = selectPatient(h);
		viewHOs(h,p);
	}
	
	private static void diagnoses(HealthSupporter h){
		System.out.println("View your patients Diagnoses");
		System.out.println("---------------------");
		Patient p = selectPatient(h);
		diseaseMenu(h,p);
	}
	
	private static void profile(HealthSupporter h){
		System.out.println("1. View your profile");
		System.out.println("2. View profile of a patient you support");
		int input;
		try{
			input = Integer.parseInt(console.nextLine());
		} catch (NumberFormatException e){
			input = 0;
		}
		switch(input){
			case 0:
				System.out.println("Invalid input, back to Health Supporter Menu");
				hsMenu(h);
				break;
			case 1:
				hsProfile(h);
				break;
			case 2:
				Patient p = selectPatient(h);
				showProfile(h,p);
				break;
		}
	}
	
	private static Patient selectPatient(HealthSupporter h){
		ArrayList<Patient> supports = dao.getSupportedPatients(h);
		int size = supports.size();
		System.out.println("Select A Patient You Support");
		System.out.println("---------------------");
		for (int i = 0; i < size; i++) {
			Patient p = supports.get(i);
			System.out.println((i+1) + ": " + p.getFname() + " " + p.getLname());
		}
		System.out.println("---------------------");
		int selection = Integer.parseInt(console.nextLine());
		while(selection - 1 >= size){
			System.out.println("Invalid selection, choose again");
			selection = Integer.parseInt(console.nextLine());
		}
		selection--;
		return supports.get(selection);
	}
	
	private static void hsProfile(HealthSupporter p){
		System.out.println(p.getFname() + " " + p.getLname() + "'s Profile Info");
		System.out.println("---------------------");
		System.out.println("SSN: " + p.getSsn());
		System.out.println("Name: " + p.getFname() + " " + p.getLname());
		java.sql.Date DOB = p.getDOB();
		System.out.println("DOB: " + DOB.toString());
		System.out.println("Address: " + p.getAddress());
		System.out.println("Phone: " + p.getPhoneNum());
		System.out.println("Sex: " + p.getSex());
		
		System.out.println();
		System.out.println("Edit Info? (Y/N)");
		String input = console.nextLine();
		if (input.equalsIgnoreCase("y")){
			editHSInfo(p);
		} else if (input.equalsIgnoreCase("n")){
			hsMenu(p);
		} else {
			System.out.println("Invalid Input, back to Health Supporter Menu");
			hsMenu(p);
		}
	}
	
	private static void editHSInfoUI(){
		System.out.println("Edit Health Supporter");
		System.out.println("---------------------");
		System.out.println("1. Set First Name");
		System.out.println("2. Set Last Name");
		System.out.println("3. Set Date Of Birth");
		System.out.println("4. Set Address");
		System.out.println("5. Set Phone Number");
		System.out.println("6. Set Sex");
		System.out.println("7. Set Password");
		System.out.println("8. Save Changes");
		System.out.println("9. Exit/Leave Changes");
		System.out.println("---------------------");
		System.out.println();
	}
	
	private static void editHSInfo(HealthSupporter p){
		//edit patient info
		boolean stay;
		HealthSupporter newP = p;
		do{
			editHSInfoUI();
			stay = true;
			int input;
			try{
				input = Integer.parseInt(console.nextLine());
			} catch (NumberFormatException e){
				input = 0;
			}
			switch (input){
				case 1:
					System.out.println("Enter new First Name");
					String fname = console.nextLine();
					newP.setFname(fname);
					break;
				case 2:
					System.out.println("Enter new Last Name");
					String lname = console.nextLine();
					newP.setLname(lname);
					break;
				case 3:
					System.out.println("Enter DOB Month (1-12): ");
					int month = Integer.parseInt(console.nextLine());
					System.out.println("Enter DOB Day: ");
					int day = Integer.parseInt(console.nextLine());
					System.out.println("Enter DOB Year: ");
					int year = Integer.parseInt(console.nextLine());
					
					String date = year + "-" + month + "-" + day;
					java.sql.Date dat = java.sql.Date.valueOf(date);
					newP.setDOB(dat);
					break;
				case 4:
					System.out.println("Enter new Address");
					String address = console.nextLine();
					newP.setAddress(address);
					break;
				case 5:
					System.out.println("Enter new Phone Num");
					String phone = console.nextLine();
					newP.setPhoneNum(phone);
					break;
				case 6:
					System.out.println("Enter new Sex (m/f)");
					String sex = console.nextLine();
					newP.setSex(sex);
					break;
				case 7:
					System.out.println("Enter new Password");
					String password = console.nextLine();
					newP.setPassword(password);
					break;
				case 8:
					try {
						dao.editHSNoPatient(newP);
						System.out.println("Patient Updated!");
					} catch (SQLException e) {
						// TODO Auto-generated catch block						
						e.printStackTrace();
						System.out.println("Error! Patient Not Updated!");
					}
					stay = false;
					hsMenu(newP);
					break;
				case 9:
					stay = false;
					System.out.println("Patient Not Updated!");
					hsMenu(p);
					break;
				case 0:
					System.out.println("Invalid input, try again!!!!!");
					break;
			}
		} while(stay);
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
	
	private static void patientMenu(Patient p){
		patientUI();
		boolean invalid;
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
					showProfile(null,p);
					break;
				case 2:
					diseaseMenu(null,p);
					break;
				case 3:
					viewHOs(null,p);
					break;
				case 4:
					viewAlerts(null,p);
					break;
				case 5:
					patientViewsHS(p);
					break;
				case 6:
					startMenu();
					break;
				case 0:
					System.out.println("Invalid input, try again!!!!!");
					patientUI();
					invalid = false;
					break;
			}
		} while(!invalid);
		
	}
	private static void viewAlerts(HealthSupporter h, Patient p){
		System.out.println(p.getFname() + " " + p.getLname() + "'s Alerts");
		ArrayList<Alert> alerts = dao.getPatientAlerts(p);
		int size = alerts.size();
		for(int i = 0; i < size; i++){
			Alert a = alerts.get(i);
			System.out.println((i+1) +": " + a.getSent().toString() + " - " + a.getHOType() + " - " + a.getAlert());
			//System.out.println((i+1) + ": " + h.getFname() + " " + h.getLname());
		}

		//TODO Clearing Alerts
		if(h==null){
			System.out.println("Want to clear an alert?");
			System.out.println("Select an Alert from above to Clear");
			//if it is a freq alert, allow them to clear, otherwise have them enter HO
			patientMenu(p);
		} else {
			System.out.println("Want to clear an alert?");
			System.out.println("Select an Alert from above to Clear");
			
			
			//dao.clearAlert(alerts.get(input));
			hsMenu(h);
		}
	}
	
	private static void viewHOs(HealthSupporter h, Patient p){
		//List the HOTypes and the corresponding thresholds/freq for this patient
		System.out.println(p.getFname() + " " + p.getLname() + "'s Health Observation Prefs");
		System.out.println("---------------------");
		//TODO call dao for the HOT's and list em out here!!!!!!!
		System.out.println("---------------------");
		System.out.println();
		
		
		//List out all the currently recorded Health Observations (by Date)
		System.out.println(p.getFname() + " " + p.getLname() + "'s Recorded Health Observations");
		System.out.println("---------------------");
		ArrayList<HealthObservation> hos = dao.getPatientHealthObs(p);
		int size = hos.size();
		for(int i = 0; i < size; i++){
			HealthObservation a = hos.get(i);
			if (a.getHoType().equals("Mood")) {
				String mood;
				if (a.getValue() == 1){
					mood = "Happy";
				} else if(a.getValue() == 2){
					mood = "Neutral";
				} else {
					mood = "Sad";
				}
				System.out.println((i+1) + "- Type: " + a.getHoType() + " Value: " + mood + " Recorded Date: " + a.getRecordedDate().toString() + " Observed Date: " + a.getObservedDate().toString());
				
			} else {
				System.out.println((i+1) + "- Type: " + a.getHoType() + " Value: " + a.getValue() + " Recorded Date: " + a.getRecordedDate().toString() + " Observed Date: " + a.getObservedDate().toString());
			}
		}
		System.out.println("---------------------");
		System.out.println("Would you like to add a Health Observation (Y/N)?");
		String yn = console.nextLine();
		while(!yn.equalsIgnoreCase("y") && !yn.equalsIgnoreCase("n")){
			System.out.println("Enter Y or N? ");
			yn = console.nextLine();
		}
		if (yn.equalsIgnoreCase("y")){
			addHO(p);
		} else {
			System.out.println("Back to Menu");
		}
		
		if(h == null){
			patientMenu(p);
		} else {
			hsMenu(h);
		}
	}
	
	
	private static void addHO(Patient p) {
		HealthObservation ho = new HealthObservation();
		ho.setPatientId(p.getSsn());
		System.out.println("Adding a Health Observation");
		System.out.println("---------------------");
		System.out.println("Select the Type");
		ArrayList<String> names = dao.listOfHOTNames();
		int size = names.size();
		for(int i = 0; i < size; i++){
			System.out.println((i+1) +": " + names.get(i));
		}
		int input;
		try{
			input = Integer.parseInt(console.nextLine());
		} catch (NumberFormatException e){
			System.out.println("Invalid Input back to Menu");
			return;
		}
		input--;
		if (input >= size) {
			System.out.println("Invalid Input back to Menu");
			return;
		}
		ho.setHoType(names.get(input));
		System.out.println("Enter the Value (If Mood enter 1-3 for Happy (1), Neutral (2), Sad (3) and if Pain enter 1-10):");
		long in;
		try{
			in = Long.parseLong(console.nextLine());
		} catch (NumberFormatException e){
			System.out.println("Invalid Input back to Menu");
			return;
		}
		ho.setValue(in);
		
		System.out.println("Enter Observed Month (1-12): ");
		int month = Integer.parseInt(console.nextLine());
		System.out.println("Enter Observed Day: ");
		int day = Integer.parseInt(console.nextLine());
		System.out.println("Enter Observed Year: ");
		int year = Integer.parseInt(console.nextLine());
		
		String date = year + "-" + month + "-" + day;
		java.sql.Date dat = java.sql.Date.valueOf(date);
		ho.setObservedDate(dat);
		
		System.out.println("Enter Recorded Month (1-12): ");
		month = Integer.parseInt(console.nextLine());
		System.out.println("Enter Recorded Day: ");
		day = Integer.parseInt(console.nextLine());
		System.out.println("Enter Recorded Year: ");
		year = Integer.parseInt(console.nextLine());
		
		String date2 = year + "-" + month + "-" + day;
		java.sql.Date dat2 = java.sql.Date.valueOf(date2);
		ho.setRecordedDate(dat2);
		
		try {
			dao.addHealthObservation(ho);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	private static void patientViewsHS(Patient p){
		boolean keep = true;
		while(keep){
			System.out.println(p.getFname() + " " + p.getLname() + "'s Health Supporter Menu!");
			System.out.println("---------------------");
			System.out.println("1. View Health Supporters");
			System.out.println("2. Edit Health Supporters ");
			System.out.println("3. Add Health Supporters ");
			System.out.println("4. Remove Health Supporters ");
			System.out.println("5. Back to Patient Menu ");
			System.out.println("---------------------");
			int input;
			try{
				input = Integer.parseInt(console.nextLine());
			} catch (NumberFormatException e){
				input = 0;
			}
			switch(input){
				case 0:
					System.out.println("Invalid input, back to Patient Menu!!!!!");
					keep = false;
					break;
				case 1:
					viewHS(p);
					break;
				case 2:
					editHS(p);		
					break;
				case 3:
					addHS(p);
					break;
				case 4:
					removeHS(p);
					break;
				case 5:
					keep = false;
					break;
			}
		}
		patientMenu(p);
	}
	
	private static void viewHS(Patient p){
		System.out.println("List of all Health Supporters");
		System.out.println("---------------------");
		ArrayList<HealthSupporter> hs = dao.getPatientsHS(p);
		for(int i = 0; i < hs.size(); i++){
			HealthSupporter h = hs.get(i);
			System.out.println((i+1) + ": " + h.getFname() + " " + h.getLname());
		}
	}
	private static void editHS(Patient p){
		System.out.println("Select a Health Supporters to Edit");
		System.out.println("---------------------");
		ArrayList<HealthSupporter> hs = dao.getPatientsHS(p);
		for(int i = 0; i < hs.size(); i++){
			HealthSupporter h = hs.get(i);
			System.out.println((i+1) + ": " + h.getFname() + " " + h.getLname());
		}
		
		int selection = Integer.parseInt(console.nextLine());
		while(selection - 1 >= hs.size()){
			System.out.println("Invalid selection, choose again");
			selection = Integer.parseInt(console.nextLine());
		}
		selection--;
		HealthSupporter h = hs.get(selection);
		editHSInfo(p, h);
	}
	private static void addHS(Patient p){
		System.out.println("Select a Health Supporter to Add");
		System.out.println("---------------------");
		//TODO add HS
		ArrayList<HealthSupporter> hs = dao.getPossibleHS(p);
		for(int i = 0; i < hs.size(); i++){
			HealthSupporter h = hs.get(i);
			System.out.println((i+1) + ": " + h.getFname() + " " + h.getLname());
		}
		int selection = Integer.parseInt(console.nextLine());
		while(selection - 1 >= hs.size()){
			System.out.println("Invalid selection, choose again");
			selection = Integer.parseInt(console.nextLine());
		}
		selection--;
		HealthSupporter h = hs.get(selection);
		addHSAlreadyPerson(h,p);
	}
	
	
	private static void addHSAlreadyPerson(HealthSupporter h, Patient p) {
		System.out.println("Enter Date Auth Month (1-12): ");
		int month = Integer.parseInt(console.nextLine());
		System.out.println("Enter Date Auth Day: ");
		int day = Integer.parseInt(console.nextLine());
		System.out.println("Enter Date Auth Year: ");
		int year = Integer.parseInt(console.nextLine());
		
		String date = year + "-" + month + "-" + day;
		java.sql.Date dat = java.sql.Date.valueOf(date);
		h.setDateAuthorized(dat);
		
		System.out.println("Enter Date Unauth Month (1-12): ");
		int month1 = Integer.parseInt(console.nextLine());
		System.out.println("Enter Date Unauth Day: ");
		int day1 = Integer.parseInt(console.nextLine());
		System.out.println("Enter Date Unauth Year: ");
		int year1 = Integer.parseInt(console.nextLine());
		
		String date1 = year1 + "-" + month1 + "-" + day1;
		java.sql.Date dat1 = java.sql.Date.valueOf(date1);
		h.setDateUnauthorized(dat1);
		h.setSupportingPatientID(p.getSsn());
		
		try {
			dao.addHSAlreadyPerson(h);
		} catch (SQLException e) {
			if(e.getErrorCode()==-20001) {
				System.out.println("Error! You cannot have more than 2 Health Supporters!");
			} else {
				e.printStackTrace();
			}
		}
	}

	private static void removeHS(Patient p){
		System.out.println("Select a Health Supporters to Remove");
		System.out.println("---------------------");
		ArrayList<HealthSupporter> hs = dao.getPatientsHS(p);
		for(int i = 0; i < hs.size(); i++){
			HealthSupporter h = hs.get(i);
			System.out.println((i+1) + ": " + h.getFname() + " " + h.getLname());
		}
		
		int selection = Integer.parseInt(console.nextLine());
		while(selection - 1 >= hs.size()){
			System.out.println("Invalid selection, choose again");
			selection = Integer.parseInt(console.nextLine());
		}
		selection--;
		HealthSupporter h = hs.get(selection);
		try {
			if (dao.removeHSForPatient(p.getSsn(), h)){
				System.out.println("Success, HS removed!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		};
		System.out.println("---------------------");
	}
	
	private static void diseaseMenu(HealthSupporter h, Patient p){
		boolean keep = true;
		while(keep){
			System.out.println("Make a selection");
			System.out.println("---------------------");
			System.out.println("1. View Diseases");
			System.out.println("2. Add Disease");
			System.out.println("3. Remove Disease");
			System.out.println("4. Back To Menu");
		
			int input;
			try{
				input = Integer.parseInt(console.nextLine());
			} catch (NumberFormatException e){
				input = 0;
			}
			switch(input){
				case 0:
					System.out.println("Invalid input, back to Menu!!!!!");
					keep = false;
					break;
				case 1:
					showPatientDiseases(p);
					break;
				case 2:
					addDisease(p);
					break;
				case 3:
					removeDisease(p);
					break;
				case 4:
					keep = false;
					break;
			}
		}
		if(h == null){
			patientMenu(p);
		} else {
			hsMenu(h);
		}
		
		
	}
	private static void addDisease(Patient p){
		ArrayList<String> allDiseases = dao.getAllDiseases();
		int size = allDiseases.size();
		System.out.println("Select A Disease to Add");
		System.out.println("---------------------");
		for (int i = 0; i < size; i++) {
			System.out.println((i+1) + ": "+ allDiseases.get(i));
		}
		System.out.println("---------------------");
		int selection = Integer.parseInt(console.nextLine());
		while(selection - 1 >= size){
			System.out.println("Invalid selection, choose again");
			selection = Integer.parseInt(console.nextLine());
		}
		selection--;
		String dis = allDiseases.get(selection);
		try {
			if(dao.addDiseaseForPatient(p.getSsn(), dis)){
				p.setSick(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void removeDisease(Patient p){
		ArrayList<String> patientsDiseases = dao.getPatientsDiseases(p.getSsn());
		System.out.println("Select number for the disease to remove");
		System.out.println("---------------------");
		int s = patientsDiseases.size();
		for (int i = 0; i < s; i++) {
			System.out.println((i+1) + ": "+ patientsDiseases.get(i));
		}
		
		int selection = Integer.parseInt(console.nextLine());
		while(selection - 1 >= s){
			System.out.println("Invalid selection, choose again");
			selection = Integer.parseInt(console.nextLine());
		}
		selection--;
		String dis = patientsDiseases.get(selection);
		try {
			dao.removeDiseaseForPatient(p.getSsn(),dis);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("---------------------");
	}
	
	private static void showPatientDiseases(Patient p){
		if (p.isSick() == 1){
			ArrayList<String> patientsDiseases = dao.getPatientsDiseases(p.getSsn());
			System.out.println(p.getFname() + " " + p.getLname() + "'s Diseases!");
			System.out.println("---------------------");
			int s = patientsDiseases.size();
			if (s == 0){
				System.out.println("No Diseases!");
			} else {
				for (int i = 0; i < s; i++) {
					System.out.println((i+1) + ": "+ patientsDiseases.get(i));
				}
			}
			
			System.out.println("---------------------");
		} else {
			System.out.println(p.getFname() + " " + p.getLname() + " is Well, so no diseases!");
			System.out.println("---------------------");
		}
	}
	
	private static void showProfile(HealthSupporter h, Patient p){
		System.out.println(p.getFname() + " " + p.getLname() + "'s Profile Info");
		System.out.println("---------------------");
		System.out.println("SSN: " + p.getSsn());
		System.out.println("Name: " + p.getFname() + " " + p.getLname());
		java.sql.Date DOB = p.getDOB();
		System.out.println("DOB: " + DOB.toString());
		System.out.println("Address: " + p.getAddress());
		System.out.println("Phone: " + p.getPhoneNum());
		System.out.println("Sex: " + p.getSex());
		int sick = p.isSick();
		if (sick == 1){
			System.out.println("Sick? yes");
		} else {
			System.out.println("Sick? no");
		}
		System.out.println();
		System.out.println("Edit Info? (Y/N)");
		String input = console.nextLine();
		if (input.equalsIgnoreCase("y")){
			editPatientInfo(null,p);
		} else if (input.equalsIgnoreCase("n")){
			if (h == null){
				patientMenu(p);
			} else {
				hsMenu(h);
			}
		} else {
			System.out.println("Invalid Input, back to Menu");
			if (h == null){
				patientMenu(p);
			} else {
				hsMenu(h);
			}
		}
	}
	
	private static void editPatientUI(){
		System.out.println("Edit Patient");
		System.out.println("---------------------");
		System.out.println("1. Set First Name");
		System.out.println("2. Set Last Name");
		System.out.println("3. Set Date Of Birth");
		System.out.println("4. Set Address");
		System.out.println("5. Set Phone Number");
		System.out.println("6. Set Sex");
		System.out.println("7. Set Password");
		System.out.println("8. Set Sick");
		System.out.println("9. Save Changes");
		System.out.println("10. Exit/Leave Changes");
		System.out.println("---------------------");
		System.out.println();
	}
	
	private static void editPatientInfo(HealthSupporter h, Patient p){
		//edit patient info
		boolean stay;
		Patient newP = p;
		do{
			editPatientUI();
			stay = true;
			int input;
			try{
				input = Integer.parseInt(console.nextLine());
			} catch (NumberFormatException e){
				input = 0;
			}
			switch (input){
				case 1:
					System.out.println("Enter new First Name");
					String fname = console.nextLine();
					newP.setFname(fname);
					break;
				case 2:
					System.out.println("Enter new Last Name");
					String lname = console.nextLine();
					newP.setLname(lname);
					break;
				case 3:
					System.out.println("Enter DOB Month (1-12): ");
					int month = Integer.parseInt(console.nextLine());
					System.out.println("Enter DOB Day: ");
					int day = Integer.parseInt(console.nextLine());
					System.out.println("Enter DOB Year: ");
					int year = Integer.parseInt(console.nextLine());
					
					String date = year + "-" + month + "-" + day;
					java.sql.Date dat = java.sql.Date.valueOf(date);
					newP.setDOB(dat);
					break;
				case 4:
					System.out.println("Enter new Address");
					String address = console.nextLine();
					newP.setAddress(address);
					break;
				case 5:
					System.out.println("Enter new Phone Num");
					String phone = console.nextLine();
					newP.setPhoneNum(phone);
					break;
				case 6:
					System.out.println("Enter new Sex (m/f)");
					String sex = console.nextLine();
					newP.setSex(sex);
					break;
				case 7:
					System.out.println("Enter new Password");
					String password = console.nextLine();
					newP.setPassword(password);
					break;
				case 8:
					System.out.println("Sick (Y/N)? ");
					String yn = console.nextLine();
					
					while(!yn.equalsIgnoreCase("y") && !yn.equalsIgnoreCase("n")){
						System.out.println("Enter Y or N? ");
						yn = console.nextLine();
					}
					if (yn.equalsIgnoreCase("y")){
						newP.setSick(1);
					} else if (yn.equalsIgnoreCase("n")){
						newP.setSick(0);
					}
					break;
				case 9:
					dao.editPatient(newP);
					stay = false;
					System.out.println("Patient Updated!");
					if (h == null){
						patientMenu(newP);
					} else {
						hsMenu(h);
					}
					
					break;
				case 10:
					stay = false;
					System.out.println("Patient Not Updated!");
					if (h == null){
						patientMenu(newP);
					} else {
						hsMenu(h);
					}
					break;
				case 0:
					System.out.println("Invalid input, try again!!!!!");
					break;
			}
		} while(stay);
	}
	
	private static void editHSUI(){
		System.out.println("Edit Health Supporter");
		System.out.println("---------------------");
		System.out.println("1. Set First Name");
		System.out.println("2. Set Last Name");
		System.out.println("3. Set Phone Number");
		System.out.println("4. Set Auth Date");
		System.out.println("5. Set Unauth Date");
		System.out.println("6. Save Changes");
		System.out.println("7. Exit/Leave Changes");
		System.out.println("---------------------");
		System.out.println();
	}
	
	private static void editHSInfo(Patient OGp, HealthSupporter p){
		//edit HS info
		boolean stay;
		HealthSupporter newP = p;
		do{
			editHSUI();
			stay = true;
			int input;
			try{
				input = Integer.parseInt(console.nextLine());
			} catch (NumberFormatException e){
				input = 0;
			}
			switch (input){
				case 1:
					System.out.println("Enter new First Name");
					String fname = console.nextLine();
					newP.setFname(fname);
					break;
				case 2:
					System.out.println("Enter new Last Name");
					String lname = console.nextLine();
					newP.setLname(lname);
					break;
				case 3:
					System.out.println("Enter new Phone Num");
					String phone = console.nextLine();
					newP.setPhoneNum(phone);
					break;
				case 4:
					System.out.println("Enter Date Auth Month (1-12): ");
					int month = Integer.parseInt(console.nextLine());
					System.out.println("Enter Date Auth Day: ");
					int day = Integer.parseInt(console.nextLine());
					System.out.println("Enter Date Auth Year: ");
					int year = Integer.parseInt(console.nextLine());
					
					String date = year + "-" + month + "-" + day;
					java.sql.Date dat = java.sql.Date.valueOf(date);
					newP.setDateAuthorized(dat);
					break;
				case 5:
					System.out.println("Enter Date Unauth Month (1-12): ");
					int month1 = Integer.parseInt(console.nextLine());
					System.out.println("Enter Date Unauth Day: ");
					int day1 = Integer.parseInt(console.nextLine());
					System.out.println("Enter Date Unauth Year: ");
					int year1 = Integer.parseInt(console.nextLine());
					
					String date1 = year1 + "-" + month1 + "-" + day1;
					java.sql.Date dat1 = java.sql.Date.valueOf(date1);
					newP.setDateUnauthorized(dat1);
					break;
				case 6:
				try {
					dao.editHS(OGp, newP);
				} catch (SQLException e) {
					e.printStackTrace();
				}
					stay = false;
					System.out.println("Health Supporter Updated!");
					//patientMenu(OGp);
					break;
				case 7:
					stay = false;
					System.out.println("Health Supporter Not Updated!");
					//patientMenu(OGp);
					break;
				case 0:
					System.out.println("Invalid input, try again!!!!!");
					break;
			}
		} while(stay);
	}

	private static HealthSupporter loginHSUI(){
		System.out.println("Health Supporter Login Menu");
		System.out.println("----------");
		System.out.println("Enter SSN");
		long user = Long.parseLong(console.nextLine());

		System.out.println("Enter Password");
		String pass = console.nextLine();
		System.out.println();
		HealthSupporter p;
		p = loginHSAction(user,pass);
		return p;
	}
	
	private static HealthSupporter loginHSAction(long user, String pass){
		return dao.healthSupporterLogin(user,pass);
	}
	
	private static Patient loginUI(){
		System.out.println("Patient Login Menu");
		System.out.println("----------");
		System.out.println("Enter SSN");
		long user = Long.parseLong(console.nextLine());

		System.out.println("Enter Password");
		String pass = console.nextLine();
		System.out.println();
		Patient p;
		p = dao.patientLogin(user,pass);
		return p;
	}
}