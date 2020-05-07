/**
 * Following code is for COMP409 A2 Winter2020
 * By Eric Shen, McGill ID 260798146.
 */

// My imports

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class q1a {
    // Parameters
    public static int n;
    public static int t;
    public static int k;
    // My synchronized arraylist to store all points
    public static List<Point> myPoints = new ArrayList<>();
    // I set a flag to track there is a thread reached K, initially set false
    public static boolean reachedK = false;
    // my epsilon value for points
    public static double epsilon = 0.0000;

    public static void  main(String[] args)
    {
        try {
            // We need three command line argument
            if (args.length != 3) {
                throw new Exception("Wrong number of arguments, you have " + args.length + " arguments");
            } else {
                //First argument is the number of random points
                n = Integer.parseInt(args[0]);
                //Second argument is the number of threads
                t = Integer.parseInt(args[1]);
                //Third argument is the failure tolerance of each thread
                k = Integer.parseInt(args[2]);

                // I make my corner points, and add them to my points set
                Point corner1 = new Point(0,0, 1);
                Point corner2 = new Point(0,1000, 2);
                Point corner3 = new Point(1000,0, 3);
                Point corner4 = new Point(1000,1000, 4);

                myPoints.add(corner1);
                myPoints.add(corner2);
                myPoints.add(corner3);
                myPoints.add(corner4);

                // Then create n random non-overlap points inside the region
                for(int i =0; i < n; i++){
                    Point x = new Point(new Random().nextFloat()*1000, new Random().nextFloat()*1000, i+5);
                    // Once there is a same point, just create another point then loop again
                    for(int j=0; j < myPoints.size(); j++){
                        if(myPoints.get(j).checkSame(x)){
                            x = new Point(new Random().nextFloat()*1000, new Random().nextFloat()*1000, 1+5);
                            j = 0;
                        }
                    }
                    myPoints.add(x);
                }
                // Initialize a thread array to keep all threads
                myThread[] myThreads = new myThread[t];
                for(int i=0; i<t; i++){
                    myThread thread = new myThread();
                    myThreads[i] = thread;
                }

                // Start all the threads
                for (int i=0; i< t;i++){
                    myThreads[i].start();
                }

                for (int i=0; i< t;i++) {
                    myThreads[i].join();
                }

                // I use a counter to count total number of edges
                // Since every edge link 2 adjacent points,the number should be 1/2 of total number of total adjacent points
                int counter = 0;
                for (int i=0; i < myPoints.size(); i++){
                    for (int j=0; j < myPoints.get(i).getMyAdjacentPoints().size(); j++){
                        counter++;
                    }
                }
                counter = counter/2;
                System.out.println("Total number of edges that were successfully added: " + counter);
            }
        } catch (Exception e) {
            System.out.println("ERROR " + e);
            e.printStackTrace();
        }
    }
}

// My Point class to store coordinates
class Point{
    private float x;
    private float y;
    private int no;
    private List<Point> adjacentPoints = new ArrayList<>();

    public Point(float x,float y, int no) {
        this.x = x;
        this.y = y;
        this.no = no;
    }

    // Helper method for creating non-overlap random points
    public boolean checkSame(Point pPoint){
        return x==pPoint.x && y==pPoint.y && Math.abs(x-pPoint.x) < q1a.epsilon && Math.abs(y-pPoint.y) < q1a.epsilon;
    }

    public void addAdjacentPoint(Point pPoint){
        adjacentPoints.add(pPoint);
    }

    public List<Point> getMyAdjacentPoints(){
        return adjacentPoints;
    }

    public int getNo(){
        return no;
    }
}

// my thread class
class myThread extends Thread
{
    private int k = 0;
    public myThread(){ }
    @Override
    public void run() {
        // I create a while loop to track if it reaches k or there already is a thread reached k
        while( k < q1a.k && !q1a.reachedK){
            k = triangulateRegion(k);
        }
        // Once a thread reached K, my flag thus set to true
        q1a.reachedK = true;
    }
    // My helper synchronized method for adding edge between random pair of points
    // which also updates k value
    public int triangulateRegion(int k){
        // Every time a thread randomly picks two points from the set, and ensures that they are not the same points
        Point a = q1a.myPoints.get(ThreadLocalRandom.current().nextInt(q1a.myPoints.size()));
        Point b = q1a.myPoints.get(ThreadLocalRandom.current().nextInt(q1a.myPoints.size()));
        while(b == a){
            b = q1a.myPoints.get(ThreadLocalRandom.current().nextInt(q1a.myPoints.size()));
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