import java.io.Serializable;
import java.util.Objects;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private String isbn;
    private String title;
    private Author author;
    private int yearPublished;

    public Book(String isbn, String title, Author author, int yearPublished) {
        setIsbn(isbn);
        setTitle(title);
        setAuthor(author);
        setYearPublished(yearPublished);
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN не може да е празен.");
        }
        if (!isbn.trim().matches("[0-9X-]+")) {
            System.err.println("Предупреждение: Въведеният ISBN '" + isbn + "' може да не е в стандартен формат.");
        }
        this.isbn = isbn.trim();
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Заглавието не може да е празно.");
        }
        this.title = title.trim();
    }

    public void setAuthor(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Авторът не може да е null.");
        }
        this.author = author;
    }

    public void setYearPublished(int yearPublished) {
        if (yearPublished <= 0 || yearPublished > java.time.Year.now().getValue() + 1) {
            throw new IllegalArgumentException("Невалидна година на издаване: " + yearPublished);
        }
        this.yearPublished = yearPublished;
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author=" + (author != null ? author.toString() : "N/A") +
                ", yearPublished=" + yearPublished +
                '}';
    }

    public String toFileString() {
        return isbn + ";" + title + ";" + author.toFileString() + ";" + yearPublished;
    }

    public static Book fromFileString(String fileString) {
        if (fileString == null || fileString.isEmpty())
            return null;
        String[] parts = fileString.split(";", 5);
        if (parts.length == 5) {
            try {
                Author author = new Author(parts[2], parts[3]);
                int year = Integer.parseInt(parts[4]);
                return new Book(parts[0], parts[1], author, year);
            } catch (NumberFormatException e) {
                System.err.println("Грешка при парсиране на година от файл: " + parts[4]);
                return null;
            } catch (IllegalArgumentException e) {
                System.err.println("Грешка при създаване на книга от файл: " + fileString + " - " + e.getMessage());
                return null;
            }
        }
        System.err.println("Невалиден формат за книга от файл: " + fileString + " (очаквани 5 части, получени "
                + parts.length + ")");
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}
