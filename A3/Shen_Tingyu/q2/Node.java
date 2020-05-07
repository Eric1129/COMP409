/**
 * COMP 409 Winter2020 A3
 * Eric Shen, McGill ID is 260798146
 */

// My helper Node class
public class Node<T> {

    public T value;
    public volatile Node<T> next;

    public Node(T value) {
        this.value = value;
        this.next = null;
    }
}