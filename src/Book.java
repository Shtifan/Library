import java.io.Serializable;

public class Book implements Serializable {
    private String isbn;
    private String title;
    private Author author;
    private int publicationYear;

    public Book(String isbn, String title, Author author, int publicationYear) {
        if (isbn == null || !isValidIsbn(isbn.trim())) {
            throw new IllegalArgumentException("ISBN is invalid or empty. Must be 10 or 13 digits.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null.");
        }
        if (publicationYear <= 0 || publicationYear > java.time.Year.now().getValue() + 1) {
            throw new IllegalArgumentException("Publication year is invalid.");
        }
        this.isbn = isbn.trim();
        this.title = title.trim();
        this.author = author;
        this.publicationYear = publicationYear;
    }

    public static boolean isValidIsbn(String isbn) {
        if (isbn == null)
            return false;
        String cleanedIsbn = isbn.replaceAll("-", "").trim();
        return cleanedIsbn.matches("\\d+");
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || !isValidIsbn(isbn.trim())) {
            throw new IllegalArgumentException("ISBN is invalid or empty. Must be 10 or 13 digits.");
        }
        this.isbn = isbn.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        this.title = title.trim();
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null.");
        }
        this.author = author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        if (publicationYear <= 0 || publicationYear > java.time.Year.now().getValue() + 1) {
            throw new IllegalArgumentException("Publication year is invalid.");
        }
        this.publicationYear = publicationYear;
    }

    @Override
    public String toString() {
        return "Book [ISBN=" + isbn + ", Title=" + title + ", Author=" + author + ", Year=" + publicationYear + "]";
    }

    public String toFileString() {
        return isbn + "|" + title + "|" + author.toFileString() + "|" + publicationYear;
    }

    public static Book fromFileString(String fileString) {
        if (fileString == null || fileString.trim().isEmpty()) {
            return null;
        }
        String[] parts = fileString.split("\\|", -1);
        if (parts.length == 4) {
            try {
                Author author = Author.fromFileString(parts[2]);
                if (author == null)
                    throw new IllegalArgumentException("Could not parse author from file string.");
                int year = Integer.parseInt(parts[3]);
                return new Book(parts[0], parts[1], author, year);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing year from file: " + parts[3]);
                return null;
            } catch (IllegalArgumentException e) {
                System.err.println("Error creating book from file string: " + e.getMessage());
                return null;
            }
        }
        System.err.println(
                "Malformed book string for parsing: " + fileString + " (Expected 4 parts, got " + parts.length + ")");
        return null;
    }
}