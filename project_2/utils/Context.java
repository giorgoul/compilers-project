package utils;

import java.util.LinkedList;

// Context used within the symbol table.
// Another class inherits from this for the
// visitors' second pass.
public class Context {
    protected int currentScope;
    protected LinkedList<String> currentPath;

    public Context() {
        this.currentScope = 0;
        this.currentPath = new LinkedList<String>();
    }

    public void incrementScope() {
        this.currentScope++;
    }

    public void decrementScope() {
        this.currentScope--;
    }

    public int getScope() {
        return this.currentScope;
    }

    public void addToPath(String name) {
        this.currentPath.addFirst(name);
    }

    // Removes the innermost name from the path,
    // for example foo -> B turns to B.
    public void removeLastFromPath() {
        this.currentPath.removeFirst();
    }

    public LinkedList<String> getPath() {
        return this.currentPath;
    }
}

class VisitorContext extends Context {
    protected String currentClass;
    protected String currentMethod;

    public VisitorContext() {
        this.currentClass = "";
        this.currentMethod = "";
    }

    public String getCurrentClass() {
        return this.currentClass;
    }

    public String getCurrentMethod() {
        return this.currentMethod;
    }

    public void setCurrentClass(String classname) {
        this.currentClass = classname;
    }

    public void setCurrentMethod(String methodname) {
        this.currentMethod = methodname;
    }
}