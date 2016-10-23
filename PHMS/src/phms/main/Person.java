package phms.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public abstract class Person {
	private long ssn;
	private String fname;
	private String lname;
	private Date DOB;
	private String address;
	private String phoneNum;
	private String sex;
	private String password;
	
	public Person(ResultSet rs) throws SQLException{
		try {
			setSsn(rs.getInt("Per_Id"));
			setFname(rs.getString("Per_FirstName"));
			setLname(rs.getString("Per_LastName"));
			setDOB(rs.getDate("Per_DateOfBirth"));
			setAddress(rs.getString("Per_Address"));
			setPhoneNum(rs.getString("Per_Phone"));
			setSex(rs.getString("Per_Sex"));
			setPassword(rs.getString("Per_Password"));
		} catch (SQLException e) {
			System.out.println("Person ERROR");
			//System.out.println(e.getMessage());
			throw e;
		}
	}
	
	public Person(){
		
	}
	
	public long getSsn() {
		return ssn;
	}
	public void setSsn(long ssn) {
		this.ssn = ssn;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	public Date getDOB() {
		return DOB;
	}
	public void setDOB(Date dOB) {
		DOB = dOB;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getSex() {
		return sex;
	}
	
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
