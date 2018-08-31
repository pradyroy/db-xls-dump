package in.pune.pradyroy.ctrlr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import in.pune.pradyroy.model.DbDetails;

@RestController
@RequestMapping("/dbdump")
public class DbXlsDumpController {

	public Connection connection = null;
	
	private String connectionurl;
	private String drivername = "org.mariadb.jdbc.Driver";
	private String servername;
	private String portnumber;
	private String dbname;
	private String username;
	private String password;
	
	@RequestMapping(value="heartbeat", method = RequestMethod.GET)
	public String heartbeat() {
		return "db-xls-dump server date " + new Date().toString();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public DbDetails getsamplerequestbody() {
		DbDetails dbDetails = new DbDetails();
		dbDetails.setServername("127.0.0.1");
		dbDetails.setPortnumber("3306");
		dbDetails.setDbname("sample-db");
		dbDetails.setUsername("root");
		dbDetails.setPassword("su3spense");
		dbDetails.setQuerystring("select * from usernames");
		return dbDetails;
	}

	@RequestMapping(method = RequestMethod.POST, produces = "application/xls")
	public ResponseEntity<InputStreamResource> dodbdump(@RequestBody DbDetails model) {
		connectionurl = model.getConnectionurl();
		servername = model.getServername();
		portnumber = model.getPortnumber();
		dbname = model.getDbname();
		username = model.getUsername();
		password = model.getPassword();
		
		deleteOldFiles();

		Date currentdate = new Date();
		String currentdatestr = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(currentdate);
		String reportFileName = "dbdump-" + currentdatestr + ".xls";
		dumpDataToExcel(executeQuery(model.getQuerystring()), reportFileName);
		
		try {
			connection.close();
			System.out.println("DB connection closed.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		File dumpFile = new File(reportFileName);
		InputStream xlsFile;
		ResponseEntity<InputStreamResource> response = null;
		try {
			xlsFile = new FileInputStream(dumpFile);

			response = ResponseEntity.ok().contentLength(dumpFile.length())
					.contentType(MediaType.parseMediaType("application/octet-stream"))
					.body(new InputStreamResource(xlsFile));

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		return response;
	}

	public Connection getDbConnection() {
		try {

			Class.forName(drivername);
			// Create a connection to the database
			Properties properties = new Properties();
			properties.setProperty("user", username);
			properties.setProperty("password", password);
			String url;
			if (null != connectionurl) {
				url = connectionurl;
			} else {
				url = "jdbc:mariadb://" + servername + ":" + portnumber + "/" + dbname;
			}
			connection = DriverManager.getConnection(url, properties);
			if (null != connection) {
				System.out.println("Established Db Connection");
			}
		} catch (ClassNotFoundException e) {
			// Could not find the database driver
			System.out.println(e);
		} catch (SQLException e) {
			// Could not connect to the database
			System.out.println(e);
		} catch (Exception e) {
			// Could not connect to the database
			System.out.println(e);
		}
		return connection;
	}

	public ResultSet executeQuery(String queryString) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = getDbConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			// stmt = getDbConnection().createStatement( );
			System.out.println("Query to be executed is: " + queryString);
			rs = stmt.executeQuery(queryString);
			if (null != rs) {
				System.out.println("Fetched the resultset. The data is getting processed.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	public int getRowCount(ResultSet rs) {
		int size = 0;
		if (rs != null) {
			try {
				rs.beforeFirst();
				rs.last();
				size = rs.getRow();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Size :  " + size);
		return size;
	}

	public void dumpDataToExcel(ResultSet rs, String filename) {
		ResultSetMetaData rsmdata;
		try {

			rsmdata = rs.getMetaData();
			// getting number of columns retrieved from resultset
			int numColumns = rsmdata.getColumnCount();
			String columnName = "";
			String columnType = "";
			HashMap<Integer, DBMetaData> hm = new HashMap<Integer, DBMetaData>();
			// Get the column names; column indices start from 1
			HSSFWorkbook myWorkBook = new HSSFWorkbook();
			HSSFSheet mySheet = myWorkBook.createSheet();
			HSSFRow myRow = null;
			HSSFCell myCell = null;
			myRow = mySheet.createRow(0);
			for (int i = 0; i < numColumns; i++) {
				columnName = rsmdata.getColumnName(i + 1);
				columnType = rsmdata.getColumnTypeName(i + 1);
				DBMetaData dbMetaData = new DBMetaData();
				dbMetaData.setColumnName(columnName);
				dbMetaData.setColumnType(columnType);
				myCell = myRow.createCell(i);
				myCell.setCellValue(columnName);
				hm.put(i + 1, dbMetaData);
			}

			// */15 * * * *

			String tableHeader = "";
			String row = "";

			int count = 1;
			int rownum = 1;
			while (rs.next()) {
				HSSFRow rowCell = mySheet.createRow((short) rownum);
				for (int i = 1; i <= numColumns; i++) {
					if (i == numColumns) {
						if (count == 1)
							tableHeader = tableHeader + hm.get(i).getColumnName();
						row = row + rs.getString(hm.get(i).getColumnName()) + "\n";
						rowCell.createCell((short) i - 1).setCellValue(rs.getString(hm.get(i).getColumnName()));
					} else {
						if (count == 1)
							tableHeader = tableHeader + hm.get(i).getColumnName() + " || ";
						rowCell.createCell((short) i - 1).setCellValue(rs.getString(hm.get(i).getColumnName()));
						row = row + rs.getString(hm.get(i).getColumnName()) + " || ";
					}
				}
				count = count + 1;
				rownum++;
			}

			FileOutputStream fileOut = new FileOutputStream(filename);
			myWorkBook.write(fileOut);

			fileOut.close();
			myWorkBook.close();
			System.out.println("Dumped rows in xls: " + getRowCount(rs));

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Dumping to Excel Sheet completed. Report is ready.");
	}

	public static String getValString(DBMetaData dbMetaData, String val) {
		String value = "";
		if (dbMetaData.getColumnType().equalsIgnoreCase("NUMBER")) {
			value = val;
		} else {
			value = "'" + val + "'";
		}
		return value;
	}

	public static boolean isNumber(String string) {
		char[] c = string.toCharArray();
		for (int i = 0; i < string.length(); i++) {
			if (!Character.isDigit(c[i])) {
				return false;
			}
		}
		return true;
	}
	
	public void deleteOldFiles() {
		try{
		    String file;
		    File folder = new File(".");
		    File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		        if (listOfFiles[i].isFile()) {
		            file = listOfFiles[i].getName();
		                if (file.endsWith(".xls") || file.endsWith(".XLS")) {
		                	listOfFiles[i].delete();
		                }
		        }
		    }
		}catch (Exception e){
		    System.out.println();
		}
	}

	/**
	 * Separate class for DBMetaData
	 * 
	 * @author pradyroy
	 *
	 */
	class DBMetaData {
		public String columnName;
		public String columnType;

		public String getColumnName() {
			return columnName;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		public String getColumnType() {
			return columnType;
		}

		public void setColumnType(String columnType) {
			this.columnType = columnType;
		}
	}

}
