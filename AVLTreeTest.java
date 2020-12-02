import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

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
        assertEquals(0, t2.insert(18, "18"));
        assertEquals(2, t2.insert(22, "22")); // should make two rotation here
        assertEquals(3, t2.size());
        BTreePrinter.printNode(t2.getRoot());
    }

    @org.junit.jupiter.api.Test
    void rotate() {
        AVLTree tree = new AVLTree();
        tree.insert(43, "43");
        tree.insert(18, "18");
        tree.insert(22, "22");
        BTreePrinter.printNode(tree.getRoot());
//        tree.rotate(tree.getRoot().getLeft(), 'L');
//        tree.rotate(tree.getRoot(), 'R');
        BTreePrinter.printNode(tree.getRoot());
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


}