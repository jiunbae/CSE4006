public class Main {
    public static void main(String[] args) {
        Integer[] a = { 1, 5, 2, 7, 4, 10, 15, 11, 13, 20, 9 };
        BST<Integer> bst = new BST();

        for (Integer n : a) {
            bst.insert(n);
        }

        bst.inOrderTraversal(System.out::println);
        System.out.println();

        bst.delete(1);
        System.out.println("min:"+bst.findMin());

        bst.delete(5);
        System.out.println("min:"+bst.findMin());

        bst.delete(2);
        System.out.println("min:"+bst.findMin());

        bst.delete(15);
        System.out.println("min:"+bst.findMin());

        bst.delete(10);
        System.out.println("min:"+bst.findMin());

        bst.delete(4);
        System.out.println("min:"+bst.findMin());

        bst.inOrderTraversal(System.out::println);
        System.out.println();

        bst.delete(7);
        System.out.println("min:"+bst.findMin());

        bst.delete(9);
        System.out.println("min:"+bst.findMin());

        bst.inOrderTraversal(System.out::println);
        System.out.println();

        bst.preOrderTraversal(System.out::println);
    }
}
