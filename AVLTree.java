import java.util.Scanner;

public class AVLTree<Key extends Comparable> {
    private Node root;
    private class Node {
        Key word;
        Node left, right;
        int height;

        public Node(Key word) {
            this.word = word;
            this.left = null;
            this.right = null;
            this.height = 0; // leaf node has height 0
        }
    }

    public AVLTree() {
        this.root = null;
    }

    // --> core AVL helper functions <--
    private int height(Node node) {
        return (node == null) ? -1 : node.height;
    }

    private void updateHeight(Node node) {
        if (node != null) { node.height = 1 + Math.max(height(node.left), height(node.right)); }
    }

    private int getBalanceFactor(Node node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    private Node rightRotate(Node node) {
        Node x = node.left;
        Node T2 = x.right;

        x.right = node;
        node.left = T2;

        updateHeight(node);
        updateHeight(x);

        return x;
    }

    private Node leftRotate(Node node) {
        Node x = node.right;
        Node T2 = x.left;

        x.left = node;
        node.right = T2;

        updateHeight(node);
        updateHeight(x);

        return x;
    }

    public void treeInsert(Key word) {
        root = insert(root, word);
    }

    private Node insert(Node node, Key word) {
        if (node == null) {
            return new Node(word);
        }
        int cmp = word.compareTo(node.word);
        if (cmp == 0) return node;
        if (cmp < 0) {
            node.left = insert(node.left, word);
        } else {
            node.right = insert(node.right, word);
        }

        updateHeight(node);

        int balance = getBalanceFactor(node);

        // rebalance if necessary (4 cases)
        // LL Case
        if (balance > 1 && getBalanceFactor(node.left) >= 0) {
            return rightRotate(node);
        }

        // LR Case
        if (balance > 1 && getBalanceFactor(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // RR Case
        if (balance < -1 && getBalanceFactor(node.right) <= 0) {
            return leftRotate(node);
        }

        // RL Case
        if (balance < -1 && getBalanceFactor(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
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
                return 1;
            }
        }
        return 0;
    }

    public void treeRemove(Key word) {
        root = remove(root, word);
    }

    private Node remove(Node node, Key word) {
        if (node == null) {
            return null; // Word not found
        }

        int cmp = word.compareTo(node.word);
        if (cmp < 0) {
            node.left = remove(node.left, word);
        } else if (cmp > 0) {
            node.right = remove(node.right, word);
        } else {
            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                Node temp = findMin(node.right);
                node.word = temp.word;
                // delete the in-order successor (rebalance the right subtree)
                node.right = remove(node.right, temp.word);
            }
        }

        if (node == null) {
            return null;
        }

        updateHeight(node);

        int balance = getBalanceFactor(node);

        // rebalance (4 cases)
        // LL Case or L0
        if (balance > 1 && getBalanceFactor(node.left) >= 0) {
            return rightRotate(node);
        }

        // LR Case
        if (balance > 1 && getBalanceFactor(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // RR Case or R0
        if (balance < -1 && getBalanceFactor(node.right) <= 0) {
            return leftRotate(node);
        }

        // RL Case
        if (balance < -1 && getBalanceFactor(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private Node findMin(Node node) { // helper to find the node with the minimum key in a subtree.
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public void treeWalk() { // inorder traversal
        treeWalkHelper(root);
    }

    private void treeWalkHelper(Node node) {
        if (node == null) return;
        treeWalkHelper(node.left);
        System.out.printf("%s%n", node.word);
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
        System.out.println(node.word + " (h:" + node.height + ", bf:" + getBalanceFactor(node) + ")");
        printTree(node.left, level + 1);
    }

    // User Interface
    public static void main(String[] args) {
        AVLTree avl = new AVLTree();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--> AVL Tree Menu <--");
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

            switch (choice) {
                case 1:
                    System.out.print("Enter word to insert: ");
                    word = scanner.nextLine().trim().toLowerCase();
                    if (word.isEmpty()) continue;
                    avl.treeInsert(word);
                    System.out.println("Inserted '" + word + "'.");
                    avl.printTree();
                    break;

                case 2:
                    System.out.print("Enter word to search: ");
                    word = scanner.nextLine().trim().toLowerCase();
                    if (word.isEmpty()) continue;
                    int found = avl.treeSearch(word);
                    if (found == 1) {
                        System.out.println("Word '" + word + "' was found.");
                    } else {
                        System.out.println("Word '" + word + "' was NOT found.");
                    }
                    break;

                case 3:
                    System.out.print("Enter word to delete: ");
                    word = scanner.nextLine().trim().toLowerCase();
                    if (word.isEmpty()) continue;
                    int preSearch = avl.treeSearch(word);
                    avl.treeRemove(word);
                    if (preSearch == 1) {
                        System.out.println("Deleted '" + word + "'.");
                    } else {
                        System.out.println("Word '" + word + "' not found, nothing to delete.");
                    }
                    avl.printTree();
                    break;

                case 4:
                    avl.treeWalk();
                    break;

                case 5:
                    avl.printTree();
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