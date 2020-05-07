import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicStampedReference;
/**
 * COMP 409 Winter2020 A3
 * Eric Shen, McGill ID is 260798146
 */

// My Lock Free stack class without ABA problem mainly follow the text
public class LockFreeStack<T> {

    // Difference here: I use the stamped reference to solve the ABA problem, and set initial stamp as O
    AtomicStampedReference<Node<T>> top = new AtomicStampedReference<>(null,0);

    // tryPush
    public boolean tryPush(Node<T> node) {

        int[] stamp = new int[1];
        Node<T> oldTop = top.get(stamp);
        node.next = oldTop;
        // New stamp
        return (top.compareAndSet(oldTop, node, stamp[0], stamp[0] + 1));
    }

    public void push(T value) {
        Node<T> node = new Node<>(value);
        while (true) {
            if (tryPush(node)) {
                return;
            }
        }
    }

    public Node<T> tryPop() throws EmptyStackException {
        // using the stamped reference to solve the ABA problem
        int[] stampHolder = new int[1];
        Node<T> oldTop = top.get(stampHolder);
        if (oldTop == null) {
            // Thread need to know Stack is empty
            throw new EmptyStackException();
        }

        Node<T> newTop = oldTop.next;
        if (top.compareAndSet(oldTop, newTop, stampHolder[0], stampHolder[0] + 1)) {
            return oldTop;
        } else {
            return null;
        }
    }

    public T pop() throws EmptyStackException {
        while (true) {
            Node<T> returnNode = tryPop();
            if (returnNode != null) {
                return returnNode.value;
            }
        }
    }
}