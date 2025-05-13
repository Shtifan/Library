import javax.swing.table.AbstractTableModel;

public class BookTableModel extends AbstractTableModel {

    private MyDynamicArray<Book> books;
    private final String[] columnNames = { "ISBN", "Заглавие", "Име Автор", "Фамилия Автор", "Година" };

    public BookTableModel(MyDynamicArray<Book> books) {
        this.books = books;
    }

    public void setBooks(MyDynamicArray<Book> books) {
        this.books = books;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= books.size()) {
            return null;
        }
        Book book = books.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return book.getIsbn();
            case 1:
                return book.getTitle();
            case 2:
                return book.getAuthor().getFirstName();
            case 3:
                return book.getAuthor().getLastName();
            case 4:
                return book.getYearPublished();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public Book getBookAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= books.size()) {
            return null;
        }
        return books.get(rowIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }

    public void fireTableRowsInserted(int firstRow, int lastRow) {
        super.fireTableRowsInserted(firstRow, lastRow);
    }

    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        super.fireTableRowsDeleted(firstRow, lastRow);
    }

    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        super.fireTableRowsUpdated(firstRow, lastRow);
    }

}
