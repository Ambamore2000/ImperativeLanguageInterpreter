 /*
Project Phase 1.2
By: Andrew Kim, Felipe Martinez

Written in Java


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

enum TokenType {
    IDENTIFIER, NUMBER, SYMBOL, KEYWORD, NULL;
}

public class Main {

    static String iFileStr, oFileStr;

    public static void main(String[] args) {

        iFileStr = "test_input.txt";//args[0];
        oFileStr = "test_output.txt";//args[1];

        Pattern numP = Pattern.compile("[\\d]+");
        Pattern symP = Pattern.compile("[\\+\\-\\*\\/\\(\\)\\:=\\;]");
        Pattern identP = Pattern.compile("\\b_*[a-zA-Z][_a-zA-Z0-9]*\\b"); 
        Pattern whiteP = Pattern.compile("[\\s]+");
        
        Matcher numM, symM, identM, whiteM;

        File iFile = new File(iFileStr);
        FileWriter writer = null;
        try { writer = new FileWriter(oFileStr);
        } catch (IOException e) { return; };

        Scanner scanner = null;
        
        try {
            try {
                scanner = new Scanner(iFile);
            } catch (FileNotFoundException e) { writer.close(); return; }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                writer.write("Line: " + line + "\n");

                String currToken = "";
                TokenType currTokenType = TokenType.NULL, charTokenType = TokenType.NULL;

                boolean isWhiteSpace = false;

                for (int x = 0; x < line.length(); x++) {

                    boolean isNewToken = false;
                    boolean invalidToken = false;

                    String newTokenChar = Character.toString(line.charAt(x));

                    whiteM = whiteP.matcher(newTokenChar);

                    if (whiteM.find()) {
                        isNewToken = true;
                        isWhiteSpace = true;
                    } else {
                        identM = identP.matcher(newTokenChar);
                        numM = numP.matcher(newTokenChar);
                        symM = symP.matcher(newTokenChar);

                        if (identM.find()) {
                            currToken = currToken + newTokenChar;
                            charTokenType = TokenType.IDENTIFIER;
                            if (currTokenType == TokenType.NULL) {
                                currTokenType = TokenType.IDENTIFIER;
                            }
                        } else if (numM.find()) {
                            currToken = currToken + newTokenChar;
                            charTokenType = TokenType.NUMBER;
                            if (currTokenType == TokenType.NULL) {
                                currTokenType = TokenType.NUMBER;
                            }
                        } else if (symM.find()) {
                            currToken = currToken + newTokenChar;
                            charTokenType = TokenType.SYMBOL;
                            if (currTokenType == TokenType.NULL) {
                                currTokenType = TokenType.SYMBOL;
                            }
                        } else {
                            invalidToken = true;
                        }

                        if (currTokenType != charTokenType && !(currTokenType == TokenType.IDENTIFIER && charTokenType == TokenType.NUMBER)) {
                            if (currTokenType != TokenType.NULL) {
                                writer.write(currToken.substring(0, currToken.length() - 1) + " : " + currTokenType.toString() + "\n");
                                currToken = "";
                            }
                            charTokenType = TokenType.NULL;
                            currTokenType = TokenType.NULL;
                            x--;
                            continue;
                        }

                    }
                    
                    if (isNewToken || x == line.length() - 1) {
                        if (currTokenType != TokenType.NULL) {
                            writer.write(currToken + " : " + currTokenType.toString() + "\n");
                            currToken = "";
                        }
                        charTokenType = TokenType.NULL;
                        currTokenType = TokenType.NULL;
                    }

                    if (invalidToken) {
                        if (currTokenType != TokenType.NULL) {
                            writer.write(currToken + " : " + currTokenType.toString() + "\n");
                            currToken = "";
                        }
                        charTokenType = TokenType.NULL;
                        currTokenType = TokenType.NULL;
                        writer.write("ERROR READING '" + newTokenChar + "'" + "\n");
                        break;
                    }
                }
                writer.write("\n");
            }
        } catch (IOException e) {

        }
        

         
        

        scanner.close();
        try {
            writer.close();
        } catch (IOException e) { return; };
    }



}*/