/**
 * Following code is for COMP409 A2 Winter2020
 * By Eric Shen, McGill ID 260798146.
 */

// My imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class q2a {
    // Parameters
    public static int n;
    public static int t;
    public static int k;
    public static int m;
    // my point list
    public static List<SpiderPoint> myPoints2 = new ArrayList<>();
    // my spider list
    public static ArrayList<Spider> spiders = new ArrayList<>();

    // I set a flag is a thread reached K, initially set false
    public static boolean reachedK = false;

    // my epsilon value for points
    public static double epsilon = 0.0000;

    // my array to track all spiders' moves
    public static int[] spiderMovesLog;

    public static void main(String[] args) {
        try {
            // We need four command line argument
            if (args.length != 4) {
                throw new Exception("Wrong number of arguments, you have " + args.length + " arguments");
            } else {
                //First argument is the number of random points
                n = Integer.parseInt(args[0]);
                //Second argument is number of threads
                t = Integer.parseInt(args[1]);
                //Third argument is k tolerance
                k = Integer.parseInt(args[2]);
                //Forth argument is simulation time
                m = Integer.parseInt(args[3]);

                spiderMovesLog = new int[t];

                // All from q1
                SpiderPoint corner1 = new SpiderPoint(0,0,1 );
                SpiderPoint corner2 = new SpiderPoint(0,1000, 2);
                SpiderPoint corner3 = new SpiderPoint(1000,0, 3);
                SpiderPoint corner4 = new SpiderPoint(1000,1000, 4);
                myPoints2.add(corner1);
                myPoints2.add(corner2);
                myPoints2.add(corner3);
                myPoints2.add(corner4);
                for(int i =0; i < n; i++){
                    SpiderPoint x = new SpiderPoint(new Random().nextFloat()*1000, new Random().nextFloat()*1000, 5+i);
                    for(int j=0; j < myPoints2.size(); j++){
                        if(myPoints2.get(j).checkSame(x)){
                            x = new SpiderPoint(new Random().nextFloat()*1000, new Random().nextFloat()*1000, 5+i);
                            j = 0;
                        }
                    }
                    myPoints2.add(x);
                }
                myThread2[] myThreads = new myThread2[t];
                for(int i=0; i< t; i++){
                    myThread2 thread = new myThread2();
                    myThreads[i] = thread;
                }
                for (int i=0; i< t;i++){
                    myThreads[i].start();
                }
                for (int i=0; i< t;i++) {
                    myThreads[i].join();
                }

                // q2 stuff begins
                // First get t spiders set on the web
                // Each spider takes 4 points
                for (int i = 0; i < t; i++) {
                    SpiderPoint s1 = randomPick();
                    while (s1.getMyAdjacentPoints().size() < 4) {
                        s1 = randomPick();
                    }
                    int size = s1.getMyAdjacentPoints().size();
                    SpiderPoint s2 = s1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
                    SpiderPoint s3 = s1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
                    while (s2 == s3) {
                        s3 = s1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
                    }
                    SpiderPoint s4 = s1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
                    while (s2 == s4 || s3 == s4) {
                        s4 = s1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
                    }
                    while (s1.isOccupied() || s2.isOccupied() || s3.isOccupied() || s4.isOccupied()) {
                        s1 = randomPick();
                        while (s1.getMyAdjacentPoints().size() < 4) {
                            s1 = randomPick();
                        }
                        size = s1.getMyAdjacentPoints().size();
                        s2 = s1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
                        s3 = s1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
                        while (s2 == s3) {
                            s3 = s1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
                        }
                        s4 = s1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
                        while (s2 == s4 || s3 == s4) {
                            s4 = s1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
                        }
                    }
                    Spider sp = new Spider(s1, s2, s3, s4);
                    spiders.add(sp);
                }

                // My spider threads
                spiderThread[] mySpiderThreads = new spiderThread[t];
                for(int i=0; i< t; i++){
                    // associate them with my spider
                    spiderThread thread = new spiderThread(spiders.get(i),i);
                    mySpiderThreads[i] = thread;
                }
                // Start all the threads
                for (int i=0; i< t;i++){
                    mySpiderThreads[i].start();
                }
                // Pause main thread until all threads are terminated
                for (int i= 0; i< t;i++){
                    mySpiderThreads[i].join();
                    System.out.println("Spider No." + i + " moves " + spiderMovesLog[i] + " space ");
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR " + e);
            e.printStackTrace();
        }
    }
    // my helper method for choose a random point
    public static SpiderPoint randomPick() {
        Random r = new Random();
        int index = r.nextInt(n + 4);
        return myPoints2.get(index);
    }
}

// My spider point class for q2a
class SpiderPoint implements Comparable<SpiderPoint>{
    private float x;
    private float y;
    // to keep track of occupied situation
    private boolean occupied = false;
    private List<SpiderPoint> adjacentPoints = new ArrayList<>();
    private int no;
    ReentrantLock lock = new ReentrantLock();

    public SpiderPoint(float x,float y, int no) {
        this.x = x;
        this.y = y;
        this.no = no;
    }

    // Helper method for creating non-overlap random points
    public boolean checkSame(SpiderPoint pPoint){
       return x==pPoint.x && y==pPoint.y && Math.abs(x-pPoint.x) < q1a.epsilon && Math.abs(y-pPoint.y) < q1a.epsilon;
    }

    public void addAdjacentPoint(SpiderPoint pPoint){
        adjacentPoints.add(pPoint);
    }

    public List<SpiderPoint> getMyAdjacentPoints(){
        return adjacentPoints;
    }

    public boolean isOccupied(){
        return occupied;
    }

    public void setOccupied(boolean x){
        occupied = x;
    }
    public int getNo(){
        return no;
    }

    // Because I have to sort my points this time, I implement comparable
    @Override
    public int compareTo(SpiderPoint o) {
        SpiderPoint sp = (SpiderPoint) o;
        return this.no - sp.no;
    }
}

// my thread class for q2a for creating points
// all same as the first one
class myThread2 extends Thread
{
    private int k = 0;
    public myThread2(){ }
    @Override
    public void run() {
        while( k < q2a.k && !q2a.reachedK){
            k = triangulateRegion(k);
        }
        q2a.reachedK = true;
    }
    public int triangulateRegion(int k){
        // Every time a thread randomly picks two points from the set, and ensures that they are not the same points
        SpiderPoint a = q2a.myPoints2.get(ThreadLocalRandom.current().nextInt(q2a.myPoints2.size()));
        SpiderPoint b = q2a.myPoints2.get(ThreadLocalRandom.current().nextInt(q2a.myPoints2.size()));
        while(b == a){
            b = q2a.myPoints2.get(ThreadLocalRandom.current().nextInt(q2a.myPoints2.size()));
        }
        if(a.getNo()<b.getNo()){
            synchronized (a){
                synchronized (b){
                    if(a.getMyAdjacentPoints().contains(b)){
                        k++;
                    }
                    else {
                        a.addAdjacentPoint(b);
                        b.addAdjacentPoint(a);
                    }
                }
            }
            return k;
        }
        else {
            synchronized (b){
                synchronized (a){
                    if(a.getMyAdjacentPoints().contains(b)){
                        k++;
                    }
                    else {
                        a.addAdjacentPoint(b);
                        b.addAdjacentPoint(a);
                    }
                }
            }
            return k;
        }
    }
}

// My helper spider class with spider data structure
class Spider {
    SpiderPoint body;
    SpiderPoint leg1;
    SpiderPoint leg2;
    SpiderPoint leg3;

    public Spider(SpiderPoint a, SpiderPoint b, SpiderPoint c, SpiderPoint d) {
        this.body = a;
        this.leg1 = b;
        this.leg2 = c;
        this.leg3 = d;
        body.setOccupied(true);
        leg1.setOccupied(true);
        leg2.setOccupied(true);
        leg3.setOccupied(true);
    }
}

// my spider threads
class spiderThread extends Thread {
    // keep tack of which spider and how many moves
    int spiderNo;
    int spiderMove = 0;
    Spider spider;

    spiderThread(Spider s, int spiderNo) {
        this.spider = s;
        this.spiderNo = spiderNo;
    }

    // My helper jump method, if it can jump, returns true then jump
    // If it can't, return false, just like trylock()
    public boolean jump(Spider s, SpiderPoint s1, SpiderPoint s2, SpiderPoint s3, SpiderPoint s4) {

        // if non of this four points is already be occupied
        if (!s1.isOccupied() && !s2.isOccupied() && !s3.isOccupied() && !s4.isOccupied()) {
            // test if someone else is faster than us to acquire lock
            if (!(s1.lock.tryLock() && s2.lock.tryLock() && s3.lock.tryLock() && s4.lock.tryLock())) {
                try {
                    s1.lock.unlock();
                    s2.lock.unlock();
                    s3.lock.unlock();
                    s4.lock.unlock();
                } catch (IllegalMonitorStateException e) {
                    return false;
                }
                // Then it fails
            } else {
                try {
                    // Spider tries to jump
                    s.body = s1;
                    s.leg1 = s2;
                    s.leg2 = s3;
                    s.leg3 = s4;

                    s1.setOccupied(true);
                    s2.setOccupied(true);
                    s3.setOccupied(true);
                    s4.setOccupied(true);

                    // release the locks
                    s1.lock.unlock();
                    s2.lock.unlock();
                    s3.lock.unlock();
                    s4.lock.unlock();

                    SpiderPoint first;
                    SpiderPoint second;
                    SpiderPoint third;
                    SpiderPoint fourth;

                    // I use an array list to keep all its parts
                    ArrayList<SpiderPoint> wholePart = new ArrayList<>();
                    wholePart.add(s.body);
                    wholePart.add(s.leg1);
                    wholePart.add(s.leg2);
                    wholePart.add(s.leg3);

                    // Then I sort them from small to large
                    Collections.sort(wholePart);
                    // reOder the point to acquire
                    first = wholePart.get(0);
                    second = wholePart.get(1);
                    third = wholePart.get(2);
                    fourth = wholePart.get(3);

                    // Then I let spider acquires the lock based on the ordering
                    // Prevent deadlock
                    first.lock.lock();
                    second.lock.lock();
                    third.lock.lock();
                    fourth.lock.lock();

                    s.body.setOccupied(false);
                    s.leg1.setOccupied(false);
                    s.leg2.setOccupied(false);
                    s.leg3.setOccupied(false);

                    s.body.lock.unlock();
                    s.leg1.lock.unlock();
                    s.leg2.lock.unlock();
                    s.leg3.lock.unlock();
                    return true;
                } catch (IllegalMonitorStateException e) {
                }
            }
        }
        // Then spider fails
        return false;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < q2a.m * 1000) {
            // Spider choose a point to fit the entire body and legs first
            int index = ThreadLocalRandom.current().nextInt(q2a.n + 4);
            SpiderPoint spiderPoint1 = q2a.myPoints2.get(index);
            while (spiderPoint1.getMyAdjacentPoints().size() < 4) {
                int index1 = ThreadLocalRandom.current().nextInt(q2a.n + 4);
                spiderPoint1 = q2a.myPoints2.get(index);
            }
            // after that it choose available points for legs, jump if there is one possible move
            int size = spiderPoint1.getMyAdjacentPoints().size();
            SpiderPoint spiderPoint2 = spiderPoint1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
            SpiderPoint spiderPoint3 = spiderPoint1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
            while (spiderPoint2 == spiderPoint3) {
                spiderPoint3 = spiderPoint1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
            }
            SpiderPoint spiderPoint4 = spiderPoint1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
            while (spiderPoint2 == spiderPoint4 || spiderPoint3 == spiderPoint4) {
                spiderPoint4 = spiderPoint1.getMyAdjacentPoints().get(ThreadLocalRandom.current().nextInt(size));
            }
            // If spider can jump, it jumps
            boolean isSuccessful = jump(spider, spiderPoint1, spiderPoint2, spiderPoint3, spiderPoint4);
            if (isSuccessful) {
                spiderMove++;
            }
            // once it finished, either success or fail, spider sleeps 40-50ms
            int sleepTime = ThreadLocalRandom.current().nextInt(10) + 40;
            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // before spider thread finished, it logs all its moves to my static move keeper
        q2a.spiderMovesLog[spiderNo] = spiderMove;
    }
}