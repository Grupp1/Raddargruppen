package tddd36.server;


	import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

	//Hämta version av databasen och skriver ut den i terminalen
	//Ska returnera "5.1.49"
	
	public class TestDatabase {

	    public static void main(String[] args) {

	        Connection con = null;
	        Statement st = null;
	        ResultSet rs = null;

	        String url = "jdbc:mysql://db-und.ida.liu.se:3306/tddd36_proj1";
	        String user = "tddd36_proj1";
	        String password = "tddd36_proj1_17a8";

	        try {
	            con = DriverManager.getConnection(url, user, password);
	            st = con.createStatement();
	        	rs = st.executeQuery("SELECT * version();");

	            if (rs.next()) {
	                System.out.println(rs.getString(3));
	            }

	        } catch (SQLException ex) {
	            Logger lgr = Logger.getLogger(TestDatabase.class.getName());
	            lgr.log(Level.SEVERE, ex.getMessage(), ex);

	        } finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	                if (st != null) {
	                    st.close();
	                }
	                if (con != null) {
	                    con.close();
	                }

	            } catch (SQLException ex) {
	                Logger lgr = Logger.getLogger(TestDatabase.class.getName());
	                lgr.log(Level.WARNING, ex.getMessage(), ex);
	            }
	        }
	    }
	}
	
