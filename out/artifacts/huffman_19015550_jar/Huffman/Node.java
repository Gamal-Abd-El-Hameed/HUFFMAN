/**
 * “I acknowledge that I am aware of the academic integrity guidelines of this course, and that I worked
 * on this assignment independently without any unauthorized help”
 */

package Huffman;

public class Node {
    String data;
    int frequency;
    Node left;
    Node right;

    public Node(String data, int frequency, Node left, Node right) {
        this.data = data;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    public Node(){}
}
