

import java.sql.Connection;
import java.sql.SQLException;

public class Name {
	private String first;
	private String middle;
	private String last;

	Name() {
	}

	Name(String firstName, String middleName, String lastName) {
		this.first = firstName;
		this.middle = middleName;
		this.last = lastName;
	}

	void addToDatabase(Connection connection, String contactId) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		DatabaseUtility.executePreparedStatement(
				connection,
				"INSERT INTO name " +
				"(contact_id, first, middle, last) " +
				"VALUES (?, ?, ?, ?)",
				contactId,
				first,
				middle,
				last);
	}

	int updateInDatabase(Connection connection, String contactId) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		return DatabaseUtility.executePreparedStatement(
				connection,
				"UPDATE name SET " +
				"    first = ?, " +
				"    middle = ?, " +
				"    last = ? " +
				"WHERE contact_id = ? ",
				first,
				middle,
				last,
				contactId);
	}

	public static void deleteFromDatabase(Connection connection, String contactId) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		DatabaseUtility.executePreparedStatement(
				connection,
				"DELETE name " +
				"WHERE contact_id = ? ",
				contactId);
	}

	public String getLastName() {
		return last;
	}
}
