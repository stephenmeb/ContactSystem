

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CallListContact {
	private Name name;
	private Phone phone;

	CallListContact(Name name, Phone phone) {
		this.name = name;
		this.phone = phone;
	}

	static List<CallListContact> getContactsForCallList(Connection connection) throws SQLException {
		try(PreparedStatement stmt = connection.prepareStatement(
				"SELECT pc.contact_id, email_address, first, middle, last, street, city, state, zip, p.number " +
				"FROM contact      pc " +
				"LEFT JOIN name    pn ON pn.contact_id = pc.contact_id " +
				"LEFT JOIN address a  ON a.contact_id = pc.contact_id " +
				"JOIN phone        p  ON p.contact_id = pc.contact_id " +
				"WHERE p.type = '" + Phone.HOME + "' " +
				"ORDER BY last, first");
				ResultSet resultSet = stmt.executeQuery();)
		{
			List<CallListContact> personalContacts = new ArrayList<>();

			while(resultSet.next()) {
				Name name = new Name(
						resultSet.getString("first"),
						resultSet.getString("middle"),
						resultSet.getString("last"));

				Phone phone = new Phone(resultSet.getString("number"));

				personalContacts.add(new CallListContact(
						name,
						phone));
			}

			return personalContacts;
		}
	}

	public String getLastName() {
		return name.getLastName();
	}
}
