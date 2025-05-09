import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;

/* Implement a symbol table of the following format:
 * identifier | type | scope | belongs_to
 * For example, `class A {int x; int y;}` corresponds to
 * A | "class" | 0 (if not within another class) | "-"
 * x | "int"   | 1 (within A)                    | "A"
 * y | "int"   | 1                               | "A"
 */

public class MySymbolTable {
    LinkedHashMap<String, LinkedList<MySymbolTableEntry>> map;
    
    public MySymbolTable() {
       this.map = new LinkedHashMap<String, LinkedList<MySymbolTableEntry>>();
    }

    public void insert(String identifier, String type, int scope, String belongsTo) {
        this.map.get(identifier).add(new MySymbolTableEntry(identifier, type, scope, belongsTo));
    }

    public MySymbolTableEntry find(String identifier, int occurrence) {
        int temp = 0;
        LinkedList<MySymbolTableEntry> result = this.map.get(identifier);
        for (MySymbolTableEntry entry : result) {
            if (++temp == occurrence) {
                return entry;
            }
        }
        return new MySymbolTableEntry("not found", "-", -1, "-");
    }
}
