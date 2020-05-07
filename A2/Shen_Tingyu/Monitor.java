import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * COMP409 Winter2020n A2
 * Eric Shen 260798146
 */

public abstract class Monitor {

    LinkedList<Thread> mainQueue;
    LinkedList<Thread> conditionQueue;
    Boolean flag;
    Condition x;
    ReentrantLock lock;

    public Monitor(){
        flag = false;
        lock = new ReentrantLock();
    }

    void enter(){

    }

    void exit(){

    }

    void await() throws InterruptedException
    {

    }

    void signal(){

    }
}
