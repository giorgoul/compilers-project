package utils;
import java.util.LinkedList;
import java.util.Vector;

/* Implement a symbol table of the following format:
 * identifier | kind | extend | type | scope | belongs_to
 * For example, `class A {int x; int y;}` corresponds to
 * A | "class" | "-" | "-" | 0 (always since classes cannot exist within other classes) | empty linked list
 * x | "var"   | "-" | "int" | 1 (within A)      | "A"
 * y | "var"   | "-" | "int" | 1                 | "A"
 */

public class MySymbolTable {
    protected Vector<MySymbolTableEntry> table;
    protected Context context;
   
    public MySymbolTable() {
       this.table = new Vector<>();
       this.context = new Context();
    }

    public void insert(String identifier, String kind, String extend, String type) {
        // Deep copy of currentPath so it stays the same even after updating this.currentPath
        MySymbolTableEntry to_insert = new MySymbolTableEntry(identifier, kind, extend, type, this.context.getScope(), new LinkedList<>(this.context.getPath()));
        this.table.add(to_insert);
    }

    public Vector<MySymbolTableEntry> getSymbolTable() {
        return this.table;
    }

    public Context getContext() {
        return this.context;
    }

    public int numOfOccurencies(String identifier) {
        int occurencies = 0;
        for (MySymbolTableEntry entry : this.table) {
            if (identifier.equals(entry.identifier)) {
                occurencies++;
            }
        }
        
        return occurencies;
    }

    public MySymbolTableEntry find(String identifier, String kind, String type, int scope, int occurrence) {
        int temp = 0;
        for (MySymbolTableEntry entry : this.table) {
            if (++temp == occurrence) {
                return entry;
            }
        }
        return new MySymbolTableEntry("not found", "-", "-", "-", -1, new LinkedList<>());
    }

    // For debugging purposes
    public void print() {
        System.out.println("identifier,kind,extend,type,scope,belongsTo");

        for (MySymbolTableEntry entry : this.table) {
            System.out.print(entry.identifier + "," + entry.kind + "," + entry.extend + "," + entry.type + "," + entry.scope + ",");
            if (entry.belongsTo.isEmpty()) {
                System.out.print("-");
            } else {
                for (String path : entry.belongsTo) {
                    System.out.print(path + "->");
                }
                // Don't print trailing -> (terminal dependent)
                System.out.print("\b\b  ");
            }
            System.out.println();
        }
    }
}
