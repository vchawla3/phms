package phms.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
//import phms.model.*;

public class PHMSDao {
	static final String jdbcURL 
	= "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
	static final String DBuser = "vchawla3";
	static final String DBpassword = "200006054";
	
	public PHMSDao(){
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}catch(Throwable oops){
			oops.printStackTrace();
		}
//        Connection conn = null;
//        Statement stmt = null;
//        ResultSet rs = null;
	}
  	
  	public ArrayList<HealthObservationType> getAllRecomendationsforPatient(Patient p){
  		ArrayList<HealthObservationType> hos = new ArrayList<HealthObservationType>();
  		Connection conn = null;
  		PreparedStatement stmt = null;
  		ResultSet rs = null;
  		try{
  			conn = openConnection();
  			String SQL = "SELECT * FROM Health_Observation_Type h, Recommendation r"
  						+ "WHERE r.Rec_HS_Patient = ? AND r.Rec_HOT_Type = h.Hot_Id";
  			stmt = conn.prepareStatement(SQL);
  			stmt.setLong(1, p.getSsn());
  			rs = stmt.executeQuery();
  			while (rs.next()) {
  				HealthObservationType pa = new HealthObservationType(rs);
  				hos.add(pa);
  			}
  			return hos;
  		} catch(SQLException e){  			
  			e.printStackTrace();
  			return null;
  		} finally {
  			close(stmt);
            close(conn);
            close(rs);
  		}
  	}
  	
  	public boolean addRecomendation(HealthObservationType ht, HealthSupporter h, Patient p) throws SQLException{
  		Connection conn = null;
  		PreparedStatement stmt = null;
  		Statement st = null;
  		ResultSet rs = null;
  		try{
  			conn = openConnection();
  			String SQL = "INSERT INTO Health_Observation_Type (Hot_Name,Hot_Disease,Hot_UpperLimit,Hot_LowerLimit,Hot_Frequency) "
  					+ "VALUES(?,?,?,?,?)";
  			stmt = conn.prepareStatement(SQL);
  			stmt.setString(1, ht.getName());
  			stmt.setString(2, ht.getDisease());
  			stmt.setLong(3, ht.getUpper());
  			stmt.setLong(4, ht.getLower());
  			stmt.setLong(5, ht.getFreq());
  			stmt.executeUpdate();
  			close(stmt);
  			SQL = "SELECT MAX(Hot_Id) FROM Health_Observation_Type";
  			stmt = conn.prepareStatement(SQL);
  			rs = stmt.executeQuery(SQL);
  			long id = 0;
  			while (rs.next()) {
  				id = rs.getLong("Hot_Id");
  			}
  			close(stmt);
  			SQL = "INSERT INTO Recommendation VALUES(?,?,?)";
  			stmt = conn.prepareStatement(SQL);
  			stmt.setLong(1, h.getSsn());
  			stmt.setLong(2, p.getSsn());
  			stmt.setLong(3, id);
  			stmt.executeUpdate();
  			conn.commit();
  			return true;
  		} catch(SQLException e){  
  			conn.rollback();
  			e.printStackTrace();
  			return false;
  		} finally {
  			close(stmt);
            close(conn);
            close(rs);
  		}
  	}
  	
	public ArrayList<HealthObservation> getHOTypesForPatient(Patient p){
		ArrayList<HealthObservation> hos = new ArrayList<HealthObservation>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try{
			conn = openConnection();
			String SQL = "SELECT * FROM Alert a, Health_Observation_Type h"
					+ "WHERE a.Al_HS_Patient = ? AND a.Al_OBS_Type = h.Hot_Id"
					+ "ORDER BY a.Al_Sent";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, p.getSsn());
			rs = stmt.executeQuery();
			while (rs.next()) {
				HealthObservation h = new HealthObservation();
				hos.add(h);
			}
			return hos;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public ArrayList<Alert> getPatientAlerts(Patient p){
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<Alert> as = new ArrayList<Alert>();
		try{
			conn = openConnection();
			String SQL = "SELECT * FROM Alert a, Health_Observation_Type h"
					+ "WHERE a.AL_READ = 0 AND a.Al_HS_Patient = ? AND a.Al_OBS_Type = h.Hot_Id"
					+ "ORDER BY a.Al_Sent";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, p.getSsn());
			rs = stmt.executeQuery();
			while (rs.next()) {
				Alert a = new Alert(rs);
				as.add(a);
			}
			return as;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public boolean clearAlert(Alert a) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<Alert> as = new ArrayList<Alert>();
		try{
			conn = openConnection();
			String SQL = "UPDATE Alert a SET"
						+ "a.Al_Read = 1"
						+ "WHERE"
						+ "a.Al_HS_Supporter = ?,"
						+ "a.Al_HS_Patient = ?,"
						+ "a.Al_OBS_Patient = ?,"
						+ "a.Al_OBS_Type = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, a.getHSId());
			stmt.setLong(2, a.getPatientId());
			stmt.setLong(3, a.getPatientId());
			stmt.setLong(4, a.getHOTypeID());
			stmt.executeUpdate();
			conn.commit();
			return true;
		} catch(SQLException e){
			conn.rollback();
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public ArrayList<Patient> getSupportedPatients(HealthSupporter h){
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<Patient> ps = new ArrayList<Patient>();
		try{
			conn = openConnection();
			String SQL = "SELECT * FROM PERSON p, Patient p, Health_Supporter h "
					+ "WHERE p.Pat_Person = h.HS_Patient AND h.HS_Supporter = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, h.getSsn());
			rs = stmt.executeQuery();
			while (rs.next()) {
				Patient p = new Patient(rs);
				ps.add(p);
			}
			return ps;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public ArrayList<HealthSupporter> getPatientsHS(Patient p){
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<HealthSupporter> hs = new ArrayList<HealthSupporter>();
		try{
			conn = openConnection();
			String SQL = "SELECT * FROM PERSON p, Health_Supporter h "
					+ "WHERE h.HS_Patient = ? AND h.HS_Supporter = p.Per_Id";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, p.getSsn());
			rs = stmt.executeQuery();
			while (rs.next()) {
				HealthSupporter h = new HealthSupporter(rs);
				hs.add(h);
			}
			return hs;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public boolean removeHSForPatient(long ssn, HealthSupporter h) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			conn = openConnection();
			String SQL = "DELETE FROM HealthSupporter WHERE HS_Supporter = ?, HS_Patient = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, h.getSsn());
			stmt.setLong(2, ssn);
			stmt.executeUpdate();
			conn.commit();
			return true;
		} catch(SQLException e){
			conn.rollback();
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
		
	}
	
	public boolean removeDiseaseForPatient(long ssn, String dis) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			conn = openConnection();
			String SQL = "DELETE FROM PatientDisease WHERE Pd_Patient = ?, Pd_DiseaseName = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, ssn);
			stmt.setString(2, dis);
			stmt.executeUpdate();
			conn.commit();
			return true;
		} catch(SQLException e){
			conn.rollback();
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
		
	}
	
	public boolean addDiseaseForPatient(long ssn, String dis) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			conn = openConnection();
			String SQL = "INSERT INTO Diagnosis VALUES(?,?)";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, ssn);
			stmt.setString(2, dis);
			stmt.executeUpdate();
			conn.commit();
			return true;
		} catch(SQLException e){
			conn.rollback();
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public ArrayList<String> getPatientsDiseases(long ssn){
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<String> diseases = new ArrayList<String>();
		try{
			conn = openConnection();
			String SQL = "SELECT * FROM Diagnosis WHERE Di_Patient = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, ssn);
			rs = stmt.executeQuery();
			while (rs.next()) {
				diseases.add(rs.getString("Di_DiseaseName"));
			}
			return diseases;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public ArrayList<String> getAllDiseases(){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<String> diseases = new ArrayList<String>();
		try{
			conn = openConnection();
			stmt = conn.createStatement();
			String SQL = "SELECT * FROM Disease";
			rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				diseases.add(rs.getString("Dis_DiseaseName"));
			}
			return diseases;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public Patient patientLogin(long user, String pass) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Patient p = null;
		try{
			conn = openConnection();
			
			String SQL = "SELECT * FROM PERSON P,PATIENT P2 "
					+ "WHERE P.Per_Id = ? "
					+ "AND P2.Pat_Person = ? "
					+ "AND P.Per_Password = ?";
					
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, user);
			stmt.setLong(2, user);
			stmt.setString(3, pass);
			rs = stmt.executeQuery();
//			Must set each variable of Patient by getting each field value
			while(rs.next()){
				p = new Patient(rs);
			}
			
			
			return p;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public HealthSupporter healthSupporterLogin(long user, String pass) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		HealthSupporter h = null;
		try{
			conn = openConnection();
			
			String SQL = "SELECT * FROM PERSON P, Health_Supporter H "
					+ "WHERE P.Per_Id = ? "
					+ "AND H.HS_Supporter = ? "
					+ "AND P.Per_Password = ?";
					
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, user);
			stmt.setLong(2, user);
			stmt.setString(3, pass);
			rs = stmt.executeQuery();
			while(rs.next()){
				h = new HealthSupporter(rs);
			}
				
			return h;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public boolean editHSNoPatient(HealthSupporter p) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = openConnection();
			//Update the person table
			String SQL = "UPDATE PERSON SET "
					+ "Per_FirstName = ?,"
					+ "Per_LastName = ?,"
					+ "Per_DateOfBirth = ?," 
					+ "Per_Address = ?,"
					+ "Per_Phone = ?,"
					+ "Per_Sex = ?,"
					+ "Per_Password = ?"
					+ "WHERE Per_Id = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setString(1, p.getFname());
			stmt.setString(2, p.getLname());
			stmt.setDate(3, p.getDOB());
			stmt.setString(4, p.getAddress());
			stmt.setString(5, p.getPhoneNum());
			stmt.setString(6, p.getSex());
			stmt.setString(6, p.getPassword());
			stmt.setLong(7, p.getSsn());
			stmt.executeUpdate();
			conn.commit();	
			return true;
		} catch(SQLException e){
			conn.rollback();
			e.printStackTrace();
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public boolean editHS(Patient OGp, HealthSupporter p) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = openConnection();
			//Update the person table
			String SQL = "UPDATE PERSON SET "
					+ "Per_FirstName = ?,"
					+ "Per_LastName = ?,"
					+ "Per_DateOfBirth = ?," 
					+ "Per_Address = ?,"
					+ "Per_Phone = ?,"
					+ "Per_Sex = ?,"
					+ "Per_Password = ?"
					+ "WHERE Per_Id = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setString(1, p.getFname());
			stmt.setString(2, p.getLname());
			stmt.setDate(3, p.getDOB());
			stmt.setString(4, p.getAddress());
			stmt.setString(5, p.getPhoneNum());
			stmt.setString(6, p.getSex());
			stmt.setString(6, p.getPassword());
			stmt.setLong(7, p.getSsn());
			stmt.executeUpdate();
			conn.commit();
			close(stmt);
			
			//update the HS table
			SQL =  "UPDATE Health_Supporter SET "
				+ "HS_DateAuthorized = ? AND HS_DateUnauthorized = ?"
				+ "WHERE HS_Supporter = ? AND HS_Patient = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setDate(1, p.getDateAuthorized());
			stmt.setDate(2, p.getDateUnauthorized());
			stmt.setLong(3, p.getSsn());
			stmt.setLong(4, OGp.getSsn());
			stmt.executeUpdate();
			conn.commit();
			return true;
		} catch(SQLException e){
			conn.rollback();
			e.printStackTrace();
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public boolean editPatient(Patient p){
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = openConnection();
			//Update the person table
			String SQL = "UPDATE PERSON SET "
					+ "Per_FirstName = ?,"
					+ "Per_LastName = ?,"
					+ "Per_DateOfBirth = ?," 
					+ "Per_Address = ?,"
					+ "Per_Phone = ?,"
					+ "Per_Sex = ?,"
					+ "Per_Password = ?"
					+ "WHERE Per_Id = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setString(1, p.getFname());
			stmt.setString(2, p.getLname());
			stmt.setDate(3, p.getDOB());
			stmt.setString(4, p.getAddress());
			stmt.setString(5, p.getPhoneNum());
			stmt.setString(6, p.getSex());
			stmt.setString(6, p.getPassword());
			stmt.setLong(7, p.getSsn());
			stmt.executeUpdate();
			close(stmt);
			
			//update the patient table
			SQL =  "UPDATE PATIENT SET "
				+ "Pat_Sick = ?"
				+ "WHERE Pat_Person = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, p.isSick());
			stmt.setLong(2, p.getSsn());
			stmt.executeUpdate();	
			return true;
		} catch(SQLException e){
			e.printStackTrace();
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public boolean addNewPatient(Patient p){
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = openConnection();
			String SQL = "INSERT INTO PERSON "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, p.getSsn());
			stmt.setString(2, p.getFname());
			stmt.setString(3, p.getLname());
			stmt.setDate(4, p.getDOB());
			stmt.setString(5, p.getAddress());
			stmt.setString(6,  p.getPhoneNum());
			stmt.setString(7,  p.getSex());
			stmt.setString(8,  p.getPassword());
			stmt.executeUpdate();
			close(stmt);
			SQL = "INSERT INTO PATIENT VALUES (?, ?)";
			stmt = conn.prepareStatement(SQL);
			stmt.setFloat(1,  p.getSsn());
			stmt.setFloat(2, p.isSick());
			stmt.executeUpdate();	
			return true;
		} catch(SQLException e){
			e.printStackTrace();
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
		
		
	}
	
	public ArrayList<String> getAllPatientSSNs(){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<String> ssns = new ArrayList<String>();
		try{
			conn = openConnection();
			stmt = conn.createStatement();
			String SQL = "SELECT Pat_Person FROM PERSON";
			rs = stmt.executeQuery(SQL);
			while (rs.next()) {
			    ssns.add(rs.getString("Pat_Person"));
			}
			return ssns;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public boolean addNewHS(HealthSupporter p){
		Connection conn = null;
		Statement stmt = null;
		try{
			conn = openConnection();
			stmt = conn.createStatement();
			String SQL = "INSERT INTO PERSON "
					+ "VALUES ("
					+ p.getSsn() + ","
					+ p.getFname() + ","
					+ p.getLname() + ","
					+ p.getDOB() + ","
					+ p.getAddress() + ","
					+ p.getPhoneNum() + ","
					+ p.getSex() + ","
					+ p.getPassword() + ""
					+ ")";
			stmt.executeUpdate(SQL);
			SQL =  "INSERT INTO Health_Supporter "
				+ "VALUES ("
				+ p.getSsn() + ","
				+ p.getSupportingPatientID() + ","
				+ p.getDateAuthorized() + ","
				+ p.getDateUnauthorized()
				+ ")";
			stmt.executeUpdate(SQL);	
			return true;
		} catch(SQLException e){
			e.printStackTrace();
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	
	private Connection openConnection() throws SQLException{
		try {
			Connection conn = DriverManager.getConnection(jdbcURL, DBuser, DBpassword);
			//Connection conn = new Connection();
			
			return conn;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw e;
		}
	}
	

	
	private void close(Connection conn) {
        if(conn != null) {
            try { conn.close(); } catch(Throwable whatever) {}
        }
    }

    private void close(Statement st) {
        if(st != null) {
            try { st.close(); } catch(Throwable whatever) {}
        }
    }

    private void close(ResultSet rs) {
        if(rs != null) {
            try { rs.close(); } catch(Throwable whatever) {}
        }
    }

  //just testing the DB connection to create/insert/select data, no real use
  	public boolean test(){
  		Connection conn = null;
  		Statement stmt = null;
  		ResultSet rs = null;
  		
  		try{
  			conn = openConnection();
  			stmt = conn.createStatement();
  			//stmt.executeUpdate("create table TEST1(val1 integer)");
  			//stmt.executeUpdate("insert into TEST values(3)");
  			//stmt.executeUpdate("insert into TEST values(4)");
//  			rs = stmt.executeQuery("SELECT * FROM Diagnosis d, Patient ps WHERE d.Di_Patient=ps.Pat_Person");
//  			rs = stmt.executeQuery("SELECT * FROM Diagnosis");
//  			while (rs.next()) {
//  			    String p = rs.getString("Di_DiseaseName");
//  			    long s = rs.getLong("Di_Patient");
//  			    System.out.println(s+":"+p);
//  			}
  			
  			rs = stmt.executeQuery("SELECT * FROM Patient");
  			while (rs.next()) {
  				long i  = rs.getLong("Pat_Person");
  			    //long i = rs.getLong("HS_Supporter");
  			    System.out.println(i);
  			}
  			return true;
  		} catch(SQLException e){
  			e.printStackTrace();
  			return false;
  		} finally {
  			close(stmt);
            close(conn);
  		}
  	}

  	
  	
  	//IMPLEMENTED QUERIES - SEPERATE FROM APPLICATION
	public void patientsWithMoreThanOneDiagnosis() {
		Connection conn = null;
  		Statement stmt = null;
  		ResultSet rs = null;
  		ArrayList<Patient> p = new ArrayList<Patient>();
  		try{
  			conn = openConnection();
  			stmt = conn.createStatement();
  			
  			rs = stmt.executeQuery("SELECT * FROM Person p, PATIENT pa"
  								+ "WHERE p.Per_Id = pa.Pat_Person AND pa.Pat_Sick = 1 AND"
  								+ "2 = (SELECT COUNT(*) FROM Diagnosis pd WHERE pd.Di_Patient = pa.Pat_Person)");
  			while (rs.next()) {
  				Patient pa = new Patient(rs);
  				p.add(pa);
  			}
  			
  		} catch(SQLException e){
  			e.printStackTrace();
  		} finally {
  			close(stmt);
            close(conn);
            close(rs);
  		}
  		int size = p.size();
  		for(int i = 0; i < size; i++){
  			Patient pa = p.get(i);
  			System.out.println(pa.getFname() + " " + pa.getLname());
  		}
	}

	public void sickPatientsWithHSAlsoSick() {
		Connection conn = null;
  		Statement stmt = null;
  		ResultSet rs = null;
  		ArrayList<Patient> p = new ArrayList<Patient>();
  		try{
  			conn = openConnection();
  			stmt = conn.createStatement();
  			
  			rs = stmt.executeQuery("SELECT * FROM Person p, PATIENT pa"
  								+ "WHERE p.Per_Id = pa.Pat_Person AND pa.Pat_Sick = 1 AND"
  								+ "EXISTS(SELECT * FROM Health_Supporter h, PATIENT pa2 WHERE"
  								+ "h.HS_Patient = pa.Pat_Person AND"
  								+ "h.HS_Supporter = pa2.Pat_Person AND"
  								+ "pa2.Pat_Sick = 1)");
  			while (rs.next()) {
  				Patient pa = new Patient(rs);
  				p.add(pa);
  			}
  			
  		} catch(SQLException e){
  			e.printStackTrace();
  		} finally {
  			close(stmt);
            close(conn);
            close(rs);
  		}
  		int size = p.size();
  		for(int i = 0; i < size; i++){
  			Patient pa = p.get(i);
  			System.out.println(pa.getFname() + " " + pa.getLname());
  		}
		
	}

	public void patientsWithTwoHS() {
		Connection conn = null;
  		Statement stmt = null;
  		ResultSet rs = null;
  		ArrayList<Patient> p = new ArrayList<Patient>();
  		try{
  			conn = openConnection();
  			stmt = conn.createStatement();
  			
  			rs = stmt.executeQuery("SELECT * FROM Person p, PATIENT pa"
  								+ "WHERE p.Per_Id = pa.Pat_Person AND"
  								+ "2 = (SELECT COUNT(*) FROM Health_Supporter h WHERE"
  								+ "h.HS_Patient = pa.Pat_Person)");
  			while (rs.next()) {
  				Patient pa = new Patient(rs);
  				p.add(pa);
  			}
  			
  		} catch(SQLException e){
  			e.printStackTrace();
  		} finally {
  			close(stmt);
            close(conn);
            close(rs);
  		}
  		int size = p.size();
  		for(int i = 0; i < size; i++){
  			Patient pa = p.get(i);
  			System.out.println(pa.getFname() + " " + pa.getLname());
  		}
		
	}

	public void alertsEachMonthEachPatient() {
		// TODO Auto-generated method stub
		
	}

	public void mostAlertsPerMonth() {
		// TODO Auto-generated method stub
		
	}
}
