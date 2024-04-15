import java.io.*;
import java.util.Scanner;

public class Main1 {
    public static void main(String[] args)
    {
        String fileName = "File4.html";

        Scanner sc = new Scanner(System.in);
        System.out.print("LZ77 - 0, decode - 1, isEqual - 2: ");
        String choice = sc.nextLine();
        sc.close();

        if(choice.equals("0"))
        {
            File file = new File(fileName);
            if(!file.exists()) {return;}

            String rawText = "", searchBuff = "", temp = "";
            int sequenceLocation, lastLocation = -1;
            try
            {
                PrintWriter scpw = new PrintWriter(new FileWriter("LZ77_" + fileName));
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
                int reading, counter = 0;
                while((reading = reader.read()) != -1)
                {
                    rawText += (char)reading;
                    counter++;
                    if(counter == 4000)
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
                                    scpw.print((char)(searchBuff.length() - lastLocation));
                                    scpw.print((char)(temp.length() - 1));
                                    scpw.print(temp.substring(temp.length() - 1));
                                    lastLocation = -1;
                                }
                                else
                                {
                                    scpw.print((char)0);
                                    scpw.print((char)0);
                                    scpw.print(temp);
                                }

                                searchBuff += temp;
                                counter -= temp.length();
                                rawText = rawText.substring(rawText.length()-counter);
                                temp = "";
                                if(searchBuff.length() > 2047)
                                {
                                    searchBuff = searchBuff.substring(searchBuff.length() - 2047);
                                }
                                break;
                            }
                            else
                            {
                                lastLocation = sequenceLocation;
                            }
                        }
                    }
                }
                reader.close();
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
                                scpw.print((char)(searchBuff.length() - lastLocation));
                                scpw.print((char)(temp.length() - 1));
                                scpw.print(temp.substring(temp.length() - 1));
                                lastLocation = -1;
                            }
                            else
                            {
                                scpw.print((char)0);
                                scpw.print((char)0);
                                scpw.print(temp);
                            }

                            searchBuff += temp;
                            counter -= temp.length();
                            rawText = rawText.substring(rawText.length()-counter);
                            temp = "";
                            if(searchBuff.length() > 2047)
                            {
                                searchBuff = searchBuff.substring(searchBuff.length() - 2047);
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
                        scpw.print((char)(searchBuff.length() - lastLocation));
                        scpw.print((char)(temp.length() - 1));
                        scpw.print(temp.substring(temp.length() - 1));
                        break;
                    }
                }
                scpw.close();
            }
            catch(Exception exception) {System.out.println("Error!");}
        }
        else if(choice.equals("1"))
        {
            File file = new File("LZ77_" + fileName);
            if(!file.exists()) {return;}

            int distance, amount;
            String rawText = "", temp = "";
            try
            {
                PrintWriter scpw = new PrintWriter(new FileWriter("DeLZ77_LZ77_" + fileName));
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("LZ77_" + fileName)));
                int reading, counter = 0;
                while((reading = reader.read()) != -1)
                {
                    temp += (char)reading;
                    counter++;
                    if(counter == 3)
                    {
                        distance = temp.charAt(0);
                        amount = temp.charAt(1);

                        if(distance == 0 && amount == 0)
                        {
                            scpw.print(temp.charAt(2));
                            rawText += temp.charAt(2);
                            temp = "";
                            counter = 0;
                        }
                        else
                        {
                            scpw.print(rawText.substring(rawText.length() - distance, rawText.length() - distance + amount));
                            scpw.print(temp.charAt(2));
                            rawText += rawText.substring(rawText.length() - distance, rawText.length() - distance + amount);
                            rawText += temp.charAt(2);
                            temp = "";
                            counter = 0;
                        }

                        if(rawText.length() > 2047)
                        {
                            rawText = rawText.substring(rawText.length() - 2047);
                        }
                    }
                }
                reader.close();
                scpw.close();
            }
            catch(Exception exception) {System.out.println("Error!");}
        }
        else if(choice.equals("2"))
        {
            try {
                FileInputStream f1 = new FileInputStream(fileName);
                FileInputStream f2 = new FileInputStream("DeLZ77_LZ77_" + fileName);
                int k1, k2;
                byte[] buf1 = new byte[1000];
                byte[] buf2 = new byte[1000];
                do {
                    k1 = f1.read(buf1);
                    k2 = f2.read(buf2);
                    if (k1 != k2) {
                        f1.close();
                        f2.close();
                        System.out.println("FALSE!");
                        break;
                    }
                    for (int i=0; i<k1; i++) {
                        if (buf1[i] != buf2[i]) {
                            f1.close();
                            f2.close();
                            System.out.println("FALSE!");
                            break;
                        }

                    }
                } while (!(k1 == -1 && k2 == -1));
                f1.close();
                f2.close();
                System.out.println("True");
            }
            catch (IOException ex) {
                System.out.println(ex.getMessage());
                System.out.println("FALSE!");
            }
        }
    }
}