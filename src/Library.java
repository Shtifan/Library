import java.io.*;

public class Library {
    private final MyArrayList<Book> books;
    private static final String DEFAULT_FILE_NAME = "library_data.txt";

    public Library() {
        this.books = new MyArrayList<>();
        try {
            loadFromFile(DEFAULT_FILE_NAME);
        } catch (IOException e) {
            System.err.println("Could not load library data: " + e.getMessage());
        }
    }

    public void addBook(Book book) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn().equals(book.getIsbn())) {
                throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists.");
            }
        }
        books.add(book);
        autoSave();
    }

    private void autoSave() {
        try {
            saveToFile(DEFAULT_FILE_NAME);
        } catch (IOException e) {
            System.err.println("Error auto-saving library: " + e.getMessage());
        }
    }

    public boolean removeBook(String isbn) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn().equals(isbn)) {
                books.remove(i);
                autoSave();
                return true;
            }
        }
        return false;
    }

    public Book findBookByIsbn(String isbn) {
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        return null;
    }

    public MyArrayList<Book> findBooksByTitle(String titleQuery) {
        MyArrayList<Book> results = new MyArrayList<>();
        String queryLower = titleQuery.toLowerCase();
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            if (book.getTitle().toLowerCase().contains(queryLower)) {
                results.add(book);
            }
        }
        return results;
    }

    public MyArrayList<Book> findBooksByAuthor(String authorQuery) {
        MyArrayList<Book> results = new MyArrayList<>();
        String queryLower = authorQuery.toLowerCase();
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            String authorFullName = book.getAuthor().getFirstName().toLowerCase() + " "
                    + book.getAuthor().getLastName().toLowerCase();
            if (authorFullName.contains(queryLower) ||
                    book.getAuthor().getFirstName().toLowerCase().contains(queryLower) ||
                    book.getAuthor().getLastName().toLowerCase().contains(queryLower)) {
                results.add(book);
            }
        }
        return results;
    }

    public boolean updateBook(String oldIsbn, Book updatedBookDetails) {
        Book bookToUpdate = findBookByIsbn(oldIsbn);
        if (bookToUpdate != null) {
            if (!oldIsbn.equals(updatedBookDetails.getIsbn()) && findBookByIsbn(updatedBookDetails.getIsbn()) != null) {
                throw new IllegalArgumentException(
                        "Cannot update: New ISBN " + updatedBookDetails.getIsbn() + " already exists.");
            }
            bookToUpdate.setIsbn(updatedBookDetails.getIsbn());
            bookToUpdate.setTitle(updatedBookDetails.getTitle());
            bookToUpdate.setAuthor(updatedBookDetails.getAuthor());
            bookToUpdate.setPublicationYear(updatedBookDetails.getPublicationYear());
            autoSave();
            return true;
        }
        return false;
    }

    public MyArrayList<Book> getAllBooks() {
        return books;
    }

    public void sortBooksByTitle() {
        MergeSort.sort(books, new MergeSort.BookTitleComparator());
    }

    public void sortBooksByAuthor() {
        MergeSort.sort(books, new MergeSort.BookAuthorComparator());
    }

    public void saveToFile(String filename) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            filename = DEFAULT_FILE_NAME;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < books.size(); i++) {
                writer.write(books.get(i).toFileString());
                writer.newLine();
            }
        }
    }

    public void loadFromFile(String filename) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            filename = DEFAULT_FILE_NAME;
        }
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Data file " + filename + " not found. Starting with an empty library.");
            return;
        }

        books.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                Book book = Book.fromFileString(line);
                if (book != null) {
                    try {
                        addBook(book);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping duplicate or invalid book during load: " + e.getMessage()
                                + " for line: " + line);
                    }
                } else {
                    System.err.println("Could not parse book from line: " + line);
                }
            }
        }
    }

    public MyArrayList<Book> findBooksByYear(int year) {
        MyArrayList<Book> results = new MyArrayList<>();
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            if (book.getPublicationYear() == year) {
                results.add(book);
            }
        }
        return results;
    }
}