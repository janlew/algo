import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Huffman {
    private final static int R = 256;

    public static void main(String [] args) throws IOException {

        File file = new File("temp.txt");
        Scanner in = new Scanner(file);

        String line = in.nextLine();
        System.out.println("Tekst: " + line);

        char [] characters = line.toCharArray();

        int [] frequency = new int[R];

        for(int i = 0 ; i < characters.length; i++){
            frequency[characters[i]]++;
        }

        Node root = buildTree(frequency);

        String [] dictionaryKeys = new String[R];
        createKeys(dictionaryKeys, root, "");

        for(int i = 0; i < dictionaryKeys.length; i++){
            if(!(dictionaryKeys[i] == null)){
                System.out.println("symbol: " + (char)(i) + " frequency: " + frequency[i] + " code: " + dictionaryKeys[i]);
            }
        }

        System.out.println();
        int a = 3 * line.length();
        System.out.println("Dlugosc przed huff: " + a);
        int b = 0;
        for(int i = 0; i < dictionaryKeys.length; i++){
            if(!(dictionaryKeys[i] == null)){
                b += frequency[i] * dictionaryKeys[i].length();
            }
        }
        System.out.println("po: " + b);

    }

    public static Node buildTree(int [] frequency){
        PriorityQueue<Node> pq = new PriorityQueue<Node>();

        for(char i = 0; i < frequency.length; i++){
            if(frequency[i] > 0){
                pq.add(new Node(i, frequency[i], null, null));
            }
        }

        while(pq.size() > 1){
            Node right = pq.remove();
            Node left = pq.remove();
            Node parent = new Node('\0', left.getFrequency() + right.getFrequency(), left, right);
            pq.add(parent);
        }

        return pq.poll();
    }

    public static void createKeys(String [] keys, Node current, String key){
        if(current.isLeaf()){
            keys[current.getSymbol()] = key;
        }else{
            createKeys(keys, current.left, key + "0");
            createKeys(keys, current.right, key + "1");
        }
    }

}