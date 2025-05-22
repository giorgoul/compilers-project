package utils;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/* Implement a symbol table of the following format:
 * identifier | kind | extend | type | scope | belongs_to
 * For example, `class A {int x; int y;}` corresponds to
 * A | "class" | "-" | "-" | 0 (always since classes cannot exist within other classes) | empty linked list
 * x | "field"   | "-" | "int" | 1 (within A)      | "A"
 * y | "field"   | "-" | "int" | 1                 | "A"
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

    // Returns a linked list of a method's parameter types, with the method (table entry) as input
    public LinkedList<String> getParameters(MySymbolTableEntry method) {
        LinkedList<String> result = new LinkedList<String>();
        boolean found = false;
        for (MySymbolTableEntry entry : this.table) {
            // Unnecessary checks, just want to make sure
            if (!found) {
                if (method.getIdentifier().equals(entry.getIdentifier()) && method.getKind().equals(entry.getKind()) &&
                    method.getExtend().equals(entry.getExtend()) && method.getType().equals(entry.getType()) && method.getScope() == entry.getScope() &&
                    CompareLinkedLists.compare(method.getBelongsTo(), entry.getBelongsTo())) {
                    found = true;
                }
            } else {
                // Method parameters are stored right after the method itself, so we keep
                // adding them until we run into another method or class
                if (entry.getKind().equals("param")) {
                    result.add(entry.getType());
                } else {
                    break;
                }
            }
        }
        return result;
    }

    // Returns the method table entry if it exists within classname
    // Searches within the parent class(es) as well
    public MySymbolTableEntry findMethod(String classname, String methodname) {
        String searchFor = classname;
        while (true) {
            for (MySymbolTableEntry entry : this.table) {
                if (entry.getIdentifier().equals(methodname) && entry.getKind().equals("method")) {
                    LinkedList<String> path = entry.getBelongsTo();
                    Iterator<String> path_iterator = path.iterator();
                    // Make sure that the first (and only) element of the path is equal to the class name
                    if (path_iterator.next().equals(searchFor)) {
                        return entry;
                    }
                }
            }
            // If the method is not found within the current class, look for it on its parent
            for (MySymbolTableEntry entry : this.table) {
                if (entry.getIdentifier().equals(searchFor) && entry.getKind().equals("class")) {
                    // Search ends if there isn't a parent class
                    if (entry.getExtend().equals("-")) {
                        return null;
                    }
                    // Change searchFor and go back to the method search
                    searchFor = entry.getExtend();
                    break;
                }
            }
        }
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
