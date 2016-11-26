import edu.princeton.cs.algs4.StdOut;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private int n = 0;          // size of the deque
    private Node first = null;     // top of deque
    private Node last = null;     // bottom of deque

    // helper linked list class
    private class Node {
        private Item item;
        private Node next;
        private Node prev;
    }

    public Deque() {                           // construct an empty deque
    }

    public boolean isEmpty()                 // is the deque empty?
    {
        return n == 0;
    }

    public int size()                        // return the number of items on the deque
    {
        return n;
    }

    public void addFirst(Item item)          // add the item to the front
    {
        if (null == item)
            throw new NullPointerException();

        // First setup a new node
        Node node = new Node();
        node.item = item;
        node.prev = null;
        node.next = first;

        // Current node .prev should point to the newly created one
        if (null != first)
            first.prev = node;

        // Switch to the new node
        first = node;

        // If it's a first element then we need to assign "last" as well
        if (null == last)
            last = first;

        ++n;
    }

    public void addLast(Item item)           // add the item to the end
    {
        if (null == item)
            throw new NullPointerException();

        // First setup a new node
        Node node = new Node();
        node.item = item;
        node.prev = last;
        node.next = null;

        // Current node .next should point to the newly created one
        if (null != last)
            last.next = node;

        // Switch to the new node
        last = node;

        // If it's a first element then we need to assign "first" as well
        if (null == first)
            first = last;

        ++n;
    }

    public Item removeFirst()                // remove and return the item from the front
    {
        if (null == first)
            throw new NoSuchElementException();

        Node node = first;
        first = first.next;

        // Remove the referencing from a neighboring object
        if (first != null)
            first.prev = null;

        // If there was just one element in the list - nullify both last and first
        if (node == last)
            last = null;

        --n;
        return node.item;
    }

    public Item removeLast()                 // remove and return the item from the end
    {
        if (null == last)
            throw new NoSuchElementException();

        Node node = last;
        last = last.prev;

        // Remove the referencing from a neighboring object
        if (last != null)
            last.next = null;

        // If there was just one element in the list - nullify both last and first
        if (node == first)
            first = null;

        --n;
        return node.item;
    }

    public Iterator<Item> iterator()         // return an iterator over items in order from front to end
    {
        return new DequeIterator();
    }

    // an iterator, doesn't implement remove() since it's optional
    private class DequeIterator implements Iterator<Item> {
        private Node current = first;
        public boolean hasNext()  { return current != null;                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    public static void main(String[] args)   // unit testing
    {
        Deque<Integer> deque = new Deque<Integer>();

        deque.addLast(0);
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);
        StdOut.println(deque.removeFirst());
        deque.addLast(5);
        deque.addLast(6);
        deque.addLast(7);
        StdOut.println(deque.removeFirst());

        StdOut.println("(" + deque.size() + " left on deque)");
    }
}
