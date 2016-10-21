package phms.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient extends Person {
	private boolean sick;

	public boolean isSick() {
		return sick;
	}

	public void setSick(boolean sick) {
		this.sick = sick;
	}
	
	public Patient(){
		
	}
	
	public Patient(ResultSet rs){
		try{
			super(rs);
		}catch(SQLException e){
			
		}
		
		
	}
}
