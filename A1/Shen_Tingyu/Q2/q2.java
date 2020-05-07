// Eric Shen
// 260798146

// This is the main program where to run 3 threads

import java.util.Random;

public class q2 {
    // I set global variables for threads use, keep them volatile to prevent conflict
    static volatile TreeNode root;
    static volatile TreeNode head;
    static volatile TreeNode tail;
    // I set a global counter for final count
    static int counter = 0;
    StringBuilder str = new StringBuilder();

    public static void main(String[] args) throws Exception{
        // Create the root with a random name and let all other values are
        root = new TreeNode(randomString(), null);
        // Generate a new random name for root's children
        String childName = randomString();
        TreeNode firstLeftChild = new TreeNode(childName, root);
        TreeNode firstRightChild = new TreeNode(childName.toUpperCase(), root);
        // Construct initial tree
        root.aLeftChild = firstLeftChild;
        root.aRightChild = firstRightChild;
        head = firstLeftChild;
        tail = firstRightChild;
        firstLeftChild.nextLeaf = firstRightChild;

        // Initialize 3 threads
        Thread0 thread0 = new Thread0();
        Thread1 thread1 = new Thread1();
        Thread2 thread2 = new Thread2();

        // Start to run 3 threads concurrently
        thread0.start();
        thread1.start();
        thread2.start();

        // Terminates three threads once they finished
        thread0.join();
        thread1.join();
        thread2.join();

        // Count nodes after threads finish
        finalCount(root);
        System.out.println();
        System.out.println("the final count from thread 2: ");
        System.out.println(counter);
        System.out.println("the final contents of the leaf threading: ");
        // Print final leaves
        finalPrint();

    }

    // My helper method to print final leaves
    public static void finalPrint(){
        TreeNode current = head;
        // Since there is no change in leaves, just from head to tail,
        // print name one by one
        while(current != tail){
            System.out.print(current.aName + " ");
            current = current.nextLeaf;
        }
        System.out.println(current.aName);

    }

    //This is my helper method to create a random name
    public static String randomString(){
        Random r = new Random(); // just create one and keep it around
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        // I set the length as 5
        final int N = 5;
        StringBuilder randomName = new StringBuilder();
        for (int i = 0; i < N; i++) {
            randomName.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        return randomName.toString();
    }

    // My helper method for count final nodes, using recursion
    public static void finalCount(TreeNode current){
        counter ++;
        if(current.aLeftChild != null){
            finalCount(current.aLeftChild);
        }
        if(current.aRightChild != null){
            finalCount(current.aRightChild);
        }
    }
}
