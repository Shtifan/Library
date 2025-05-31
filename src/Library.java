import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

class Library {
    private final ArrayList<Book> books;
    private static final String FILE_NAME = "library_data.txt";
    private static final String DELIMITER = "||";

    public Library() {
        this.books = new ArrayList<>();
    }

    public void addBook(Book book) throws IllegalArgumentException {
        for (Book b : books) {
            if (b.getIsbn().equals(book.getIsbn())) {
                throw new IllegalArgumentException("Книга с ISBN '" + book.getIsbn() + "' вече съществува!");
            }
        }
        books.add(book);
    }

    public boolean updateBook(String originalIsbn, Book updatedBook) throws IllegalArgumentException {
        Book bookToUpdate = null;
        int bookIndex = -1;

        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn().equals(originalIsbn)) {
                bookToUpdate = books.get(i);
                bookIndex = i;
                break;
            }
        }

        if (bookToUpdate == null) {
            return false;
        }

        if (!originalIsbn.equals(updatedBook.getIsbn())) {
            for (Book b : books) {
                if (b.getIsbn().equals(updatedBook.getIsbn())) {
                    throw new IllegalArgumentException("Новият ISBN '" + updatedBook.getIsbn() + "' вече се използва от друга книга.");
                }
            }
        }

        books.set(bookIndex, updatedBook);
        return true;
    }

    public boolean removeBook(String isbn) {
        return books.removeIf(book -> book.getIsbn().equals(isbn));
    }

    public Book findBookByIsbn(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        return null;
    }

    public ArrayList<Book> searchBooks(String searchTerm, String criteria) {
        ArrayList<Book> foundBooks = new ArrayList<>();
        String term = searchTerm.toLowerCase();
        for (Book book : books) {
            boolean match = false;
            switch (criteria.toLowerCase()) {
                case "isbn":
                    if (book.getIsbn().toLowerCase().contains(term)) match = true;
                    break;
                case "title":
                    if (book.getTitle().toLowerCase().contains(term)) match = true;
                    break;
                case "author":
                    if (book.getAuthor().toString().toLowerCase().contains(term)) match = true;
                    break;
            }
            if (match) {
                foundBooks.add(book);
            }
        }
        return foundBooks;
    }

    public ArrayList<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public void sortBooks(String criteria) {
        Comparator<Book> comparator = null;
        if ("title".equalsIgnoreCase(criteria)) {
            comparator = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
        } else if ("author".equalsIgnoreCase(criteria)) {
            comparator = Comparator.comparing(b -> b.getAuthor().toString(), String.CASE_INSENSITIVE_ORDER);
        }

        if (comparator != null) {
            quickSort(this.books, 0, this.books.size() - 1, comparator);
        }
    }

    private void quickSort(List<Book> list, int low, int high, Comparator<Book> comparator) {
        if (low < high) {
            int pi = partition(list, low, high, comparator);
            quickSort(list, low, pi - 1, comparator);
            quickSort(list, pi + 1, high, comparator);
        }
    }

    private int partition(List<Book> list, int low, int high, Comparator<Book> comparator) {
        Book pivot = list.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    public void saveBooksToFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Book book : books) {
                writer.println(book.getIsbn() + DELIMITER + book.getTitle() + DELIMITER + book.getAuthor().getFirstName() + DELIMITER + book.getAuthor().getLastName() + DELIMITER + book.getYear());
            }
        }
    }

    public void loadBooksFromFile() throws IOException {
        books.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(Pattern.quote(DELIMITER));
                if (parts.length == 5) {
                    try {
                        String isbn = parts[0];
                        String title = parts[1];
                        String authorFirstName = parts[2];
                        String authorLastName = parts[3];
                        int year = Integer.parseInt(parts[4]);
                        books.add(new Book(isbn, title, new Author(authorFirstName, authorLastName), year));
                    } catch (NumberFormatException e) {
                        System.err.println("Грешка при парсване на годината за ред: " + line + " -> " + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("Грешен формат на ред: " + line + " -> " + e.getMessage());
                    }
                } else {
                    System.err.println("Некоректен ред във файла: " + line + " (очакван брой части: 5, получен: " + parts.length + ")");
                }
            }
        }
    }
}
