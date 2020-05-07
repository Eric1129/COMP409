//Eric Shen 260798146

// I created this TreeNode class to keep basic values
public class TreeNode {

    //since attributes may be used for 3 threads, let them be volatile
    protected volatile String aName;
    protected volatile TreeNode aParent;
    protected volatile TreeNode aLeftChild = null;
    protected volatile TreeNode aRightChild = null;
    protected volatile TreeNode nextLeaf = null;

    // Another constructor to construct null children
    TreeNode(String pName, TreeNode parent) {
        aName = pName;
        aParent = parent;
    }
}
