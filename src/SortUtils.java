import java.util.Comparator;

public class SortUtils {

    public static <T> void quickSort(MyDynamicArray<T> array, Comparator<T> comparator) {
        if (array == null || array.size() <= 1) {
            return;
        }
        quickSortRecursive(array, 0, array.size() - 1, comparator);
    }

    private static <T> void quickSortRecursive(MyDynamicArray<T> array, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            int pivotIndex = partition(array, low, high, comparator);
            quickSortRecursive(array, low, pivotIndex - 1, comparator);
            quickSortRecursive(array, pivotIndex + 1, high, comparator);
        }
    }

    private static <T> int partition(MyDynamicArray<T> array, int low, int high, Comparator<T> comparator) {
        T pivot = array.get(high);
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (comparator.compare(array.get(j), pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }

        swap(array, i + 1, high);

        return i + 1;
    }

    private static <T> void swap(MyDynamicArray<T> array, int i, int j) {
        T temp = array.get(i);
        array.set(i, array.get(j));
        array.set(j, temp);
    }
}