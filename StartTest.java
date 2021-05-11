import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StartTest {

	/**
	 * recreate the database and insert two contacts
	 * @throws Exception
	 */
	@BeforeEach
	void setup() throws Exception {
		ContactSystem.dropDatabaseElements();

		String[] args = new String[3];

		// restful HTTP method ex: GET, POST, ETC.
		args[0] = "POST";

		// relative path URL
		args[1] = "/contact";

		// JSON string
		args[2] =
			"{" +
			"	\"name\":{" +
			"	\"first\": \"Harold\"," +
			"	\"middle\": \"Francis\"," +
			"	\"last\": \"Gilkey\"" +
			"	}," +
			"	\"address\":{" +
			"		\"street\": \"8360 High Autumn Row\"," +
			"		\"city\": \"Cannon\"," +
			"		\"state\": \"Delaware\"," +
			"		\"zip\": \"19797\"" +
			"	}," +
			"	\"phone\":[" +
			"         {" +
			"        	 \"number\":\"302-611-9148\"," +
			"        	 \"type\":\"home\"" +
			"         }," +
			"         {" +
			"        	 \"number\":\"302-532-9427\"," +
			"        	 \"type\":\"mobile\"" +
			"         }" +
			"     ]," +
			"	\"email\":\"harold.gilkey@yahoo.com\"" +
			"}";

		ContactSystem.main(args);

		// restful HTTP method ex: GET, POST, ETC.
		args[0] = "POST";

		// relative path URL
		args[1] = "/contact";

		// JSON string
		args[2] =
			"{" +
			"	\"name\":{" +
			"	\"first\": \"Stephen\"," +
			"	\"middle\": \"M\"," +
			"	\"last\": \"Baytop\"" +
			"	}," +
			"	\"address\":{" +
			"		\"street\": \"111 High Autumn Row\"," +
			"		\"city\": \"Cannon\"," +
			"		\"state\": \"Delaware\"," +
			"		\"zip\": \"19797\"" +
			"	}," +
			"	\"phone\":[" +
			"         {" +
			"        	 \"number\":\"111-532-9427\"," +
			"        	 \"type\":\"mobile\"" +
			"         }" +
		    "     ]," +
			"	\"email\":\"stephen.baytop@yahoo.com\"" +
			"}";

		ContactSystem.main(args);
	}

	/**
	 * test that all records are returned
	 * @throws Exception
	 */
	@Test
	void testGetAllContacts() throws Exception {
		List<Contact> contacts = Contact.getAllContacts(DatabaseUtility.getConnection());

		assert(contacts.size() == 2);
	}

	/**
	 * test that the correct record is returned
	 * @throws Exception
	 */
	@Test
	void testGetSpecificContact() throws Exception {
		Contact contact = Contact.getContact(DatabaseUtility.getConnection(), "2");

		assert(2 == contact.getId());
	}

	/**
	 * test that only the record with a home phone is returned
	 * @throws Exception
	 */
	@Test
	void testGetCallList() throws Exception {
		List<CallListContact> callList = CallListContact.getContactsForCallList(DatabaseUtility.getConnection());

		assert(callList.size() == 1);
		assert("Gilkey".equals(callList.get(0).getLastName()));
	}

	/**
	 * test that last name is changed
	 * @throws Exception
	 */
	@Test
	void testPut() throws Exception {
		String[] args = new String[3];

		args[0] = "PUT";
		args[1] = "/contact/1";
		args[2] =
			"{" +
			"	\"name\":{" +
			"	\"first\": \"Harold\"," +
			"	\"middle\": \"Francis\"," +
			"	\"last\": \"Davis\"" +
			"	}," +
			"	\"address\":{" +
			"		\"street\": \"8360 High Autumn Row\"," +
			"		\"city\": \"Cannon\"," +
			"		\"state\": \"Delaware\"," +
			"		\"zip\": \"19797\"" +
			"	}," +
			"	\"phone\":[" +
			"         {" +
			"        	 \"number\":\"302-611-9148\"," +
			"        	 \"type\":\"home\"" +
			"         }," +
			"         {" +
			"        	 \"number\":\"302-532-9427\"," +
			"        	 \"type\":\"mobile\"" +
			"         }" +
			"     ]," +
			"	\"email\":\"harold.gilkey@yahoo.com\"" +
			"}";

		ContactSystem.main(args);

		Contact contact = Contact.getContact(DatabaseUtility.getConnection(), "1");

		assert("Davis".equals(contact.getLastName()));
	}

	/**
	 * test that the specified record is deleted
	 * @throws Exception
	 */
	@Test
	void testDelete() throws Exception {
		String[] args = new String[3];

		args[0] = "DELETE";
		args[1] = "/contact/1";
		args[2] = "";

		ContactSystem.main(args);

		List<Contact> contacts = Contact.getAllContacts(DatabaseUtility.getConnection());

		assert(contacts.size() == 1);
	}
}
