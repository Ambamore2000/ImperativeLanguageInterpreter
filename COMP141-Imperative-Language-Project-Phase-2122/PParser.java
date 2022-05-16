import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;

public class PParser {

    FileWriter writer;

    String[][] tokenList;
    String next_token;
    int currIndex;

    public PParser(FileWriter writer, String[][] tokenList) {

        this.tokenList = tokenList;
        this.writer = writer;
    }

    void parse() throws IOException {
        currIndex = 0;
        consumeToken();

        PTNode tree = parseStatement();
        writer.write("Tokens:\n\n");

        for (int x = 0; x < tokenList.length; x++) {
            if (tokenList[x][0] == null) break;
            writer.write(tokenList[x][1] + " " + tokenList[x][0] + "\n");
        }
        
        writer.write("\nAST:\n\n");

        printPreorder(writer, tree, 0);

    }

    void cprintPreorder(PTNode node, int tab)
    {
        if (node == null)
            return;
 
        for (int x = 0; x < tab; x++) {
            System.out.print("\t");
        }

        System.out.print(node.type + " " + node.value + "\n");

        cprintPreorder(node.left, tab + 1);
 
        if (node.middle != null) cprintPreorder(node.middle, tab + 1);

        cprintPreorder(node.right, tab + 1);
    }

    void printPreorder(FileWriter writer, PTNode node, int tab)
    {

        if (node == null)
            return;
 
        try {
        for (int x = 0; x < tab; x++) {
            writer.write("\t");
        }
            if (node.type == null)
                writer.write(node.value + "\n");
            else
                writer.write(node.type + " " + node.value + "\n");
        } catch (IOException e) { }

        if (node.value.equals("endif")) {
            System.out.println("wtf");
            cprintPreorder(node, 0);
        }

        printPreorder(writer, node.left, tab + 1);

        if (node.middle != null)
            printPreorder(writer, node.middle, tab + 1);

        printPreorder(writer, node.right, tab + 1);
    }
    private void consumeToken() {

        next_token = tokenList[currIndex][0];
        currIndex++;
        if (next_token == null)
            next_token = "";
        
    }

    PTNode parseExpression() {
        PTNode tree = parseTerm();

        while (next_token.equals("+")) {
            consumeToken();
            tree = new PTNode("+", tree, parseTerm());
            tree.type = "SYMBOL";
        }

        return tree;
    }

    PTNode parseTerm() {
        PTNode tree = parseFactor();
        
        while (next_token.equals("-")) {
            consumeToken();
            tree = new PTNode("-", tree, parseFactor());
            tree.type = "SYMBOL";
        } 

        return tree;
    }

    PTNode parseFactor() {
        PTNode tree = parsePiece();
        
        while (next_token.equals("/")) {
            consumeToken();
            tree = new PTNode("/", tree, parsePiece());
            tree.type = "SYMBOL";
        }

        return tree;
    }

    PTNode parsePiece() {
        PTNode tree = parseElement();

        while (next_token.equals("*")) {
            consumeToken();
            tree = new PTNode("*", tree, parseElement());
            tree.type = "SYMBOL";
        }
        
        return tree;
    }

    PTNode parseElement() {
        if (next_token.equals("(")) {
            consumeToken();
            PTNode tree = parseExpression();
            if (next_token.equals(")")) {
                consumeToken();
                return tree;
            } else {
                System.out.println("ERROR: Could not parse ''" + next_token + "''");
                return null;
            }
        } else if (Pattern.compile("\\b_*[a-zA-Z][_a-zA-Z0-9]*\\b").matcher(next_token).find()) {
            return parseIdentifier();
        } else if (Pattern.compile("[\\d]+").matcher(next_token).find()) {
            return parseNumber();
        } else {
            System.out.println("ERROR: Could not parse ''" + next_token + "''");
            return null;
        }
    }
    
    PTNode parseIdentifier() {
        String n = next_token;
        consumeToken();
        PTNode tree = new PTNode(n);
        tree.type = "IDENTIFIER";
        return tree;
    }
    
    PTNode parseNumber() {
        String n = next_token;
        consumeToken();
        PTNode tree = new PTNode(n);
        tree.type = "NUMBER";
        return tree;
    }

/*
    ADDED GRAMMAR RULES
*/

    PTNode parseStatement() {
        PTNode tree = parseBaseStatement();

        while (next_token.equals(";")) {
            consumeToken();
            tree = new PTNode(";", tree, parseBaseStatement());
            tree.type = "SYMBOL";
        }
        
        return tree;
    }

    PTNode parseBaseStatement() {
        PTNode tree;
        if (next_token.equals("if")) { 
            tree = parseIfStatement();
        } else if (next_token.equals("while")) {
            tree = parseWhileStatement();
        } else if (next_token.equals("skip")) {
            consumeToken();
            tree = new PTNode("skip");
            tree.type = "SKIP";
        } else {
            tree = parseAssignment();
        }
        
        return tree;
    }

    PTNode parseAssignment() {
        PTNode tree = new PTNode(next_token);
        consumeToken();

        if (next_token.equals(":=")) {

            consumeToken();

            tree = new PTNode(":=", tree, parseExpression());
            
            tree.left.type = "IDENTIFIER";
            tree.type = "SYMBOL";
        }

        return tree;
    }

    PTNode parseIfStatement() {
        PTNode tree = null;

        if (next_token.equals("if")) {

            consumeToken();
            PTNode expression = parseExpression();

            if (next_token.equals("then")) {

                consumeToken();
                PTNode thenStatement = parseStatement();

                if (next_token.equals("else")) {

                    consumeToken();
                    PTNode elseStatement = parseStatement();

                    if (next_token.equals("endif")) {

                        tree = new PTNode("IF-STATEMENT", expression, thenStatement, elseStatement);

                    }
                }
            }
        }

        return tree;
    }

    PTNode parseWhileStatement() {

        PTNode tree = null;
        
        if (next_token.equals("while")) {

            consumeToken();
            PTNode expression = parseExpression();
            
            if (next_token.equals("do")) {

                consumeToken();
                PTNode statement = parseStatement();

                if (next_token.equals("endwhile")) {

                    tree = new PTNode("WHILE-LOOP", expression, statement);

                }
            }
        }

        return tree;
    }
}