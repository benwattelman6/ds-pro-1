import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    @org.junit.jupiter.api.Test
    void insert() {
        // TODO: Do real testing for this after implementing the rebalance & height

        AVLTree tree = new AVLTree();
        for (int i = 5; i > 0; i--) {
            tree.insert(i, "A key");
        }
        for (int i = 0; i < 10; i++) {
            tree.insert(i, "B key");
        }
    }
}