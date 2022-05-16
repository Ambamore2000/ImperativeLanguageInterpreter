// Java code for stack implementation
import java.io.*;
import java.util.*;
 
public class PEvaluator {

    FileWriter writer;
    Stack<String> stack;

    public PEvaluator(FileWriter writer) {
        stack = new Stack<String>();
        this.writer = writer;
    }

    public void evaluate() {
        boolean isEvaluatable = stack.size() >= 3;
        while (isEvaluatable) {
            
            String firstVal = stack.get(stack.size() - 1);
            String secondVal = stack.get(stack.size() - 2);
            String thirdVal = stack.get(stack.size() - 3);

            int firstValInt;
            int secondValInt;
            try { 
                firstValInt = Integer.parseInt(firstVal);
                secondValInt = Integer.parseInt(secondVal);

                //TODO Check for errors??
                if (thirdVal.equals("+")) {
                        stack.pop();
                        stack.pop();
                        stack.pop();
                        stack.push(String.valueOf(secondValInt + firstValInt));
                } else if (thirdVal.equals("*")) {
                        stack.pop();
                        stack.pop();
                        stack.pop();
                        stack.push(String.valueOf(secondValInt * firstValInt));
                } else if (thirdVal.equals("-")) {
                        stack.pop();
                        stack.pop();
                        stack.pop();
                        stack.push(String.valueOf(secondValInt - firstValInt));
                } else if (thirdVal.equals("/")) {
                        stack.pop();
                        stack.pop();
                        stack.pop();
                        try {
                        stack.push(String.valueOf(secondValInt / firstValInt));
                        } catch (ArithmeticException aE) {
                            try {
                                System.out.println("EVALUATOR: Division by zero invalid");
                                writer.write("EVALUATOR: Division by zero invalid");
                                writer.close();
                                System.exit(1);
                            } catch (IOException ioE) { ioE.printStackTrace(); }
                        }
                }

                if (stack.size() < 3) isEvaluatable = false;

            } catch (NumberFormatException e) {
                isEvaluatable = false;
            }
        }
    }

    void pushToStack(String value) {
        stack.push(value);
        evaluate();
    }
    
    void printEvaluation() throws IOException {
        writer.write("Output: " + stack.get(0) + "\n");
    }
}