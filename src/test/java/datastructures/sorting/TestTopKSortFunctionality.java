package datastructures.sorting;

import misc.BaseTest;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import misc.Searcher;

import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestTopKSortFunctionality extends BaseTest {
    @Test(timeout=SECOND)
    public void testSimpleUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Searcher.topKSort(5, list);
        assertEquals(5, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(15 + i, top.get(i));
        }
    }
    
    @Test(timeout=SECOND)
    public void testEmptyUsage() {
        IList<Integer> list = new DoubleLinkedList<>();

        IList<Integer> top = Searcher.topKSort(5, list);
        assertEquals(0, top.size());
    }
    
    @Test(timeout=SECOND)
    public void testNegativeUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        try {
            Searcher.topKSort(-5, list);
        } catch (IllegalArgumentException ex){
            //Do nothing
        }
        
    }
    
    public void testBiggerUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Searcher.topKSort(25, list);
        assertEquals(20, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(15 + i, top.get(i));
        }
    }
}
