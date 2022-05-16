import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PScanner {

    private String iFileStr, oFileStr;

    PScanner(String[] args) {
        if (args.length == 0) {
            iFileStr = "test_input.txt";
            oFileStr = "test_output.txt";
        } else {
            iFileStr = args[0];
            oFileStr = args[1];
        }
    }

    public void scan() throws IOException {
        Pattern numP = Pattern.compile("[\\d]+");
        Pattern symP = Pattern.compile("[\\+\\-\\*\\/\\(\\)\\:=\\;]");
        Pattern identP = Pattern.compile("\\b_*[a-zA-Z][_a-zA-Z0-9]*\\b");
        Pattern whiteP = Pattern.compile("[\\s]+");

        Matcher numM, symM, identM, whiteM;

        File iFile = new File(iFileStr);
        FileWriter writer = new FileWriter(oFileStr);

        Scanner scanner = new Scanner(iFile);

        String[][] tokenList = new String[100][2];
        
        int tokenListIndex = 0;

        while (scanner.hasNextLine()) {

            String currToken = "";
            String line = scanner.nextLine();

            TokenType currTokenType = TokenType.NULL, charTokenType = TokenType.NULL;

            for (int x = 0; x < line.length(); x++) {

                boolean isNewToken = false;
                boolean invalidToken = false;

                String newTokenChar = Character.toString(line.charAt(x));
                
                whiteM = whiteP.matcher(newTokenChar);

                if (whiteM.find()) {
                    isNewToken = true;
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
                        
                        if ((currToken.length() == 1 && !currToken.equals(":")) || (currToken.length() == 2 && currToken.equals(":="))) {
                            isNewToken = true;
                        }
                    } else {
                        invalidToken = true;
                    }

                    if (currTokenType != charTokenType && !(currTokenType == TokenType.IDENTIFIER && charTokenType == TokenType.NUMBER)) {
                        if (currTokenType != TokenType.NULL) {

                            if (currTokenType == TokenType.IDENTIFIER && 
                            (currToken.equals("if") || currToken.equals("then") ||
                            currToken.equals("else") || currToken.equals("endif") ||
                            currToken.equals("while") || currToken.equals("do") ||
                            currToken.equals("endwhile") || currToken.equals("skip"))) {
                                currTokenType = TokenType.KEYWORD;
                            }

                            tokenList[tokenListIndex][0] = currToken.substring(0, currToken.length() - 1);
                            tokenList[tokenListIndex][1] = currTokenType.toString();
                            tokenListIndex++;
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

                        if (currTokenType == TokenType.IDENTIFIER && 
                            (currToken.equals("if") || currToken.equals("then") ||
                            currToken.equals("else") || currToken.equals("endif") ||
                            currToken.equals("while") || currToken.equals("do") ||
                            currToken.equals("endwhile") || currToken.equals("skip"))) {
                            currTokenType = TokenType.KEYWORD;
                        }
                        
                        tokenList[tokenListIndex][0] = currToken;
                        tokenList[tokenListIndex][1] = currTokenType.toString();
                        tokenListIndex++;
                        currToken = "";
                    }
                    charTokenType = TokenType.NULL;
                    currTokenType = TokenType.NULL;
                }

                if (invalidToken) {
                    if (currTokenType != TokenType.NULL) {
                        
                        tokenList[tokenListIndex][0] = currToken;
                        tokenList[tokenListIndex][1] = currTokenType.toString();
                        tokenListIndex++;
                        currToken = "";
                    }
                    charTokenType = TokenType.NULL;
                    currTokenType = TokenType.NULL;
                    writer.write("ERROR READING '" + newTokenChar + "'" + "\n");
                    break;
                }

            }
        }
        
        writer.write("\n");
        Main.pParser = new PParser(writer, tokenList);
        Main.pParser.parse();

        scanner.close();
        writer.close();
        
    }

}