import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class LibraryGUI extends JFrame {
    private final Library library;
    private final DefaultTableModel tableModel;
    private final JTable bookTable;

    private final JTextField isbnField;
    private final JTextField titleField;
    private final JTextField authorFirstNameField;
    private final JTextField authorLastNameField;
    private final JTextField yearField;
    private final JTextField searchField;
    private final JComboBox<String> searchTypeComboBox;

    private static final String DATA_FILE = "library_data.txt";

    public LibraryGUI() {
        library = new Library();

        setTitle("Library Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        JMenuItem exitItem = new JMenuItem("Exit");

        saveItem.addActionListener(e -> saveLibrary());
        loadItem.addActionListener(e -> loadLibrary());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = { "ISBN", "Title", "Author", "Year" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        isbnField = new JTextField(20);
        inputPanel.add(isbnField, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        titleField = new JTextField(20);
        inputPanel.add(titleField, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Author First Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        authorFirstNameField = new JTextField(20);
        inputPanel.add(authorFirstNameField, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Author Last Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        authorLastNameField = new JTextField(20);
        inputPanel.add(authorLastNameField, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        yearField = new JTextField(5);
        inputPanel.add(yearField, gbc);
        gbc.weightx = 0;

        JPanel bookActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Book");
        JButton editButton = new JButton("Update Selected Book");
        JButton clearFieldsButton = new JButton("Clear Fields");

        bookActionsPanel.add(addButton);
        bookActionsPanel.add(editButton);
        bookActionsPanel.add(clearFieldsButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(bookActionsPanel, gbc);

        mainPanel.add(inputPanel, BorderLayout.EAST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Library Actions"));

        JButton deleteButton = new JButton("Delete Selected");
        JButton sortTitleButton = new JButton("Sort by Title");
        JButton sortAuthorButton = new JButton("Sort by Author");

        searchField = new JTextField(15);
        searchTypeComboBox = new JComboBox<>(new String[] { "By ISBN", "By Title", "By Author", "By Year" });
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All Books");

        controlPanel.add(deleteButton);
        controlPanel.add(sortTitleButton);
        controlPanel.add(sortAuthorButton);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchTypeComboBox);
        controlPanel.add(searchButton);
        controlPanel.add(showAllButton);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        add(mainPanel);

        addButton.addActionListener(e -> addBook());
        editButton.addActionListener(e -> editBook());
        deleteButton.addActionListener(e -> deleteBook());
        clearFieldsButton.addActionListener(e -> clearInputFields());

        sortTitleButton.addActionListener(e -> {
            library.sortBooksByTitle();
            refreshTable(library.getAllBooks());
        });
        sortAuthorButton.addActionListener(e -> {
            library.sortBooksByAuthor();
            refreshTable(library.getAllBooks());
        });
        searchButton.addActionListener(e -> searchBooks());
        showAllButton.addActionListener(e -> refreshTable(library.getAllBooks()));

        bookTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && bookTable.getSelectedRow() != -1) {
                populateFieldsFromSelectedRow();
            }
        });

        loadLibrary();
        refreshTable(library.getAllBooks());
    }

    private void clearInputFields() {
        isbnField.setText("");
        titleField.setText("");
        authorFirstNameField.setText("");
        authorLastNameField.setText("");
        yearField.setText("");
        isbnField.setEditable(true);
        bookTable.clearSelection();
    }

    private void populateFieldsFromSelectedRow() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            String isbn = (String) tableModel.getValueAt(selectedRow, 0);
            Book book = library.findBookByIsbn(isbn);

            if (book != null) {
                isbnField.setText(book.getIsbn());
                titleField.setText(book.getTitle());
                authorFirstNameField.setText(book.getAuthor().getFirstName());
                authorLastNameField.setText(book.getAuthor().getLastName());
                yearField.setText(String.valueOf(book.getPublicationYear()));
                isbnField.setEditable(true);
            }
        }
    }

    private void addBook() {
        try {
            String isbn = isbnField.getText();
            String title = titleField.getText();
            String authorFirstName = authorFirstNameField.getText();
            String authorLastName = authorLastNameField.getText();
            if (isbn.isEmpty() || title.isEmpty() || authorFirstName.isEmpty() || authorLastName.isEmpty()
                    || yearField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Book.isValidIsbn(isbn)) {
                JOptionPane.showMessageDialog(this, "Invalid ISBN format. Must be 10 or 13 digits.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int year = Integer.parseInt(yearField.getText());
            Author author = new Author(authorFirstName, authorLastName);
            Book book = new Book(isbn, title, author, year);

            library.addBook(book);
            refreshTable(library.getAllBooks());
            clearInputFields();
            JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid year format.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit.", "Selection Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String originalIsbn = (String) tableModel.getValueAt(selectedRow, 0);

        try {
            String newIsbn = isbnField.getText();
            String title = titleField.getText();
            String authorFirstName = authorFirstNameField.getText();
            String authorLastName = authorLastNameField.getText();

            if (newIsbn.isEmpty() || title.isEmpty() || authorFirstName.isEmpty() || authorLastName.isEmpty()
                    || yearField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled for update.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Book.isValidIsbn(newIsbn)) {
                JOptionPane.showMessageDialog(this, "Invalid new ISBN format. Must be 10 or 13 digits.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int year = Integer.parseInt(yearField.getText());
            Author author = new Author(authorFirstName, authorLastName);
            Book updatedBookDetails = new Book(newIsbn, title, author, year);

            if (library.updateBook(originalIsbn, updatedBookDetails)) {
                refreshTable(library.getAllBooks());
                clearInputFields();
                JOptionPane.showMessageDialog(this, "Book updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Book with original ISBN " + originalIsbn + " not found for update.", "Update Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid year format.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.", "Selection Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete book with ISBN: " + isbn + "?", "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (library.removeBook(isbn)) {
                refreshTable(library.getAllBooks());
                clearInputFields();
                JOptionPane.showMessageDialog(this, "Book deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Book not found for deletion.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchBooks() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Search Error",
                    JOptionPane.WARNING_MESSAGE);
            refreshTable(library.getAllBooks());
            return;
        }
        String searchType = (String) searchTypeComboBox.getSelectedItem();
        MyArrayList<Book> results = new MyArrayList<>();

        try {
            if ("By ISBN".equals(searchType)) {
                Book book = library.findBookByIsbn(query);
                if (book != null) {
                    results.add(book);
                }
            } else if ("By Title".equals(searchType)) {
                results = library.findBooksByTitle(query);
            } else if ("By Author".equals(searchType)) {
                results = library.findBooksByAuthor(query);
            } else if ("By Year".equals(searchType)) {
                try {
                    int year = Integer.parseInt(query);
                    results = library.findBooksByYear(year);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid year.", "Search Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No books found matching your criteria.", "Search Result",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            refreshTable(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error during search: " + e.getMessage(), "Search Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveLibrary() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(DATA_FILE));
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                library.saveToFile(file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Library data saved successfully to " + file.getName(),
                        "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadLibrary() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(DATA_FILE));
        File defaultFile = new File(DATA_FILE);
        if (defaultFile.exists()) {
            try {
                library.loadFromFile(DATA_FILE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading data from " + DATA_FILE + ": " + ex.getMessage(),
                        "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println(DATA_FILE + " not found. Starting with empty library.");
        }
    }

    private void refreshTable(MyArrayList<Book> booksToShow) {
        tableModel.setRowCount(0);
        for (int i = 0; i < booksToShow.size(); i++) {
            Book book = booksToShow.get(i);
            tableModel.addRow(new Object[] {
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor().toString(),
                    book.getPublicationYear()
            });
        }
    }
}