# HUFFMAN
A program that implements Huffman's algorithm. The implementation allows compressing and decompressing arbitrary files. The program considers more than one byte. For example, instead of just collecting the frequencies and finding codewords for single bytes. The same can be done assuming the basic unit is n bytes, where n is an integer.
-	To use it for compressing an input file, the following will be called:
java -jar huffman_19015550.jar c absolute_path_to_input_file n  
-	c means compressing the file.
-	n is the number of bytes that will be considered together.
-	To use it for decompressing an input file, the following be called:
java -jar huffman_19015550.jar d absolute_path_to_input_file

-	If the user chooses to compress a file with the name abc.exe, the compressed file should have the name <19015550>.<n>.abc.exe.hc where <n> should be replaced by n (the number of bytes per group). The compressed file appears in the same directory of the input file. The program prints the compression ratio and the compression time.
-	If the user chooses to decompress a file with name abc.exe.hc, the output file will be named extracted.abc.exe. This will appear in the same directory of the input file. The program will print the decompression time in this case.

## Note:
The compression ratio can be defined using multiple ways. I calculate it in the same way as 7-zip does, which is the size of the compressed file divided by the size of the original file. 
