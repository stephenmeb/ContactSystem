

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Phone {
	public static final String HOME = "home";
	public static final String MOBILE = "mobile";

	private String number;
	private String type;

	Phone(String number) {
		this.number = number;
	}

	Phone(String number, String type) {
		this.number = number;
		this.type = type;
	}

	boolean isHome() {
		return Phone.HOME.equals(type);
	}

	boolean isMobile() {
		return Phone.MOBILE.equals(type);
	}

	void addToDatabase(Connection connection, String contactId) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		DatabaseUtility.executePreparedStatement(
				connection,
				"INSERT INTO phone " +
				"(contact_id, number, type) " +
				"VALUES (?, ?, ?)",
				contactId,
				number,
				type);
	}

	static List<Phone> getPhoneData(Connection connection, String contactId) throws SQLException {
		List<Phone> phones = new ArrayList<>();

		try(PreparedStatement stmt = connection.prepareStatement(
				"SELECT number, type " +
				"FROM phone " +
				"WHERE contact_id = ? ");)
		{
			stmt.setString(1, contactId);

			try(ResultSet resultSet = stmt.executeQuery()) {
				while(resultSet.next()) {
					phones.add(new Phone(
							resultSet.getString("number"),
							resultSet.getString("type")));
				}
			}
		}

		return phones;
	}

	int updateInDatabase(Connection connection, String contactId) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		return DatabaseUtility.executePreparedStatement(
				connection,
				"UPDATE phone SET " +
				"    number = ? " +
				"WHERE contact_id = ? " +
				"AND type = ? ",
				number,
				contactId,
				type);
	}

	static void deleteHomeFromDatabase(Connection connection, String contactId) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		DatabaseUtility.executePreparedStatement(
				connection,
				"DELETE phone " +
				"WHERE contact_id = ? " +
				"AND type = 'home'",
				contactId);
	}

	static void deleteMobileFromDatabase(Connection connection, String contactId) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		DatabaseUtility.executePreparedStatement(
				connection,
				"DELETE phone " +
				"WHERE contact_id = ? " +
				"AND type = 'mobile'",
				contactId);
	}
}
