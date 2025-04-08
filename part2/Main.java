import java_cup.runtime.*;
import java.io.*;

class Main {
    public static void main(String[] argv) throws Exception{
        ParserIR p = new ParserIR(new ScannerIR(new InputStreamReader(System.in)));
        p.parse();
    }
}
