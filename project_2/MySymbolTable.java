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
    // Better to keep the current context in here as it's
    // more relevant to the symbol table
    protected int currentScope;
    // Helps build the belongsTo path
    protected String currentClassName;
    protected String currentMethodName;
    protected LinkedList<String> currentPath;

    
    public MySymbolTable() {
       this.table = new Vector<>();
       this.currentScope = 0;
       this.currentClassName = "";
       this.currentMethodName = "";
       this.currentPath = new LinkedList<>();
    }

    public void insert(String identifier, String kind, String extend, String type) {
        // Deep copy of currentPath so it stays the same even after updating this.currentPath
        MySymbolTableEntry to_insert = new MySymbolTableEntry(identifier, kind, extend, type, this.currentScope, new LinkedList<>(currentPath));
        this.table.add(to_insert);
    }

    public void incrementScope() {
        this.currentScope++;
    }

    public void decrementScope() {
        this.currentScope--;
    }

    public void setClassName(String name) {
        this.currentClassName = name;
    }

    public void setMethodName(String name) {
        this.currentMethodName = name;
    }

    public String getClassName() {
        return this.currentClassName;
    }

    public String getMethodName() {
        return this.currentMethodName;
    }

    public void addToPath(String name) {
        this.currentPath.addFirst(name);
    }

    // Removes the innermost name from the path,
    // for example foo -> B turns to B.
    public void removeLastFromPath() {
        this.currentPath.removeFirst();
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
            for (String path : entry.belongsTo) {
                System.out.print(path + "->");
            }
            System.out.print("\b\b  ");
            System.out.println();
        }
    }
}
