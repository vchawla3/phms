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
	
	public boolean removeHSForPatient(long ssn, HealthSupporter h) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			conn = openConnection();
			String SQL = "DELETE FROM HealthSupporter WHERE HS_Supporter = ?, HS_Patient = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, h.getSsn());
			stmt.setLong(2, ssn);
			stmt.executeUpdate();
			return true;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
		
	}
	
	public boolean removeDiseaseForPatient(long ssn, String dis) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			conn = openConnection();
			String SQL = "DELETE FROM PatientDisease WHERE Pd_Patient = ?, Pd_DiseaseName = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, ssn);
			stmt.setString(2, dis);
			stmt.executeUpdate();
			return true;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
		
	}
	
	public boolean addDiseaseForPatient(long ssn, String dis){
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			conn = openConnection();
			String SQL = "INSERT INTO Diagnosis VALUES(?,?)";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, ssn);
			stmt.setString(2, dis);
			stmt.executeUpdate();
			return true;
		} catch(SQLException e){
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
	
	public boolean editHS(Patient OGp, HealthSupporter p){
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
			return true;
		} catch(SQLException e){
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
  			rs = stmt.executeQuery("SELECT * FROM Person p ,Patient ps WHERE p.Per_Id=ps.Pat_Person");
  			while (rs.next()) {
  				long s = rs.getLong("Per_Id");
  			    String p = rs.getString("Per_Password");
  			    int sick = rs.getInt("Pat_Sick");
  			    System.out.println(s+":"+p+":"+sick);
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
}
