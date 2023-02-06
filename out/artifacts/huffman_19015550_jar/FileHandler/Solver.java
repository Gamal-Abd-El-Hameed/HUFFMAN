/**
 * “I acknowledge that I am aware of the academic integrity guidelines of this course, and that I worked
 * on this assignment independently without any unauthorized help”
 */

package FileHandler;
import Huffman.Huffman;

import java.io.*;

public class Solver {
    public void solve(String[] inputArgs) throws IOException {
        if (inputArgs[0].equalsIgnoreCase("c")) {
            String srcPath = inputArgs[1];
            int n = Integer.parseInt(inputArgs[2]);
            int lastSeparator = srcPath.lastIndexOf(File.separator);
            String destPath = srcPath.substring(0, lastSeparator + 1) + "19015550." + n + "." + srcPath.substring(lastSeparator + 1) + ".hc";
            new Huffman().compress(n, srcPath, destPath);
        }
        else if (inputArgs[0].equalsIgnoreCase("d")) {
            String srcPath = inputArgs[1];
            int lastSeparator = srcPath.lastIndexOf(File.separator);
            int hc = srcPath.lastIndexOf(".hc");
            String destPath = srcPath.substring(0, lastSeparator + 1) + "extracted." + srcPath.substring(lastSeparator + 1, hc);
            new Huffman().decompress(srcPath, destPath);
        }
    }
}
