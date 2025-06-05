import java.io.Serializable;

public class Author implements Serializable {
    private final String firstName;
    private final String lastName;

    public Author(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Author first and last names cannot be empty.");
        }
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    public String toFileString() {
        return firstName + ";" + lastName;
    }

    public static Author fromFileString(String fileString) {
        if (fileString == null || fileString.trim().isEmpty()) {
            return null;
        }
        String[] parts = fileString.split(";", -1);
        if (parts.length == 2) {
            return new Author(parts[0], parts[1]);
        }
        System.err.println("Malformed author string for parsing: " + fileString);
        return new Author("Unknown", "Author");
    }
}