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
    private int nodes;
    AVLNode externalLeaf;

    /*
     * public AVLTree()
     *AVL Tree constructor, includes initializing external leaf
     *O(1)
     */
    public AVLTree() {
        //initialize external leaf with isExternal = true
        this.externalLeaf = new AVLNode("", -1);
        this.externalLeaf.setIsExternal(true);
        this.externalLeaf.setHeight(-1); // important to get the balance afterwards
        this.externalLeaf.setSize(0); //important to get the size afterwards
        this.root = null;
        this.nodes = 0;
    }

    /**
     * public boolean empty()
     * returns true if and only if the tree is empty
     * Complexity: O(1)
     */
    public boolean empty() {
        return getRoot() == null;
    }

    /**
     * public String search(int k)
     * returns the info of an item with key k if it exists in the tree
     * otherwise, returns null
     * Complexity: O(logn)
     */
    public String search(int k) {
        IAVLNode n = search(getRoot(), k); // O(logn)
        if (n == null) return null;
        if (!n.isRealNode()) return null; //O(1)
        return n.getValue();
    }

    /**
     * private IAVLNode search(IAVLNode root, int k)
     * Recursive function for the above search (method overloading) - searches for node with key k in the subtree of root
     * Each iteration is O(1), number of recursions in worst case is O(h) = O(logn) recursive calls
     * Complexity: O(logn)
     */
    public IAVLNode search(IAVLNode root, int k) {
        if (root == null) return null;
        if (!root.isRealNode()) return null;
        if (root.getKey() == k) return root;
        if (root.getKey() < k) return search(root.getRight(), k);
        return search(root.getLeft(), k);
    }


    /**
     * AVLNode createNewNode(int k, String i)
     * This method creates a new node with key `k` and info `i`
     * Complexity: O(1) [same as AVLNode constructor]
     */
    AVLNode createNewNode(int k, String i) {
        AVLNode n = new AVLNode(i, k);
        n.setLeft(this.externalLeaf);
        n.setRight(this.externalLeaf);
        return n;
    }

    /**
     * public int insert(int k, String i)
     * inserts an item with key k and info i to the AVL tree.
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
     * promotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
     * returns -1 if an item with key k already exists in the tree.
     * Complexity: O(logn)
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
            b = newNode;
            this.setRoot(b);
        } else {
            if (k == b.getKey()) {
                return -1;
            }
            // If the key is greater then the leaf node key
            // Assign the new node to be its right child
            if (k > b.getKey()) {
                b.setRight(newNode);
            }

            // If the new key is less then the leaf node key
            // Assign the new node to be its left child
            else {
                b.setLeft(newNode);
            }
            newNode.setParent(b);
        }
        if (updateHeight(b)) counter++;
        updateNodeSize(b);

        IAVLNode p = newNode;
        while (p != null) {
            int actions = rebalance(p);
            if (actions > 0) {
                counter += actions;
            }
            if (updateHeight(p)) counter++;
            updateNodeSize(p); // TODO: not sure this is ideal location within loop, in any case it's constant number of operations
            p = p.getParent();
        }
        if (updateHeight(getRoot())) counter++; // TODO: not sure necessary after change in loop, in any case it's constant number of operations
        this.nodes++; // increment the number of nodes
        return counter;
    }

    /**
     * public int rebalance(IAVLNode n)
     * Rebalancing a single node - check if any rotation is needed.
     * Returns number of balancing operations needed, including promotions (single rotation - 2 actions, double rotation - 5 actions)
     * If no rebalancing actions are needed - returns 0.
     * Complexity: O(1)
     */
    public int rebalance(IAVLNode n) {
        int counter = 0;
        int balance = getBalance(n);
        if (balance > 1) {
            // heavy on the left
            if (getBalance(n.getLeft()) < 0) {
                // This is LR case
                counter = 5; // add the rotation to counter
                rotate(n.getLeft(), 'L');
            } else {
                counter = 2; // add the rotation to counter
                //TODO: single rotation for 'delete' should be 3, while for 'insert' it should be 2
            }
            rotate(n, 'R');
        } else if (balance < -1) {
            // heavy on the right
            if (getBalance(n.getRight()) > 0) {
                // This is RL case
                counter = 5;
                //TODO: single rotation for 'delete' should be 3, while for 'insert' it should be 2
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
     * deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
     * demotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
     * returns -1 if an item with key k was not found in the tree.
     * Complexity: O(logn)
     */
    public int delete(int k) {
        IAVLNode toDelete = search(this.root, k);
        int counter = 0;

        // k is not in tree
        if (toDelete == null) return -1;
        else if (!toDelete.isRealNode()) return -1;

        // case 'k is root' handled within the function
        IAVLNode p = toDelete.getParent(); //rank problem starts from p, could be null if k is root

        if (!toDelete.getLeft().isRealNode() && !toDelete.getRight().isRealNode()) { // k is a leaf
            deleteLeaf(toDelete);
        } else if (toDelete.getLeft().isRealNode() && !toDelete.getRight().isRealNode()) { // k only has left child
            deleteUnary(toDelete, 'L');
        } else if (toDelete.getRight().isRealNode() && !toDelete.getLeft().isRealNode()) { //k only has right child
            deleteUnary(toDelete, 'R');
        } else { // k is a binary node
            IAVLNode suc = successor(toDelete);
            toDelete.setInfo(suc.getValue());
            toDelete.setKey(suc.getKey());
            p = suc.getParent();
            if (suc.getLeft().isRealNode() && suc.getRight().isRealNode())  //suc is a leaf
                deleteLeaf(suc);
            else
                deleteUnary(suc, 'R');
        }

        //rebalance from p upwards
        if (updateHeight(p))
            counter++;
        updateNodeSize(p);
        IAVLNode node = p;
        while (node != null) {
            int actions = rebalance(node);
            if (actions > 0) counter += actions;
            if (updateHeight(node)) counter++;
            updateNodeSize(node); // TODO: not sure this is ideal location within loop, in any case it's constant number of operations
            node = node.getParent();
        }

        this.nodes--;

        return counter;
    }

    /**
     * public void deleteLeaf (IAVLNode x)
     * receives IAVLNode x indicating leaf node to be deleted
     * deletes x and updates pointers
     * Complexity: O(1)
     */
    public void deleteLeaf(IAVLNode x) {
        IAVLNode p = x.getParent();
        if (p == null) {
            this.root = null;
            return;
        }

        // checking if this is right or left child
        // and replacing with external leaf
        if (p.getKey() > x.getKey()) p.setLeft(this.externalLeaf);
        else p.setRight(this.externalLeaf);
    }


    /**
     * public void deleteUnary (IAVLNode x, char c)
     * receives IAVLNode x indicating unary node to be deleted, and char c indicating on which side is x's child
     * deletes x and updates pointers
     * Complexity: O(1)
     */
    public void deleteUnary(IAVLNode x, char c) {
        IAVLNode p = x.getParent(); //p could be null if x is root
        IAVLNode child = c == 'L' ? x.getLeft() : x.getRight();
        child.setParent(p);

        if (p != null) { // x isn't root
            boolean isLeftChild = (p.getLeft() == x);
            if (isLeftChild)
                p.setLeft(child);
            else
                p.setRight(child);
        } else { // x is the root, the child is the new root of the tree
            setRoot(child);
        }

    }


    /*
     * public IAVLNode successor (IAVLNode x)
     * returns the item following x according to the sorted order of keys.
     * If x has the largest key - returns null.
     *     Complexity (for AVL): O(logn)
     */

    public IAVLNode successor(IAVLNode x) {
        if (x.getRight().isRealNode()) {
            return this.minSubtree(x.getRight());
        }
        IAVLNode y = x.getParent();
        while (y != null && x == y.getRight()) {
            x = y;
            y = x.getParent();
        }
        return y;
    }

    /**
     * public IAVLNode minSubtree(IAVLNode x)
     * returns the IAVLNode with minimum value of x's subtree
     * Complexity (for AVL): O(logn)
     */
    public IAVLNode minSubtree(IAVLNode x) {
        while (!x.getLeft().isRealNode()) {
            x = x.getLeft();
        }
        return x;
    }

    /**
     * public String min()
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty
     * Complexity: O(logn)
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
//        if (this.empty()) return null;
//        IAVLNode n = minSubtree(this.getRoot());
//        if (n == null) return null;
//        return n.getValue();
    }

    /**
     * public String max()
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty
     * Complexity: O(logn)
     */
    public String max() {
        // traverse all the way right
        if (empty()) return null;
        IAVLNode n = getRoot();
        IAVLNode a = null;
        while (n != null && n.isRealNode()) {
            a = n;
            n = n.getRight();
        }
        if (a == null) return null;
        return a.getValue();
    }

    /**
     * public int[] keysToArray()
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     * Complexity: O(n)
     */
    public int[] keysToArray() {
        int[] arr = new int[this.size()];
        ListIterator<IAVLNode> listIterator = inOrderTraversal(getRoot()).listIterator();
        int i = 0;
        while (listIterator.hasNext()) {
            IAVLNode n = listIterator.next();
            arr[i] = n.getKey();
            i++;
        }
        return arr;
    }


    /**
     * public String[] infoToArray()
     * Returns an array which contains all info in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     * Complexity: O(n)
     */
    public String[] infoToArray() {
        String[] arr = new String[this.size()];
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
     * Returns the number of nodes in the tree.
     * precondition: none
     * postcondition: none
     * Complexity: O(1)
     */
    public int size() {
        if (this.empty()) return 0;
        return this.root.getSize();
    }

    /**
     * public int getRoot()
     * Returns the root AVL node, or null if the tree is empty
     * precondition: none
     * postcondition: none
     * Complexity: O(1)
     */
    public IAVLNode getRoot() {
        return this.root;
    }

    /**
     * This method returns the rank of the tree.
     * empty tree rank is -1
     * Complexity: O(1)
     */
    public int getRank() {
        if (this.empty()) return -1;
        return getRoot().getHeight();
    }

    public String toString() {
        IAVLNode root = this.getRoot();
        return "Tree root: " + (root != null ? root.getValue() : "EMPTY");
    }

    /**
     * public AVLTree toTree (IAVLNode root)
     * Create a new tree from given node x and sets x to be its root
     * if x is null/external node, returns empty tree
     * Complexity: O(1)
     *
     * @return
     */
    public static AVLTree toTree(IAVLNode x) {
        AVLTree t = new AVLTree();
        if (x.isRealNode()) {
            t.setRoot(x);
        }
        x.setParent(null);
        return t;
    }

    /**
     * public string split(int x)
     * splits the tree into 2 trees according to the key x.
     * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
     * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
     * postcondition: none
     * Complexity: O(logn)
     */
    public AVLTree[] split(int x) {
        // tree shouldn't be used after this: https://moodle.tau.ac.il/mod/forum/discuss.php?d=39446
        IAVLNode n = search(getRoot(), x); // get the node with key x
        AVLTree small = toTree(n.getLeft());
        AVLTree big = toTree(n.getRight());
        // node sizes unchanged

        IAVLNode parent = n.getParent();
        while (parent != null) {
            // we don't want to mess the current node, so create new one with same values
            IAVLNode replacer = createNewNode(parent.getKey(), parent.getValue());
            if (parent.getKey() > n.getKey()) {
                // current node is left child => smaller than parent => we want to join with big
                big.join(replacer, toTree(parent.getRight()));
            } else {
                // current node is right child => bigger than parent => we want to join with small
                small.join(replacer, toTree(parent.getLeft()));
            }
            updateHeight(replacer);
            updateNodeSize(replacer);
            parent = parent.getParent();
        }
        return new AVLTree[]{small, big};
    }

    /**
     * public join(IAVLNode x, AVLTree t)
     * joins t and x with the tree.
     * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
     * precondition: keys(x,t) < keys() or keys(x,t) > keys(). t/tree might be empty (rank = -1).
     * postcondition: none
     * Complexity: O(logn)
     */
    public int join(IAVLNode x, AVLTree t) {
        if (t.empty()) {
            int rank = this.getRank();
            this.insert(x.getKey(), x.getValue());
            return rank + 1;
        }
        if (this.empty()) {
            this.setRoot(t.getRoot());
            int rank = this.getRank();
            this.nodes = t.size();
            this.insert(x.getKey(), x.getValue());
            return rank + 1;
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
        updateNodeSize(x);
        left.setParent(x);
        right.setParent(x);
        x.setParent(c);
        if (c == null) {
            // x is new root, and tree is balanced
            this.setRoot(x);
            return 1;
        } else if (biggerKeys > 0)
            c.setRight(x);
        else
            c.setLeft(x);
        IAVLNode p = x;
        while (p != null) {
            if (p.getParent() == null) {
                this.setRoot(p);
            }
            rebalance(p);
            updateHeight(p);
            updateNodeSize(p);

            p = p.getParent();

        }
        this.nodes += 1 + t.size(); // update the amount of nodes (x + all the nodes of t);
        return Math.abs(rankDiff) + 1;
    }

    /**
     * public void setRoot (IAVLNode newRoot)
     * Set the tree root to be newRoot
     * Time complexity: O(1)
     */
    public void setRoot(IAVLNode newRoot) {
        this.root = newRoot;
    }

    /**
     * private boolean updateHeight(IAVLNode n)
     * Updating the node height if necessary (in-place).
     * Returns true if height changed, false otherwise.
     * Complexity: O(1)
     */
    private boolean updateHeight(IAVLNode n) {
        if (n == null) return false;
        if (!n.isRealNode()) return false;
        int formerHeight = n.getHeight();
        n.setHeight(Math.max(n.getLeft().getHeight(), n.getRight().getHeight()) + 1);
        return formerHeight != n.getHeight();
    }

    /**
     * public IAVLNode rotate(IAVLNode root, char type)
     * Perform single rotation as part of a rebalancing process.
     * Receives current root of subtree (before rotation) and type of rotation ('R' or 'L').
     * Returns new root.
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

    /**
     * public void updateParentsAfterRotation(IAVLNode newRoot, IAVLNode oldRoot)
     * Updates new & old root pointers AND their sizes.
     * Complexity: O(1)
     */
    public void updateParentsAfterRotation(IAVLNode newRoot, IAVLNode oldRoot) {
        newRoot.setParent(oldRoot.getParent());
        oldRoot.setParent(newRoot);
        if (newRoot.getParent() == null) //oldRoot was tree root
            setRoot(newRoot);
        else if (newRoot.getParent().getKey() > newRoot.getKey()) newRoot.getParent().setLeft(newRoot);
        else newRoot.getParent().setRight(newRoot);
        //update size of both nodes - they are the only ones that change during rotation
        updateNodeSize(oldRoot); //have to update 'lower' node first
        updateNodeSize(newRoot);
    }

    /**
     * public IAVLNode rotateLeft(IAVLNode oldRoot)
     * Rotates subtree rooted with oldRoot to the left.
     * Returns new root (after rotation).
     * Complexity: O(1)
     */
    public IAVLNode rotateLeft(IAVLNode oldRoot) {
        IAVLNode newRoot = oldRoot.getRight();
        IAVLNode z = newRoot.getLeft();

        newRoot.setLeft(oldRoot);
        oldRoot.setRight(z);
        z.setParent(oldRoot);
        updateParentsAfterRotation(newRoot, oldRoot);

        updateHeight(oldRoot);
        updateHeight(newRoot);
        return newRoot;
    }

    /**
     * public IAVLNode rotateRight(IAVLNode oldRoot)
     * Rotates subtree rooted with oldRoot to the right.
     * Returns new root (after rotation).
     * Complexity: O(1)
     */
    public IAVLNode rotateRight(IAVLNode oldRoot) {
        IAVLNode newRoot = oldRoot.getLeft();
        IAVLNode z = newRoot.getRight();

        newRoot.setRight(oldRoot);
        oldRoot.setLeft(z);
        z.setParent(oldRoot);
        updateParentsAfterRotation(newRoot, oldRoot);

        updateHeight(oldRoot);
        updateHeight(newRoot);
        return newRoot;
    }

    /**
     * public int getBalance(IAVLNode node)
     * Receives node and returns the height difference between its left child and its right child.
     * If node is null or external leaf - returns 0.
     * Complexity: O(1)
     */
    public int getBalance(IAVLNode node) {
        if (node == null || !node.isRealNode()) return 0;
        return (node.getLeft().getHeight() - node.getRight().getHeight());

    }

    /**
     * public LinkedList<IAVLNode> inOrderTraversal(IAVLNode root)
     * Recursive function, returns a linked list of in-order traversal of a subtree rooted in ``root``.
     * If tree is empty - returns empty list.
     * Complexity: O(n)
     */
    public LinkedList<IAVLNode> inOrderTraversal(IAVLNode root) {
        LinkedList<IAVLNode> list = new LinkedList<IAVLNode>();
        if (root == null)
            return list; //return empty list
        if (!root.isRealNode())
            return list; // reached externl leaf
        list.addAll(inOrderTraversal(root.getLeft())); // add all = O(1)
        list.add(root); // O(1)
        list.addAll(inOrderTraversal(root.getRight())); // add all = O(1)
        return list;
    }

    /**
     * public void updateNodeSize(IAVLNode node)
     * Receives node and updates its size field, according to sizes of its childrens' subtrees.
     * Complexity: O(1)
     */
    public void updateNodeSize(IAVLNode node) {
        if (node != null && node.isRealNode()) {
            node.setSize(node.getLeft().getSize() + node.getRight().getSize() + 1);
        }
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

        public void setInfo(String info); // sets the info of the node

        public void setKey(int key); // sets the key of the node

        public int getSize(); // returns value of 'size' field

        public void setSize(int s); // sets value of 'size' field


    }

    /**
     * public class AVLNode
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

        /**
         * public AVLNode(String info, int key)
         * AVLNode constructor - receives info and key and generates AVLNode instance with those parameters.
         * Complexity: O(1)
         */
        public AVLNode(String info, int key) {
            this.info = info;
            this.key = key;
            this.isExternal = false;
            this.height = 0;

        }

        /**
         * public void setInfo(String info)
         * Receives String info and sets the instance's 'info' field to it.
         * Complexity: O(1)
         */
        public void setInfo(String info) {
            this.info = info;
        }

        /**
         * public void setKey (int key)
         * Receives int key and sets the instance's 'key' field to it.
         * Complexity: O(1)
         */
        public void setKey(int key) {
            this.key = key;
        }

        /**
         * public String toString()
         * Returns string including instance's key and info
         * Complexity: O(1)
         */
        public String toString() {
            return this.key + ": " + this.info;
        }

        /**
         * public void setIsExternal (boolean b)
         * Receives boolean b and sets the instance's 'isExternal' field to it.
         * Complexity: O(1)
         */
        public void setIsExternal(boolean b) {
            this.isExternal = b;
        }

        /**
         * public int getKey()
         * Returns the `key` value
         * Complexity: O(1)
         */
        public int getKey() {
            return this.key;
        }

        /**
         * public String getValue()
         * Returns the `info` value
         * Complexity: O(1)
         */
        public String getValue() {
            return this.info;
        }

        /**
         * public void setLeft(IAVLNode node)
         * sets the 'left' pointer to received node
         * Complexity: O(1)
         */
        public void setLeft(IAVLNode node) {
            this.left = (AVLNode) node;
        }

        /**
         * public IAVLNode getLeft()
         * Returns the left child of this node
         * Time complexity: O(1)
         */
        public IAVLNode getLeft() {
            return left; // to be replaced by student code
        }

        /**
         * public void setRight(IAVLNode node)
         * sets the 'right' pointer to received node
         * Complexity: O(1)
         */
        public void setRight(IAVLNode node) {
            //TODO: think about setting height, etc
            this.right = (AVLNode) node;
        }

        /**
         * public IAVLNode getRight()
         * Returns the right child of this node
         * Complexity: O(1)
         */
        public IAVLNode getRight() {
            return right; // to be replaced by student code
        }

        /**
         * public void setParent(IAVLNode node)
         * Updates the parent of this node
         * Complexity: O(1)
         */
        public void setParent(IAVLNode node) {
            if (node == this) {
                return; // don't do anything stupid
            }
            this.parent = (AVLNode) node;
        }

        /**
         * public IAVLNode getParent()
         * Returns the parent of this node
         * Complexity: O(1)
         */
        public IAVLNode getParent() {
            return this.parent;
        }

        /**
         * public boolean isRealNode()
         * Returns True iff this is a non-virtual AVL node AKA external leaf
         * Complexity: O(1)
         */
        public boolean isRealNode() {
            return !this.isExternal; // to be replaced by student code
        }

        /**
         * public void setHeight(int height)
         * Updates the height of this node
         * Complexity: O(1)
         */
        public void setHeight(int height) {
            this.height = height;
        }

        /**
         * public int getHeight()
         * Returns the parent of this node
         * Complexity: O(1)
         */
        public int getHeight() {
            return this.height;
        }

        /**
         * public int getSize()
         * Returns the size of this node i.e. the size of its subtree
         * Complexity: O(1)
         */
        public int getSize() {
            return this.size;
        }

        /**
         * public void setSize(int size)
         * Returns the size of this node i.e. the size of its subtree
         * Complexity: O(1)
         */
        public void setSize(int s) {
            this.size = s;
        }


    }

}
  

