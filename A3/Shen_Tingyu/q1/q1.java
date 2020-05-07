import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is for COMP409 Winter2020 A3.
 * Eric Shen, McGill ID 260798146.
 */

public class q1 {

    // Parameters: String Length, number of Threads and optional random-seed parameter.
    public static int n;
    public static int t;
    public static long s;

    public static char[] myString;
    public static ConcurrentHashMap myFinalTriple;

    public static void main(String[] args) {

        try {
            if (args.length != 3) {
                throw new Exception("Wrong number of arguments, you have " + args.length + " arguments");
            } else {
                //First argument is for String Length
                n = Integer.parseInt(args[0]);
                //Second argument is for number of Threads
                t = Integer.parseInt(args[1]);
                //Third argument is the optional random-seed parameter.
                s = Integer.parseInt(args[2]);

                // I construct char array here
                myString = Bracket.construct(n,s);
                // I create thread pool as handout asks
                ExecutorService executorService = Executors.newFixedThreadPool(t);

                // If thread number is greater than string lengh, the extra threads will always be idle
                if(t>n){
                    t = n;
                }

                // I divide tasks to every thread, if not divided evenly, I add rest from the beginning
                int taskNumber = myString.length/t;
                int rest = myString.length - taskNumber*t;

                // Thus all thread will have a final triple value, I store it at a concurrent hashmap
                myFinalTriple = new ConcurrentHashMap();

                // My timer starts here
                long startTime = System.currentTimeMillis();
                int startPosition = 0;

                // I start thread one by one by using the pool
                // So thread 0 will start at position 0
                for (int i = 0; i < t; i++){
                    int finalStartPosition = startPosition;
                    int finalI = i;

                    // As explained above the first thread will pick up one more task until no more task
                    if(i < rest) {
                        taskNumber++;
                    }
                    int finalTaskNumber = taskNumber;
                    // Then I submit task
                    executorService.submit(new Runnable() {
                        Triple x = null;
                        Triple y = null;

                        @Override
                        public void run() {
                            // It starts from startpoint to convert char to a tuple and merge to final answer for this thread
                            for (int j = 0; j < finalTaskNumber; j++) {
                                if (q1.myString[finalStartPosition + j] == '[') {
                                    y = new Triple(false, 1, 1);
                                }
                                if (q1.myString[finalStartPosition + j] == '*') {
                                    y = new Triple(true, 0, 0);
                                }
                                if (q1.myString[finalStartPosition + j] == ']') {
                                    y = new Triple(false, -1, -1);
                                }
                                // If x is null means it's at first char, so just simply add it
                                if (x == null) {
                                    x = y;
                                } else {
                                    // This is the algorithm from handout
                                    boolean newOk = ((x.ok && y.ok) || (((x.f + y.f) == 0) && (x.m >= 0) && ((x.f + y.m) >= 0)));
                                    int newF = x.f + y.f;
                                    int newM = Math.min(x.m, x.f + y.m);
                                    Triple z = new Triple(newOk, newF, newM);
                                    x = z;
                                }
                            }
                            // Once loop is over, add final tuple an the correct position as thread maps
                            myFinalTriple.put(finalI, x);
                        }
                    });
                    // This is for correcting task number and start position
                    if(i < rest) {
                        taskNumber--;
                        startPosition = startPosition + taskNumber + 1;
                    }
                    else {
                        startPosition = startPosition + taskNumber;
                    }
                }

                // Finally I shut down the whole pool and wait for all threads terminated
                executorService.shutdown();
                while (!executorService.isTerminated());

                // Right now just a small array which can be easily merged
                // I merge all tuples into a final tuple
                Triple x = null;
                for(int i =0; i<t; i++){
                    if(x == null){
                        x = (Triple) myFinalTriple.get(i);
                    }
                    else {
                        Triple y = (Triple) myFinalTriple.get(i);
                        boolean newOk = ( (x.ok&&y.ok) || (((x.f+y.f) == 0)&&(x.m >= 0)&&((x.f+y.m)>=0)) );
                        int newF = x.f + y.f;
                        int newM = Math.min(x.m,x.f+y.m);
                        Triple z = new Triple(newOk,newF,newM);
                        x = z;
                    }
                }

                // My timer ends here
                long endTime = System.currentTimeMillis();

                System.out.println("It costs " + (endTime - startTime) + " milliseconds");
                System.out.println( x.ok + " " + Bracket.verify());
            }
        } catch (Exception e) {
            System.out.println("ERROR " + e);
            e.printStackTrace();
        }
    }
}

// This is my helper tuple class
class Triple{
    boolean ok;
    int f;
    int m;

    Triple(boolean ok, int f, int m){
        this.ok = ok;
        this.f = f;
        this.m = m;
    }
}