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

    // Returns a comma-separated string of a method's parameter types
    public String getParameters(MySymbolTableEntry method) {
        String result = "";
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
                    result += entry.getType() + ", ";
                } else {
                    break;
                }
            }
        }
        // Remove trailing ", "
        if (!result.equals(""))
            result = result.substring(0, result.length() - 2);
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
                if (entry.getIdentifier().equals(searchFor) && (entry.getKind().equals("class") || entry.getKind().equals("mainclass"))) {
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

    // Same implementation as findMethod
    public MySymbolTableEntry findField(String classname, String fieldname) {
        String searchFor = classname;
        while (true) {
            for (MySymbolTableEntry entry : this.table) {
                if (entry.getIdentifier().equals(fieldname) && entry.getKind().equals("field")) {
                    LinkedList<String> path = entry.getBelongsTo();
                    Iterator<String> path_iterator = path.iterator();
                    if (path_iterator.next().equals(searchFor)) {
                        return entry;
                    }
                }
            }
            for (MySymbolTableEntry entry : this.table) {
                if (entry.getIdentifier().equals(searchFor) && entry.getKind().equals("class")) {
                    if (entry.getExtend().equals("-")) {
                        return null;
                    }
                    searchFor = entry.getExtend();
                    break;
                }
            }
        }
    }

    public MySymbolTableEntry findVar(String classname, String methodname, String varname) {
        for (MySymbolTableEntry entry : this.table) {
            if (entry.getIdentifier().equals(varname) && (entry.getKind().equals("var") || entry.getKind().equals("param"))) {
                LinkedList<String> path = entry.getBelongsTo();
                Iterator<String> path_iterator = path.iterator();
                if (path_iterator.next().equals(methodname)) {
                    if (path_iterator.next().equals(classname)) {
                        return entry;
                    }
                }
            }
        }
        return null;
    }

    public MySymbolTableEntry findClass(String classname) {
        for (MySymbolTableEntry entry : this.table) {
            if (entry.getIdentifier().equals(classname) && entry.getKind().equals("class")) {
                return entry;
            }
        }
        return null;
    }

    // Is class2 a parent of class1?
    public boolean isSubclass(String class1, String class2) {
        if (class1.equals("int") || class1.equals("int[]") || class1.equals("boolean") || class1.equals("boolean[]")) return false;
        if (class2.equals("int") || class2.equals("int[]") || class2.equals("boolean") || class2.equals("boolean[]")) return false;
        String toFind = class1;
        while (true) {
            for (MySymbolTableEntry entry : this.table) {
                if (entry.getIdentifier().equals(toFind) && entry.getKind().equals("class")) {
                    // Search ends if there isn't a parent class (same as before)
                    if (entry.getExtend().equals("-")) return false;
                    if (entry.getExtend().equals(class2)) return true;
                    toFind = entry.getExtend();
                }
            }
        }
    }

    public Vector<MySymbolTableEntry> getMethods(String class1) {
        Vector<MySymbolTableEntry> methods = new Vector<MySymbolTableEntry>();
        boolean found = false;
        for (MySymbolTableEntry entry : this.table) {
            if (!found) {
                if (entry.getIdentifier().equals(class1) && entry.getKind().equals("class")) {
                    found = true;
                }
            } else {
                if (entry.getKind().equals("method")) {
                    methods.add(entry);
                } else if (entry.getKind().equals("class")) {
                    break;
                }
            }
        }
        return methods;
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
