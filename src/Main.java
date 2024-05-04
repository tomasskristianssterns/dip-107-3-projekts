import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String choiceStr;
        String sourceFile, resultFile, firstFile, secondFile;

        loop: while (true) {

            System.out.println("\n>> comp - compress file");
            System.out.println(">> decomp - decompress file (LZ77)");
			System.out.println(">> Hcomp - compress file (Huffman)");
            System.out.println(">> Hdecomp decompress file (Huffman)");
            System.out.println(">> size - get file size");
            System.out.println(">> equal - compare two files");
            System.out.println(">> about - about authors");
            System.out.println(">> exit - exit program");

            choiceStr = sc.next();

            switch (choiceStr) {
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
                case "Hcomp":
                    System.out.print("archive name: ");
                    sourceFile = sc.next();
                    System.out.print("file name: ");
                    resultFile = sc.next();

                    Huffman huffman_1 = new Huffman(sourceFile, resultFile);
                    huffman_1.compressFile();
                    break;

				case "Hdecomp":
                    System.out.print("archive name: ");
                    sourceFile = sc.next();
                    System.out.print("file name: ");
                    resultFile = sc.next();

                    Huffman huffman_2 = new Huffman(sourceFile, resultFile);
                    huffman_2.decompressFile();
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
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        }

        sc.close();
    }

    public static void size(String sourceFile) {
        try {
            FileInputStream f = new FileInputStream(sourceFile);
            System.out.println("size: " + f.available());
            f.close();
        } catch (IOException ex) {
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
                for (int i = 0; i < k1; i++) {
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
        } catch (IOException ex) {
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

			for (int key : nodeValues.keySet()) { // n amount of keys
				outputStream.write(key);
			}

            for (int key : nodeValues.keySet()) { // n amount of bit count
                int bitLength = nodeValues.get(key).length();
                outputStream.write(bitLength);
            }

            int NUMBER_OF_BITS = 8;
            String writingBits = "";

            for (int key : nodeValues.keySet()) { // bitStream of prefixes
                String allBits = nodeValues.get(key);
                writingBits += allBits;
                while(writingBits.length() > NUMBER_OF_BITS){
                    String substring = writingBits.substring(0, NUMBER_OF_BITS);
                    int textInt = Integer.parseInt(substring, 2);
                    outputStream.write(textInt);
                    writingBits = writingBits.substring(NUMBER_OF_BITS);
                }
            }

            System.out.println("Leftover bits: " + writingBits.length());

            for (int i = writingBits.length(); i<= NUMBER_OF_BITS; i++){
                writingBits += "0";
            }
            outputStream.write(Integer.parseInt(writingBits, 2));

			int byteRead;
            String allBits = "";

			while ((byteRead = inputStream.read()) != -1) {
                allBits += nodeValues.get(byteRead);
                while(allBits.length() > NUMBER_OF_BITS){
                    String substring = allBits.substring(0, NUMBER_OF_BITS);
                    int textInt = Integer.parseInt(substring, 2);
                    outputStream.write(textInt);
                    allBits = allBits.substring(NUMBER_OF_BITS);
                }
			}
            System.out.println("Leftover amount of bits: " + allBits.length());
            if(!allBits.isEmpty()) {
                int textInt = Integer.parseInt(allBits, 2);
                outputStream.write(textInt);
                outputStream.write(allBits.length());
            }

            inputStream.close();
			outputStream.close();

		} catch(Exception exception) {
			System.out.println("Error in InputOutputStream!");
		}

    }

	void decompressFile() {
		File file = new File(sourceFile);
		if (!file.exists()) {
			System.out.println("File not found!");
			return;
		}

        HashMap<String, Integer> nodeValues = new HashMap<>(); // Prefix, key

		try {
			InputStream inputStream = new FileInputStream(sourceFile);
			OutputStream outputStream = new FileOutputStream(resultFile);

			int n = inputStream.read();
			if (n == -1) {
				throw new IOException("Error 1");
			}


			for (int i = 0; i < n; i++) {
				int key = inputStream.read();
				int count = inputStream.read();
				if (key == -1 || count == -1) {
					throw new IOException("Error 2");
				}
				int byteRead = inputStream.read(); // TODO this doesnt work for count > 8, fix changes in Huffman.compressFile()
				if (byteRead == -1) {
					throw new IOException("Error 3");
				}
				StringBuilder value = new StringBuilder(Integer.toBinaryString(byteRead & 0xFF));
				if (value.length() > count) {
					value = new StringBuilder(value.substring(value.length() - count));
				} else {
					while (value.length() < count) {
						value.insert(0, "0");
					}
				}
				nodeValues.put(value.toString(), key);
			}

			int byteRead;
            String allBits = "", currentBits = "";
			while ((byteRead = inputStream.read()) != -1) {
                String binaryNumber = String.format("%8s", Integer.toBinaryString(byteRead & 0xFF));
                binaryNumber = binaryNumber.replace(' ', '0');;
                allBits += binaryNumber;
                for (int i = 0; i < allBits.length(); i++) {
                    currentBits += allBits.charAt(i);
                    if (nodeValues.get(currentBits) != null) {
                        int key = nodeValues.get(currentBits);
                        outputStream.write(key);
                        currentBits = "";
                        allBits = allBits.substring(i+1);
                        i = -1;
                        if (allBits.length() < 8) {
                            break;
                        }
                    }
                }
            }

			inputStream.close();
			outputStream.close();

			System.out.println("Decompression completed successfully.");
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
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
