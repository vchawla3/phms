package phms.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
			rs = stmt.executeQuery(SQL);
			p = new Patient(rs);	
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
	
	public boolean addNewPatient(Patient p){
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
			SQL =  "INSERT INTO PATIENT "
				+ "VALUES ("
				+ p.getSsn() + ","
				+ p.isSick()+ ""
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
  			//rs = stmt.executeQuery("SELECT * FROM TEST");
  			while (rs.next()) {
  			    int s = rs.getInt("VAL1");
  			    System.out.println(s);
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
