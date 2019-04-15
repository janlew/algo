public class Main {

    public static void main(String[] args) {

        BTree<Integer> t = new BTree<>(3);
        t.insert(1);
        t.insert(9);
        t.insert(5);
        t.insert(41);
        t.insert(21);
        t.insert(14);
        t.insert(7);
        t.insert(32);
        t.insert(15);
        t.insert(22);

        t.insert(3);
        t.insert(6);
        t.insert(8);
        t.insert(24);
        t.insert(33);
        t.insert(66);


        System.out.println("---------------------");
        t.search(6);
        System.out.println("---------------------");
        t.search(9);
        System.out.println("---------------------");

        System.out.println("================");
        t.root.print();
        System.out.println("================");

        t.remove(9);
        t.traverse();
        System.out.println();

        System.out.println("================");
        t.root.print();
        System.out.println("================");


    }
}
