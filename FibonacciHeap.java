import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

    // Before implementing F-heap, firstly define a basic class about nodes
    // The root node of the smallest heap forms a doubly linked list
class FibonacciNode {
    int value;
    String key; // information of hash table
    int degree; // reserve the information of every node's degree  //each node has four pointers (up, down, left, right)...
    FibonacciNode parent;
    FibonacciNode child;
    FibonacciNode left;
    FibonacciNode right;
    boolean mark; // this is used to increaskey or decreasekey

    FibonacciNode(String key, int value) {
        this.degree = 0;
        this.value = value;
        this.parent = null;
        this.child = null;
        this.left = null;
        this.right = null;
        this.mark = false;
        this.key = key;
    }
}


public class FibonacciHeap {
    private int size;
    private FibonacciNode max;

    public FibonacciHeap() {
        max = null; // max = the pinter of max
        size = 0;  // initialize Fibonacci heap
    }
    // There are three main operations to the Fibonacci heap
    // Insert operation
    public FibonacciNode insert(String key, int value) {
        FibonacciNode node = new FibonacciNode(key, value); // two situations
        if (max == null) {
            max = node;
            max.left = max; // the left pointer points to the max
            max.right = max; // the right pointer points to the max  // this performs a minimum circulation of doubly linked list
        } else {
            insertToRootList(node);  // the insert opertion of the doubly linked list
            if (node.value > max.value) {
                max = node;
            }
        }
        size++;
        return node;
    }

    // Increase value operation
    public void increase(FibonacciNode target, int value) {
        FibonacciNode parent = target.parent;
        target.value = value;
        // if the target is a nide of root list
        if (parent == null) {
            if (value >= max.value)
                max = target;
        } else if (parent.value < value) {
            parent.child = deleteChild(target);
            meld(target);
            cut(parent);
        }
    }

    // Pop the maximum element(move max)
    public FibonacciNode pop() {
        if (size == 0)
            return null;
        FibonacciNode maxNode = max;
        addChildrenToRootList(maxNode); // add the children of a node to root list
        deleteParent(maxNode); // delete the pointer information, and let them equal to null
        if (max == max.left)
            max = null;
        else {
            max = max.left;
            deleteNodeOnList(maxNode);  // delete the max node
            updateMaxAndMerge();  // while merging, update the max information
        }
        size--;
        return maxNode;
    }

    // Other helper functions
    private FibonacciNode deleteChild(FibonacciNode target) {
        if (target == target.left)
            return null;

        target.left.right = target.right;
        target.right.left = target.left;
        return target.left;
    }

    private void insertToRootList(FibonacciNode node) {
        max.left.right = node;
        node.left = max.left;
        node.right = max;
        max.left = node;
    }

    private void updateMaxAndMerge() {
        Map<Integer, FibonacciNode> degreeNodesMap = new HashMap<>();
        FibonacciNode current = max;
        // use the method like counting numbers to traversal
        int count = 0;
        do {
            current = current.right; //before merging, firstly reserve the right
            count++;
        } while (current != max && current.left != current);
        for (int i = 0; i < count; i++) {
            FibonacciNode right = current.right;
            FibonacciNode curr = addDegreeNode(degreeNodesMap, current); // complete an insert operation into hash table
            if (curr.value >= max.value)
                max = curr; // update the max
            current = right;
        }
    }


    private FibonacciNode addDegreeNode(Map<Integer, FibonacciNode> degreeNodesMap, FibonacciNode node) {
        int nodeDegree = node.degree;
        FibonacciNode mapNode = degreeNodesMap.get(nodeDegree);
        if (mapNode == null)
            degreeNodesMap.put(nodeDegree, node);
        else {
            if (node.key.equals(mapNode.key))
                return node;
            if (node.value >= mapNode.value) {
                deleteNodeOnList(mapNode);
                mapNode.left = mapNode;
                mapNode.right = mapNode;
                insertChildToNode(node, mapNode);
                degreeNodesMap.put(nodeDegree, null);
                node = addDegreeNode(degreeNodesMap, node);
            } else {
                deleteNodeOnList(node);
                node.left = node;
                node.right = node;
                insertChildToNode(mapNode, node);
                degreeNodesMap.put(nodeDegree, null);
                node = addDegreeNode(degreeNodesMap, mapNode);
            }
        }
        return node;
    }

    private void insertChildToNode(FibonacciNode parent, FibonacciNode child) {
        if (parent.child == null)
            parent.child = child;
        else
            insertChild(parent.child, child);

        child.parent = parent;
        parent.degree += 1;
    }

    private void insertChild(FibonacciNode target, FibonacciNode node) {
        FibonacciNode left = target.left;
        left.right = node;
        node.left = left;
        node.right = target;
        target.left = node;
    }

    private void deleteNodeOnList(FibonacciNode node) {
        node.left.right = node.right;
        node.right.left = node.left;
    }
    // add the children of a node to root list ——> traverse the children
    // In the children area, the brother nodes forms a doubly linked list
    private void addChildrenToRootList(FibonacciNode node) {
        FibonacciNode child = node.child;
        ArrayList<FibonacciNode> children = new ArrayList<FibonacciNode>();
        // traverse and then find all the children nodes,then save to the children array list
        if (child == null) {
            return;
        }
        do {
            child.parent = null;
            children.add(child);
            child = child.left;
        } while (child != node.child);

        for (FibonacciNode c : children) {
            insertToRootList(c);
        }
    }
    // delete the pointing information, and let it equal to null
    // the parent is deleted, the pointing information should also be deleted.
    private void deleteParent(FibonacciNode node) {
        FibonacciNode child = node.child;
        if (child == null)
            return;
        do {
            child.parent = null;
            child = child.right;
        } while (child != node.child);
    }

    private void meld(FibonacciNode target) {
        target.parent = null; // it has been into the rootlist, so it does not have parent node.
        target.mark = false;
        insertToRootList(target);
        if (target.value >= max.value)
            max = target; // stay updating max
    }
    // check the parents
    private void cut(FibonacciNode parent) {
        if (null == parent) {
            return; // already in the root list
        }
        parent.degree--;
        if (!parent.mark) {
            parent.mark = true;
        } else {
            parent.parent = deleteChild(parent);
            meld(parent);
            cut(parent.parent);  //Recursive up
        }
    }
}
