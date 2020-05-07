/**
 * COMP409 Winter2020 A2
 * Eric Shen, McGill ID 260798146
 */
public class MonitorSW extends Monitor {

    @Override
    void enter(){
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
        Thread x = mainQueue.remove();
        x.interrupt();
    }

    @Override
    void await() throws InterruptedException {
        conditionQueue.add(Thread.currentThread());
        lock.unlock();
        wait();
    }

    @Override
    void signal(){
        if(conditionQueue.isEmpty()){

        }
        else {
            Thread x = conditionQueue.remove();
            x.interrupt();
            lock.unlock();
        }

    }

}
