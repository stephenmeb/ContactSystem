public enum PhoneType {
	HOME("home"), MOBILE("mobile"), WORK("work");

	private String value;

	PhoneType(String value) {
		this.value = value;
	}

	String getValue() {
		return value;
	}

	boolean matches(String value) {
		return this.value.equals(value);
	}
}
