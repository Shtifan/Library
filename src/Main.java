import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    private final Library library;
    private DefaultTableModel tableModel;
    private JTable bookTable;

    private JTextField isbnField;
    private JTextField titleField;
    private JTextField authorFirstNameField;
    private JTextField authorLastNameField;
    private JTextField yearField;
    private JTextField searchField;

    private String originalIsbnForEdit = null;

    public Main() {
        library = new Library();

        setTitle("Система за управление на библиотека");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
        try {
            library.loadBooksFromFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Грешка при зареждане на книги от файл: " + e.getMessage(), "Грешка при зареждане", JOptionPane.ERROR_MESSAGE);
        }
        refreshTable(library.getAllBooks());
    }

    private void initializeUI() {
        String[] columnNames = {"ISBN", "Заглавие", "Автор", "Година"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Данни за книга"));

        inputPanel.add(new JLabel("ISBN:"));
        isbnField = new JTextField();
        inputPanel.add(isbnField);

        inputPanel.add(new JLabel("Заглавие:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Име на автор:"));
        authorFirstNameField = new JTextField();
        inputPanel.add(authorFirstNameField);

        inputPanel.add(new JLabel("Фамилия на автор:"));
        authorLastNameField = new JTextField();
        inputPanel.add(authorLastNameField);

        inputPanel.add(new JLabel("Година:"));
        yearField = new JTextField();
        inputPanel.add(yearField);

        JPanel crudButtonPanel = new JPanel(new FlowLayout());
        JButton addOrUpdateButton = new JButton("Добави/Обнови");
        JButton loadForEditButton = new JButton("Зареди за редакция");
        JButton deleteButton = new JButton("Изтрий");
        JButton clearFormButton = new JButton("Изчисти полетата");

        crudButtonPanel.add(addOrUpdateButton);
        crudButtonPanel.add(loadForEditButton);
        crudButtonPanel.add(deleteButton);
        crudButtonPanel.add(clearFormButton);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.add(inputPanel, BorderLayout.CENTER);
        formPanel.add(crudButtonPanel, BorderLayout.SOUTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Търсене и Сортиране"));
        searchField = new JTextField(15);
        JButton searchByIsbnButton = new JButton("ISBN");
        JButton searchByTitleButton = new JButton("Заглавие");
        JButton searchByAuthorButton = new JButton("Автор");
        JButton showAllButton = new JButton("Всички");

        searchPanel.add(new JLabel("Търси по:"));
        searchPanel.add(searchField);
        searchPanel.add(searchByIsbnButton);
        searchPanel.add(searchByTitleButton);
        searchPanel.add(searchByAuthorButton);
        searchPanel.add(showAllButton);

        JButton sortByTitleButton = new JButton("Сорт. Заглавие");
        JButton sortByAuthorButton = new JButton("Сорт. Автор");
        searchPanel.add(sortByTitleButton);
        searchPanel.add(sortByAuthorButton);

        JPanel fileOpPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Запази във файл");
        fileOpPanel.add(saveButton);

        setLayout(new BorderLayout(5, 5));
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.WEST);

        JPanel bottomControls = new JPanel(new BorderLayout());
        bottomControls.add(searchPanel, BorderLayout.CENTER);
        bottomControls.add(fileOpPanel, BorderLayout.EAST);

        add(bottomControls, BorderLayout.SOUTH);

        addOrUpdateButton.addActionListener(e -> addOrUpdateBookAction());
        loadForEditButton.addActionListener(e -> loadSelectedBookForEditingAction());
        deleteButton.addActionListener(e -> deleteBookAction());
        clearFormButton.addActionListener(e -> clearInputFieldsAndState());

        searchByIsbnButton.addActionListener(e -> searchBooksAction("isbn"));
        searchByTitleButton.addActionListener(e -> searchBooksAction("title"));
        searchByAuthorButton.addActionListener(e -> searchBooksAction("author"));
        showAllButton.addActionListener(e -> refreshTable(library.getAllBooks()));


        sortByTitleButton.addActionListener(e -> sortBooksAction("title"));
        sortByAuthorButton.addActionListener(e -> sortBooksAction("author"));

        saveButton.addActionListener(e -> saveBooksAction());

        bookTable.getSelectionModel().addListSelectionListener(event -> {
            boolean rowSelected = bookTable.getSelectedRow() != -1;
            loadForEditButton.setEnabled(rowSelected);
            deleteButton.setEnabled(rowSelected);
        });
        loadForEditButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private boolean validateInputFields(boolean isNewBook) {
        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String authorFirstName = authorFirstNameField.getText().trim();
        String authorLastName = authorLastNameField.getText().trim();
        String yearStr = yearField.getText().trim();

        if (isbn.isEmpty() || title.isEmpty() || authorFirstName.isEmpty() || authorLastName.isEmpty() || yearStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Всички полета са задължителни!", "Грешка при въвеждане", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (isNewBook && isValidIsbnFormat(isbn)) {
            return false;
        }
        if (!originalIsbnForEdit_equals_isbnField() && isValidIsbnFormat(isbn)) {
            return false;
        }

        try {
            int year = Integer.parseInt(yearStr);
            if (year < 0 || year > java.time.Year.now().getValue() + 5) {
                JOptionPane.showMessageDialog(this, "Невалидна година!", "Грешка при въвеждане", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Годината трябва да е валидно число!", "Грешка при въвеждане", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean originalIsbnForEdit_equals_isbnField() {
        if (originalIsbnForEdit == null) return false;
        return originalIsbnForEdit.equals(isbnField.getText().trim());
    }

    private boolean isValidIsbnFormat(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ISBN не може да бъде празен.", "Невалиден ISBN", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    private void addOrUpdateBookAction() {
        boolean isNewBook = (originalIsbnForEdit == null);
        if (!validateInputFields(isNewBook)) {
            return;
        }

        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String authorFirstName = authorFirstNameField.getText().trim();
        String authorLastName = authorLastNameField.getText().trim();
        int year = Integer.parseInt(yearField.getText().trim());

        Author author = new Author(authorFirstName, authorLastName);
        Book book = new Book(isbn, title, author, year);

        try {
            if (isNewBook) {
                library.addBook(book);
                JOptionPane.showMessageDialog(this, "Книгата е добавена успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else {
                boolean updated = library.updateBook(originalIsbnForEdit, book);
                if (updated) {
                    JOptionPane.showMessageDialog(this, "Книгата е обновена успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Книгата за обновяване не беше намерена (ISBN: " + originalIsbnForEdit + ").", "Грешка", JOptionPane.ERROR_MESSAGE);
                }
            }
            refreshTable(library.getAllBooks());
            clearInputFieldsAndState();
            saveBooksAction();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedBookForEditingAction() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = bookTable.convertRowIndexToModel(selectedRow);
            String isbnFromTable = (String) tableModel.getValueAt(modelRow, 0);

            Book bookToEdit = library.findBookByIsbn(isbnFromTable);

            if (bookToEdit != null) {
                isbnField.setText(bookToEdit.getIsbn());
                titleField.setText(bookToEdit.getTitle());
                authorFirstNameField.setText(bookToEdit.getAuthor().getFirstName());
                authorLastNameField.setText(bookToEdit.getAuthor().getLastName());
                yearField.setText(String.valueOf(bookToEdit.getYear()));

                originalIsbnForEdit = bookToEdit.getIsbn();

                JOptionPane.showMessageDialog(this, "Данните са заредени. Променете и натиснете 'Добави/Обнови'.", "Редакция", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Грешка: Избраната книга не беше намерена в библиотеката.", "Грешка", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Моля, изберете книга за редакция.", "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteBookAction() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = bookTable.convertRowIndexToModel(selectedRow);
            String isbnToDelete = (String) tableModel.getValueAt(modelRow, 0);

            int choice = JOptionPane.showConfirmDialog(this, "Сигурни ли сте, че искате да изтриете книга с ISBN: " + isbnToDelete + "?", "Потвърждение за изтриване", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                if (library.removeBook(isbnToDelete)) {
                    refreshTable(library.getAllBooks());
                    clearInputFieldsAndState();
                    saveBooksAction();
                    JOptionPane.showMessageDialog(this, "Книгата е изтрита успешно!");
                } else {
                    JOptionPane.showMessageDialog(this, "Грешка при изтриване. Книгата не е намерена.", "Грешка", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Моля, изберете книга за изтриване.", "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchBooksAction(String criteria) {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Моля, въведете текст за търсене.", "Търсене", JOptionPane.INFORMATION_MESSAGE);
            refreshTable(library.getAllBooks());
            return;
        }
        ArrayList<Book> results = library.searchBooks(searchTerm, criteria);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Няма намерени книги.", "Търсене", JOptionPane.INFORMATION_MESSAGE);
        }
        refreshTable(results);
    }

    private void sortBooksAction(String criteria) {
        library.sortBooks(criteria);
        refreshTable(library.getAllBooks());
        JOptionPane.showMessageDialog(this, "Книгите са сортирани по " + ("title".equals(criteria) ? "заглавие." : "автор."), "Сортиране", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshTable(List<Book> bookList) {
        tableModel.setRowCount(0);
        for (Book book : bookList) {
            Object[] row = {book.getIsbn(), book.getTitle(), book.getAuthor().toString(), book.getYear()};
            tableModel.addRow(row);
        }
        bookTable.clearSelection();
    }

    private void clearInputFieldsAndState() {
        isbnField.setText("");
        titleField.setText("");
        authorFirstNameField.setText("");
        authorLastNameField.setText("");
        yearField.setText("");
        originalIsbnForEdit = null;
        searchField.setText("");
        refreshTable(library.getAllBooks());
    }

    private void saveBooksAction() {
        try {
            library.saveBooksToFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Грешка при запазване на данните: " + e.getMessage(), "Грешка при запис", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}