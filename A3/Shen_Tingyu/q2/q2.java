import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is COMP409 Winter2020 A3
 * Eric Shen, McGill ID is 260798146.
 */

public class q2 {

    // My parameters
    // the number of the threads
    public static int p;
    // upper bound for the random delay
    public static int d;
    // the total number of operations each thread attempts to do
    public static int n;
    // timeout factor to use
    public static int t;
    // size of the elimination array
    public static int e;

    // My final counter of push and successful pop
    volatile static int finalNumberOfPushes = 0;
    volatile static int finalNumberOfSuccessfulPops = 0;
    // Create my stack, type is string
    public static EliminationStack<String> eliminationStack;

    // My helper method for count thread numbers to total numbers
    // Use synchronized to avoid data race
    public static synchronized void count(int numberOfPushes, int numberOfSuccessfulPops) {
        finalNumberOfSuccessfulPops += numberOfSuccessfulPops;
        finalNumberOfPushes += numberOfPushes;
    }

    public static void main(String[] args) {

        try {
            //initialize the parameters of the program
            if (args.length != 5) {
                throw new Exception("Wrong number of arguments, you have " + args.length + " arguments");
            }else {
                p = Integer.parseInt(args[0]);
                d = Integer.parseInt(args[1]);
                n = Integer.parseInt(args[2]);
                t = Integer.parseInt(args[3]);
                e = Integer.parseInt(args[4]);

                eliminationStack = new EliminationStack<>(e, t);

                // I create thread array and start all them
                StackTester<String>[] threads = new StackTester[p];
                for (int i = 0; i < p; i++) {
                    threads[i] = new StackTester<>(n);
                }
                // My timer begins here
                long startTime = System.currentTimeMillis();

                // Start all threads and wait until they end
                for (int i = 0; i < p; i++) {
                    threads[i].start();
                }
                for (int i = 0; i < p; i++) {
                    threads[i].join();
                }

                // My timer ends here
                long endTime = System.currentTimeMillis();

                System.out.println("The whole program lasts " + (endTime - startTime) + " milliseconds");
                System.out.print("The total number of pushes done by all threads, successful pops done by all threads and nodes remaining in the stack, respectively: "
                        + finalNumberOfPushes + " " + finalNumberOfSuccessfulPops + " ");

                //Last to find how many nodes remaining in stack
                int[] stampHolder = new int[1];
                int counter = 0;
                Node<String> top = eliminationStack.top.get(stampHolder);
                Node<String> current = top;

                // If top is null means no remaining
                if (top != null) {
                    // increase the counter by counting the top itself
                    counter++;
                    // move the current node
                    while (current.next != null) {
                        current = current.next;
                        counter++;
                    }
                }

                System.out.println(counter);
            }
        } catch (Exception e) {
            System.out.println("ERROR " + e);
            e.printStackTrace();
        }
    }
}

// My thread class to test stack
class StackTester<T> extends Thread {

    // My parameters
    private LinkedList<Node<T>> linkedList = new LinkedList<>();
    int numberOfAttempts;
    int numberOfPushes = 0;
    int numberOfSuccessfulPops = 0;
    int numberOfFailed = 0;

    public StackTester(int numAttempts) {
        this.numberOfAttempts = numAttempts;
    }

    @Override
    public void run() {
        // I use a while loop to keep track of reaching n
        // Every time thread does an operation, counter will add 1
        int attempts = 0;
        while (attempts < numberOfAttempts) {
            // It has 50% chance to perform a push
            if (ThreadLocalRandom.current().nextDouble() > 0.5) {
                if (linkedList.size() > 0) {
                    // It has another 50% chance to perform a re-push
                    if (ThreadLocalRandom.current().nextDouble() > 0.5) {
                        int position = ThreadLocalRandom.current().nextInt(linkedList.size());
                        Node<T> node = linkedList.get(position);
                        q2.eliminationStack.push((String) node.value);
                    } else {
                        // Or just push a new value, just randomly give it "A"
                        q2.eliminationStack.push("A");
                    }
                } else {
                    // If there is no old values, just push a new value
                    q2.eliminationStack.push("A");
                }
                numberOfPushes++;
                attempts++;
            } else {
                // Another 50% chance is to perform a pop
                try {
                    String poppedValue = q2.eliminationStack.pop();
                    // we have a buffer with size 20 as handout says
                    if (linkedList.size() == 20) {
                        // we first remove the oldest node
                        // and add this newly popped one
                        linkedList.removeLast();
                        Node<T> node = new Node(poppedValue);
                        linkedList.addFirst(node);
                        attempts++;
                        numberOfSuccessfulPops++;
                    } else {
                        Node<T> node = new Node(poppedValue);
                        linkedList.addFirst(node);
                        attempts++;
                        numberOfSuccessfulPops++;
                    }
                } catch (Exception e) {
                    numberOfFailed++;
                    attempts++;
                }
            }
            // Once finish either push or pop, thread will sleep
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(q2.d));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // When loop ends
        // Add this thread number of pushes and successful pops to total number of pushes and successful pops
        q2.count(numberOfPushes, numberOfSuccessfulPops);
    }
}
