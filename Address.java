

import java.sql.Connection;
import java.sql.SQLException;

public class Address {
	private String street;
	private String city;
	private String state;
	private String zip;

	Address() {
	}

	Address(String street, String city, String state, String zip) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}

	void addToDatabase(Connection connection, String contactId) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		DatabaseUtility.executePreparedStatement(
				connection,
				"INSERT INTO address " +
				"(contact_id, street, city, state, zip) " +
				"VALUES (?, ?, ?, ?, ?)",
				contactId,
				street,
				city,
				state,
				zip);
	}

	int updateInDatabase(Connection connection, String contactId) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		return DatabaseUtility.executePreparedStatement(
				connection,
				"UPDATE address SET " +
				"    street = ?, " +
				"    city = ?, " +
				"    state = ?, " +
				"    zip = ? " +
				"WHERE contact_id = ? ",
				street,
				city,
				state,
				zip,
				contactId);
	}

	static void deleteFromDatabase(Connection connection, String contactId) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		DatabaseUtility.executePreparedStatement(
				connection,
				"DELETE address " +
				"WHERE contact_id = ? ",
				contactId);
	}
}
