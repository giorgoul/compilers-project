package checks;
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
}
