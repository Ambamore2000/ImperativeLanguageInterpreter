import java.io.IOException;

public class Main {
    
    static PScanner pScanner;
    static PParser pParser;

    public static void main(String[] args) {
        pScanner = new PScanner(args);
        try {
            pScanner.scan();
        } catch (IOException e) {
            System.out.println("ERROR: Scanner failed to scan...");
        }
    }

}