package phms.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient extends Person {
	private boolean sick;
	
	public Patient(){
		
	}

	public Patient(ResultSet rs){
		super(rs);
		try{
			setSick(rs.getBoolean("Sick"));
		}catch(SQLException e){
			System.out.println("Patient ERROR");
			e.printStackTrace();
			System.out.println(e.getMessage());
		}	
	}
	
	public boolean isSick() {
		return sick;
	}

	public void setSick(boolean sick) {
		this.sick = sick;
	}
}
