package phms.main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient extends Person {
	private int sick;
	
	public Patient(){
		
	}

	public Patient(ResultSet rs){
		super(rs);
		try{
			setSick(rs.getInt("Sick"));
		}catch(SQLException e){
			System.out.println("Patient ERROR");
			e.printStackTrace();
			System.out.println(e.getMessage());
		}	
	}
	
	public int isSick() {
		return sick;
	}

	public void setSick(int sick) {
		this.sick = sick;
	}
}
