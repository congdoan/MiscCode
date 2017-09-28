import java.util.*;


public class QuickSort<T extends Comparable<T>> {
  
  private Random rand = new Random();
  
  public void sort(T[] a) {
    sort(a, 0, a.length - 1);
  }
  
  private void sort(T[] a, int lo, int hi) {
    // Base case
    if (lo >= hi) {
      return;
    }
    
    // Partition
    int pivot = partition(a, lo, hi);
    
    // Recur
    sort(a, lo, pivot - 1);
    sort(a, pivot + 1, hi);
  }
  
  private int partition(T[] a, int lo, int hi) {
    int idx = lo + rand.nextInt(hi - lo + 1);    
    T pivot = a[idx];
    swap(a, lo, idx);
    int left = lo;
    for (int i = lo + 1; i <= hi; i++) {
      if (less(a[i], pivot)) {
        swap(a, i, ++left);
      }
    }
    swap(a, lo, left);
    return left;
  }
  
  private boolean isSorted(T[] a) {
    for (int i = 1; i < a.length; i++) {
      if (less(a[i], a[i-1])) {
        return false;
      }
    }
    return true;
  }
  
  private boolean less(T a, T b) {
    return a.compareTo(b) < 0;
  }
  
  private void swap(T[] a, int i, int j) {
    T tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }
  
  public static void main(String[] args) {
    QuickSort<Integer> qsort = new QuickSort<>();
    Integer[] a = {1, 2, 1};
    qsort.sort(a);
    System.out.println(Arrays.asList(a));
    assert qsort.isSorted(a);

    a = new Integer[] {1, 4, 9, 3, 2, 1, 7};
    qsort.sort(a);
    System.out.println(Arrays.asList(a));
    assert qsort.isSorted(a);

    a = new Integer[] {1, 2,3,4,5};
    qsort.sort(a);
    System.out.println(Arrays.asList(a));
    assert qsort.isSorted(a);

    a = new Integer[] {5, 4,3,2,1};
    qsort.sort(a);
    System.out.println(Arrays.asList(a));
    assert qsort.isSorted(a);

    a = new Integer[] {1, 1};
    qsort.sort(a);
    System.out.println(Arrays.asList(a));
    assert qsort.isSorted(a);

    a = new Integer[] {1, 2};
    qsort.sort(a);
    System.out.println(Arrays.asList(a));
    assert qsort.isSorted(a);

    a = new Integer[] {2, 1};
    qsort.sort(a);
    System.out.println(Arrays.asList(a));
    assert qsort.isSorted(a);

    a = new Integer[] {6};
    qsort.sort(a);
    System.out.println(Arrays.asList(a));
    assert qsort.isSorted(a);
  }
  
}
