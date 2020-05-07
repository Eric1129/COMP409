import java.util.EmptyStackException;
import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This is for COMP409 Winter2020 A3.
 * Eric Shen, McGill ID is for 260798146
 */

// My EliminationStack mainly follow the text
public class EliminationStack<T> extends LockFreeStack<T> {
    public static int capacity;
    EliminationArray<T> eliminationArray;

    public EliminationStack(int capacity, int timeout) {
        this.capacity = capacity;
        eliminationArray = new EliminationArray<>(capacity, timeout);
    }

    @Override
    public void push(T value) {
        Node<T> node = new Node<T>(value);

        while (true) {
            if (this.tryPush(node)) {
                return;
            } else try {
                T otherValue = this.eliminationArray.visit(value);
                if (otherValue == null) {
                    // Value is successfully exchanged
//                        System.out.println("Value changed!");
                    return;
                }
            } catch (TimeoutException e) {
                // Timeout occurs
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public T pop() throws EmptyStackException {
        while (true) {
            Node<T> returnNode = tryPop();
            if (returnNode != null) {
                returnNode.next = null;
                return returnNode.value;
            } else try{
                T otherValue = this.eliminationArray.visit(null);
                if (otherValue != null) {
                    return otherValue;
                }
            } catch (TimeoutException e) {
                // Timeout occurs
            } catch (InterruptedException e) {
                    e.printStackTrace();
            }
        }
    }
}

// My Elimination Array class, follow the text book
class EliminationArray<T> {

    // My attributes
    private int timeout;
    Exchanger<T>[] exchangers;
    Random random;

    int capacity;

    // Difference here is that timeout and capacity value is fixed as parameter
    public EliminationArray(int capacity, int timeout) {

        this.timeout = timeout;
        this.capacity = capacity;
        random = new Random();
        exchangers = (Exchanger<T>[]) new Exchanger[capacity];

        for (int i = 0; i < capacity; i++) {
            exchangers[i] = new Exchanger<T>();
        }
    }

    public T visit(T value) throws TimeoutException, InterruptedException {
        int slot = random.nextInt(this.capacity);
        return (exchangers[slot].exchange(value, timeout, TimeUnit.MILLISECONDS));
    }
}


