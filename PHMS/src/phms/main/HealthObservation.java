package phms.main;

import java.sql.*;

public class HealthObservation {
	private long patientId;
	private long hoTypeId;
	private String hoType;
	private long value;
	private Date recordedDate;
	private Date observedDate;
	public HealthObservation(){
		
	}
	
	public HealthObservation(ResultSet rs) throws SQLException{
		setPatientId(rs.getLong("Ho_Patient"));
		setHoTypeId(rs.getLong("Ho_ObservationType"));
		setHoType(rs.getString("Hot_Name"));
		setValue(rs.getLong("Ho_Value"));
		setRecordedDate(rs.getDate("Ho_RecordedDateTime"));
		// TODO fix once the column is added to the table
		//setObservedDate(rs.getDate("Ho_RecordedDateTime"));
	}

	public long getPatientId() {
		return patientId;
	}
	public void setPatientId(long patientId) {
		this.patientId = patientId;
	}
	public long getHoTypeId() {
		return hoTypeId;
	}
	public void setHoTypeId(long hoTypeId) {
		this.hoTypeId = hoTypeId;
	}
	public String getHoType() {
		return hoType;
	}
	public void setHoType(String hoType) {
		this.hoType = hoType;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	public Date getRecordedDate() {
		return recordedDate;
	}
	public void setRecordedDate(Date recordedDate) {
		this.recordedDate = recordedDate;
	}
	public Date getObservedDate() {
		return observedDate;
	}
	public void setObservedDate(Date observedDate) {
		this.observedDate = observedDate;
	}

}
