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
     * Creates a connection between the Java applictaion and the database.
     * 
     * @return conn: A variable which holds the connection between the Java application and the database.
     */
    public static Connection getConnection() throws SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            System.out.println("MySQL Driver not found!");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}