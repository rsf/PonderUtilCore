/*
 * Created on Dec 8, 2004
 */
package uk.org.ponder.sortutil.test;

import java.util.Random;

import uk.org.ponder.sortutil.DefaultComparator;
import uk.org.ponder.sortutil.ObjArraySortFacade;
import uk.org.ponder.sortutil.Sort;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class SortTester {
  public static void printArray(Object[] array) {
    for (int i = 0; i < array.length; ++ i) {
      System.out.print(array[i] + " ");
    }
    System.out.println();
  }
  
  public static void testSorted(Object[] array) {
    for (int i = 1; i < array.length; ++ i) {
      if (DefaultComparator.instance.compare(array[i - 1], array[i]) > 0) {
        throw new UniversalRuntimeException("Unsorted array at index " + i);
      }
    }
  }
  
  public static void timeSort(int SIZE) {
    Object[] array = new Object[SIZE];
    Random rand = new Random();
    for (int i = 0; i < SIZE; ++ i) {
      array[i] = new Integer(rand.nextInt(1000000));
    }
    //printArray(array);
    long time = System.currentTimeMillis();
    Sort.quicksort(new ObjArraySortFacade(array, null));
    double ttime = (System.currentTimeMillis()- time) / 1000.0;
    System.out.println(SIZE + "  " + ttime + " theta " + (ttime / (SIZE * Math.log(SIZE))));
    testSorted(array);
    //printArray(array); 
  }
  
  public static void main(String[] args) {
    int size = 2;
    for (int i = 1; i < 25; ++ i) {
      timeSort(size);
      size*=2;
    }
  }
}
