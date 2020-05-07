import java.util.LinkedList;

/**
 * COMP409 2020Winter A2
 * Eric Shen, McGill ID 260798146
 */
public class MonitorSUW extends Monitor {

    // a new linkedlist which only store the one who gives lock
    LinkedList<Thread> s = new LinkedList<>();

    @Override
    public void enter(){
        if(lock.tryLock()){

        }
        else {
            try {
                mainQueue.add(Thread.currentThread());
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void exit(){
        if(s.isEmpty()){
            if(mainQueue.isEmpty()){
                lock.unlock();
            }
            else {
                Thread y = mainQueue.remove();
                y.interrupt();
                lock.unlock();
            }
        }
        else {
            Thread x = s.remove();
            x.interrupt();
            lock.unlock();
        }
    }

    @Override
    public synchronized void await() throws InterruptedException
    {
        conditionQueue.add(Thread.currentThread());
        Thread x = mainQueue.remove();
        x.interrupt();
        lock.unlock();
        wait();
    }

    @Override
    public synchronized void signal(){
        Thread x = mainQueue.remove();
        x.interrupt();
        mainQueue.add(conditionQueue.remove());
        notify();
    }

}
