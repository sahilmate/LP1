package src;

import java.io.*;
import java.util.*;

public class Pass_2 {
    public static void main(String[] args) throws IOException {
        // Creating file objects to read data from the 3 text files
        BufferedReader br = new BufferedReader(new FileReader("src/asm/intermediate.txt"));
        BufferedReader b2 = new BufferedReader(new FileReader("src/asm/symTab.txt"));
        BufferedReader b3 = new BufferedReader(new FileReader("src/asm/litTab.txt"));

        // Creating file writer object
        FileWriter fw = new FileWriter("src/asm/Pass2output.txt");

        // Map the pointer and the symbol/literal addresses
        HashMap<Integer, String> symSymbol = new HashMap<>();
        HashMap<Integer, String> litSymbol = new HashMap<>();
        HashMap<Integer, String> litAddr = new HashMap<>();
        int symtabPointer = 1, littabPointer = 1;
        String line, s;

        // Mapping symbol addresses with symbol ptr
        while ((s = b2.readLine()) != null) {
            s = s.trim();  // Remove any leading/trailing whitespace
            if (s.isEmpty()) continue;  // Skip empty lines

            String[] word = s.split("\t+");  // Split by one or more tab characters
            if (word.length > 1) {
                symSymbol.put(symtabPointer++, word[1]);  // Safely access the second part
            } else {
                System.out.println("Skipping invalid symbol line: " + s);
            }
        }

        // Mapping literal addresses with literal ptr
        while ((s = b3.readLine()) != null) {
            s = s.trim();  // Remove any leading or trailing whitespace
            if (s.isEmpty()) continue;  // Skip empty lines

            String[] word = s.split("\t+");  // Split by one or more tab characters
            if (word.length > 1) {
                litSymbol.put(littabPointer, word[0]);
                litAddr.put(littabPointer++, word[1]);
            } else {
                System.out.println("Skipping invalid literal line: " + s);
            }
        }

        // Reading intermediate code
        while ((line = br.readLine()) != null) {
            line = line.trim();  // Trim the line for extra spaces
            if (line.isEmpty()) continue;  // Skip empty lines

            // Skipping processing of Assembler Directives
            if (line.contains("AD")) {
                fw.write("\n");
                continue;
            }

            // Processing imperative statements
            else if (line.contains("IS")) {
                if (line.contains("IS,00")) {
                    fw.write("+ 00  0  000\n");
                } else {
                    fw.write("+ " + line.substring(4, 6) + "  " + line.charAt(8) + "  ");
                }

                // Process symbol (S)
                if (line.contains("(S,")) {
                    int startIdx = line.indexOf("(S,") + 3;
                    int endIdx = line.indexOf(")", startIdx);
                    if (startIdx > 0 && endIdx > startIdx) {
                        String symbolIndex = line.substring(startIdx, endIdx);
                        try {
                            String symbol = symSymbol.get(Integer.parseInt(symbolIndex));
                            fw.write(symbol + "\n");
                        } catch (NumberFormatException e) {
                            System.out.println("Error parsing symbol index: " + symbolIndex);
                        }
                    }
                }

                // Process literal (L)
                else if (line.contains("(L,")) {
                    int startIdx = line.indexOf("(L,") + 3;
                    int endIdx = line.indexOf(")", startIdx);
                    if (startIdx > 0 && endIdx > startIdx) {
                        String literalIndex = line.substring(startIdx, endIdx);
                        try {
                            String literalAddrValue = litAddr.get(Integer.parseInt(literalIndex));
                            fw.write(literalAddrValue + "\n");
                        } catch (NumberFormatException e) {
                            System.out.println("Error parsing literal index: " + literalIndex);
                        }
                    }
                }
            }

            // Process DS statement
            else if (line.contains("DL,01")) {
                fw.write("+ 00  0  00" + line.charAt(10) + "\n");
            } else if (line.contains("DL,02")) {
                fw.write("\n");
            }
        }

        // Close all file readers and writers
        fw.close();
        br.close();
        b2.close();
        b3.close();
    }
}
