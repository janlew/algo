public class BTreeNode<T extends Comparable<T>> {
    Comparable<T> keys[];     // Tablica kluczy.
    int t;          // Minimalna ilość kluczy jaka może się znajdować w Wierzchołku.
    BTreeNode C[];  // Tablica dzieci.
    int n;          // Aktualna liczba kluczy.
    boolean leaf;   // Prawda, gdy wierzchołek jest liściem. W przeciwnym przypadku fałsz.

    BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;

        // Zaalokowanie pamięci dla maksymalnej liczby możliwych kluczy
        // i tablica na Dzieci wierzchołka.
        keys = new Comparable[2 * t - 1];
        C = new BTreeNode[2 * t];

        // Inicjalizacja aktualnej liczby kluczy w wierzchołku
        n = 0;
    }

    // Funkcja pomocnicza, któa zwraca indeks pierwszego klucza, który jest
    // większy lub równy 'k'
    int findKey(T k) {
        int idx = 0;
        //while( idx < n && keys[ idx ] < k )
        while (idx < n && keys[idx].compareTo(k) < 0)
            idx++;
        return idx;
    }

    // Funkcja do usuwania klucza 'k' z poddrzewa zakorzenionego w tym wierzchołku.
    void remove(T k) {
        int idx = findKey(k);

        // Klucz do usunięcia znajduje się w tym wierzchołku.
        if (idx < n && keys[idx].compareTo(k) == 0) {
            // Jeśli wierzchołek jest liściem, wywoływane jest 'removeFromLeaf'
            // w przeciwnym przypadku 'removeFromNonLeaf'
            if (leaf)
                removeFromLeaf(idx);
            else
                removeFromNonLeaf(idx);
        } else {
            // Jeśli ten wierzchołek jest liściem, to znaczy, że klucza do usunięcia tutaj nie ma.
            if (leaf) {
                System.out.println("Klucza, który chcesz usunąć, nie ma w drzewie");
                return;
            }

            // Klucz do usunięcia znajduje się w poddrzewie zakorzenionym w tym wierzchołku.
            // 'flag' sygnalizuje czy klucz znajduje się w poddrzewie zakorzenionym w ostatnim
            // dziecku tego wierzchołka (wskaźnik skrajnie na prawo).
            boolean flag = (idx == n);

            // Jeśli dziecko, w którym znajduje się klucz, ma mniej niż 't' kluczy, wypełniamy to dziecko
            if (C[idx].n < t)
                fill(idx);

            // Jeśli ostatnie dziecko zostało złączone, to musiało się złączyć z wierzchołkiem obok (na lewo),
            // więc klucz znajduje się w [idx-1] dziecku i stamtąd go usuwamy. W przeciwnym przypadku [ idx ]
            if (flag && idx > n)
                C[idx - 1].remove(k);
            else
                C[idx].remove(k);
        }
    }

    // Funkcja do usuwania klucza o indeksie 'idx' z wierzchołka, który jest liściem.
    void removeFromLeaf(int idx) {
        // Przesuń wszystkie klucza za 'idx' o jedno miejsce w lewo.
        for (int i = idx + 1; i < n; i++)
            keys[i - 1] = keys[i];

        // Zmniejsz liczbę kluczy;
        n--;
    }

    // Funkcja do usuwania klucza o indeksie 'idx' z wierzchołka, który NIE jest liściem.
    void removeFromNonLeaf(int idx) {
        T k = (T) keys[idx];

        if (C[idx].n >= t) {
            T pred = getPred(idx);
            keys[idx] = pred;
            C[idx].remove(pred);
        } else if (C[idx + 1].n >= t) {
            T succ = getSucc(idx);
            keys[idx] = succ;
            C[idx + 1].remove(succ);
        } else {
            merge(idx);
            C[idx].remove(k);
        }
    }

    // Funkcja zwracająca poprzednika z keys[idx]
    T getPred(int idx) {
        // Poruszamy się prawymi wskaźnikami, aż nie dotrzemy do liścia.
        BTreeNode<T> cur = C[idx];
        while (!cur.leaf)
            cur = cur.C[cur.n];

        // Zwraca ostatni klucz liścia.
        return (T) cur.keys[cur.n - 1];
    }

    T getSucc(int idx) {
        // Poruszamy się lewymi wierzchołkami, aż nie dotrzemy do liścia.
        BTreeNode cur = C[idx + 1];
        while (!cur.leaf)
            cur = cur.C[0];

        // Zwróc pierwszy klucz liścia.
        return (T) cur.keys[0];
    }

    // Funkcja wypełniająca dziecko, które ma mniej niż t-1 kluczy
    void fill(int idx) {
        // Jeśli poprzednie dzieclko (C[idx-1]) ma więcej niż t-1 kluczy, zabierz klucz od tego dziecka.
        if (idx != 0 && C[idx - 1].n >= t)
            borrowFromPrev(idx);

            // Jeśli następne dziecko (C[idx+1]) ma więcej niż t-1 kluczy, zabierz klucz od tego dziecka.
        else if (idx != n && C[idx + 1].n >= t)
            borrowFromNext(idx);

            // Łączenie C[idx] z jego potąkiem.
            // Jeśli C[idx] jest ostatnim dzieckiem, połącz go z poprzednim potomkiem.
            // W przeciwnym wypadku połącz go z następnym potomkiem.
        else {
            if (idx != n)
                merge(idx);
            else
                merge(idx - 1);
        }
    }

    // Funkcja do zabierania klucza z C[idx-1] i wkładania go do C[idx]
    void borrowFromPrev(int idx) {
        BTreeNode child = C[idx];
        BTreeNode sibling = C[idx - 1];

        // Przenoszenie kluczy do C[idx]
        for (int i = child.n - 1; i >= 0; --i)
            child.keys[i + 1] = child.keys[i];

        // Jeśli C[idx] nie jest liściem, przesuń wszystkie jego dzieci o jedno miejsce.
        if (!child.leaf) {
            for (int i = child.n; i >= 0; i--)
                child.C[i + 1] = child.C[i];
        }

        // Ustawianie pierwszego klucza dziecka na keys[idx-1] z obecnego wierzchołka.
        child.keys[0] = keys[idx - 1];

        if (!leaf)
            child.C[0] = sibling.C[sibling.n];

        keys[idx - 1] = sibling.keys[sibling.n - 1];

        child.n += 1;
        sibling.n -= 1;
    }

    // Funkcja do zabierania klucza z C[idx+1] i wkładania go do C[idx]
    void borrowFromNext(int idx) {
        BTreeNode child = C[idx];
        BTreeNode sibling = C[idx + 1];

        // Klucze keys[idx] są włożone jako ostatnie do C[idx]
        child.keys[child.n] = keys[idx];

        // Pierwsze dziecko potomka jest włożone jako ostatnie dziecko do C[idx]
        if (!child.leaf)
            child.C[child.n + 1] = sibling.C[0];

        // Pierwszy klucz z potomka jest włożony do keys[ idx ]
        keys[idx] = sibling.keys[0];

        // Przenoszenie wszystkich kluczy w potomku i jedno miejsce w lewo.
        for (int i = 1; i < sibling.n; i++)
            sibling.keys[i - 1] = sibling.keys[i];

        // Przenoszenie dzieci jedno miejsce w lewo
        if (!sibling.leaf) {
            for (int i = 1; i <= sibling.n; i++)
                sibling.C[i - 1] = sibling.C[i];
        }

        // Zwiększanie i zmniejszanie liczby kluczy w C[idx] i C[idx+1]
        child.n += 1;
        sibling.n -= 1;
    }

    //Funkcja łączoąca C[idx] z [Cidx+1]
    // C[idx+1] jest usuwany po złączeniu
    void merge(int idx) {
        BTreeNode child = C[idx];
        BTreeNode sibling = C[idx + 1];

        child.keys[t - 1] = keys[idx];

        // Przekopiowywanie kluczy
        for (int i = 0; i < sibling.n; i++)
            child.keys[i + t] = sibling.keys[i];

        // Przekopiowywanie dzieci z C[idx+1] do C[idx]
        if (!child.leaf) {
            for (int i = 0; i <= sibling.n; i++)
                child.C[i + t] = sibling.C[i];
        }

        for (int i = idx + 1; i < n; i++)
            keys[i - 1] = keys[i];
        for (int i = idx + 2; i <= n; i++)
            C[i - 1] = C[i];

        // Zaktualizowanie liczby dzieci w bieżącym wierzchołku
        child.n += sibling.n + 1;
        n--;
    }

    // Funkcja pomocnicza, wstawiająca klucz do wierzchołka.
    // Wierzchołek NIE może być pełny, gdy ta funkcja jest wywołana.
    void insertNonFull(T k) {
        // Indeks skrajnie prawego elementu
        int i = n - 1;

        // Jeśli wierzchołek jest liściem
        if (leaf == true) {
            // (1) Szukanie miejsca, w którym można umieścić klucz.
            // (2) Przesuwanie pozostałych elementów o jedno miejsce w prawo.
            //while( i >= 0 && keys[ i ] > k )
            while (i >= 0 && keys[i].compareTo(k) > 0) {
                keys[i + 1] = keys[i];
                i--;
            }

            // Umieść klucz w znalezionej (dla niego) pozycji
            keys[i + 1] = k;
            n = n + 1;
        } else // Jeśli wierzchołek nie jest liściem
        {
            // Znajdź dziecko, do którego należy włożyć klucz
            //while( i >= 0 && keys[ i ] > k )
            while (i >= 0 && keys[i].compareTo(k) > 0)
                i--;

            // Sprawdź czy znalezione dziecko ma już maksymalną liczbę kluczy
            if (C[i + 1].n == 2 * t - 1) {
                // Jeśli dzieciak jest pełny, to go podziel
                splitChild(i + 1, C[i + 1]);
                //if( keys[ i + 1 ] < k )
                if (keys[i + 1].compareTo(k) < 0)
                    i++;
            }
            C[i + 1].insertNonFull(k);
        }
    }

    // Funkcja pomocnicza, która dzieli dzieciaka 'y'.
    // Uwaga: dzieciak 'y' musi być pełny, gdy ta funkcja jest wywołana.
    void splitChild(int i, BTreeNode y) {
        // Stwórz nowy wierzchołek
        BTreeNode z = new BTreeNode(y.t, y.leaf);
        z.n = t - 1;

        // Skopiuj ostatnie (t-1) kluczy z 'y' do 'z'
        for (int j = 0; j < t - 1; j++)
            z.keys[j] = y.keys[j + t];

        // Skopiuj ostatnie 't' dzieci z 'y' do 'z'
        if (y.leaf == false) {
            for (int j = 0; j < t; j++)
                z.C[j] = y.C[j + t];
        }

        // Zmniejsz liczbę kluczy w 'y'
        y.n = t - 1;

        for (int j = n; j >= i + 1; j--) {
            C[j + 1] = C[j];
        }

        // Zapisz nowe dziecko do tego wierzchołka
        C[i + 1] = z;

        for (int j = n - 1; j >= i; j--)
            keys[j + 1] = keys[j];

        // Skopiuj środkowy klucz z 'y' do tego wierzchołka
        keys[i] = y.keys[t - 1];

        // Zwiększ liczbę kluczy w tym wierzchołku
        n = n + 1;
    }

    // Funkcja wypisująca drzewo w porządku rosnącym
    void traverse() {
        int i;
        for (i = 0; i < n; i++) {
            // Jeśli ten wierzchołek nie jest liściem, to przed wypisaniem jego kluczy,
            // wypisuj jego dzieci
            if (leaf == false)
                C[i].traverse();
            System.out.print(" " + keys[i].toString());
        }
        // Wypisz poddrzewo zakorzenione w srajnie prawym wskaźniku
        if (leaf == false)
            C[i].traverse();
    }

    BTreeNode search(T k) {
        // Znajdź pierwszy klucz większy lub równy 'k'
        int i = 0;
        while (i < n && keys[i].compareTo(k) < 0)
            i++;

        // Jeśli znaleziony klucz jest równy 'k', zwróć ten wierzchołek.
        if (keys[i].compareTo(k) == 0) {
            System.out.println("Znaleziono " + k);
            return this;
        }

        // Jeśli nie znaleziono tutaj klucza, a ten wierzchołek jest liściem.
        if (leaf == true) {
            System.out.println("Nie znaleziono " + k);
            return null;
        }

        // Szukaj klucza u odpowiedniego dziecka
        return C[i].search(k);
    }

    void print() {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) {
                System.out.print(keys[i] + " ");
            }
        }
        System.out.println();

        int len = C.length;

        for (int i = 0; i < C.length; i++) {
            if (C[i] != null) {
                len = i + 1;
            }
        }

        System.out.println("Len = " + len + " C.len = " + C.length);

        for (int i = 0; i < (2* t - 1); i++) {
            if (keys[i] == null) {
                keys[i] = C[i - 1].keys[0];
            }
        }

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < C[0].keys.length; j++) {
                if (C[i].keys[j] == null) {
                    if (i < len-1) {
                        C[i].keys[j] = 999999;
                    } else if ( i == len-1) {
                        C[i].keys[j] = 0;
                    }
                }
            }
        }


        for (int i = 0; i < len; i++) {
            for (int j = 0; j < C[i].keys.length; j++) {

                if (i < len - 1 && C[i].keys[j].compareTo(keys[i]) == -1) {
                    System.out.print(C[i].keys[j] + " ");
                } else if (i == len - 1 && C[i].keys[j].compareTo(keys[i-1]) == 1) {
                    System.out.print(C[i].keys[j] + " ");
                }
            }
            System.out.println();
        }
    }
}

