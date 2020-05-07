// Eric Shen
// 260798146

// This is my thread class for Thread0, which prints leaves
public class Thread0 extends Thread{

    boolean notFinish = true;
    public Thread0() {
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        TreeNode current = q2.head;

        // I use a while loop to keep track of 5 milliseconds
        // and also last time if not finished need to wait until finished
        while (System.currentTimeMillis() - startTime < 5000 || notFinish) {
            // If it starts a new loop, means it's not finished
            notFinish = true;
            if(current == q2.head){
                System.out.print("*" + current.aName + " ");
                try {
                    Thread.sleep(50);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                // Have to consider conflict with thread1 situation
                if (current.nextLeaf == null){
                    current = current.aRightChild;
                }
                else {
                    current = current.nextLeaf;
                }
            }
            else if(current == q2.tail){
                try {
                    System.out.println(current.aName + " ");
                    Thread.sleep(200);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                System.out.println();
                current = q2.head;
                notFinish = false;  // After printing tail, the whole traversal finish
            }
            else {
                // This is for nodes except head and tail
                System.out.print(current.aName + " ");
                try {
                    Thread.sleep(50);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                // Have to consider conflict with thread1 situation
                if(current.nextLeaf == null){
                    current = current.aRightChild;
                }
                else {
                    current = current.nextLeaf;
                }
            }
        }
    }
}
