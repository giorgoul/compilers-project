import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import checks.SymbolTableChecks;
import syntaxtree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length != 1){
            System.err.println("Usage: java Main <inputFile>");
            System.exit(1);
        }



        FileInputStream fis = null;
        try{
            fis = new FileInputStream(args[0]);
            MiniJavaParser parser = new MiniJavaParser(fis);

            Goal root = parser.Goal();

            System.err.println("Program parsed successfully.");

            MyVisitor eval = new MyVisitor();
            root.accept(eval, null);
            eval.symbolTable.print();
            try {
                // Basic symbol table checks
                SymbolTableChecks.TypeExistsCheck(eval.symbolTable);
                SymbolTableChecks.DoubleDeclarationCheck(eval.symbolTable);
                // Relatively weaker check, goes before the declaration one
                SymbolTableChecks.ExtendedClassExistsCheck(eval.symbolTable);
                SymbolTableChecks.ExtendsBeforeDeclarationCheck(eval.symbolTable);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            MySecondVisitor eval2 = new MySecondVisitor(eval.symbolTable);
            root.accept(eval2, null);
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