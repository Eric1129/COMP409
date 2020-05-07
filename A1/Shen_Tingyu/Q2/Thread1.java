// Eric Shen
// 260798146

import java.util.concurrent.ThreadLocalRandom;

// Thread1 class is to construct new children
public class Thread1 extends Thread {

    boolean notFinish = true;
    TreeNode current;

    public Thread1() { }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        current = q2.head;

        // I use while loop to keep track 5 milliseconds and last time if traversal is not finished
        while (System.currentTimeMillis() - startTime < 5000 || notFinish) {
            // If enter loop, means traversal not finished
            notFinish = true;

            if(current == q2.head){
                // If head expand
                if(ThreadLocalRandom.current().nextDouble() < 0.1){
                    String name = q2.randomString();
                    current.aLeftChild = new TreeNode(name, current);
                    current.aRightChild = new TreeNode(name.toUpperCase(), current);
                    current.aLeftChild.nextLeaf = current.aRightChild;
                    current.aRightChild.nextLeaf = current.nextLeaf;
                    try {
                        Thread.sleep(20);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    current.nextLeaf = null;
                    q2.head = current.aLeftChild;
                    current = current.aRightChild.nextLeaf;
                }
                else {
                    try {
                        Thread.sleep(20);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    current = current.nextLeaf;
                }
            }
            else if(current == q2.tail){
                // If tail expands
                if(ThreadLocalRandom.current().nextDouble() < 0.1){
                    String name = q2.randomString();
                    current.aLeftChild = new TreeNode(name, current);
                    current.aRightChild = new TreeNode(name.toUpperCase(), current);
                    current.aLeftChild.nextLeaf = current.aRightChild;
                    q2.tail = current.aRightChild;

                    // let previous node's next leaf point to left children
                    TreeNode temp = q2.head;
                    while(temp.nextLeaf != current){
                        temp = temp.nextLeaf;
                    }
                    temp.nextLeaf = current.aLeftChild;
                }
                try {
                    Thread.sleep(200);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                current = q2.head;
                notFinish = false;
            }
            else {
                // If other nodes except head and tail expands
                if(ThreadLocalRandom.current().nextDouble() < 0.1){
                    String name = q2.randomString();
                    current.aLeftChild = new TreeNode(name, current);
                    current.aRightChild = new TreeNode(name.toUpperCase(), current);
                    current.aLeftChild.nextLeaf = current.aRightChild;
                    current.aRightChild.nextLeaf = current.nextLeaf;

                    TreeNode temp = q2.head;
                    while(temp.nextLeaf != current){
                        temp = temp.nextLeaf;
                    }
                    temp.nextLeaf = current.aLeftChild;
                    current.nextLeaf = null;

                    try {
                        Thread.sleep(20);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    current = current.aRightChild.nextLeaf;
                }
                else {
                    try {
                        Thread.sleep(20);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    current = current.nextLeaf;
                }
            }
        }
    }

}
