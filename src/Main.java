// 231RDB190 Tomass Kristiāns Šterns
// 231RDB331 Petr Gabuniia
// 231RDB008 Valentīns Koposovs

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;
// import java.io.*;

/*
	Benchmark, only LZ77 compresion:
	Filename             original size     compresed size   compresion 
	Tests/File1.html         80831			    39862          2.03x
	Tests/File2.html        346119     		   150934          2.29x
	Tests/File3.html         83535			    41239          2.03x
	Tests/File4.html        208048			    98862          2.10x
	__________________________________________________________________
										   average compresion: 2.11x
*/


public class Main {


	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String choiseStr;
		String sourceFile, resultFile, firstFile, secondFile;
		
		loop: while (true) {

			System.out.println("\n\n>> comp - compress file");
			System.out.println(">> decomp - decompress file");
            System.out.println(">> Huffman - (test version)");
			System.out.println(">> size - get file size");
			System.out.println(">> equal - compare two files");
			System.out.println(">> about - about authors");
			System.out.println(">> exit - exit program");
			
			choiseStr = sc.next();
			
			switch (choiseStr) {
				case "comp":
				System.out.print("source file name: ");
				sourceFile = sc.next();
				System.out.print("archive name: ");
				resultFile = sc.next();

				LZ77 lz77_1 = new LZ77(sourceFile, resultFile);
				lz77_1.compressFile();
				break;
			case "decomp":
				System.out.print("archive name: ");
				sourceFile = sc.next();
				System.out.print("file name: ");
				resultFile = sc.next();

				LZ77 lz77_2 = new LZ77(sourceFile, resultFile);
				lz77_2.decompressFile();
				break;
            case "Huffman":
                System.out.print("archive name: ");
                sourceFile = sc.next();
                System.out.print("file name: ");
                resultFile = sc.next();

                Huffman huffman = new Huffman(sourceFile, resultFile);
                huffman.compressFile();
                break;
			case "size":
				System.out.print("file name: ");
				sourceFile = sc.next();
				size(sourceFile);
				break;
			case "equal":
				System.out.print("first file name: ");
				firstFile = sc.next();
				System.out.print("second file name: ");
				secondFile = sc.next();
				System.out.println(equal(firstFile, secondFile));
				break;
			case "about":
				about();
				break;
			case "exit":
				break loop;
			}
		}

		sc.close();
	}
	
	public static void size(String sourceFile) {
		try {
			FileInputStream f = new FileInputStream(sourceFile);
			System.out.println("size: " + f.available());
			f.close();
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	
	public static boolean equal(String firstFile, String secondFile) {
		try {
			FileInputStream f1 = new FileInputStream(firstFile);
			FileInputStream f2 = new FileInputStream(secondFile);
			int k1, k2;
			byte[] buf1 = new byte[1000];
			byte[] buf2 = new byte[1000];
			do {
				k1 = f1.read(buf1);
				k2 = f2.read(buf2);
				if (k1 != k2) {
					f1.close();
					f2.close();
					return false;
				}
				for (int i=0; i<k1; i++) {
					if (buf1[i] != buf2[i]) {
						f1.close();
						f2.close();
						return false;
					}
						
				}
			} while (!(k1 == -1 && k2 == -1));
			f1.close();
			f2.close();
			return true;
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}
	
	public static void about() {
		System.out.println("231RDB190 Tomass Kristiāns Šterns");
		System.out.println("231RDB331 Petr Gabuniia");
		System.out.println("231RDB008 Valentīns Koposovs");
	}
}

class LZ77 {
	String sourceFile;
	String resultFile;

	LZ77(String sourceFile, String resultFile) {
		this.sourceFile = sourceFile;
		this.resultFile = resultFile;
	}

	void compressFile() {
		File file = new File(sourceFile);
            if(!file.exists()) {
				System.out.println("File not found!");
				return;
			}

            String rawText = "", searchBuff = "", temp = "";
            int sequenceLocation, lastLocation = -1, MAX_READ_AMOUNT = 4000, MAX_SEARCH_BUFFER_SIZE = 2047;

		try {
                PrintWriter printWriter = new PrintWriter(new FileWriter(resultFile));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
                int readValue, counter = 0;
                while((readValue = bufferedReader.read()) != -1)
                {
                    rawText += (char)readValue;
                    counter++;
                    if(counter == MAX_READ_AMOUNT)
                    {
                        for(int i = 0; i < rawText.length(); i++)
                        {
                            temp += rawText.charAt(i);
                            if(searchBuff.length() >= temp.length()) {
                                sequenceLocation = searchBuff.indexOf(temp);
                            } else {
                                sequenceLocation = -1;
                            }

                            if(sequenceLocation == -1)
                            {
                                if(lastLocation != -1) {
                                    printWriter.print((char)(searchBuff.length() - lastLocation));
                                    printWriter.print((char)(temp.length() - 1));
                                    printWriter.print(temp.substring(temp.length() - 1));
                                    lastLocation = -1;
                                } else {
                                    printWriter.print((char)0);
                                    printWriter.print((char)0);
                                    printWriter.print(temp);
                                }

                                searchBuff += temp;
                                counter -= temp.length();
                                rawText = rawText.substring(rawText.length()-counter);
                                temp = "";
                                if(searchBuff.length() > MAX_SEARCH_BUFFER_SIZE) {
                                    searchBuff = searchBuff.substring(searchBuff.length() - MAX_SEARCH_BUFFER_SIZE);
                                }

                                break;
                            } else {
                                lastLocation = sequenceLocation;
                            }
                        }
                    }
                }
				bufferedReader.close();

                while(counter > 0)
                {
                    for(int i = 0; i < rawText.length(); i++)
                    {
                        temp += rawText.charAt(i);
                        if(searchBuff.length() >= temp.length())
                        {
                            sequenceLocation = searchBuff.indexOf(temp);
                        }
                        else
                        {
                            sequenceLocation = -1;
                        }

                        if(sequenceLocation == -1) 
                        {
                            if(lastLocation != -1)
                            {
                                printWriter.print((char)(searchBuff.length() - lastLocation));
                                printWriter.print((char)(temp.length() - 1));
                                printWriter.print(temp.substring(temp.length() - 1));
                                lastLocation = -1;
                            }
                            else
                            {
                                printWriter.print((char)0);
                                printWriter.print((char)0);
                                printWriter.print(temp);
                            }

                            searchBuff += temp;
                            counter -= temp.length();
                            rawText = rawText.substring(rawText.length()-counter);
                            temp = "";
                            if(searchBuff.length() > MAX_SEARCH_BUFFER_SIZE)
                            {
                                searchBuff = searchBuff.substring(searchBuff.length() - MAX_SEARCH_BUFFER_SIZE);
                            }
                            break;
                        }
                        else
                        {
                            lastLocation = sequenceLocation;
                        }
                    }

                    if(!temp.isEmpty())
                    {
                        printWriter.print((char)(searchBuff.length() - lastLocation));
                        printWriter.print((char)(temp.length() - 1));
                        printWriter.print(temp.substring(temp.length() - 1));
                        break;
                    }
                }
                printWriter.close();
            }
            catch(Exception exception) {System.out.println("Error in either readValue or file opening!");}
	}

	void decompressFile() {
		File file = new File(sourceFile);
		if(!file.exists()) {return;}

		String rawText = "", temp = "";
		int distance, amount, MAX_READ_AMOUNT = 3, MAX_SEARCH_BUFFER_SIZE = 2047;

		try
		{
			PrintWriter printWriter = new PrintWriter(new FileWriter(resultFile));
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
			int readValue, counter = 0;
			while((readValue = bufferedReader.read()) != -1)
			{
				temp += (char)readValue;
				counter++;
				if(counter == MAX_READ_AMOUNT) {
					distance = temp.charAt(0);
					amount = temp.charAt(1);

					if(distance == 0 && amount == 0) {
						printWriter.print(temp.charAt(2));
						rawText += temp.charAt(2);
						temp = "";
						counter = 0;
					} else {
						printWriter.print(rawText.substring(rawText.length() - distance, rawText.length() - distance + amount));
						printWriter.print(temp.charAt(2));
						rawText += rawText.substring(rawText.length() - distance, rawText.length() - distance + amount);
						rawText += temp.charAt(2);
						temp = "";
						counter = 0;
					}

					if(rawText.length() > MAX_SEARCH_BUFFER_SIZE)
					{
						rawText = rawText.substring(rawText.length() - MAX_SEARCH_BUFFER_SIZE);
					}
				}
			}
			bufferedReader.close();
			printWriter.close();
		}
		catch(Exception exception) {System.out.println("Error in either readValue or file opening!");}
	}
}

class Huffman {
	String sourceFile;
	String resultFile;

	Huffman(String sourceFile, String resultFile) {
		this.sourceFile = sourceFile;
		this.resultFile = resultFile;
	}

	void compressFile(){
        File file = new File(sourceFile);
        if(!file.exists()) {
            System.out.println("File not found!");
            return;
        }

        String searchBuff = "", temp = "";
        int sequenceLocation, lastLocation = -1, MAX_READ_AMOUNT = 4000, MAX_SEARCH_BUFFER_SIZE = 2047;

		HashMap<Integer, Integer> letterFrequency = new HashMap<>(); // (int)char, frequency

        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
            int readValue, counter = 0;
            while((readValue = bufferedReader.read()) != -1) {
				if (letterFrequency.get(readValue) == null){
					letterFrequency.put(readValue, 1);
				} else {
					letterFrequency.put(readValue, letterFrequency.get(readValue) + 1);
				}
            }
            bufferedReader.close();

        } catch(Exception exception) {
			System.out.println("Error in readValue!");
		}

		System.out.println("Size of map is: " + letterFrequency.size());
		System.out.println(letterFrequency);

		int n = letterFrequency.size();
		PriorityQueue<HuffmanNode> q = new PriorityQueue<>(n, new MyComparator());

		for (int key: letterFrequency.keySet()) {

			// creating a Huffman node object
			// and add it to the priority queue.
			HuffmanNode hn = new HuffmanNode();

			hn.charValue = key;
			hn.frequency = letterFrequency.get(key);

			hn.left = null;
			hn.right = null;

			// add functions adds
			// the huffman node to the queue.
			q.add(hn);
		}

		HuffmanNode root = null;

		while (q.size() > 1) {

			// first min extract.
			HuffmanNode x = q.peek();
			q.poll();

			// second min extract.
			HuffmanNode y = q.peek();
			q.poll();

			// new node f which is equal
			HuffmanNode f = new HuffmanNode();

			// to the sum of the frequency of the two nodes
			// assigning values to the f node.
			f.frequency = x.frequency + y.frequency;
			f.charValue = -1;

			// first extracted node as left child.
			f.left = x;

			// second extracted node as the right child.
			f.right = y;

			// marking the f node as the root node.
			root = f;

			// add this node to the priority-queue.
			q.add(f);
		}

		HashMap<Integer, String> nodeValues = new HashMap<>();  // key, bits

		class Local {
			void traverseTree(HuffmanNode root, String s) {
				if (root.left == null && root.right == null && (root.charValue != -1)) {
					// charValue is the (int)character in the node
					nodeValues.put(root.charValue, s);
					return;
				}
				// if left then add "0" to the code
				// if right then add "1" to the code
				traverseTree(root.left, s + "0");
				traverseTree(root.right, s + "1");
			}
		}

		new Local().traverseTree(root, "");

		System.out.println(nodeValues);

		try{
            PrintWriter printWriter = new PrintWriter(new FileWriter(resultFile));
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
//			printWriter.print((char)n);

//			for (int key: nodeValues.keySet()) {

//				int bitLength = nodeValues.get(key).length();
//				int bits = Integer.parseInt(nodeValues.get(key), 2);

//				printWriter.print((char)key);
//				printWriter.print((char)bitLength);
//				printWriter.print((char)bits);
//			}

			String rawText = "";
			int readValue;

			while((readValue = bufferedReader.read()) != -1)
			{
				rawText = rawText + nodeValues.get(readValue);

				if (rawText.length() > 255){
					for (int i=0; i<=255; i+=16) {
						String substring = rawText.substring(i, i+16);
						int substringInt = Integer.parseInt(substring, 2);
						printWriter.print((char)substringInt);
					}
					rawText = rawText.substring(256);
				}
			}

			for (int i=0; i<rawText.length(); i+=16) {
 					try{
 						String substring = rawText.substring(i, i+16);
 						printWriter.print((char)Integer.parseInt(substring, 2));
 					}
 					catch (StringIndexOutOfBoundsException e) {
 						String substring = rawText.substring(i);
 						printWriter.print((char)Integer.parseInt(substring, 2));
 					}
			}

            printWriter.close();
			bufferedReader.close();

		} catch(Exception exception) {
			System.out.println("Error in printWriter!");
		}


	}



	void decompressFile(){}
}

class HuffmanNode { 
	int frequency;  //
	int charValue;

	HuffmanNode left; 
	HuffmanNode right; 
} 

class MyComparator implements Comparator<HuffmanNode> { 
	public int compare(HuffmanNode x, HuffmanNode y) { 
		return x.frequency - y.frequency;
	} 
} 