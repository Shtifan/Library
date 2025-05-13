import java.io.Serializable;

public class Author implements Serializable {
    private static final long serialVersionUID = 1L;
    private String firstName;
    private String lastName;

    public Author(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Името на автора не може да е празно.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Фамилията на автора не може да е празна.");
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

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Името на автора не може да е празно.");
        }
        this.firstName = firstName.trim();
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Фамилията на автора не може да е празна.");
        }
        this.lastName = lastName.trim();
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    public String toFileString() {
        return firstName + ";" + lastName;
    }

    public static Author fromFileString(String fileString) {
        if (fileString == null || fileString.isEmpty())
            return null;
        String[] parts = fileString.split(";", 2);
        if (parts.length == 2) {
            try {
                return new Author(parts[0], parts[1]);
            } catch (IllegalArgumentException e) {
                System.err.println("Грешка при парсиране на автор от файл: " + fileString + " - " + e.getMessage());
                return null;
            }
        }
        System.err.println("Невалиден формат за автор от файл: " + fileString);
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Author author = (Author) o;
        return java.util.Objects.equals(firstName, author.firstName) &&
                java.util.Objects.equals(lastName, author.lastName);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(firstName, lastName);
    }
}
