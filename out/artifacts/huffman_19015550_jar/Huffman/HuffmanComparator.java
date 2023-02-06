/**
 * “I acknowledge that I am aware of the academic integrity guidelines of this course, and that I worked
 * on this assignment independently without any unauthorized help”
 */

package Huffman;


import java.util.Comparator;

public class HuffmanComparator implements Comparator<Node> {
    public int compare(Node a, Node b) {
        return a.frequency - b.frequency;
    }
}
