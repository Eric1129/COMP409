// Eric Shen
// 260798146

// This thread2 class is to count nodes
public class Thread2 extends Thread {

    // Set a global counter
    int counter;

    public Thread2(){ }

    @Override
    public void run() {
        // I set start timer
        long startTime = System.currentTimeMillis();

        // Using while loop to keep record 5 seconds, since I run whole session
        while (System.currentTimeMillis() - startTime < 5000) {

            // Use my helper method, every time use DFS from root
            DFS(q2.root);
            System.out.println(" Counter is " + counter);
            // After finish one counting, sleep 200 milliseconds
            try {
                Thread.sleep(200);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            // Reset the counter
            counter = 0;
        }
    }

    // This is my helper method DFS to count nodes, using recursion
    // Move a node sleep 10 milliseconds
    public void DFS(TreeNode current){
        counter++;
        if(current.aLeftChild != null){
            try {
                Thread.sleep(10);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            DFS(current.aLeftChild);
        }
        if(current.aRightChild != null){
            try {
                Thread.sleep(10);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            DFS(current.aRightChild);
        }
    }
}
