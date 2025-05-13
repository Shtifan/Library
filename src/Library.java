import java.io.*;
import java.util.Comparator;

public class Library {
    private MyDynamicArray<Book> books;
    String dataFile = "library_data.txt";

    public Library() {
        this.books = new MyDynamicArray<>();
    }

    public Library(String dataFileName) {
        this.books = new MyDynamicArray<>();
        this.dataFile = dataFileName;
    }

    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Книгата не може да бъде null.");
        }
        if (findBookByIsbn(book.getIsbn()) != null) {
            throw new IllegalArgumentException("Книга с ISBN '" + book.getIsbn() + "' вече съществува.");
        }
        books.add(book);
    }

    public boolean removeBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            System.err.println("Опит за изтриване на книга с празен ISBN.");
            return false;
        }
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn().equalsIgnoreCase(isbn.trim())) {
                books.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean updateBook(String isbn, Book updatedBookData) {
        if (isbn == null || isbn.trim().isEmpty() || updatedBookData == null) {
            throw new IllegalArgumentException(
                    "ISBN за търсене или новите данни на книгата не могат да бъдат празни/null.");
        }

        for (int i = 0; i < books.size(); i++) {
            Book currentBook = books.get(i);
            if (currentBook.getIsbn().equalsIgnoreCase(isbn.trim())) {
                if (!isbn.equalsIgnoreCase(updatedBookData.getIsbn())
                        && findBookByIsbn(updatedBookData.getIsbn()) != null) {
                    throw new IllegalArgumentException(
                            "Вече съществува друга книга с новия ISBN '" + updatedBookData.getIsbn() + "'.");
                }
                try {
                    currentBook.setIsbn(updatedBookData.getIsbn());
                    currentBook.setTitle(updatedBookData.getTitle());
                    currentBook.setAuthor(updatedBookData.getAuthor());
                    currentBook.setYearPublished(updatedBookData.getYearPublished());
                    return true;
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Грешка при обновяване на данни: " + e.getMessage());
                }
            }
        }
        return false;
    }

    public Book findBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty())
            return null;
        String searchIsbn = isbn.trim();
        for (Book book : books) {
            if (book.getIsbn().equalsIgnoreCase(searchIsbn)) {
                return book;
            }
        }
        return null;
    }

    public MyDynamicArray<Book> findBooksByTitle(String title) {
        MyDynamicArray<Book> results = new MyDynamicArray<>();
        if (title == null || title.trim().isEmpty())
            return results;
        String searchTitleLower = title.trim().toLowerCase();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(searchTitleLower)) {
                results.add(book);
            }
        }
        return results;
    }

    public MyDynamicArray<Book> findBooksByAuthor(String authorName) {
        MyDynamicArray<Book> results = new MyDynamicArray<>();
        if (authorName == null || authorName.trim().isEmpty())
            return results;
        String searchAuthorLower = authorName.trim().toLowerCase();
        for (Book book : books) {
            String fullName = book.getAuthor().getFirstName().toLowerCase() + " "
                    + book.getAuthor().getLastName().toLowerCase();
            if (fullName.contains(searchAuthorLower) ||
                    book.getAuthor().getFirstName().toLowerCase().contains(searchAuthorLower) ||
                    book.getAuthor().getLastName().toLowerCase().contains(searchAuthorLower)) {
                results.add(book);
            }
        }
        return results;
    }

    public MyDynamicArray<Book> getAllBooks() {
        return books;
    }

    public void sortByTitle() {
        SortUtils.quickSort(books, Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
    }

    public void sortByAuthor() {
        Comparator<Book> authorComparator = Comparator
                .comparing((Book b) -> b.getAuthor().getLastName(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing((Book b) -> b.getAuthor().getFirstName(), String.CASE_INSENSITIVE_ORDER);
        SortUtils.quickSort(books, authorComparator);
    }

    public void saveToFile() {
        saveToFile(this.dataFile);
    }

    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Book book : books) {
                writer.println(book.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Грешка при запазване на библиотеката във файл '" + filename + "': " + e.getMessage());
        }
    }

    public void loadFromFile() {
        loadFromFile(this.dataFile);
    }

    public void loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Файлът '" + filename + "' не съществува. Няма заредени данни.");
            this.books.clear();
            return;
        }

        MyDynamicArray<Book> loadedBooks = new MyDynamicArray<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty())
                    continue;

                Book book = Book.fromFileString(line);
                if (book != null) {
                    boolean duplicate = false;
                    for (Book existingBook : loadedBooks) {
                        if (existingBook.getIsbn().equalsIgnoreCase(book.getIsbn())) {
                            System.err.println("Грешка на ред " + lineNumber + ": Дублиран ISBN '" + book.getIsbn()
                                    + "' във файла. Книгата е пропусната.");
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) {
                        loadedBooks.add(book);
                    }
                } else {
                    System.err.println(
                            "Грешка при четене на ред " + lineNumber + " от файл: Невалиден формат или данни.");
                }
            }
            this.books = loadedBooks;
        } catch (FileNotFoundException e) {
            System.err.println("Файлът '" + filename + "' не е намерен.");
            this.books.clear();
        } catch (IOException e) {
            System.err.println("Грешка при четене от файл '" + filename + "': " + e.getMessage());
        }
    }
}
