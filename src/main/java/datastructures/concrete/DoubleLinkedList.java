package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        if (this.front == null) {
            this.front = new Node<T>(item);
            this.back = this.front;

        }else {
            this.back.next = new Node<T>(this.back, item, null);
            this.back = this.back.next;
        }
        this.size++;
    }

    @Override
    public T remove() {
        if (this.front == null) {
            throw new EmptyContainerException();
        }
        if (this.size() == 1) {
            Node<T> removed = this.front;
            this.front = null;
            this.back = null;
            this.size--;
            return removed.data;
            
        }else {
            Node<T> removed = this.back;
            this.back = this.back.prev;
            this.size--;
            return removed.data;
        }
        
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> curr = this.front;
        int i = 0;
        while (curr != null) {
            if (i == index) {
                return curr.data;
            }
            curr = curr.next;
            i++;
        }
        return null;
    }
    
    @Override
    public void set(int index, T item) {
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }
        
        Node<T> curr = this.front;
        int i = 0;
        while (curr != null) {
            if (i == index) {
                Node<T> replacement = new Node<T>(curr.prev, item, curr.next);
                if (curr.prev != null) {
                    curr.prev.next = replacement;
                }
                
                if (curr.next != null) {
                    curr.next.prev = replacement;
                }
                
                if (i == 0) {
                    this.front = replacement;
                }
                
                if (i == this.size() - 1) {
                    this.back = replacement;
                }
            }
            curr = curr.next;
            i++;
        }
        
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || index >= this.size() + 1) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            if (this.front == null) {
                this.front = new Node<T>(item);
                this.back = this.front;
            }else {
                Node<T> insertion = new Node<T>(null, item, this.front);
                this.front = insertion;
                if (this.front.next != null) {
                    this.front.next.prev = insertion;
                }
            }
            this.size++;
        }else if (index == this.size()) {
            Node<T> insertion = new Node<T>(this.back, item, null);
            this.back = insertion;
            if (this.back.prev != null) {
                this.back.prev.next = insertion;
            }
            this.size++;
        }else {
            if (index < this.size()/2) {
                Node<T> curr = this.front;
                int i = 0;
                while (curr != null) {
                    if (i == index) {
                        Node<T> insertion = new Node<T>(curr.prev, item, curr);
                        curr.prev.next = insertion;
                        curr.prev = insertion;
                        this.size++;
                        return;
                    }
                    curr = curr.next;
                    i++;
                }
            }else {
                index = this.size() - index - 1;
                Node<T> curr = this.back;
                int i = 0;
                while (curr != null) {
                    if (i == index) {
                        Node<T> insertion = new Node<T>(curr.prev, item, curr);
                        curr.prev.next = insertion;
                        curr.prev = insertion;
                        this.size++;
                        return;
                    }
                    curr = curr.prev;
                    i++;
                }
            }
        }
    }

    @Override
    public T delete(int index) {
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (this.size() == 1){
            Node<T> curr = this.front;
            this.front = null;
            this.back = null;
            curr.prev = null;
            curr.next = null;
            this.size--;
            return curr.data;
        }else if (index == 0) {
            Node<T> curr = this.front;
            this.front = curr.next;
            this.front.prev = null;
            curr.prev = null;
            curr.next = null;
            this.size--;
            return curr.data;
        }else if (index == this.size()-1) {
            Node<T> curr = this.back;
            this.back = curr.prev;
            this.back.next = null;
            curr.prev = null;
            curr.next = null;
            this.size--;
            return curr.data;
        }else if (index < this.size()/2) {
            Node<T> curr = this.front;
            int i = 0;
            while (curr != null) {
                if (i == index) {
                    if (curr.prev != null) {
                        curr.prev.next = curr.next;
                    }
                    
                    if (curr.next != null) {
                        curr.next.prev = curr.prev;
                    }
                    curr.prev = null;
                    curr.next = null;
                    this.size--;
                    return curr.data;
                }
                curr = curr.next;
                i++;
            }
        }else {
            Node<T> curr = this.back;
            index = this.size() - index - 1;
            int i = 0;
            while (curr != null) {
                if (i == index) {
                    if (curr.prev != null) {
                        curr.prev.next = curr.next;
                    }
                    
                    if (curr.next != null) {
                        curr.next.prev = curr.prev;
                    }
                    curr.prev = null;
                    curr.next = null;
                    this.size--;
                    return curr.data;
                }
                curr = curr.prev;
                i++;
            }
        }
        
        return null;
    }

    @Override
    public int indexOf(T item) {
        Node<T> curr = this.front;
        int i = 0;
        while (curr != null) {
            if (curr.data == null ? item == null : curr.data.equals(item)) {
                return i;
            }
            curr = curr.next;
            i++;
        }
        return -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(T other) {
        Node<T> curr = this.front;
        while (curr != null) {
            if (curr.data == null ? other == null : curr.data.equals(other)) {
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return this.current != null;
            
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }else {
                T data = current.data;
                current = current.next;
                return data;
            }
        }
    }
}
