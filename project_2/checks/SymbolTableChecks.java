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
}
