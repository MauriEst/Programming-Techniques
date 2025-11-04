import java.util.ArrayList;
import java.util.List;

public class BTree {
    private BTreeNode root;
    private final int m;
    private final int maxKeys;
    private final int minKeys;

    private class BTreeNode {
        List<Integer> keys;
        List<BTreeNode> children;
        boolean leaf;

        BTreeNode(int m, boolean leaf) {
            this.leaf = leaf;
            this.keys = new ArrayList<>(m - 1);
            this.children = new ArrayList<>(m);
        }

        /**
         * Finds the first index 'i' in the keys list such that
         * 'key <= keys.get(i)'.
         * @param key The key to search for
         * @return index for this key
         */
        int findKeyIndex(int key) {
            int index = 0;
            while (index < keys.size() && keys.get(index) < key) {
                index++;
            }
            return index;
        }

        /**
         * Checks if the node is at maximum key capacity (maxKeys)
         */
        boolean isFull() {
            return keys.size() == maxKeys;
        }

        /**
         * Checks if the node is at its minimum key capacity (minKeys)
         */
        boolean isMinimal() {
            return keys.size() == minKeys;
        }
    }

    /**
     * Constructor for the BTree.
     * @param m The order of the tree (e.g., 3 for a 2-3 tree)
     */
    public BTree(int m) {
        if (m < 3) {
            throw new IllegalArgumentException("B-Tree order must be at least 3.");
        }
        this.m = m;
        this.maxKeys = m - 1;
        this.minKeys = (int) Math.ceil(m / 2.0) - 1;
        this.root = new BTreeNode(m,true);
    }

    /**
     * Searches for a key in the B-Tree.
     *
     * @param key The integer key to search for.
     * @return true if the key is found, false otherwise.
     */
    public boolean search(int key) {
        return search(root, key);
    }

    private boolean search(BTreeNode node, int key) {
        int i = node.findKeyIndex(key);

        if (i < node.keys.size() && node.keys.get(i) == key) {
            return true;
        }

        if (node.leaf) {
            return false;
        }

        return search(node.children.get(i), key);
    }

    /**
     * Inserts a new key into the B-Tree.
     *
     * @param key The integer key to insert.
     */
    public void insert(int key) {
        BTreeNode r = root;
        if (r.isFull()) {
            BTreeNode newRoot = new BTreeNode(m, false);
            newRoot.children.add(r);
            splitChild(newRoot, 0);
            this.root = newRoot;
            insertNonFull(newRoot, key);
        } else {
            insertNonFull(r, key);
        }
    }

    /**
     * Inserts a key into a node that is guaranteed to be non-full.
     */
    private void insertNonFull(BTreeNode node, int key) {
        int i = node.findKeyIndex(key);

        if (node.leaf) {
            node.keys.add(i, key);
        } else {
            BTreeNode child = node.children.get(i);

            if (child.isFull()) {
                splitChild(node, i);
                if (key > node.keys.get(i)) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), key);
        }
    }

    /**
     * Splits a full child node of a given parent node.
     * The child is at index `childIndex` in `parent.children`.
     * The full child is split into two, and the middle key is
     * promoted to the parent
     */
    private void splitChild(BTreeNode parent, int childIndex) {
        BTreeNode fullChild = parent.children.get(childIndex);
        BTreeNode newSibling = new BTreeNode(m, fullChild.leaf);

        int middleKey = fullChild.keys.get(minKeys);

        parent.keys.add(childIndex, middleKey);

        for (int k = maxKeys - 1; k > minKeys; k--) {
            newSibling.keys.add(0, fullChild.keys.remove(k));
        }

        fullChild.keys.remove(minKeys);

        if (!fullChild.leaf) {
            for (int k = m - 1; k > minKeys; k--) {
                newSibling.children.add(0, fullChild.children.remove(k));
            }
        }

        parent.children.add(childIndex + 1, newSibling);
    }

    /**
     * Deletes a key from the B-Tree.
     *
     * @param key The integer key to delete.
     */
    public void delete(int key) {
        delete(root, key);
        if (root.keys.isEmpty() && !root.leaf) {
            root = root.children.get(0);
        }
    }

    private void delete(BTreeNode node, int key) {
        int i = node.findKeyIndex(key);
        if (i < node.keys.size() && node.keys.get(i) == key) {
            if (node.leaf) {
                node.keys.remove(i);
            } else {
                deleteFromInternalNode(node, i, key);
            }
        } else {
            if (node.leaf) {
                return;
            }
            hasSufficientKeys(node, i);
            int newChildIndex = node.findKeyIndex(key);
            delete(node.children.get(newChildIndex), key);
        }
    }

    /**
     * Handles deletion of a key from an internal node.
     * Replaces the key with its predecessor or successor, or merges children
     */
    private void deleteFromInternalNode(BTreeNode node, int keyIndex, int key) {
        BTreeNode leftChild = node.children.get(keyIndex);
        BTreeNode rightChild = node.children.get(keyIndex + 1);

        if (leftChild.keys.size() > minKeys) {
            int predecessor = findPredecessor(leftChild);
            node.keys.set(keyIndex, predecessor);
            delete(leftChild, predecessor);
        } else if (rightChild.keys.size() > minKeys) {
            int successor = findSuccessor(rightChild);
            node.keys.set(keyIndex, successor);
            delete(rightChild, successor);
        } else {
            merge(node, keyIndex);
            delete(leftChild, key);
        }
    }

    /**
     * Ensures that the child at `childIndex` of `parent` has at least
     * `minKeys + 1` keys, so we can safely delete from it.
     */
    private void hasSufficientKeys(BTreeNode parent, int childIndex) {
        BTreeNode child = parent.children.get(childIndex);

        if (child.isMinimal()) {
            BTreeNode leftSibling = (childIndex > 0) ? parent.children.get(childIndex - 1) : null;
            BTreeNode rightSibling = (childIndex < parent.children.size() - 1) ? parent.children.get(childIndex + 1) : null;

            if (leftSibling != null && leftSibling.keys.size() > minKeys) {
                shiftKeyFromLeft(parent, childIndex);
            } else if (rightSibling != null && rightSibling.keys.size() > minKeys) {
                shiftKeyFromRight(parent, childIndex);
            } else {
                if (leftSibling != null) {
                    merge(parent, childIndex - 1);
                } else {
                    merge(parent, childIndex);
                }
            }
        }
    }

    /**
     * Borrows a key from the left sibling (at `childIndex - 1`)
     * and moves it to the child (at `childIndex`).
     */
    private void shiftKeyFromLeft(BTreeNode parent, int childIndex) {
        BTreeNode child = parent.children.get(childIndex);
        BTreeNode leftSibling = parent.children.get(childIndex - 1);

        child.keys.add(0, parent.keys.get(childIndex - 1));
        parent.keys.set(childIndex - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1));

        if (!leftSibling.leaf) {
            child.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
        }
    }

    /**
     * Borrows a key from the right sibling (at `childIndex + 1`)
     * and moves it to the child (at `childIndex`).
     */
    private void shiftKeyFromRight(BTreeNode parent, int childIndex) {
        BTreeNode child = parent.children.get(childIndex);
        BTreeNode rightSibling = parent.children.get(childIndex + 1);
        child.keys.add(parent.keys.get(childIndex));
        parent.keys.set(childIndex, rightSibling.keys.remove(0));

        if (!rightSibling.leaf) {
            child.children.add(rightSibling.children.remove(0));
        }
    }

    /**
     * Merges the child at `leftChildIndex + 1` into the child at `leftChildIndex`.
     * The separating key from the parent is also moved down.
     */
    private void merge(BTreeNode parent, int leftChildIndex) {
        BTreeNode leftChild = parent.children.get(leftChildIndex);
        BTreeNode rightChild = parent.children.get(leftChildIndex + 1);

        // pull separating key from parent down to left child
        leftChild.keys.add(parent.keys.remove(leftChildIndex));

        // move all keys from right child to left child
        leftChild.keys.addAll(rightChild.keys);

        // move all children from right to left if not leaft
        if (!leftChild.leaf) {
            leftChild.children.addAll(rightChild.children);
        }

        parent.children.remove(leftChildIndex + 1);
    }

    /**
     * Finds the in-order predecessor of a key (max key in the left subtree).
     */
    private int findPredecessor(BTreeNode node) {
        while (!node.leaf) {
            node = node.children.get(node.children.size() - 1);
        }
        return node.keys.get(node.keys.size() - 1);
    }

    /**
     * Finds the in-order successor of a key (min key in the right subtree).
     */
    private int findSuccessor(BTreeNode node) {
        while (!node.leaf) {
            node = node.children.get(0);
        }
        return node.keys.get(0);
    }

//    /**
//     * Prints the B-Tree in a level-order traversal for visualization.
//     */
//    public void print() {
//        if (root == null) {
//            System.out.println("Tree is empty.");
//            return;
//        }
//
//        Queue<BTreeNode> queue = new LinkedList<>();
//        queue.add(root);
//        int level = 0;
//        while (!queue.isEmpty()) {
//            int levelSize = queue.size();
//            System.out.print("Level " + level + ": ");
//            for (int i = 0; i < levelSize; i++) {
//                BTreeNode node = queue.poll();
//                System.out.print(node.keys + " ");
//                if (!node.leaf) {
//                    queue.addAll(node.children);
//                }
//            }
//            System.out.println();
//            level++;
//        }
//    }

    public void print() {
        if (root == null || root.keys.isEmpty()) {
            System.out.println("Tree (m=" + m + ") is empty.");
        } else {
            System.out.println("B-Tree (m=" + m + "):");
            printRecursive(root, "");
        }
    }

    private void printRecursive(BTreeNode node, String indent) {
        System.out.println(indent + node.keys);

        if (!node.leaf) {
            String childIndent = indent + "  ";
            for (BTreeNode child : node.children) {
                printRecursive(child, childIndent);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("=          TEST 1: Order m = 4               =");
        System.out.println("==============================================");

        BTree t4 = new BTree(4);
        System.out.println("Step 1: Created empty tree (m=4).");
        t4.print();
        System.out.println("----------------------------------------------");

        System.out.println("Step 2: Inserting 1 to 20");
        for (int i = 1; i <= 20; i++) {
            t4.insert(i);
        }
        System.out.println("----------------------------------------------");

        System.out.println("Step 3: Tree structure after insertions:");
        t4.print();
        System.out.println("----------------------------------------------");

        System.out.println("Step 4: Deleting even numbers");
        for (int i = 2; i <= 20; i += 2) {
            t4.delete(i);
        }
        System.out.println("----------------------------------------------");

        // 5. Display the tree structure
        System.out.println("Step 5: Tree structure after deletions:");
        t4.print();
        System.out.println("\n\n");


        System.out.println("==============================================");
        System.out.println("=          TEST 2: Order m = 5               =");
        System.out.println("==============================================");

        BTree t5 = new BTree(5);
        System.out.println("Step 1: Created empty tree (m=5).");
        t5.print();
        System.out.println("----------------------------------------------");

        System.out.println("Step 2: Inserting 1 to 20");
        for (int i = 1; i <= 20; i++) {
            t5.insert(i);
        }
        System.out.println("----------------------------------------------");

        System.out.println("Step 3: Tree structure after insertions:");
        t5.print();
        System.out.println("----------------------------------------------");

        System.out.println("Step 4: Deleting even numbers");
        for (int i = 2; i <= 20; i += 2) {
            t5.delete(i);
        }
        System.out.println("----------------------------------------------");

        System.out.println("Step 5: Tree structure after deletions:");
        t5.print();
    }
}
