/**
 *  COMP409 Winter2020
 *  Eric Shen, McGill ID 260798146
 */

// My Signal and Continue subclass
public class MonitorSC extends Monitor {

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
    void exit(){
        if(mainQueue.isEmpty()){
            lock.unlock();
        }
        Thread x = mainQueue.getFirst();
        x.interrupt();
    }

    @Override
    void await() throws InterruptedException
    {
        conditionQueue.add(Thread.currentThread());
        Thread x = mainQueue.remove();
        x.interrupt();
        lock.unlock();
        wait();
    }

    @Override
    public void signal(){
        mainQueue.add(conditionQueue.remove());
    }
}
