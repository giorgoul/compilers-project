import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import checks.SymbolTableChecks;
import syntaxtree.*;
import utils.PrintOffsets;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length == 0){
            System.err.println("Usage: java Main <inputFile1> <inputFile2> ...");
            System.exit(1);
        }

        FileInputStream fis = null;
        for (int i = 0; i < args.length; i++) {
            try{
                fis = new FileInputStream(args[i]);
                MiniJavaParser parser = new MiniJavaParser(fis);

                Goal root = parser.Goal();

                MyVisitor eval = new MyVisitor();
                root.accept(eval, null);
                try {
                    // Basic symbol table checks
                    SymbolTableChecks.TypeExistsCheck(eval.symbolTable);
                    SymbolTableChecks.DoubleDeclarationCheck(eval.symbolTable);
                    SymbolTableChecks.ProperOverrideCheck(eval.symbolTable);
                    SymbolTableChecks.ExtendedClassExistsCheck(eval.symbolTable);
                    SymbolTableChecks.ExtendsBeforeDeclarationCheck(eval.symbolTable);
                } catch (Exception ex) {
                    System.out.println(args[i] + ": Fail");
                    continue;
                }
                try {
                    MySecondVisitor eval2 = new MySecondVisitor(eval.symbolTable);
                    root.accept(eval2, null);
                    System.out.println(args[i] + ": Success");
                    PrintOffsets.print(eval.symbolTable);
                } catch (Exception ex) {
                    System.out.println(args[i] + ": Fail");
                }
            }
            catch(ParseException ex){
                System.out.println(ex.getMessage());
            }
            catch(FileNotFoundException ex){
                System.err.println(ex.getMessage());
            }
            finally{
                try{
                    if(fis != null) fis.close();
                }
                catch(IOException ex){
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
}