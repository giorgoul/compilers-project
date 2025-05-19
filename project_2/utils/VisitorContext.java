package utils;

public class VisitorContext extends Context {
    protected String currentClass;
    protected String currentMethod;
    // Determines whether the identifier visitor should
    // return the string repr. of an identifier or its
    // type
    protected String identifierReturns;

    public VisitorContext() {
        this.currentClass = "";
        this.currentMethod = "";
        this.identifierReturns = "";
    }

    public String getCurrentClass() {
        return this.currentClass;
    }

    public String getCurrentMethod() {
        return this.currentMethod;
    }

    public String getIdentifierRetruns() {
        return this.identifierReturns;
    }

    public void setCurrentClass(String classname) {
        this.currentClass = classname;
    }

    public void setCurrentMethod(String methodname) {
        this.currentMethod = methodname;
    }

    public void setIdentifierReturns(String returns) {
        this.identifierReturns = returns;
    }
}