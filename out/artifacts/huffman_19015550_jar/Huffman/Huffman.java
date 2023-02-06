/**
 * “I acknowledge that I am aware of the academic integrity guidelines of this course, and that I worked
 * on this assignment independently without any unauthorized help”
 */

package Huffman;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Huffman {
    HashMap<String, Integer> frequencyMap = new HashMap<>();
    int maxSize = 100_000_000;
    HashMap<String, String> prefixCodeMap = new HashMap<>();
    public void compress(int groups, String srcPath, String destPath) throws IOException {
        long startTime = System.currentTimeMillis();
        FileInputStream fis = new FileInputStream(srcPath);
        BufferedInputStream bin = new BufferedInputStream(fis);
        FileOutputStream fos = new FileOutputStream(destPath);
        BufferedOutputStream bout = new BufferedOutputStream(fos);
        byte[] inputBytes = new byte[maxSize / groups * groups];
        int read;
        while((read = bin.read(inputBytes)) > 0) {
            buildHashMapFrequency(inputBytes, groups, read);
        }
        fis.close();
        bin.close();
        Node root = buildTree();
        buildPrefixCode(root, new StringBuilder());
        fis = new FileInputStream(srcPath);
        bin = new BufferedInputStream(fis);
        byte[] outputBytes = new byte[maxSize];
        outputBytes[0] = (byte) groups;
        int outputByteIdx = 1;
        String key, value;
        for (HashMap.Entry<String, String> entry: prefixCodeMap.entrySet()) {
            value = entry.getValue();
            outputBytes[outputByteIdx++] = (byte) value.length();
            for (int j = 0; j < Math.ceilDiv(value.length(), 8) - 1; j++) {
                outputBytes[outputByteIdx++] = (byte) Integer.parseInt(value.substring(j * 8, (j + 1) * 8), 2);
            }
            outputBytes[outputByteIdx++] = (byte) Integer.parseInt(value.substring((Math.ceilDiv(value.length(), 8) - 1) * 8), 2);
            key = entry.getKey();
            outputBytes[outputByteIdx++] = (byte) (key.length() / 8);
            for (int j = 0; j < groups && j < key.length() / 8; j++) {
                outputBytes[outputByteIdx++] = (byte) Integer.parseInt(key.substring(j * 8, (j + 1) * 8), 2);
            }
        }
        outputBytes[outputByteIdx++] = (byte) 255;
        for (int counter = 0; counter < outputByteIdx; counter++)
            bout.write(outputBytes[counter]);
        bout.flush();
        StringBuilder builder, resultBuilder = new StringBuilder(), tempBuilder;
        int lastByteSize = 0;
        while((read = bin.read(inputBytes)) > 0) {
            int length, outputLength;
            outputBytes = new byte[maxSize];
            outputByteIdx = 0;
            int readIdx = 0;
            while (readIdx < read) {
                builder = new StringBuilder();
                for (int j = 0; j < groups && readIdx < read; j++) {
                    tempBuilder = new StringBuilder(Integer.toBinaryString(inputBytes[readIdx++] & 0xFF));
                    while (tempBuilder.length() < 8)
                        tempBuilder.insert(0, '0');
                    builder.append(tempBuilder);
                }
                length = resultBuilder.length();
                if (length >= maxSize) {
                    outputLength = length / 8;
                    for (int j = 0; j < outputLength; j++) {
                        outputBytes[outputByteIdx++] = (byte) Integer.parseInt(resultBuilder.substring(j * 8, 8 * (j + 1)), 2);
                    }
                    for (int counter = 0; counter < outputByteIdx; counter++)
                        bout.write(outputBytes[counter]);
                    bout.flush();
                    outputBytes = new byte[maxSize];
                    outputByteIdx = 0;
                    resultBuilder = new StringBuilder(resultBuilder.substring(length - length % 8));
                }
                resultBuilder.append(prefixCodeMap.get(builder.toString()));
            }
            outputLength = resultBuilder.length() / 8;
            int outputBuilderIdx;
            for (outputBuilderIdx = 0; outputBuilderIdx < outputLength; outputBuilderIdx++) {
                outputBytes[outputByteIdx++] = (byte) Integer.parseInt(resultBuilder.substring(outputBuilderIdx * 8, 8 * (outputBuilderIdx + 1)), 2);
            }
            lastByteSize = resultBuilder.length() % 8;
            resultBuilder = new StringBuilder(resultBuilder.substring(outputBuilderIdx * 8));
            for (int counter = 0; counter < outputByteIdx; counter++)
                bout.write(outputBytes[counter]);
            bout.flush();
        }
        fis.close();
        bin.close();
        if (!resultBuilder.isEmpty()) {
            bout.write((byte) Integer.parseInt(resultBuilder.toString(), 2));
        }
        bout.write((byte) lastByteSize);
        bout.flush();
        fos.close();
        bout.close();
        long originFileSize = Files.size(Path.of(srcPath));
        long compressedFileSize = Files.size(Path.of(destPath));
        long finishTime = System.currentTimeMillis();
        System.out.println("Compression Time in ms: " + (finishTime - startTime));
        System.out.println("Compression Ratio: " + 100.0 * compressedFileSize / originFileSize + " %");
    }

    public void decompress(String srcPath, String destPath) throws IOException {
        long startTime = System.currentTimeMillis();
        int kIdx, resultIdx, builderIdx;
        FileInputStream fis = new FileInputStream(srcPath);
        BufferedInputStream bin = new BufferedInputStream(fis);
        FileOutputStream fos = new FileOutputStream(destPath);
        BufferedOutputStream bout = new BufferedOutputStream(fos);
        byte[] inputBytes = new byte[maxSize];
        int groups = 0, readIdx, bitSize, read, byteLoops, lastByteSize = 0, valueLength;
        boolean firstIteration = true, lastLoop = false;
        byte[] result;
        StringBuilder key, value, tempByte, temp = new StringBuilder();
        String k;
        while((read = bin.read(inputBytes)) > 0) {
            readIdx = 0;
            if (firstIteration) {
                firstIteration = false;
                groups = inputBytes[readIdx++] & 0xFF;
                while (readIdx < read) {
                    bitSize = inputBytes[readIdx++] & 0xFF;
                    if (bitSize == ((byte) 255 & 0xFF)) {
                        break;
                    }
                    byteLoops = Math.ceilDiv(bitSize, 8);
                    key = new StringBuilder();
                    for (int j = 0; j < byteLoops; j++) {
                        key.append(Integer.toBinaryString(inputBytes[readIdx++] & 0xFF));
                        while (key.length() < bitSize && key.length() < (j + 1) * 8)
                            key.insert(j * 8, "0");
                    }
                    valueLength = inputBytes[readIdx++] & 0xFF;
                    value = new StringBuilder();
                    for (int j = 0; j < valueLength && readIdx < read; j++) {
                        tempByte = new StringBuilder(Integer.toBinaryString(inputBytes[readIdx++] & 0xFF));
                        while (tempByte.length() < 8)
                            tempByte.insert(0, '0');
                        value.append(tempByte);
                    }
                    prefixCodeMap.put(key.toString(), value.toString());
                }
            }
            int builderLength, writeIdx;
            while (readIdx < read) {
                tempByte = new StringBuilder(Integer.toBinaryString(inputBytes[readIdx++] & 0xFF));
                while (tempByte.length() < 8)
                    tempByte.insert(0, '0');
                if (readIdx == read && bin.available() == 0) {
                    lastLoop = true;
                    lastByteSize = Integer.parseInt(tempByte.toString(), 2);
                    break;
                }
                builderLength = temp.length();
                if (builderLength >= maxSize) {
                    k = "";
                    resultIdx = 0;
                    writeIdx = 0;
                    result = new byte[maxSize / groups * groups];
                    while (writeIdx < temp.length()) {
                        k += temp.charAt(writeIdx++);
                        if(prefixCodeMap.containsKey(k)) {
                            k = prefixCodeMap.get(k);
                            kIdx = 0;
                            while (kIdx < k.length()) {
                                result[resultIdx++] = (byte) Integer.parseInt(k.substring(kIdx, kIdx + 8), 2);
                                kIdx += 8;
                            }
                            k = "";
                        }
                    }
                    for (int counter = 0; counter < resultIdx; counter++)
                        bout.write(result[counter]);
                    bout.flush();
                    temp = new StringBuilder(k);
                }
                temp.append(tempByte);
            }
            k = "";
            builderIdx = 0;
            if (lastLoop && lastByteSize != 0) {
                temp.delete(temp.length() - 8, temp.length() - lastByteSize);
            }
            while (builderIdx < temp.length()) {
                k += temp.charAt(builderIdx++);
                if(prefixCodeMap.containsKey(k)) {
                    k = prefixCodeMap.get(k);
                    kIdx = 0;
                    while (kIdx < k.length()) {
                        bout.write((byte) Integer.parseInt(k.substring(kIdx, kIdx + 8), 2));
                        kIdx += 8;
                    }
                    k = "";
                }
            }
            bout.flush();
            temp = new StringBuilder(k);
        }
        fis.close();
        bin.close();
        fos.close();
        bout.close();
        long finishTime = System.currentTimeMillis();
        System.out.println("Decompression Time in ms: " + (finishTime - startTime));
    }
    private void buildHashMapFrequency(byte[] bytes, int groups, int read) {
        StringBuilder builder, tempBuilder;
        String string;
        int i = 0;
        while (i < read) {
            builder = new StringBuilder();
            for (int j = 0; i < read && j < groups; j++) {
                tempBuilder = new StringBuilder(Integer.toBinaryString(bytes[i++] & 0xFF));
                while (tempBuilder.length() < 8)
                    tempBuilder.insert(0, '0');
                builder.append(tempBuilder);
            }
//            System.out.println("code: " + builder);
            string = builder.toString();
            frequencyMap.put(string, frequencyMap.getOrDefault(string, 0) + 1);
        }
    }

    private Node buildTree() {
        int uniqueElementsSize = frequencyMap.size();
        PriorityQueue<Node> Q = new PriorityQueue<>(uniqueElementsSize, new HuffmanComparator());
        for (HashMap.Entry<String, Integer> node: frequencyMap.entrySet()) {
            Q.add(new Node(node.getKey(), node.getValue(), null, null));
        }
        Node x, y, z;
        for (int i = 0; i < uniqueElementsSize - 1; i++) {
            x = Q.remove();
            y = Q.remove();
            z = new Node("", x.frequency + y.frequency, x, y);
            Q.add(z);
        }
        return Q.remove();
    }

    private void buildPrefixCode(Node root, StringBuilder code) {
        if (root == null)
            return;
        buildPrefixCode(root.left, new StringBuilder(code).append('0'));
        buildPrefixCode(root.right, new StringBuilder(code).append('1'));
        if (!root.data.isEmpty()) {
            prefixCodeMap.put(root.data, code.toString());
        }
    }
}
