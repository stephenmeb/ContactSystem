import java.sql.Connection;
import java.sql.SQLException;

import com.google.gson.Gson;

public class ContactSystem {

	/**
	 * @param args
	 * args[0] = HTTP Method (GET, POST, etc.),
	 * args[1] = relative input URL,
	 * args[2] = JSON string
	 */
	public static void main(String[] args) {
		Connection connection = null;

		try {
			connection = DatabaseUtility.getConnection();

			if(!areDatabaseElementsCreated(connection)) {
				createDatabaseElements(connection);
			}

			handleInput(args[0], args[1], args[2], connection);
		} catch(Exception exception) {
			System.out.println("ERR: " + exception.getMessage());

			// for production purposes a third party logging library would be used
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch(SQLException sqlException) {
					System.out.println("ERR: " + sqlException.getMessage());

					// for production purposes a third party logging library would be used
				}
			}
		}
	}

	/**
	 * are tables and sequences created
	 * @param connection
	 * @return
	 */
	private static boolean areDatabaseElementsCreated(Connection connection) {
		try {
			DatabaseUtility.selectString(connection, "SELECT 1 from contact");
		} catch (SQLException sqlException) {
			return false;
		}

		return true;
	}

	/**
	 * create tables and sequences
	 * @param connection
	 * @throws SQLException
	 */
	private static void createDatabaseElements(Connection connection) throws SQLException {
		try {
			System.out.println("creating database elements...");

			DatabaseUtility.executeStatement(
					connection,
					"CREATE TABLE contact( " +
					"    contact_id      INTEGER NOT NULL, " +
					"    email_address   VARCHAR(255) NOT NULL," +
					"    PRIMARY KEY (contact_id))");

			DatabaseUtility.executeStatement(connection, "CREATE SEQUENCE contact_id;");

			DatabaseUtility.executeStatement(
					connection,
					"CREATE TABLE name( " +
					"    contact_id INTEGER NOT NULL, " +
					"    first      VARCHAR(255), " +
					"    middle     VARCHAR(255), " +
					"    last       VARCHAR(255), " +
					"    PRIMARY KEY (contact_id))");

			DatabaseUtility.executeStatement(
					connection,
					"CREATE TABLE address( " +
				    "    contact_id INTEGER NOT NULL, " +
					"    street     VARCHAR(255), " +
					"    city       VARCHAR(255), " +
					"    state      VARCHAR(255), " +
					"    zip        VARCHAR(255), " +
					"    PRIMARY KEY (contact_id))");

			DatabaseUtility.executeStatement(
					connection,
					"CREATE TABLE phone( " +
					"contact_id INTEGER NOT NULL, " +
					"number     VARCHAR(255), " +
					"type       VARCHAR(255), " +
					"PRIMARY KEY (contact_id, type))");

			System.out.println("database elements created");
		} catch (SQLException sqlException2) {
			System.out.println("ERR: " + sqlException2.getMessage());

			// for production purposes a third party logging library would be used
		}
	}

	/**
	 * handle GET, POST, PUT and DELETE HTTP methods for a restful interface
	 * @param jsonString
	 * @param connection
	 * @throws Exception
	 */
	private static void handleInput(String httpMethod, String relativeInputURL, String jsonString, Connection connection) throws Exception {
		// commit all transactions together to avoid data corruption
		connection.setAutoCommit(false);


		// add a new contact
		if("POST".equals(httpMethod)) {
			Gson gson = new Gson();

			// use the Google Gson library to convert from a JSON string to a Java object
			Contact contact = gson.fromJson(jsonString, Contact.class);

			contact.addToDatabase(connection);

			System.out.println("record added");

		// get contact data
		} else if("GET".equals(httpMethod)) {
			// get keywords and ids from the URL
			String[] urlSplit = relativeInputURL.split("/");

			// use the Google Gson library to convert from a JSON string to a Java object
			Gson gson = new Gson();

			// print all contact data
			if(urlSplit.length < 3) {
				System.out.println(gson.toJson(Contact.getAllContacts(connection)));

			// print contact data as specified in step #4
			} else if("call-list".equals(urlSplit[2])) {
				System.out.println(gson.toJson(CallListContact.getContactsForCallList(connection)));

			// get contact data by id
			} else {
				System.out.println(gson.toJson(Contact.getContact(connection, urlSplit[2])));
			}

		// update contact data by id
		} else if("PUT".equals(httpMethod)) {
			// use the Google Gson library to convert from a JSON string to a Java object
			Gson gson = new Gson();

			Contact contact = gson.fromJson(jsonString, Contact.class);
			contact.setId(Integer.parseInt(relativeInputURL.split("/")[2]));

			contact.updateInDatabase(connection);

			System.out.println("record updated");

		// delete contact data by id
		} else if("DELETE".equals(httpMethod)) {
			Contact.deleteFromDatabase(connection, relativeInputURL.split("/")[2]);

			System.out.println("record deleted");
		}

		connection.commit();
	}

	/**
	 * drop tables and sequences
	 * @throws SQLException
	 */
	static void dropDatabaseElements() throws SQLException {
		Connection connection = null;

		try {
			connection = DatabaseUtility.getConnection();

			System.out.println("dropping elements...");

			DatabaseUtility.executeStatement(connection, "DROP TABLE contact;");
			DatabaseUtility.executeStatement(connection, "DROP SEQUENCE contact_id");
			DatabaseUtility.executeStatement(connection, "DROP TABLE name;");
			DatabaseUtility.executeStatement(connection, "DROP TABLE address;");
			DatabaseUtility.executeStatement(connection, "DROP TABLE phone;");

			System.out.println("elements dropped");

		} catch(Exception exception) {
			System.out.println("ERR: " + exception.getMessage());

			// for production purposes a third party logging library would be used
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch(SQLException sqlException) {
					System.out.println("ERR: " + sqlException.getMessage());

					// for production purposes a third party logging library would be used
				}
			}
		}
	}
}
