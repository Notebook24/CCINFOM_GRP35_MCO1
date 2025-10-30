import java.sql.Connection; //Used to hold the active connection between the Java app and the database
import java.sql.DriverManager; //Used to create the conenction between the Java appp and the database
import java.sql.SQLException; //Allows SQL Exceptions

public class DBConnection{
    /**
     * Link to the database, tells the Java file to use JDBC.
     */
    private static final String URL = "jdbc:mysql://localhost:3306/GRP35_db";
    /**
     * Name of the user of the SQL account.
     */ 
    private static final String USER = "root";
    /**
     * Password of the SQL account.
     */
    private static final String PASSWORD = "Choichoi22";
    /**
     * This variable will hold the database connection.
     */
    private static Connection conn = null;

    /**
     * Creates a connection between the Java applictaion and the database.
     * 
     * @return conn: A variable which holds the connection between the Java application and the database.
     */
    public static Connection getConnection(){
        if (conn == null){
            try{
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
        return conn;
    }
}