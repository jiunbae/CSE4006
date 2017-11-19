public class Main {
    public static void main(String[] args) {
        Integer[] a = { 1, 5, 2, 7, 4, 10, 15, 11, 13, 20, 9 };
        BinaryTree<Integer> binaryTree = new BinaryTree();

        for (Integer n : a) {
            binaryTree.insert(n);
        }

        binaryTree.inOrderTraversal(System.out::println);
        System.out.println();

        binaryTree.delete(1);
        System.out.println("min:"+ binaryTree.findMin());

        binaryTree.delete(5);
        System.out.println("min:"+ binaryTree.findMin());

        binaryTree.delete(2);
        System.out.println("min:"+ binaryTree.findMin());

        binaryTree.delete(15);
        System.out.println("min:"+ binaryTree.findMin());

        binaryTree.delete(10);
        System.out.println("min:"+ binaryTree.findMin());

        binaryTree.delete(4);
        System.out.println("min:"+ binaryTree.findMin());

        binaryTree.inOrderTraversal(System.out::println);
        System.out.println();

        binaryTree.delete(7);
        System.out.println("min:"+ binaryTree.findMin());

        binaryTree.delete(9);
        System.out.println("min:"+ binaryTree.findMin());

        binaryTree.inOrderTraversal(System.out::println);
        System.out.println();

        binaryTree.preOrderTraversal(System.out::println);
    }
}
