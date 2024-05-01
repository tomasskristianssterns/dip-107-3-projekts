// 231RDB190 Tomass Kristiāns Šterns
// 231RDB331 Petr Gabuniia
// 231RDB008 Valentīns Koposovs

import java.io.*;
import java.util.*;
// import java.io.*;

/*
	Benchmark, only LZ77 compresion, windows results:
	Filename             original size     compresed size   compresion 
	Tests/File1.html         80831			    39862          2.03x
	Tests/File2.html        346119     		   150934          2.29x
	Tests/File3.html         83535			    41239          2.03x
	Tests/File4.html        208048			    98862          2.10x
	__________________________________________________________________
										   average compresion: 2.11x


	Benchmark, only LZ77 compresion, MacOS results:
	Filename             original size     compresed size   compresion 
	Tests/File1.html         80479			    39790          2.02x
	Tests/File2.html        344523     		   150593          2.29x
	Tests/File3.html         83069			    41097          2.02x
	Tests/File4.html        206694			    98613          2.10x
	Tests/File5.html        123085			    60540          2.03x
	Tests/File6.html        175131			    88181          1.99x
	Tests/File7.html         80219			    43051          1,86x
	__________________________________________________________________
										   average compresion: 2.04x


	Benchmark, Huffman compresion, MacOS results:
	Filename             original size     compresed size   compresion 
	Tests/File1.html         80479			    45762          1.76x
	Tests/File2.html        344523     		   145640          2.37x
	Tests/File3.html         83069			    45954          1.82x
	Tests/File4.html        206694			    97614          2.12x
	Tests/File5.html        123085			    63648          1.93x
	Tests/File6.html        175131			    88487          1.98x
	Tests/File7.html         80219			    47440          1,69x
	__________________________________________________________________
										   average compresion: 1.95x

*/


public class Main {


	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String choiseStr;
		String sourceFile, resultFile, firstFile, secondFile;
		
		loop: while (true) {

			System.out.println("\n>> comp - compress file");
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

/**		Comresses original file using LZ77 algorythm idea
 *		Read ahead and search buffer are set to 65535 for efficiency
 *
 *		Result file consists of sequences of 3 characters:
 *			how many charaters back in search buffer is the beginning of sequence of characters,
 *			the length of the sequence of character,
 *			and the next character after this sequence
 *
 *		The algorythm works as follows:
 *			It reads in characters from the file until the read ahead buffer is filled (65535 characters) or till the end of the file
 *			Then it reads the character from read ahead buffer and adds it to string
 *			It then looks if such string exists in search buffer:
 *				if it does, it reads next character from read ahead buffer and if exists in search buffer check is repeated
 *				if not - it saves the data explained previous section in result file
 *
 *			It then adds the string to search buffer
 *			If search buffer is bigger than 65535 by the end of previous action:
 *				it removes the the oldest characters put in it until it is 65535 in size
 *			This action is continued until the end of the file or till the read ahead buffer is empty
 */

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
            int sequenceLocation, lastLocation = -1, MAX_READ_AMOUNT = 65535, MAX_SEARCH_BUFFER_SIZE = 65535; //old READ = 4000, old SEARCH = 2047

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
		int distance, amount, MAX_READ_AMOUNT = 3, MAX_SEARCH_BUFFER_SIZE = 65535;

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

	/**
	 *   Compresses LZ77 compressed file.
	 *   In final file writes symbols in following sequence:
	 *   	* 1 number n, represents number of different symbols in LZ77 compressed file
	 *   	* n triplets (value, bit length, prefix: base 2 converted to base 10)

	 *      * byteRead of bytes using @param sourceFile
	 *      * written in @param resultFile
	 */
	void compressFile() {
		File file = new File(sourceFile);
		if (!file.exists()) {
			System.out.println("File not found!");
			return;
		}

		String searchBuff = "", temp = "";
		int sequenceLocation, lastLocation = -1, MAX_READ_AMOUNT = 4000, MAX_SEARCH_BUFFER_SIZE = 2047;

		HashMap<Integer, Integer> letterFrequency = new HashMap<>(); // value, frequency

		try {
			InputStream inputStream = new FileInputStream(sourceFile);
			int byteRead;
			while ((byteRead = inputStream.read()) != -1) {
				if (letterFrequency.get(byteRead) == null) {
					letterFrequency.put(byteRead, 1);
				} else {
					letterFrequency.put(byteRead, letterFrequency.get(byteRead) + 1);
				}
			}
			inputStream.close();

		} catch (Exception exception) {
			System.out.println("Error in readValue!");
		}

		System.out.println("Size of map is: " + letterFrequency.size());
		System.out.println(letterFrequency);

		int n = letterFrequency.size();
		PriorityQueue<HuffmanNode> q = new PriorityQueue<>(n, new MyComparator());

		for (int key : letterFrequency.keySet()) {
			HuffmanNode hn = new HuffmanNode();

			hn.charValue = key;
			hn.frequency = letterFrequency.get(key);

			hn.left = null;
			hn.right = null;

			q.add(hn);
		}

		HuffmanNode root = null;

		while (q.size() > 1) {
			HuffmanNode x = q.peek();
			q.poll();

			HuffmanNode y = q.peek();
			q.poll();

			HuffmanNode f = new HuffmanNode();

			f.frequency = x.frequency + y.frequency;
			f.charValue = -1;

			f.left = x;
			f.right = y;

			root = f;

			q.add(f);
		}

		HashMap<Integer, String> nodeValues = new HashMap<>();  // key, bits

		class Local {
			void traverseTree(HuffmanNode root, String s) {
				if (root.left == null && root.right == null && (root.charValue != -1)) {
					nodeValues.put(root.charValue, s);
					return;
				}
				// if left: add "0", right: add "1"
				traverseTree(root.left, s + "0");
				traverseTree(root.right, s + "1");
			}
		}

		new Local().traverseTree(root, "");

		System.out.println(nodeValues);

		try {
			OutputStream outputStream = new FileOutputStream(resultFile);
			InputStream inputStream = new FileInputStream(sourceFile);
			outputStream.write(n);

			for (int key : nodeValues.keySet()) {
				outputStream.write(key);

				int bitLength = nodeValues.get(key).length();
				outputStream.write(bitLength);

				String allBits = nodeValues.get(key);
				int bitsInt = Integer.parseInt(allBits, 2);
				outputStream.write(bitsInt);
			}

			int byteRead;

			while ((byteRead = inputStream.read()) != -1) {
				int textInt = Integer.parseInt(nodeValues.get(byteRead), 2);
				outputStream.write(textInt);
			}

            inputStream.close();
			outputStream.close();

		} catch(Exception exception) {
			System.out.println("Error in InputOutputStream!");
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
