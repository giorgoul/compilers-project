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
        // Initialize non-existent linked list
        if (this.map.get(identifier) == null) {
            LinkedList<MySymbolTableEntry> list = new LinkedList<MySymbolTableEntry>();
            this.map.put(identifier, list);
        }
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

    // For debugging purposes
    public void print() {
        System.out.println("identifier,type,scope,belongsTo");

        // https://stackoverflow.com/questions/12310914/how-to-iterate-through-linkedhashmap-with-lists-as-values
        for (String key : map.keySet()) {
            LinkedList<MySymbolTableEntry> result = this.map.get(key);
            for (MySymbolTableEntry entry : result) {
                System.out.println(entry.id + "," + entry.type + "," + entry.scope + "," + entry.belongsTo);
            }
        }
    }
}
