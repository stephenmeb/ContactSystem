import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Contact {
	private int id;
	private Name name;
	private Address address;
	private List<Phone> phone;
	private String email;

	Contact() {
	}

	Contact(int contactId, Name name, Address address, List<Phone> phone, String email) {
		this.id = contactId;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.email = email;
	}

	void setId(int id) {
		this.id = id;
	}

	void addToDatabase(Connection connection) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		id = DatabaseUtility.selectInt(connection, "SELECT contact_id.nextval");

		if(name != null) {
			name.addToDatabase(connection, getIdAsString());
		}

		if(address != null) {
			address.addToDatabase(connection, getIdAsString());
		}

		if(phone != null) {
			for(Phone currentPhone: phone) {
				currentPhone.addToDatabase(connection, getIdAsString());
			}
		}

		DatabaseUtility.executePreparedStatement(
				connection,
				"INSERT INTO contact(contact_id, email_address) VALUES (?, ?)",
				getIdAsString(),
				email);
	}

	void updateInDatabase(Connection connection) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		if(name == null) {
			Name.deleteFromDatabase(connection, getIdAsString());
		} else {
			if(name.updateInDatabase(connection, getIdAsString()) == 0) {
				name.addToDatabase(connection, getIdAsString());
			}
		}

		if(address == null) {
			Address.deleteFromDatabase(connection, getIdAsString());
		} else {
			if(address.updateInDatabase(connection, getIdAsString()) == 0) {
				address.addToDatabase(connection, getIdAsString());
			}
		}

		List<String> savedPhoneTypes = new ArrayList<>();

		for(Phone currentPhone: phone) {
			savedPhoneTypes.add(currentPhone.getType());

			if(currentPhone.updateInDatabase(connection, getIdAsString()) == 0) {
				currentPhone.addToDatabase(connection, getIdAsString());
			}
		}

		for(PhoneType currentPhoneType: PhoneType.values()) {
			boolean found = false;
			for(String currentSavedPhoneType: savedPhoneTypes) {
				if(currentPhoneType.matches(currentSavedPhoneType)) {
					found = true;

					break;
				}
			}

			if(!found) {
				Phone.deleteFromDatabase(connection, getIdAsString(), currentPhoneType.getValue());
			}
		}

		DatabaseUtility.executePreparedStatement(
				connection,
				"UPDATE contact SET " +
				"    email_address = ? " +
				"WHERE contact_id = ? ",
				email,
				getIdAsString());
	}

	static void deleteFromDatabase(Connection connection, String id) throws SQLException
	{
		//------------------------
		// In a production system, sanitization, validation and security checking code would go here
		//------------------------

		Name.deleteFromDatabase(connection, id);
		Address.deleteFromDatabase(connection, id);
		Phone.deleteFromDatabase(connection, id);
		DatabaseUtility.executePreparedStatement(
				connection,
				"DELETE FROM contact " +
				"WHERE contact_id = ? ",
				id);
	}

	static List<Contact> getAllContacts(Connection connection) throws SQLException {
		try(PreparedStatement stmt = connection.prepareStatement(
				"SELECT pc.contact_id, email_address, first, middle, last, street, city, state, zip " +
				"FROM contact      pc " +
				"LEFT JOIN name    pn ON pn.contact_id = pc.contact_id " +
				"LEFT JOIN address a  ON a.contact_id = pc.contact_id ");
				ResultSet resultSet = stmt.executeQuery();)
		{
			return getContactsFromResultSet(connection, resultSet);
		}
	}

	static Contact getContact(Connection connection, String contactId) throws Exception {
		try(PreparedStatement stmt = connection.prepareStatement(
				"SELECT pc.contact_id, email_address, first, middle, last, street, city, state, zip " +
				"FROM contact      pc " +
				"LEFT JOIN name     pn ON pn.contact_id = pc.contact_id " +
				"LEFT JOIN address a  ON a.contact_id = pc.contact_id " +
				"LEFT JOIN phone   p  ON p.contact_id = pc.contact_id " +
				"WHERE p.contact_id = ? ");)
		{
			stmt.setString(1, contactId);

			try(ResultSet resultSet = stmt.executeQuery();) {
				List<Contact> personalContact = getContactsFromResultSet(connection, resultSet);

				if(personalContact.isEmpty()) {
					throw new Exception("contact not found");
				}

				return personalContact.get(0);
			}
		}
	}

	static List<Contact> getContactsFromResultSet(Connection connection, ResultSet resultSet) throws SQLException {
		List<Contact> personalContacts = new ArrayList<>();

		while(resultSet.next()) {
			Name name = new Name(
					resultSet.getString("first"),
					resultSet.getString("middle"),
					resultSet.getString("last"));

			Address address = new Address(
					resultSet.getString("street"),
					resultSet.getString("city"),
					resultSet.getString("state"),
					resultSet.getString("zip"));

			String contactId = resultSet.getString("contact_id");

			List<Phone> phones = Phone.getPhoneData(connection, contactId);

			personalContacts.add(new Contact(
					Integer.parseInt(contactId),
					name,
					address,
					phones,
					resultSet.getString("email_address")));
		}

		return personalContacts;
	}

	public int getId() {
		return id;
	}

	public String getIdAsString() {
		return String.valueOf(id);
	}

	public String getLastName() {
		return name.getLastName();
	}
}
