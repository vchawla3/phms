package phms.main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient extends Person {
	private int sick;
	
	public Patient(){
		
	}

	public Patient(ResultSet rs) throws SQLException{
		super(rs);
		try{
			setSick(rs.getInt("Pat_Sick"));
		}catch(SQLException e){
			System.out.println("Patient ERROR");
			//System.out.println(e.getMessage());
			throw e;
		}	
	}
	
	public int isSick() {
		return sick;
	}

	public void setSick(int sick) {
		this.sick = sick;
	}
}
