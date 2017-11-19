package thread.safe;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTest {
    static Node<Integer> node;

    @BeforeClass
    public static void makeInstance() throws Exception {
        node = new Node<Integer>(0);
    }
}