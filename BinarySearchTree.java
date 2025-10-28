import java.util.Scanner;

public class BinarySearchTree<Key extends Comparable> {
    private Node root;
    private class Node {
        Key word;
        Node left, right;
        int counter; // might need a counter for duplicate keys

        public Node(Key word) { this(null, null, word, 1); }

        public Node (Node left, Node right, Key word, int counter) {
            this.left = left;
            this.right = right;
            this.word = word;
            this.counter = counter;
        }
    }

    BinarySearchTree() {}

    public int treeInsert(Key word) {
        int[] beforeCount = new int[1];
        root = insert(root, word, beforeCount);
        return beforeCount[0]; // return # of occurrences of word before insert
    }

    private Node insert(Node node, Key word, int[] beforeCount) {
        if (node == null) {
            beforeCount[0] = 0;
            return new Node(word);
        }
        int cmp = word.compareTo(node.word);
        if (cmp < 0) {
            node.left = insert(node.left, word, beforeCount);
        } else if (cmp > 0) {
            node.right = insert(node.right, word, beforeCount);
        } else {
            beforeCount[0] = node.counter;
            node.counter++;
        }
        return node;
    }

    public int treeSearch(Key word) {
        Node current = root;
        while (current != null) {
            int cmp = word.compareTo(current.word);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return current.counter;
            }
        }
        return 0;
    }

    public int treeRemove(Key word) {
        int[] beforeCount = new int[1];
        beforeCount[0] = 0;
        root = remove(root, word, beforeCount);
        return beforeCount[0]; // return # of occurrences of word before deletion
    }

    private Node remove(Node node, Key word, int[] beforeCount) {
        if (node == null) return null;
        int cmp = word.compareTo(node.word);
        if (cmp < 0) {
            node.left = remove(node.left, word, beforeCount);
        } else if (cmp > 0) {
            node.right = remove(node.right, word, beforeCount);
        } else {
            beforeCount[0] = node.counter;
            if (node.counter > 1) { // word has duplicates. decrement the counter.
                node.counter--;
                return node;
            }
            // counter is 1. delete the physical node
            if (node.left == null) { // node with 0 or 1 child
                return node.right;
            }
            if (node.right == null) { // node with 0 or 1 child
                return node.left;
            }
            // node with 2 children:
            // 1. find the in-order successor (smallest node in the right subtree)
            Node successor = findMin(node.right);
            // 2. copy the successor data to this node
            node.word = successor.word;
            node.counter = successor.counter;
            // 3. delete the successor from the right subtree
            node.right = deleteMin(node.right);
        }
        return node;
    }

    private Node findMin(Node node) { // helper to find the node with the minimum key in a subtree.
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Node deleteMin(Node node) { // helper to delete the node with the minimum key from a subtree.
        if (node.left == null) {
            return node.right; // the right child replaces the min node
        }
        node.left = deleteMin(node.left);
        return node;
    }


    public void treeWalk() { // inorder traversal
        treeWalkHelper(root);
    }

    private void treeWalkHelper(Node node) {
        if (node == null) return;
        treeWalkHelper(node.left);
        System.out.printf("%s(%d)%n", node.word, node.counter);
        treeWalkHelper(node.right);
    }

    public void printTree() {
        printTree(root, 0);
    }

    private void printTree(Node node, int level) {
        if (node == null) { return; }
        printTree(node.right, level + 1);
        for (int i = 0; i < level; i++) {
            System.out.print("      ");
        }
        System.out.println(node.word);
        printTree(node.left, level + 1);
    }

    // User Interface
    public static void main(String[] args) {
        BinarySearchTree<String> bst = new BinarySearchTree<>();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--> Binary Search Tree Menu <--");
            System.out.println("1. Insert word");
            System.out.println("2. Search word");
            System.out.println("3. Delete word");
            System.out.println("4. Tree walk");
            System.out.println("5. Print tree");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            String word;
            int count;

            switch (choice) {
                case 1:
                    System.out.print("Enter word to insert: ");
                    word = scanner.nextLine().trim().toLowerCase();
                    if (word.isEmpty()) continue;
                    count = bst.treeInsert(word);
                    System.out.println("Inserted '" + word + "'. Count before: " + count);
                    break;

                case 2:
                    System.out.print("Enter word to search: ");
                    word = scanner.nextLine().trim().toLowerCase();
                    if (word.isEmpty()) continue;
                    count = bst.treeSearch(word);
                    System.out.println("Word '" + word + "' found " + count + " time(s).");
                    break;

                case 3:
                    System.out.print("Enter word to delete: ");
                    word = scanner.nextLine().trim().toLowerCase();
                    if (word.isEmpty()) continue;
                    count = bst.treeRemove(word);
                    if (count > 0) {
                        System.out.println("Deleted one '" + word + "'. Count before: " + count);
                    } else {
                        System.out.println("Word '" + word + "' not found. Count before: 0");
                    }
                    break;

                case 4:
                    bst.treeWalk();
                    break;

                case 5:
                    bst.printTree();
                    break;

                case 0:
                    running = false;
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
}
