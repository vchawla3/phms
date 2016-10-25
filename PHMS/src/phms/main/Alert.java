package phms.main;

import java.sql.*;
public class Alert {

	private Long patientId;
	private Long HSId;
	private int read;
	private Date sent;
	private Long HOTypeID;
	private String HOType;
	private String alert;
	public Alert(){
		
	}
	
	public Alert(ResultSet rs) throws SQLException{
		try{
			setPatientId(rs.getLong("Al_HS_Patient"));
			setHSId(rs.getLong("Al_HS_Supporter"));
			setHOTypeID(rs.getLong("Al_OBS_Type"));
			setHOType(rs.getString("Hot_Name"));
			setRead(rs.getInt("Al_Read"));
			setSent(rs.getDate("Al_Sent"));
			setAlert(rs.getString("Al_Alert"));
		}catch(SQLException e){
			System.out.println("Alert ERROR");
			e.printStackTrace();
			throw e;
		}
	}
	public Long getPatientId() {
		return patientId;
	}
	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}
	public Long getHSId() {
		return HSId;
	}
	public void setHSId(Long hSId) {
		HSId = hSId;
	}
	public int getRead() {
		return read;
	}
	public void setRead(int read) {
		this.read = read;
	}
	public Date getSent() {
		return sent;
	}
	public void setSent(Date sent) {
		this.sent = sent;
	}
	public String getHOType() {
		return HOType;
	}
	public void setHOType(String hOType) {
		HOType = hOType;
	}
	public String getAlert() {
		return alert;
	}
	public void setAlert(String alert) {
		this.alert = alert;
	}
	public Long getHOTypeID() {
		return HOTypeID;
	}
	public void setHOTypeID(Long hOTypeID) {
		HOTypeID = hOTypeID;
	}
}
