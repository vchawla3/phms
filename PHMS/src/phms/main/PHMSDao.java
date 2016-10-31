package phms.main;

import java.sql.CallableStatement;
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
	
//	static final String DBuser = "aapatel8";
//	static final String DBpassword = "200005768";
	
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
	

	public ArrayList<HealthSupporter> getPossibleHS(Patient p) {
		ArrayList<HealthSupporter> list = new ArrayList<HealthSupporter>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try{
			conn = openConnection();
			String SQL = "select * from Person "
					+ "where Per_ID != ? AND "
					+ "Per_Id NOT IN ( select HS_Supporter from  Health_Supporter where HS_Patient = ?)";
			
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, p.getSsn());
			stmt.setLong(2, p.getSsn());
			rs = stmt.executeQuery();
			while (rs.next()) {
				HealthSupporter pa = new HealthSupporter(rs, true);
				list.add(pa);
			}
			return list;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public boolean addHealthObservation(HealthObservation ho) throws SQLException{
		ArrayList<Long> ids = findhoID(ho);
		for (int i = 0; i < ids.size(); i++) {
			long hoID = ids.get(i);
			ho.setHoTypeId(hoID);
			
			Connection conn = null;
			PreparedStatement stmt = null;
			//ResultSet rs = null;
			try{
				conn = openConnection();
				String SQL = "INSERT INTO Health_Observation VALUES(?,?,?,?,?)";
				stmt = conn.prepareStatement(SQL);
				stmt.setLong(1, ho.getPatientId());
				stmt.setLong(2, ho.getHoTypeId());
				stmt.setLong(3, ho.getValue());
				stmt.setDate(4, ho.getObservedDate());
				stmt.setDate(5, ho.getRecordedDate());
				stmt.executeUpdate();
				conn.commit();
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
		return true;
		
	}
	private ArrayList<Long> findhoID(HealthObservation h) {
		ArrayList<Long> ids = new ArrayList<Long>();
		//long id;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try{
			conn = openConnection();
			//First check recommends to see if it has it
			String SQL = "SELECT h.Hot_Id FROM Health_Observation_Type h, Recommendation r "
					+ "WHERE r.Rec_HOT_Type = h.Hot_Id AND h.Hot_Name = ? AND r.Rec_HS_Patient = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setString(1, h.getHoType());
			stmt.setLong(2, h.getPatientId());
			rs = stmt.executeQuery();
			if (rs.next()) {
				ids.add(rs.getLong("Hot_Id"));
			} else {
				close(stmt);
				close(rs);
				//Then check the if there is a HOT ID for any disease they have
				SQL = "SELECT h.Hot_Id FROM Health_Observation_Type h WHERE h.Hot_Name = ? AND h.Hot_Disease "
						+ "IN (SELECT Di_DiseaseName FROM Diagnosis WHERE Di_Patient = ?)";
				stmt = conn.prepareStatement(SQL);
				stmt.setString(1, h.getHoType());
				stmt.setLong(2, h.getPatientId());
				rs = stmt.executeQuery();
				if(rs.next()) {
					ids.add(rs.getLong("Hot_Id"));
					while (rs.next()){
						ids.add(rs.getLong("Hot_Id"));
					}
				} else {
					close(stmt);
					close(rs);
					//Lastly just get the generic one, so disease null and id not in reccomendation
					SQL = "SELECT h.Hot_Id FROM Health_Observation_Type h WHERE h.Hot_Name = ? AND h.Hot_Disease IS NULL "
							+ "AND h.Hot_Id NOT IN(SELECT r.Rec_HOT_Type FROM Recommendation r)";
					stmt = conn.prepareStatement(SQL);
					stmt.setString(1, h.getHoType());
					rs = stmt.executeQuery();
					rs.next();
					ids.add(rs.getLong("Hot_Id")); 
				}
			}
			return ids;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
		
	}

	public ArrayList<String> listOfHOTNames(){
		ArrayList<String> list = new ArrayList<String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			conn = openConnection();
			stmt = conn.createStatement();
			String SQL = "SELECT DISTINCT Hot_Name FROM Health_Observation_Type";
			rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				list.add(rs.getString("Hot_Name"));
			}
			return list;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	public ArrayList<HealthObservation> getPatientHealthObs(Patient p) {
		ArrayList<HealthObservation> hos = new ArrayList<HealthObservation>();
  		Connection conn = null;
  		PreparedStatement stmt = null;
  		ResultSet rs = null;
  		try{
  			conn = openConnection();
  			String SQL = "SELECT * FROM Health_Observation_Type ho, Health_Observation h "
  						+ "WHERE h.Ho_Patient = ? AND h.Ho_ObservationType = ho.Hot_Id ORDER BY h.Ho_ObservedDateTime";
  			stmt = conn.prepareStatement(SQL);
  			stmt.setLong(1, p.getSsn());
  			rs = stmt.executeQuery();
  			while (rs.next()) {
  				HealthObservation pa = new HealthObservation(rs);
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
	
	public ArrayList<HealthObservationType> getHOTPrefs(Patient p){
		ArrayList<HealthObservationType> hos = new ArrayList<HealthObservationType>();
		Connection conn = null;
  		PreparedStatement stmt = null;
  		ResultSet rs = null;
  		try{
  			conn = openConnection();
  			String SQL = "SELECT * FROM Health_Observation_Type h, Recommendation r "
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
  	public ArrayList<HealthObservationType> getAllRecomendationsforPatient(Patient p){
  		ArrayList<HealthObservationType> hos = new ArrayList<HealthObservationType>();
  		Connection conn = null;
  		PreparedStatement stmt = null;
  		ResultSet rs = null;
  		try{
  			conn = openConnection();
  			String SQL = "with tmp as "
  					+ "(SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency "
  					+ "from Health_Observation_Type h, Recommendation r, Person p "
  					+ "where h.Hot_Id = r.Rec_HOT_Type AND p.Per_Id=r.Rec_HS_Patient AND p.Per_Id = ? "
  					+ "UNION "
  					+ "SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency "
  					+ "from Health_Observation_Type h "
  					+ "where "
  					+ "h.Hot_Disease IN (select d.Di_DiseaseName from Person p, Diagnosis d where d.Di_Patient=p.Per_Id AND p.Per_Id = ?) "
  					+ "AND h.Hot_Id NOT IN (SELECT Rec_HOT_Type from Recommendation) "
  					+ "AND h.hot_name NOT IN (SELECT h.Hot_Name "
  					+ "from Health_Observation_Type h, Recommendation r, Person p "
  					+ "where h.Hot_Id = r.Rec_HOT_Type AND p.Per_Id=r.Rec_HS_Patient AND p.Per_Id = ?)) "
  					+ "select * from tmp "
  					+ "union "
  					+ "SELECT h.Hot_Id, h.Hot_Name, h.Hot_UpperLimit, h.Hot_LowerLimit, h.Hot_Frequency "
  					+ "from Health_Observation_Type h "
  					+ "where h.Hot_Disease IS NULL AND h.hot_name NOT IN(tmp)";

  			stmt = conn.prepareStatement(SQL);
  			stmt.setLong(1, p.getSsn());
  			stmt.setLong(2, p.getSsn());
  			stmt.setLong(3, p.getSsn());
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
  			if (ht.getDisease() == null){
  				stmt.setNull(2, java.sql.Types.VARCHAR);
  			} else {
  				stmt.setString(2, ht.getDisease());
  			}
  			stmt.setLong(3, ht.getUpper());
  			if (ht.getLower() == -1) {
  				stmt.setNull(4, java.sql.Types.INTEGER);
  			} else {
  				stmt.setLong(4, ht.getLower());
  			}
  			
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
			String SQL = "SELECT * FROM Alert a, Health_Observation_Type h "
					+ "WHERE a.Al_PER_Patient = ? AND a.Al_OBS_Type = h.Hot_Id "
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
			String SQL = "SELECT * FROM Alert a, Health_Observation_Type h "
					+ "WHERE a.AL_READ = 0 AND a.Al_PER_Patient = ? AND a.Al_HOT_Type = h.Hot_Id "
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
			String SQL = "UPDATE Alert a SET "
						+ "a.Al_Read = 1 "
						+ "WHERE "
						+ "a.Al_PER_Patient = ? AND "
						+ "a.Al_HOT_Type = ?";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, a.getPatientId());
			stmt.setLong(2, a.getHOTypeID());
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
			String SQL = "SELECT * FROM PERSON p, PATIENT p2, Health_Supporter h "
					+ "WHERE p.Per_Id = h.HS_Patient AND h.HS_Supporter = ? AND p.Per_Id = p2.PAT_PERSON "
					+ "AND h.HS_DateAuthorized <= TRUNC(SYSDATE) "
					+ "AND (h.HS_DateUnauthorized > TRUNC(SYSDATE) OR h.HS_DateUnauthorized IS NULL)";
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
					+ "WHERE h.HS_Patient = ? AND h.HS_Supporter = p.Per_Id "
					+ "AND h.HS_DateAuthorized <= TRUNC(SYSDATE) "
					+ "AND (h.HS_DateUnauthorized > TRUNC(SYSDATE) OR h.HS_DateUnauthorized IS NULL)";;
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
			String SQL = "DELETE FROM Health_Supporter WHERE HS_Supporter = ? AND HS_Patient = ?";
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
		CallableStatement stmt = null;
		try{
			conn = openConnection();
			//Call stored proc to update patient table 
			String call = "{ ? = call deleteDiagnosis(?, ?) }";
			stmt = conn.prepareCall(call);
			stmt.registerOutParameter(1, oracle.jdbc.OracleTypes.NUMBER);
			stmt.setString(2, dis);
			stmt.setLong(3, ssn);
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
			if(e.getErrorCode() == 00001){
				System.out.println("Cannot add same disease twice");
			}
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
			stmt.setString(7, p.getPassword());
			stmt.setLong(8, p.getSsn());
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
			stmt.setString(7, p.getPassword());
			stmt.setLong(8, p.getSsn());
			stmt.executeUpdate();
			conn.commit();
			close(stmt);
			
			//update the HS table
			SQL =  "UPDATE Health_Supporter SET "
				+ "HS_DateAuthorized = ?, HS_DateUnauthorized = ? "
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
			stmt.setString(7, p.getPassword());
			stmt.setLong(8, p.getSsn());
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
			SQL = "INSERT INTO PATIENT VALUES (?, ?, NULL)";
			stmt = conn.prepareStatement(SQL);
			stmt.setFloat(1,  p.getSsn());
			//default set sick to 0, will be updated as diseases added/removed
			stmt.setFloat(2, 0);
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
	
	//Unused and didnt use prepared statement anyway
//	public boolean addNewHS(HealthSupporter p){
//		Connection conn = null;
//		Statement stmt = null;
//		try{
//			conn = openConnection();
//			stmt = conn.createStatement();
//			String SQL = "INSERT INTO PERSON "
//					+ "VALUES ("
//					+ p.getSsn() + ","
//					+ p.getFname() + ","
//					+ p.getLname() + ","
//					+ p.getDOB() + ","
//					+ p.getAddress() + ","
//					+ p.getPhoneNum() + ","
//					+ p.getSex() + ","
//					+ p.getPassword() + ""
//					+ ")";
//			stmt.executeUpdate(SQL);
//			SQL =  "INSERT INTO Health_Supporter "
//				+ "VALUES ("
//				+ p.getSsn() + ","
//				+ p.getSupportingPatientID() + ","
//				+ p.getDateAuthorized() + ","
//				+ p.getDateUnauthorized()
//				+ ")";
//			stmt.executeUpdate(SQL);	
//			return true;
//		} catch(SQLException e){
//			e.printStackTrace();
//			return false;
//		} finally {
//			close(stmt);
//            close(conn);
//		}
//	}
	
	public boolean addNewPerson(Patient p){
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
			return true;
		} catch(SQLException e){
			e.printStackTrace();
			return false;
		} finally {
			close(stmt);
            close(conn);
		}
	}
	
	public boolean addHSAlreadyPerson(HealthSupporter h) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = openConnection();
			String SQL = "INSERT INTO Health_Supporter VALUES (?, ?, ?, ?)";
			stmt = conn.prepareStatement(SQL);
			stmt.setFloat(1,  h.getSsn());
			stmt.setFloat(2, h.getSupportingPatientID());
			stmt.setDate(3, h.getDateAuthorized());
			if (h.getDateUnauthorized() == null){
				stmt.setNull(4,java.sql.Types.DATE);
			} else {
				stmt.setDate(4, h.getDateUnauthorized());
			}
			
			stmt.executeUpdate();	
			return true;
		} catch(SQLException e){
			//e.printStackTrace();
			//Cannot add more than 2 HS per Patient !!!
			throw e;
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
//  			rs = stmt.executeQuery("SELECT table_name FROM user_tables");
//  			while (rs.next()) {
//  				System.out.println(rs.getString("table_name"));
//  			}
  			
  			rs = stmt.executeQuery("SELECT * FROM Person");

  			
  			while (rs.next()) {
  				long i  = rs.getLong("Per_Id");
  			    //long i = rs.getLong("HS_Supporter");
  			    System.out.println(i);
  			    //System.out.println(rs.getString("Dis_DiseaseName"));
  			    System.out.println(rs.getString("Per_Password"));
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
  			
  			rs = stmt.executeQuery("SELECT * FROM Person p, PATIENT pa "
  								+ "WHERE p.Per_Id = pa.Pat_Person AND pa.Pat_Sick = 1 AND "
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
  			
  			rs = stmt.executeQuery("SELECT * FROM Person p, PATIENT pa "
  								+ "WHERE p.Per_Id = pa.Pat_Person AND pa.Pat_Sick = 1 AND "
  								+ "EXISTS(SELECT * FROM Health_Supporter h, PATIENT pa2 WHERE "
  								+ "h.HS_Patient = pa.Pat_Person AND "
  								+ "h.HS_Supporter = pa2.Pat_Person AND "
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
  			rs = stmt.executeQuery("SELECT * FROM Person p, PATIENT pa "
  								+ "WHERE p.Per_Id = pa.Pat_Person AND "
  								+ "2 = (SELECT COUNT(*) FROM Health_Supporter h WHERE "
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


	public boolean addHealthObservationAlreadyID(HealthObservation ho) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		//ResultSet rs = null;
		try{
			conn = openConnection();
			String SQL = "INSERT INTO Health_Observation VALUES(?,?,?,?,?)";
			stmt = conn.prepareStatement(SQL);
			stmt.setLong(1, ho.getPatientId());
			stmt.setLong(2, ho.getHoTypeId());
			stmt.setLong(3, ho.getValue());
			stmt.setDate(4, ho.getObservedDate());
			stmt.setDate(5, ho.getRecordedDate());
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

}
