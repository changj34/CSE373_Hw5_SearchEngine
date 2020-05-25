package datastructures.sorting;

import datastructures.concrete.ArrayHeap;
import misc.BaseTest;
import org.junit.Test;

import java.lang.reflect.Field;

/*
 *
   When writing tests for a given interface (e.g., IPriorityQueue), your tests should use only the
   methods specified by the public interface and nothing else (e.g., your tests should not use any
   additional public methods or public instance variables in your implementation). Because if your
   tests rely on a public method that is present only your implementation or if your tests rely on
   a public variable that you defined in your implementation, those tests will fail when they are
   run against our implementations or any other implementations (which would not have your extra public method or the public instance variable).

   That said, you may want to test the internal structure of your heap for debugging purposes. Such tests would be specific to your implementation and should not be tested on other implementations. Which is why we are providing you this file. Any tests you write in this file will not be tested against our implementations. So use this file to write any tests you need to test/debug the internal structure of your Heap.

   To test the internal structure of your heap, you need to somehow access the heap array.
   There are two main ways to access the heap's internal structure somehow.
   1) (recommended) Use something called *reflection*, which allows your inspect fields and methods of a program at runtime and execute it.
   2) Add a getter method to get an element at a given index in the ArrayHeap array.

   This file contains an example test that uses reflection to get access to the heap array.
   Build on this example and write your own tests.

 */

public class TestArrayHeapInternal extends BaseTest {
    protected <T extends Comparable<T>> ArrayHeap<T> makeInstance() {
        return new ArrayHeap<>();
    }

    @Test(timeout=SECOND)
    public void testBasicReflection() {
        ArrayHeap<Integer> heap = this.makeInstance();
        heap.insert(3);
        Comparable[] array = getArrayFromHeap(heap);
        assertEquals(3, array[0]);
    }

    private static Comparable[] getArrayFromHeap(ArrayHeap heap) {
        return getField(heap, "heap", Comparable[].class);
    }

    private static <T> T getField(Object obj, String fieldName, Class<T> expectedType) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(obj);
            return expectedType.cast(value);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }
}
