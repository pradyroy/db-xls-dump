package in.pune.pradyroy.model;

public class DbDetails extends BaseModel{	
	
	private String connectionurl;
	private String servername ;
	private String portnumber ;
	private String dbname ;
	private String username ;
	private String password ;
	private String querystring ;
	
	public String getConnectionurl() {
		return connectionurl;
	}
	public void setConnectionurl(String connectionurl) {
		this.connectionurl = connectionurl;
	}
	public String getServername() {
		return servername;
	}
	public void setServername(String servername) {
		this.servername = servername;
	}
	public String getPortnumber() {
		return portnumber;
	}
	public void setPortnumber(String portnumber) {
		this.portnumber = portnumber;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getQuerystring() {
		return querystring;
	}
	public void setQuerystring(String querystring) {
		this.querystring = querystring;
	}
}
