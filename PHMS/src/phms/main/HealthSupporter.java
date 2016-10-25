package phms.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class HealthSupporter extends Person {
	private long supportingPatientID;
	
	private Date dateAuthorized;
	private Date dateUnauthorized;
	
	public HealthSupporter(){
		
	}
	
	public HealthSupporter(ResultSet rs) throws SQLException{
		super(rs);
		try{
			setSupportingPatientID(rs.getInt("HS_Patient"));
			setDateAuthorized(rs.getDate("HS_DateAuthorized"));
			setDateUnauthorized(rs.getDate("HS_DateUnauthorized"));
		}catch(SQLException e){
			System.out.println("HealthSupporter ERROR");
			//e.printStackTrace();
			throw e;
			//System.out.println(e.getMessage());
		}
	}

	public long getSupportingPatientID() {
		return supportingPatientID;
	}

	public void setSupportingPatientID(long supportingPatientID) {
		this.supportingPatientID = supportingPatientID;
	}

	public Date getDateAuthorized() {
		return dateAuthorized;
	}

	public void setDateAuthorized(Date dateAuthorized) {
		this.dateAuthorized = dateAuthorized;
	}

	public Date getDateUnauthorized() {
		return dateUnauthorized;
	}

	public void setDateUnauthorized(Date dateUnauthorized) {
		this.dateUnauthorized = dateUnauthorized;
	}
}
