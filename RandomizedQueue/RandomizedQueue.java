import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private int n = 0;
    private Item[] a;

    public RandomizedQueue()                 // construct an empty randomized queue
    {
        a = (Item[]) new Object[2];
        n = 0;
    }

    public boolean isEmpty()                 // is the queue empty?
    {
        return n == 0;
    }

    public int size()                        // return the number of items on the queue
    {
        return n;
    }

    // resize the underlying array holding the elements
    private void resize(int capacity) {
        // textbook implementation
        Item[] temp = (Item[]) new Object[capacity];
        for (int i = 0; i < n; i++) {
            temp[i] = a[i];
        }
        a = temp;

        // alternative implementation
        // a = java.util.Arrays.copyOf(a, capacity);
    }

    public void enqueue(Item item)           // add the item
    {
        if (null == item)
            throw new NullPointerException();

        if (n == a.length) resize(2*a.length);
        a[n++] = item;
    }

    public Item dequeue()                    // remove and return a random item
    {
        if (n == 0)
            throw new NoSuchElementException();

        // Get a random item
        int i = StdRandom.uniform(n);
        Item item = a[i];

        // Since we're removing it -
        // replace the empty slot with
        // the last item in our list
        // and check if we need to resize
        // the container
        a[i] = a[n - 1];
        a[--n] = null;

        if (n > 0 && n == a.length/4) resize(a.length/2);
        return item;
    }

    public Item sample()                     // return (but do not remove) a random item
    {
        if (n == 0)
            throw new NoSuchElementException();
        // Just peek at a  random element
        return a[StdRandom.uniform(n)];
    }

    public Iterator<Item> iterator()         // return an independent iterator over items in random order
    {
        return new RandomizedQueueIterator();
    }

    // an iterator, doesn't implement remove() since it's optional
    private class RandomizedQueueIterator implements Iterator<Item> {
        private int index;
        private Item[] current;

        public RandomizedQueueIterator() {
            // Create a copy of the holding container
            current = (Item[]) new Object[n];
            for (int i = 0; i < n; i++) {
                current[i] = a[i];
            }
            // And shuffle it, so it's always randomized
            StdRandom.shuffle(current);
            index = 0;
        }

        public boolean hasNext() {
            return index != n;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return current[index++];
        }
    }

    public static void main(String[] args)   // unit testing
    {
        RandomizedQueue<String> queue = new RandomizedQueue<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            queue.enqueue(item);
        }

        StdOut.println("Iterating: ");
        for (String item : queue) {
            StdOut.println(item);
        }
        StdOut.println("(" + queue.size() + " left on deque)");

    }
}
