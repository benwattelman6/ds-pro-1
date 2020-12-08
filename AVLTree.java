import java.util.LinkedList;
import java.util.ListIterator;

/**
 * AVLTree
 * <p>
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 */

public class AVLTree {


    private IAVLNode root;
    private int min;
    private int max;
    private int nodes;
    AVLNode externalLeaf;


    public AVLTree() {
        //initialize external leaf with isExternal = true
        externalLeaf = new AVLNode("", -1);
        externalLeaf.setIsExternal(true);
        externalLeaf.setHeight(-1); // important to get the balance afterwards
        root = null;
        nodes = 0;
    }

    /**
     * public boolean empty()
     * <p>
     * returns true if and only if the tree is empty
     */
    public boolean empty() {
        return getRoot() == null; // to be replaced by student code
    }

    /**
     * public String search(int k)
     * <p>
     * returns the info of an item with key k if it exists in the tree
     * otherwise, returns null
     */
    public String search(int k) {
        IAVLNode n = search(getRoot(), k);
        if (n == null) return null;
        if (!n.isRealNode()) return null;
        return n.getValue();  // to be replaced by student code
    }

    private IAVLNode search(IAVLNode root, int k) {
        if (root == null) return null;
        if (!root.isRealNode()) return null;
        if (root.getKey() == k) return root;
        if (root.getKey() < k) return search(root.getRight(), k);
        return search(root.getLeft(), k);
    }

    /**
     * This method create a new node with key `k` and info `i`
     * <p>
     * Complexity: O(1) [same as AVLNode constructor]
     *
     * @param k
     * @param i
     * @return
     */
    AVLNode createNewNode(int k, String i) {
        AVLNode n = new AVLNode(i, k);
        n.setLeft(this.externalLeaf);
        n.setRight(this.externalLeaf);
        return n;
    }

    /**
     * public int insert(int k, String i)
     * <p>
     * inserts an item with key k and info i to the AVL tree.
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
     * promotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
     * returns -1 if an item with key k already exists in the tree.
     */
    public int insert(int k, String i) {
        int counter = 0;
        IAVLNode newNode = createNewNode(k, i);
        IAVLNode a = getRoot();
        IAVLNode b = null;
        while (a != null && a.isRealNode()) {
            b = a;
            if (b.getKey() == k) return -1;
            if (k < a.getKey()) a = a.getLeft();
            else a = a.getRight();
        }

        // We need to insert under b

        // If the root is null the tree is empty
        // The new node is the root node
        if (b == null) {
            System.out.printf("[Insert] Setting `%d` as root\n", k);
            b = newNode;
            this.setRoot(b);
        } else {
            if (k == b.getKey()) {
                System.out.printf("[Insert] Key `%d` already exists in tree\n", k);
                return -1;
            }
            // If the key is greater then the leaf node key
            // Assign the new node to be its right child
            if (k > b.getKey()) {
                System.out.printf("[Insert] Setting `%d` as right child of `%d`\n", k, b.getKey());
                b.setRight(newNode);
            }

            // If the new key is less then the leaf node key
            // Assign the new node to be its left child
            else {
                System.out.printf("[Insert] Setting `%d` as left child of `%d`\n", k, b.getKey());
                b.setLeft(newNode);
            }
            newNode.setParent(b);
        }
        if (updateHeight(b)) counter++;

        IAVLNode p = newNode;
        while (p != null) {
            int actions = rebalance(p);
            if (actions > 0) {
                System.out.println("[insert] Rotation occurred. Tree is balanced.");
                counter += actions;
                // TODO: can break here?
            }
            if (updateHeight(p)) counter++;
            p = p.getParent();
        }
        if (updateHeight(getRoot())) counter++; // TODO: not sure necessary after change in loop
        this.nodes++; // increment the number of nodes
        return counter;    // to be replaced by student code
    }

    /**
     * Rebalancing a single node
     *
     * @param n
     * @return
     */
    int rebalance(IAVLNode n) {
        System.out.println("[rebalance] rebalancing node " + n.getKey());
        int counter = 0;
        int balance = getBalance(n);
        if (balance > 1) {
            System.out.println("[rebalance] left heavy");
            // heavy on the left
            if (getBalance(n.getLeft()) < 0) {
                // This is LR case
                System.out.println("[rebalance] the right subtree causing it, should be LR");
                counter = 5; // add the rotation to counter
                rotate(n.getLeft(), 'L');
            } else {
                counter = 2; // add the rotation to counter
            }
            rotate(n, 'R');
        } else if (balance < -1) {
            System.out.println("[rebalance] right heavy");
            // heavy on the right
            if (getBalance(n.getRight()) > 0) {
                // This is RL case
                System.out.println("[rebalance] the right left causing it, should be RL");
                counter = 5;
                rotate(n.getRight(), 'R');
            } else {
                counter = 2; // add the rotation to counter
            }
            rotate(n, 'L');
        }
        return counter; // add the rotation to counter
    }

    /**
     * public int delete(int k)
     * <p>
     * deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
     * demotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
     * returns -1 if an item with key k was not found in the tree.
     */
    public int delete(int k) {
        return 42;    // to be replaced by student code
    }

    /**
     * public String min()
     * <p>
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty
     */
    public String min() {
        // need to traverse all the way left
        if (empty()) return null;
        IAVLNode n = getRoot();
        IAVLNode a = null;
        while (n != null && n.isRealNode()) {
            a = n;
            n = n.getLeft();
        }
        if (a == null) return null;
        return a.getValue(); // to be replaced by student code
    }

    /**
     * public String max()
     * <p>
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty
     */
    public String max() {
        if (empty()) return null;
        IAVLNode n = getRoot();
        IAVLNode a = null;
        while (n != null && n.isRealNode()) {
            a = n;
            n = n.getRight();
        }
        if (a == null) return null;
        return a.getValue(); // to be replaced by student code
    }

    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     */
    public int[] keysToArray() {
        int[] arr = new int[size()]; // to be replaced by student code
        ListIterator<IAVLNode> listIterator = inOrderTraversal(getRoot()).listIterator();
        int i = 0;
        while (listIterator.hasNext()) {
            IAVLNode n = listIterator.next();
            arr[i] = n.getKey();
            i++;
        }
        return arr;              // to be replaced by student code
    }


    /**
     * public String[] infoToArray()
     * <p>
     * Returns an array which contains all info in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     */
    public String[] infoToArray() {
        String[] arr = new String[size()]; // to be replaced by student code
        ListIterator<IAVLNode> listIterator = inOrderTraversal(getRoot()).listIterator();
        int i = 0;
        while (listIterator.hasNext()) {
            IAVLNode n = listIterator.next();
            arr[i] = n.getValue();
            i++;
        }
        return arr;
    }

    /**
     * public int size()
     * <p>
     * Returns the number of nodes in the tree.
     * <p>
     * precondition: none
     * postcondition: none
     */
    public int size() {
        return nodes; // to be replaced by student code
    }

    /**
     * public int getRoot()
     * <p>
     * Returns the root AVL node, or null if the tree is empty
     * <p>
     * precondition: none
     * postcondition: none
     */
    public IAVLNode getRoot() {
        return root;
    }

    /**
     * This method returns the rank of the tree.
     * empty tree rank is
     */
    public int getRank() {
        if (empty()) return -1;
        return getRoot().getHeight();
    }

    public String toString() {
        IAVLNode root = this.getRoot();
        return "Tree root: " + (root != null ? root.getValue() : "EMPTY");
    }

    /**
     * public string split(int x)
     * <p>
     * splits the tree into 2 trees according to the key x.
     * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
     * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
     * postcondition: none
     */
    public AVLTree[] split(int x) {
        return null;
    }

    /**
     * public join(IAVLNode x, AVLTree t)
     * <p>
     * joins t and x with the tree.
     * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
     * precondition: keys(x,t) < keys() or keys(x,t) > keys(). t/tree might be empty (rank = -1).
     * postcondition: none
     */
    public int join(IAVLNode x, AVLTree t) {
        if (t.empty()) {
            System.out.println("`t` is an empty tree, so just need to insert `x`");
            int rank = this.getRank();
            this.insert(x.getKey(), x.getValue());
            return rank;
        }
        if (this.empty()) {
            System.out.println("Current tree is empty");
            this.setRoot(t.getRoot());
            int rank = this.getRank();
            this.nodes = t.size();
            this.insert(x.getKey(), x.getValue());
            return rank;
        }
        int rankDiff = this.getRank() - t.getRank();

        AVLTree T1, T2; // rank(T1) <= rank(T2)
        if (rankDiff >= 0) {
            // this is the bigger tree
            T1 = t;
            T2 = this;
        } else {
            // t is the bigger tree
            T1 = this;
            T2 = t;
        }
        int biggerKeys = T1.getRoot().getKey() - T2.getRoot().getKey(); // can't be 0!

        IAVLNode a, b, c, left, right;
        a = T1.getRoot();
        b = T2.getRoot();
        while (b.getHeight() > a.getHeight()) {
            if (biggerKeys > 0) {
                // T1 (the smaller tree), has bigger keys, thus it should hang from the right
                // so we are traveling the right spine
                b = b.getRight();
            } else {
                // T2 has bigger keys, thus T1 should hang from the left
                // so we are traveling the left spine
                b = b.getLeft();
            }
        }
        // we got b!
        c = b.getParent();

        if (biggerKeys > 0) {
            right = a;
            left = b;
        } else {
            right = b;
            left = a;
        }
        x.setLeft(left);
        x.setRight(right);
        x.setHeight(T1.getRank() + 1);
        left.setParent(x);
        right.setParent(x);
        x.setParent(c);
        if (c == null) {
            // x is new root, and tree is balanced
            this.setRoot(x);
            return 1;
        } else if (biggerKeys > 0) c.setRight(x);
        else c.setLeft(x);
        IAVLNode p = x;
        while (p != null) {
            if (p.getParent() == null) {
                this.setRoot(p);
            }
            rebalance(p);
            updateHeight(p);

            p = p.getParent();

        }
        this.nodes += 1 + t.size(); // update the amount of nodes (x + all the nodes of t);
        return Math.abs(rankDiff) + 1;
    }

    /**
     * Set the tree root
     * <p>
     * Time complexity: O(1)
     */
    public void setRoot(IAVLNode newRoot) {
        this.root = newRoot;
    }

    /**
     * Updating the node height if necessary.
     * Return true if height changed, false otherwise.
     *
     * @param n
     * @return
     */
    private boolean updateHeight(IAVLNode n) {
        int formerHeight = n.getHeight();
        n.setHeight(Math.max(n.getLeft().getHeight(), n.getRight().getHeight()) + 1);
        return formerHeight != n.getHeight();
    }

    /**
     * Perform single rotation as part of a rebalancing.
     * Receives current root of subtree (before rotation) and type of rotation ('R' or 'L')
     * Time complexity: O(1)
     */
    public IAVLNode rotate(IAVLNode root, char type) {
        // ensure nothing bad happen with invalid input
        if (root == null || !root.isRealNode()) {
            return null;
        }
        if (type == 'R') return rotateRight(root);
        else return rotateLeft(root);
    }

    void updateParentsAfterRotation(IAVLNode newRoot, IAVLNode oldRoot) {
        newRoot.setParent(oldRoot.getParent());
        oldRoot.setParent(newRoot);
        if (newRoot.getParent() == null)
            setRoot(newRoot);
        else if (newRoot.getParent().getKey() > newRoot.getKey()) newRoot.getParent().setLeft(newRoot);
        else newRoot.getParent().setRight(newRoot);
    }

    /**
     * Rotate subtree rooted with oldRoot to the left
     *
     * @param oldRoot
     * @return
     */
    IAVLNode rotateLeft(IAVLNode oldRoot) {
        IAVLNode newRoot = oldRoot.getRight();
        IAVLNode z = newRoot.getLeft();

        newRoot.setLeft(oldRoot);
        oldRoot.setRight(z);

        updateParentsAfterRotation(newRoot, oldRoot);

        updateHeight(oldRoot);
        updateHeight(newRoot);
        return newRoot;
    }

    /**
     * Rotate subtree rooted with oldRoot to the right
     *
     * @param oldRoot
     */
    IAVLNode rotateRight(IAVLNode oldRoot) {
        IAVLNode newRoot = oldRoot.getLeft();
        IAVLNode z = newRoot.getRight();

        newRoot.setRight(oldRoot);
        oldRoot.setLeft(z);

        updateParentsAfterRotation(newRoot, oldRoot);

        updateHeight(oldRoot);
        updateHeight(newRoot);
        return newRoot;
    }


    int getBalance(IAVLNode node) {
        if (node == null || !node.isRealNode()) return 0;
        return (node.getLeft().getHeight() - node.getRight().getHeight());

    }

    /**
     * Get a linked list of inorder traversal of a subtree rooted in ``root``
     *
     * @param root
     * @return
     */
    LinkedList<IAVLNode> inOrderTraversal(IAVLNode root) {
        LinkedList<IAVLNode> list = new LinkedList<IAVLNode>();
        if (root == null) return list;
        if (!root.isRealNode()) return list; // reached externl leaf
        list.addAll(inOrderTraversal(root.getLeft())); // add all = O(1)
        list.add(root); // O(1)
        list.addAll(inOrderTraversal(root.getRight())); // add all = O(1)
        return list;
    }

    /**
     * public interface IAVLNode
     * ! Do not delete or modify this - otherwise all tests will fail !
     */
    public interface IAVLNode {
        public int getKey(); //returns node's key (for virtuval node return -1)

        public String getValue(); //returns node's value [info] (for virtuval node return null)

        public void setLeft(IAVLNode node); //sets left child

        public IAVLNode getLeft(); //returns left child (if there is no left child return null)

        public void setRight(IAVLNode node); //sets right child

        public IAVLNode getRight(); //returns right child (if there is no right child return null)

        public void setParent(IAVLNode node); //sets parent

        public IAVLNode getParent(); //returns the parent (if there is no parent return null)

        public boolean isRealNode(); // Returns True if this is a non-virtual AVL node

        public void setHeight(int height); // sets the height of the node

        public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
    }

    /**
     * public class AVLNode
     * <p>
     * If you wish to implement classes other than AVLTree
     * (for example AVLNode), do it in this file, not in
     * another file.
     * This class can and must be modified.
     * (It must implement IAVLNode)
     */
    public class AVLNode implements IAVLNode {

        private String info;
        private int key;
        private int height;
        private int size;

        private AVLNode left;
        private AVLNode right;
        private AVLNode parent;

        private boolean isExternal;


        public AVLNode(String info, int key) {
            this.info = info;
            this.key = key;
            this.isExternal = false;
            this.height = 0;

        }

        public String toString() {
            return this.key + ": " + this.info;
        }

        public void setIsExternal(boolean b) {
            this.isExternal = b;
        }

        /**
         * Returns the `key` value
         * <p>
         * Time complexity: O(1)
         */
        public int getKey() {
            return this.key;
        }

        public String getValue() {
            return this.info; // to be replaced by student code
        }

        public void setLeft(IAVLNode node) {
            //TODO: think about setting height, etc
            this.left = (AVLNode) node;
        }

        /**
         * Returns the left child of this node
         * <p>
         * Time complexity: O(1)
         */
        public IAVLNode getLeft() {
            return left; // to be replaced by student code
        }

        public void setRight(IAVLNode node) {
            //TODO: think about setting height, etc
            this.right = (AVLNode) node;
        }

        /**
         * Returns the right child of this node
         * <p>
         * Time complexity: O(1)
         */
        public IAVLNode getRight() {
            return right; // to be replaced by student code
        }

        /**
         * Updates the parent of this node
         * <p>
         * Time complexity: O(1)
         */
        public void setParent(IAVLNode node) {
            this.parent = (AVLNode) node;
        }

        /**
         * Returns the parent of this node
         * <p>
         * Time complexity: O(1)
         */
        public IAVLNode getParent() {
            return this.parent;
        }

        // Returns True if this is a non-virtual AVL node
        public boolean isRealNode() {
            return !this.isExternal; // to be replaced by student code
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return this.height;
        }
    }

}
  

