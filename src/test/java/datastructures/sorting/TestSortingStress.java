package datastructures.sorting;

import misc.BaseTest;
import misc.Searcher;

import org.junit.Test;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestSortingStress extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }
    
    @Test(timeout=10*SECOND)
    public void testDownHeap() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        IPriorityQueue<Integer> heap2 = this.makeInstance();
        for (int i = 200000; i > 0; i--) {
            heap.insert(i);
            heap2.insert(i);
        }
        assertEquals(200000, heap.size());
        List<Integer> sort = new ArrayList<Integer>();
        for (int i = 0; i < heap.size(); i++) {
            sort.add(heap2.removeMin());
        }
        Collections.sort(sort);
        for (int i = 0; i < sort.size(); i++) {
            assertEquals(heap.removeMin(), sort.get(i));
        }
    }
    
    @Test(timeout=10*SECOND)
    public void testUpHeap() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        IPriorityQueue<Integer> heap2 = this.makeInstance();
        for (int i = 0; i < 200000; i++) {
            heap.insert(i);
            heap2.insert(i);
        }
        assertEquals(200000, heap.size());
        List<Integer> sort = new ArrayList<Integer>();
        for (int i = 0; i < heap.size(); i++) {
            sort.add(heap2.removeMin());
        }
        Collections.sort(sort);
        for (int i = 0; i < sort.size(); i++) {
            assertEquals(heap.removeMin(), sort.get(i));
        }
    }
    
    @Test(timeout=10*SECOND)
    public void testAltHeap() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        IPriorityQueue<Integer> heap2 = this.makeInstance();
        int a = 0;
        int b = 199999;
        for (int i = 0; i < 100000; i++) {
            heap.insert(a);
            heap.insert(b);
            heap2.insert(a);
            heap2.insert(b);
            a++;
            b--;
        }
        assertEquals(200000, heap.size());
        List<Integer> sort = new ArrayList<Integer>();
        for (int i = 0; i < heap.size(); i++) {
            sort.add(heap2.removeMin());
        }
        Collections.sort(sort);
        for (int i = 0; i < sort.size(); i++) {
            assertEquals(heap.removeMin(), sort.get(i));
        }
    }
    
    @Test(timeout=10*SECOND)
    public void testBigK() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 499999; i >= 0; i--) {
            list.add(i);
        }

        IList<Integer> top = Searcher.topKSort(100000, list);
        assertEquals(100000, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(400000 + i, top.get(i));
        }
    }
}
