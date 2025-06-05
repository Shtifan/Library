import java.util.Comparator;

@SuppressWarnings("unchecked")
public class MergeSort {
    public static <T> void sort(MyArrayList<T> list, Comparator<? super T> comparator) {
        if (list == null || list.size() <= 1) {
            return;
        }
        T[] array = list.toArray((T[]) new Object[0]);
        T[] temp = (T[]) new Object[array.length];
        mergeSort(array, temp, 0, array.length - 1, comparator);

        list.clear();
        for (T item : array) {
            list.add(item);
        }
    }

    private static <T> void mergeSort(T[] array, T[] temp, int left, int right, Comparator<? super T> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(array, temp, left, mid, comparator);
            mergeSort(array, temp, mid + 1, right, comparator);
            merge(array, temp, left, mid, right, comparator);
        }
    }

    private static <T> void merge(T[] array, T[] temp, int left, int mid, int right, Comparator<? super T> comparator) {
        if (right + 1 - left >= 0)
            System.arraycopy(array, left, temp, left, right + 1 - left);

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (comparator.compare(temp[i], temp[j]) <= 0) {
                array[k] = temp[i];
                i++;
            } else {
                array[k] = temp[j];
                j++;
            }
            k++;
        }

        while (i <= mid) {
            array[k] = temp[i];
            k++;
            i++;
        }
    }

    public static class BookTitleComparator implements Comparator<Book> {
        @Override
        public int compare(Book b1, Book b2) {
            return b1.getTitle().compareToIgnoreCase(b2.getTitle());
        }
    }

    public static class BookAuthorComparator implements Comparator<Book> {
        @Override
        public int compare(Book b1, Book b2) {
            int lastNameComp = b1.getAuthor().getLastName().compareToIgnoreCase(b2.getAuthor().getLastName());
            if (lastNameComp != 0) {
                return lastNameComp;
            }
            return b1.getAuthor().getFirstName().compareToIgnoreCase(b2.getAuthor().getFirstName());
        }
    }
}