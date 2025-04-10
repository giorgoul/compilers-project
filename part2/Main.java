import java_cup.runtime.*;
import java.io.*;

class Main {
    public static void main(String[] argv) throws Exception{
        ParserIR p_ir = new ParserIR(new ScannerIR(new InputStreamReader(System.in)));
        p_ir.parse();
        ParserIRJava p_ir_java = new ParserIRJava(new ScannerIR(new FileReader("Translated.ir")));
        p_ir_java.parse();
    }
}
