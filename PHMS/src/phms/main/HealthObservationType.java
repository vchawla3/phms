package phms.main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HealthObservationType {

	private long hotID;
	private String name;
	private String disease;
	private Long upper;
	private Long lower;
	private Long freq;
	
	public HealthObservationType(){
		
	}
	
	public HealthObservationType(ResultSet rs) throws SQLException{
		setHotID(rs.getLong("Hot_Id"));
		setName(rs.getString("Hot_Name"));
		setDisease(rs.getString("Hot_Disease"));
		setUpper(rs.getLong("Hot_UpperLimit"));
		if (rs.wasNull()){
			setUpper(null);
		}
		setLower(rs.getLong("Hot_LowerLimit"));
		if (rs.wasNull()){
			setLower(null);
		}
		setFreq(rs.getLong("Hot_Frequency"));
		if (rs.wasNull()){
			setFreq(null);
		}
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
	public Long getUpper() {
		return upper;
	}
	public void setUpper(Long upper) {
		this.upper = upper;
	}
	public Long getLower() {
		return lower;
	}
	public void setLower(Long lower) {
		this.lower = lower;
	}
	public Long getFreq() {
		return freq;
	}
	public void setFreq(Long freq) {
		this.freq = freq;
	}
}
