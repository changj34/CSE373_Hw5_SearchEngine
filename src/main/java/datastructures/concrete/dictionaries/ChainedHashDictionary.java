package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    private int size = 0;

    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashDictionary() {
        this.chains = makeArrayOfChains(11);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int length) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[length];
    }

    //basic hash of key % size
    private int hash(K key, int length) {
        if (key == null) {
            return 0;
        }
        return Math.abs(key.hashCode() % length);
    }
    
    private boolean shouldRehash() {
        return (((double) this.size()) / this.chains.length) > 0.75;
    }
    
    @Override
    public V get(K key) {
        if (this.containsKey(key)) {
            return this.chains[hash(key, this.chains.length)].get(key);
        } else {
            throw new NoSuchKeyException();
        }
    }
    
    @Override
    public void put(K key, V value) {
        if (this.shouldRehash()) {
            Iterator<KVPair<K, V>> iter = this.iterator();
            this.chains = makeArrayOfChains(this.chains.length * 2);
            while (iter.hasNext()) {
                KVPair<K, V> pair = iter.next();
                int hash = hash(pair.getKey(), this.chains.length);
                if (this.chains[hash] == null) {
                    this.chains[hash] = new ArrayDictionary<K, V>();
                }
                this.chains[hash].put(pair.getKey(), pair.getValue());
            }
        }
        if (!this.containsKey(key)) {
            this.size++;
        }
        int hash = hash(key, this.chains.length);
        if (this.chains[hash] == null) {
            this.chains[hash] = new ArrayDictionary<K, V>();
        }
        this.chains[hash].put(key, value);
    }

    @Override
    public V remove(K key) {
        if (this.containsKey(key)) {
            this.size--;
            return this.chains[hash(key, this.chains.length)].remove(key);
        }else {
            throw new NoSuchKeyException();
        }
    }

    @Override
    public boolean containsKey(K key) {
        IDictionary<K, V> chain = this.chains[hash(key, this.chains.length)];
        if (chain != null 
                && chain.containsKey(key)) {
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int current = 0;
        private Iterator<KVPair<K, V>> iterator;
        
        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
        }

        private Iterator<KVPair<K, V>> findNextIterator(){
            for (int i = this.current; i < this.chains.length; i++) {
                if (this.chains[i]!= null) {
                    this.current = i + 1;
                    return this.chains[i].iterator();   
                }
            }
            return null;
        }
        
        public boolean hasNext() {
            if (this.iterator == null) {
                this.iterator = this.findNextIterator();
                if (this.iterator == null) {
                    return false;
                }
            }
            
            if (this.iterator.hasNext()) {
                return true;
            }else {
                this.iterator = null;
                return hasNext();
            }
        }

        @Override
        public KVPair<K, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }else {
                return this.iterator.next();
            }
        }
    }
}
