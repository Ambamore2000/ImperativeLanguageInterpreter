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

        PTNode tree = parseExpression();//TODO: For PR3.2: parseStatement();
        writer.write("Tokens:\n\n");

        for (int x = 0; x < tokenList.length; x++) {
            if (tokenList[x][0] == null) break;
            writer.write(tokenList[x][0] + " : " + tokenList[x][1] + "\n");
        }
        
        writer.write("\nAST:\n\n");

        printPreorder(writer, tree, 0);

        PEvaluator evaluator = new PEvaluator(writer);
        pushEvaluator(evaluator, tree);
        evaluator.printEvaluation();
    }

    void pushEvaluator(PEvaluator evaluator, PTNode node)
    {

        if (node == null)
            return;
 
        evaluator.pushToStack(node.value);

        pushEvaluator(evaluator, node.left);

        if (node.middle != null)
            pushEvaluator(evaluator, node.middle);

        pushEvaluator(evaluator, node.right);
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
                writer.write(node.value + " : " + node.type + "\n");
        } catch (IOException e) { }

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
                try {
                    System.out.println("PARSER: Could not parse '" + next_token + "'");
                    writer.write("PARSER: Could not parse '" + next_token + "'");
                    writer.close();
                    System.exit(1);
                } catch (IOException e) { e.printStackTrace(); }
                return null;
            }
        } else if (Pattern.compile("\\b_*[a-zA-Z][_a-zA-Z0-9]*\\b").matcher(next_token).find()) {
            return parseIdentifier();
        } else if (Pattern.compile("[\\d]+").matcher(next_token).find()) {
            return parseNumber();
        } else {
            try {
                System.out.println("PARSER: Could not parse '" + next_token + "'");
                writer.write("PARSER: Could not parse '" + next_token + "'");
                writer.close();
                System.exit(1);
            } catch (IOException e) { e.printStackTrace(); }
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