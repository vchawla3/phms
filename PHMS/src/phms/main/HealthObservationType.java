package phms.main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HealthObservationType {

	private long hotID;
	private String name;
	private String disease;
	private long upper;
	private long lower;
	private long freq;
	
	public HealthObservationType(){
		
	}
	
	public HealthObservationType(ResultSet rs) throws SQLException{
		setHotID(rs.getLong("Hot_Id"));
		setName(rs.getString("Hot_Name"));
		setDisease(rs.getString("Hot_Disease"));
		setUpper(rs.getLong("Hot_UpperLimit"));
		setLower(rs.getLong("Hot_LowerLimit"));
		setFreq(rs.getLong("Hot_Frequency"));
	}
	
	public long getHotID() {
		return hotID;
	}
	public void setHotID(long hotID) {
		this.hotID = hotID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisease() {
		return disease;
	}
	public void setDisease(String disease) {
		this.disease = disease;
	}
	public long getUpper() {
		return upper;
	}
	public void setUpper(long upper) {
		this.upper = upper;
	}
	public long getLower() {
		return lower;
	}
	public void setLower(long lower) {
		this.lower = lower;
	}
	public long getFreq() {
		return freq;
	}
	public void setFreq(long freq) {
		this.freq = freq;
	}
}
