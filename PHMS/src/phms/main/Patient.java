package phms.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class Patient extends Person {
	private int sick;
	private Date dateGotSick;
	
	public Patient(){
		
	}

	public Patient(ResultSet rs) throws SQLException{
		super(rs);
		try{
			setSick(rs.getInt("Pat_Sick"));
			setDateGotSick(rs.getDate("Pat_FeltSickOn"));
		}catch(SQLException e){
			System.out.println("Patient ERROR");
			//System.out.println(e.getMessage());
			throw e;
		}	
	}
	
	public Date getDateGotSick() {
		return dateGotSick;
	}

	public void setDateGotSick(Date dateGotSick) {
		this.dateGotSick = dateGotSick;
	}

	public int isSick() {
		return sick;
	}

	public void setSick(int sick) {
		this.sick = sick;
	}
}
