import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtility {
	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:tcp://localhost/~/test";

	//  database credentials (should be stored in a secure file somewhere else)
	private static final String USER = "user";
	private static final String PASS = "th!sisithep@sswor0d";

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		// register JDBC driver
		Class.forName(JDBC_DRIVER);

		// System.out.println("Connecting to the database...");

		Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);

		// System.out.println("Successfully connected to the database");

		return connection;
	}

	/**
	 * encapsulate try with resources boilerplate code
	 * @param connection
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws SQLException
	 */
	public static boolean executeStatement(Connection connection, String sql, String... parameters) throws SQLException {
		try(Statement stmt = connection.createStatement()) {
			return stmt.execute(sql);
		}
	}

	/**
	 * encapsulate try DML update code
	 * @param connection
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws SQLException
	 */
	public static int executePreparedStatement(Connection connection, String sql, String... parameters) throws SQLException {
		try(PreparedStatement stmt = connection.prepareStatement(sql)) {

			int i = 1;

			for(String currentParameter : parameters) {
				stmt.setString(i++, currentParameter);
			}

			return stmt.executeUpdate();
		}
	}

	/**
	 * encapsulate try-catch boilerplate code
	 * @param connection
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static String selectString(Connection connection, String sql) throws SQLException {
		try(PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet resultSet = stmt.executeQuery();)
		{
			if(resultSet.next()) {
				String string = resultSet.getString(1);

				return string == null ? "" : string;
			}
		}

		return null;
	}

	/**
	 * encapsulate try-catch boilerplate code
	 * @param connection
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static int selectInt(Connection connection, String sql) throws SQLException {
		try(PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet resultSet = stmt.executeQuery();)
		{
			resultSet.next();

			return resultSet.getInt(1);
		}
	}
}
