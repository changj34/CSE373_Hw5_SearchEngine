package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

/**
 * See IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int length;

    // Feel free to add more fields and constants.

    public ArrayHeap() {
        this.heap = makeArrayOfT(3);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int size) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[size]);
    }

    @Override
    public T removeMin() {
        if (heap[0] == null) {
            throw new EmptyContainerException();
        }
        
        T min = heap[0];
        heap[0] = heap[this.size() - 1];
        heap[this.size() - 1] = null;
        this.length--;
        
        boolean percolateDown = true;
        int parent = 0;
        while (percolateDown) {
            int leastChild = findLowestChild(parent);
            if (leastChild >= 0 && heap[parent].compareTo(heap[leastChild]) > 0) {
                
                T swap = heap[parent];
                heap[parent] = heap[leastChild];
                heap[leastChild] = swap;
                
                parent = leastChild;
            } else {
                percolateDown = false;
            }
        }
        return min;
    }
    private int findLowestChild(int parent) {
        int min = -1;
        if (parent*4 + 1 < heap.length && heap[parent*4 + 1] != null) {
            min = parent*4 + 1;
            
            for (int i = 2; i < 5; i++) {
                int child = (parent * 4) + i;
                if (child < heap.length && heap[child] != null && heap[min].compareTo(heap[child]) > 0) {
                        min = child;
                }
            }
        }
        return min;
    }
    
    @Override
    public T peekMin() {
        if (heap[0] == null) {
            throw new EmptyContainerException();
        }
        
        return heap[0];
    }

    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        
        this.length++;
        if (this.size() > this.heap.length) {
            lengthen();
        }
        int childIndex = this.size() - 1;
        heap[childIndex] = item;
        
        boolean percolateUp = true;
        while (percolateUp) {
            int parentIndex = (childIndex - 1)/this.NUM_CHILDREN;
            if (heap[childIndex].compareTo(heap[parentIndex]) < 0) {
                T temp = heap[childIndex];
                heap[childIndex] = heap[parentIndex];
                heap[parentIndex] = temp;
                childIndex = parentIndex;
            }else {
                percolateUp = false;
            }
        }
    }
    
    private void lengthen() {
        T[] temp = this.heap;
        int size = this.heap.length * 2;
        this.heap = this.makeArrayOfT(size);
        for (int i = 0; i < temp.length; i++) {
            this.heap[i] = temp[i];
        }
    }
    
    @Override
    public int size() {
        return this.length;
    }
}
