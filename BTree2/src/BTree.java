public class BTree<T extends Comparable<T>> {
    BTreeNode<T> root;     // Wskaźnik na korzeń
    int t;              // Minimalna ilość elementów w wierzchołku

    public BTree(int t) {
        root = null;
        this.t = t;
    }

    // Wypisuje drzewo w porządku rosnącym
    public void traverse() {
        if (root != null)
            root.traverse();
    }

    // Funkcja szukająca klucza w drzewie
    public BTreeNode search(T k) {
        if (root == null) {
            return null;
        } else {
            return root.search(k);
        }
    }

    // Funkcja wkładająca klucz do drzewa.
    public void insert(T k) {
        // Jeśli drzewo jest puste
        if (root == null) {
            // Zaalokuj pamięć na nowy korzeń
            root = new BTreeNode<>(t, true);
            root.keys[0] = k;   // Umieść klucz do nowo-utworzonego korzenia.
            root.n = 1;           // Zaktualizuj liczbę kluczy w korzeniu.
        } else // Drzewo nie jest puste.
        {
            // Jeśli korzeń jest pełny, to wysokość drzewa wzrasta.
            if (root.n == 2 * t - 1) {
                // Zaalokuj pamięć na nowy korzeń.
                BTreeNode s = new BTreeNode(t, false);

                // Umieść stary korzeń jako dziecko nowego korzenia.
                s.C[0] = root;

                // Podziel stary korzeń i umieść jeden klucz w nowym korzeniu
                s.splitChild(0, root);

                // Nowy korzeń ma teraz dwójkę dzieci. Decydujemy które z dzieci
                // będzie miało w sobie nowy (umieszczany) klucz.
                int i = 0;

                if (s.keys[0].compareTo(k) < 0)
                    i++;
                s.C[i].insertNonFull(k);

                // Zamień korzeń
                root = s;
            } else // Jeśli korzeń nie jest pełny, włóż po prostu do korzenia
            {
                root.insertNonFull(k);
            }
        }
    }

    // Funkcja usuwająca klucz z drzewa.
    public void remove(T k) {
        if (root == null) {
            System.out.println("Drzewo jest puste. Nie można usunąć");
            return;
        }

        // Wywołanie funkcji usuwającej dla korzenia.
        root.remove(k);
        // Jeśli po usunięciu korzeń ma 0 elementów, to ustaw jego pierwsze dziecko
        // jako nowy korzeń. W przeciwnym wypadku ustaw korzeń na null.
        if (root.n == 0) {
            BTreeNode tmp = root;
            if (root.leaf)
                root = null;
            else
                root = root.C[0];
        }
    }
}
