import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {



    @org.junit.jupiter.api.Test
    void init() {

        AVLTree t1 = new AVLTree();
        assertTrue(t1.empty());
        assertEquals(-1, t1.getRank());
        assertNull(t1.getRoot());
        assertNull(t1.min());
        assertNull(t1.max());
        assertFalse(t1.externalLeaf.isRealNode());
    }

    @org.junit.jupiter.api.Test
    void createNewNode() {
        AVLTree t1 = new AVLTree();
        AVLTree.IAVLNode n = t1.createNewNode(10, "funny info");
        assertEquals(10, n.getKey());
        assertEquals("funny info", n.getValue());
        assertEquals(0, n.getHeight());
        assertTrue(n.isRealNode());
        assertFalse(n.getLeft().isRealNode());
        assertFalse(n.getRight().isRealNode());
    }


    @org.junit.jupiter.api.Test
    void insert() {
        // TODO: Do real testing for this after implementing the rebalance & height

        AVLTree t1 = new AVLTree();
        assertEquals(0, t1.size());

        assertEquals(0, t1.insert(1, "1"));
        assertEquals(1, t1.size());
        assertEquals(-1, t1.insert(1, "1"));
        assertEquals(1, t1.size());

        AVLTree t2 = new AVLTree();
        assertEquals(0, t2.insert(43, "43"));
        assertEquals(1, t2.insert(18, "18"));
        assertEquals(6, t2.insert(22, "22")); // should make two rotation here
        assertEquals(3, t2.size());
        BTreePrinter.printNode(t2.getRoot());

    }

    @org.junit.jupiter.api.Test
    void rotate() {
        AVLTree tree = new AVLTree();
        AVLTree.IAVLNode[] a = new AVLTree.IAVLNode[10];

        for (int i = 0; i < 10; i++) {
            a[i] = tree.createNewNode(i, "info for " + i);
        }
        for (int i = 1; i < 10; i++) {
            a[i].setParent(a[(i - 1) / 2]);
            if (2 * i + 1 < 10) a[i].setLeft(a[2 * i + 1]);
            if (2 * i + 2 < 10) a[i].setRight(a[2 * i + 2]);
        }
        a[0].setLeft(a[1]);
        a[0].setRight(a[2]);
        tree.setRoot(a[0]);
        tree.rotateRight(tree.getRoot());
        assertEquals(tree.getRoot(), a[1]);
        assertEquals(a[0].getParent(), a[1]);
        assertNull(a[1].getParent());
        assertEquals(a[4], a[0].getLeft());
        assertEquals(a[0], a[4].getParent());
        tree.rotateLeft(tree.getRoot());
        assertEquals(a[0], tree.getRoot());
        assertEquals(a[0], a[1].getParent());
        assertNull(a[0].getParent());
        assertEquals(a[4], a[1].getRight());
        assertEquals(a[1], a[4].getParent());
    }

    @org.junit.jupiter.api.Test
    void empty() {
        AVLTree t1 = new AVLTree();
        assertTrue(t1.empty());
        t1.insert(1, "1");
        assertFalse(t1.empty());
        // todo check after delete
    }


    @org.junit.jupiter.api.Test
    void search() {
        AVLTree t1 = new AVLTree();
        for (int i = 1; i < 20; i++) {
            t1.insert(i, "Key is " + i);
        }
        System.out.println(t1.getRoot().getKey());
        for (int i = 1; i < 20; i++) {
            assertEquals("Key is " + i, t1.search(i));
        }
        assertNull(t1.search(50));
    }

    @org.junit.jupiter.api.Test
    void min() {
        AVLTree t1 = new AVLTree();
        for (int i = 1; i < 20; i++) {
            t1.insert(i, "Key is " + i);
        }
        assertEquals("Key is 1", t1.min());
    }

    @org.junit.jupiter.api.Test
    void max() {
        AVLTree t1 = new AVLTree();
        for (int i = 1; i < 20; i++) {
            t1.insert(i, "Key is " + i);
        }
        assertEquals("Key is 19", t1.max());
    }

    @org.junit.jupiter.api.Test
    void inOrderTraversal() {
        AVLTree t1 = new AVLTree();
        for (int i = 1; i < 20; i++) {
            t1.insert(i, "Key is " + i);
        }
        LinkedList<AVLTree.IAVLNode> a = t1.inOrderTraversal(t1.getRoot());
        for (int i = 1; i < 20; i++) {
            assertEquals(i, a.get(i - 1).getKey());
        }
    }

    @org.junit.jupiter.api.Test
    void keysToArray() {
        AVLTree t1 = new AVLTree();
        for (int i = 1; i < 20; i++) {
            t1.insert(i, "Key is " + i);
        }
        int[] res = t1.keysToArray();
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19}, res);
    }

    @org.junit.jupiter.api.Test
    void join() {
        AVLTree t1 = new AVLTree();
        AVLTree t2 = new AVLTree();
        AVLTree t3 = new AVLTree();
        for (int i = 1; i < 5; i++) {
            t1.insert(i, "Key is " + i);
        }
        for (int i = 9; i < 30; i++) {
            t2.insert(i, "Key is " + i);
        }
        t3.insert(7, "7");
//        t2.join(t3.getRoot(), t1);

        AVLTree t4 = new AVLTree();

        t4.join(t3.getRoot(), t2);
        System.out.println("");
    }

}