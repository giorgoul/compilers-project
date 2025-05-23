package checks;
import java.util.Vector;
import utils.MySymbolTable;
import utils.MySymbolTableEntry;
import utils.CompareLinkedLists;

public class SymbolTableChecks {
    public static void DoubleDeclarationCheck(MySymbolTable table) throws Exception {
        // Using for instead of foreach loop to skip first iteration's entry
        for (int i = 0; i < table.getSymbolTable().size(); i++) {
            MySymbolTableEntry entry = table.getSymbolTable().elementAt(i);
            for (int j = 0; j < table.getSymbolTable().size(); j++) {
                MySymbolTableEntry entry2 = table.getSymbolTable().elementAt(j);
                if (i != j && entry.getIdentifier().equals(entry2.getIdentifier()) && CompareLinkedLists.compare(entry.getBelongsTo(), entry2.getBelongsTo()))
                    throw new Exception("Semantic error: Identifier declared more than once");
            }
        }
    }

    public static void ExtendedClassExistsCheck(MySymbolTable table) throws Exception {
        for (int i = 0; i < table.getSymbolTable().size(); i++) {
            MySymbolTableEntry entry = table.getSymbolTable().elementAt(i);
            if (entry.getExtend().equals("-")) continue;
            String inheritedFrom = entry.getExtend();
            boolean found = false;
            for (int j = 0; j < table.getSymbolTable().size(); j++) {
                MySymbolTableEntry entry2 = table.getSymbolTable().elementAt(j);
                if (entry2.getIdentifier().equals(inheritedFrom)) {
                    found = true;
                    break;
                }
            }
            if (!found) throw new Exception("Semantic error: Parent class doesn't exist");
        }
    }

    public static void ExtendsBeforeDeclarationCheck(MySymbolTable table) throws Exception {
        for (int i = 0; i < table.getSymbolTable().size(); i++) {
            MySymbolTableEntry entry = table.getSymbolTable().elementAt(i);
            // Find classes that inherit from another class
            if (entry.getExtend().equals("-")) continue;
            // Go up the chain of inheritances and make sure each class declaration is before
            // the declaration of its descendants
            String inheritedFrom = entry.getExtend();
            boolean done = false;
            while (!done) {
                boolean found = false;
                // Lookup class with identifier inheritedFrom. If it doesn't exist before i,
                // throw an exception
                // Change inheritedFrom depending on whether the class found inherits
                // from another class
                for (int j = 0; j < i; j++) {
                    if (j < i) {
                        MySymbolTableEntry entry2 = table.getSymbolTable().elementAt(j);
                        if (entry2.getIdentifier().equals(inheritedFrom)) {
                            if (!entry2.getExtend().equals("-")) {
                                found = true;
                                inheritedFrom = entry2.getExtend();
                            } else {
                                done = true;
                                break;
                            }
                        }
                    }
                }
                if (!found && !done)
                    throw new Exception("Semantic error: Class extends before extended class' declaration");
                else
                    found = false;
            }
        }
    }

    public static void TypeExistsCheck(MySymbolTable table) throws Exception {
        for (MySymbolTableEntry entry : table.getSymbolTable()) {
            if (entry.getKind().equals("var") || entry.getKind().equals("field") || entry.getKind().equals("param")) {
                // int, int[], boolean, boolean[] always exist
                if (entry.getType().equals("int") || entry.getType().equals("int[]") || entry.getType().equals("boolean") || entry.getType().equals("boolean[]")) {
                    continue;
                } else {
                    boolean found = false;
                    for (MySymbolTableEntry entry2 : table.getSymbolTable()) {
                        if (entry2.getKind().equals("class"))
                            if (entry2.getIdentifier().equals(entry.getType()))
                                found = true;
                    }
                    if (!found)
                        throw new Exception("Semantic Error: Type " + entry.getType() + " doesn't exist");
                }
            }
        }
    }

    public static void ProperOverrideCheck(MySymbolTable table) throws Exception {
        // For each extended class' method, search its parent and compare the return
        // type and parameters
        for (MySymbolTableEntry entry : table.getSymbolTable()) {
            if (entry.getKind().equals("class") && !entry.getExtend().equals("-")) {
                Vector<MySymbolTableEntry> methods = table.getMethods(entry.getIdentifier());
                // Search for method in parent class and then compare return type, parameters
                for (MySymbolTableEntry method : methods) {
                    MySymbolTableEntry parentMethod = table.findMethod(entry.getExtend(), method.getIdentifier());
                    if (parentMethod == null) continue;
                    if (!parentMethod.getType().equals(method.getType())) {
                        throw new Exception("Semantic error: Improper overriding");
                    }
                    String parentParameters = table.getParameters(parentMethod);
                    String parameters = table.getParameters(method);
                    if (!parentParameters.equals(parameters)) {
                        throw new Exception("Semantic error: Improper overriding");
                    }
                }
            }
        }
    }
}
