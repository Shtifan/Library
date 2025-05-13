import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LibraryGUI extends JFrame {

    private Library library;
    private BookTableModel tableModel;
    private JTable bookTable;
    private JTextField isbnField;
    private JTextField titleField;
    private JTextField authorFirstNameField;
    private JTextField authorLastNameField;
    private JTextField yearField;
    private JTextField searchField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JButton showAllButton;
    private JButton sortTitleButton;
    private JButton sortAuthorButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton clearFieldsButton;

    private boolean editMode = false;
    private String isbnForEdit = null;

    public LibraryGUI() {
        library = new Library("library_data.txt");
        library.loadFromFile();

        setTitle("Система за управление на библиотека");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        attachListeners();

        tableModel.setBooks(library.getAllBooks());
    }

    private void initComponents() {
        tableModel = new BookTableModel(new MyDynamicArray<>());
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setAutoCreateRowSorter(true);

        isbnField = new JTextField(15);
        titleField = new JTextField(25);
        authorFirstNameField = new JTextField(15);
        authorLastNameField = new JTextField(15);
        yearField = new JTextField(5);
        searchField = new JTextField(20);

        addButton = new JButton("Добави книга");
        updateButton = new JButton("Обнови избрана");
        updateButton.setEnabled(false);
        deleteButton = new JButton("Изтрий избрана");
        deleteButton.setEnabled(false);
        searchButton = new JButton("Търси");
        showAllButton = new JButton("Покажи всички");
        sortTitleButton = new JButton("Сортирай по Заглавие");
        sortAuthorButton = new JButton("Сортирай по Автор");
        saveButton = new JButton("Запази");
        loadButton = new JButton("Зареди");
        clearFieldsButton = new JButton("Изчисти полета");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        JScrollPane tableScrollPane = new JScrollPane(bookTable);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout(10, 5));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Данни за книга"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(isbnField, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Заглавие:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Автор Име:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        formPanel.add(authorFirstNameField, gbc);
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Фамилия:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        formPanel.add(authorLastNameField, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Година:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(yearField, gbc);
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(clearFieldsButton, gbc);
        gbc.anchor = GridBagConstraints.WEST;

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionButtonPanel.add(addButton);
        actionButtonPanel.add(updateButton);
        actionButtonPanel.add(deleteButton);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Търсене / Филтриране"));
        searchPanel.add(new JLabel("Търси по ISBN/Заглавие/Автор:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        JPanel extraActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        extraActionsPanel.add(sortTitleButton);
        extraActionsPanel.add(sortAuthorButton);
        extraActionsPanel.add(saveButton);
        extraActionsPanel.add(loadButton);

        controlPanel.add(formPanel, BorderLayout.NORTH);
        controlPanel.add(actionButtonPanel, BorderLayout.CENTER);
        controlPanel.add(extraActionsPanel, BorderLayout.SOUTH);

        add(controlPanel, BorderLayout.SOUTH);
        add(searchPanel, BorderLayout.NORTH);
    }

    private void attachListeners() {
        addButton.addActionListener(e -> addBookAction());
        updateButton.addActionListener(e -> updateBookAction());
        deleteButton.addActionListener(e -> deleteBookAction());
        clearFieldsButton.addActionListener(e -> clearInputFields());

        searchButton.addActionListener(e -> searchAction());
        showAllButton.addActionListener(e -> showAllAction());
        searchField.addActionListener(e -> searchAction());

        sortTitleButton.addActionListener(e -> sortAction(true));
        sortAuthorButton.addActionListener(e -> sortAction(false));

        saveButton.addActionListener(e -> saveAction());
        loadButton.addActionListener(e -> loadAction());

        bookTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && bookTable.getSelectedRow() != -1) {
                int selectedViewRow = bookTable.getSelectedRow();
                int modelRow = bookTable.convertRowIndexToModel(selectedViewRow);
                populateFieldsFromSelectedRow(modelRow);
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                editMode = true;
                isbnForEdit = (String) tableModel.getValueAt(modelRow, 0);
                isbnField.setEditable(false);
                addButton.setEnabled(false);
            } else {
                if (bookTable.getSelectedRow() == -1) {
                    if (!editMode) {
                        clearInputFields();
                        updateButton.setEnabled(false);
                        deleteButton.setEnabled(false);
                        isbnForEdit = null;
                        isbnField.setEditable(true);
                        addButton.setEnabled(true);
                    }
                }
            }
        });

        bookTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedViewRow = bookTable.getSelectedRow();
                    if (selectedViewRow != -1) {
                        int modelRow = bookTable.convertRowIndexToModel(selectedViewRow);
                        populateFieldsFromSelectedRow(modelRow);
                        updateButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                        editMode = true;
                        isbnForEdit = (String) tableModel.getValueAt(modelRow, 0);
                        isbnField.setEditable(false);
                        addButton.setEnabled(false);
                    }
                }
            }
        });
    }

    private void addBookAction() {
        try {
            String isbn = isbnField.getText();
            String title = titleField.getText();
            String authorFirst = authorFirstNameField.getText();
            String authorLast = authorLastNameField.getText();
            String yearStr = yearField.getText();

            if (isbn.trim().isEmpty() || title.trim().isEmpty() ||
                    authorFirst.trim().isEmpty() || authorLast.trim().isEmpty() ||
                    yearStr.trim().isEmpty()) {
                showError("Всички полета са задължителни!");
                return;
            }

            int year = Integer.parseInt(yearStr.trim());

            Author author = new Author(authorFirst, authorLast);
            Book book = new Book(isbn, title, author, year);

            library.addBook(book);

            tableModel.setBooks(library.getAllBooks());

            clearInputFields();
            showMessage("Книгата е добавена успешно.");

        } catch (NumberFormatException ex) {
            showError("Невалиден формат за година. Моля, въведете число.");
        } catch (IllegalArgumentException ex) {
            showError("Грешка при добавяне: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Възникна неочаквана грешка: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updateBookAction() {
        if (!editMode || isbnForEdit == null) {
            showError("Моля, първо изберете книга от таблицата, която да редактирате.");
            return;
        }

        try {
            String updatedIsbn = isbnField.getText();
            String title = titleField.getText();
            String authorFirst = authorFirstNameField.getText();
            String authorLast = authorLastNameField.getText();
            String yearStr = yearField.getText();

            if (updatedIsbn.trim().isEmpty() || title.trim().isEmpty() ||
                    authorFirst.trim().isEmpty() || authorLast.trim().isEmpty() ||
                    yearStr.trim().isEmpty()) {
                showError("Всички полета са задължителни при редакция!");
                return;
            }

            int year = Integer.parseInt(yearStr.trim());

            Author updatedAuthor = new Author(authorFirst, authorLast);
            Book updatedBookData = new Book(updatedIsbn, title, updatedAuthor, year);

            boolean success = library.updateBook(isbnForEdit, updatedBookData);

            if (success) {
                tableModel.fireTableDataChanged();
                int updatedRow = -1;
                for (int i = 0; i < tableModel.getRowCount(); ++i) {
                    if (tableModel.getValueAt(i, 0).equals(updatedIsbn)) {
                        updatedRow = bookTable.convertRowIndexToView(i);
                        break;
                    }
                }
                if (updatedRow != -1) {
                    bookTable.setRowSelectionInterval(updatedRow, updatedRow);
                } else {
                    bookTable.clearSelection();
                }

                showMessage("Книгата е обновена успешно.");
                resetToNonEditMode();
            } else {
                showError("Книгата с ISBN '" + isbnForEdit + "' не беше намерена за обновяване.");
                resetToNonEditMode();
            }

        } catch (NumberFormatException ex) {
            showError("Невалиден формат за година. Моля, въведете число.");
        } catch (IllegalArgumentException ex) {
            showError("Грешка при обновяване: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Възникна неочаквана грешка при обновяване: " + ex.getMessage());
            ex.printStackTrace();
            resetToNonEditMode();
        }
    }

    private void deleteBookAction() {
        int selectedViewRow = bookTable.getSelectedRow();
        if (selectedViewRow == -1) {
            showError("Моля, изберете книга от таблицата за изтриване.");
            return;
        }

        int modelRow = bookTable.convertRowIndexToModel(selectedViewRow);
        Book bookToDelete = tableModel.getBookAt(modelRow);

        if (bookToDelete == null) {
            showError("Не може да се определи коя книга да бъде изтрита.");
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Сигурни ли сте, че искате да изтриете книгата:\n" +
                        "ISBN: " + bookToDelete.getIsbn() + "\n" +
                        "Заглавие: " + bookToDelete.getTitle(),
                "Потвърждение за изтриване",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean removed = library.removeBook(bookToDelete.getIsbn());
            if (removed) {
                tableModel.setBooks(library.getAllBooks());
                showMessage("Книгата е изтрита успешно.");
                clearInputFields();
                resetToNonEditMode();
            } else {
                showError("Грешка при опит за изтриване на книгата.");
                resetToNonEditMode();
            }
        }
    }

    private void searchAction() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showError("Моля, въведете текст за търсене.");
            showAllAction();
            return;
        }

        MyDynamicArray<Book> results = new MyDynamicArray<>();
        MyDynamicArray<Book> foundByIsbn = new MyDynamicArray<>();
        Book found = library.findBookByIsbn(searchTerm);
        if (found != null) {
            foundByIsbn.add(found);
        }

        MyDynamicArray<Book> foundByTitle = library.findBooksByTitle(searchTerm);
        MyDynamicArray<Book> foundByAuthor = library.findBooksByAuthor(searchTerm);

        addUnique(results, foundByIsbn);
        addUnique(results, foundByTitle);
        addUnique(results, foundByAuthor);

        if (results.isEmpty()) {
            showMessage("Няма намерени книги по зададения критерий.");
            tableModel.setBooks(results);
        } else {
            tableModel.setBooks(results);
            showMessage(results.size() + " книги намерени.");
        }
        bookTable.clearSelection();
        resetToNonEditMode();
    }

    private void addUnique(MyDynamicArray<Book> target, MyDynamicArray<Book> source) {
        for (Book bookToAdd : source) {
            boolean exists = false;
            for (Book existingBook : target) {
                if (existingBook.equals(bookToAdd)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                target.add(bookToAdd);
            }
        }
    }

    private void showAllAction() {
        tableModel.setBooks(library.getAllBooks());
        searchField.setText("");
        bookTable.clearSelection();
        resetToNonEditMode();
        showMessage("Показани са всички книги.");
    }

    private void sortAction(boolean byTitle) {
        if (byTitle) {
            library.sortByTitle();
            showMessage("Книгите са сортирани по заглавие.");
        } else {
            library.sortByAuthor();
            showMessage("Книгите са сортирани по автор (фамилия, име).");
        }
        tableModel.fireTableDataChanged();
        bookTable.clearSelection();
        resetToNonEditMode();
    }

    private void saveAction() {
        try {
            library.saveToFile();
            showMessage("Библиотеката е запазена успешно във файл: " + library.dataFile);
        } catch (Exception ex) {
            showError("Грешка при запазване на файла: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadAction() {
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Сигурни ли сте, че искате да заредите данни от файл?\n" +
                        "Всички незапазени промени в текущата библиотека ще бъдат загубени.",
                "Потвърждение за зареждане",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                library.loadFromFile();
                tableModel.setBooks(library.getAllBooks());
                showMessage("Библиотеката е заредена успешно от файл: " + library.dataFile);
                bookTable.clearSelection();
                resetToNonEditMode();
            } catch (Exception ex) {
                showError("Грешка при зареждане от файла: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void populateFieldsFromSelectedRow(int modelRow) {
        Book selectedBook = tableModel.getBookAt(modelRow);
        if (selectedBook != null) {
            isbnField.setText(selectedBook.getIsbn());
            titleField.setText(selectedBook.getTitle());
            authorFirstNameField.setText(selectedBook.getAuthor().getFirstName());
            authorLastNameField.setText(selectedBook.getAuthor().getLastName());
            yearField.setText(String.valueOf(selectedBook.getYearPublished()));
        }
    }

    private void clearInputFields() {
        isbnField.setText("");
        titleField.setText("");
        authorFirstNameField.setText("");
        authorLastNameField.setText("");
        yearField.setText("");

        if (editMode || !addButton.isEnabled()) {
            resetToNonEditMode();
        }
        bookTable.clearSelection();
    }

    private void resetToNonEditMode() {
        editMode = false;
        isbnForEdit = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        isbnField.setEditable(true);
        addButton.setEnabled(true);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Информация", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Грешка", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);
        });
    }
}
