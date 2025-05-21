package utils;

import java.util.LinkedList;

public class VisitorContext extends Context {
    protected String currentClass;
    protected String currentMethod;
    // Determines whether the identifier visitor should
    // return the string repr. of an identifier or its
    // type
    protected String identifierReturns;
    protected LinkedList<String> tempLinkedList;

    public VisitorContext() {
        this.currentClass = "";
        this.currentMethod = "";
        this.identifierReturns = "";
        this.tempLinkedList = new LinkedList<String>();
    }

    public String getCurrentClass() {
        return this.currentClass;
    }

    public String getCurrentMethod() {
        return this.currentMethod;
    }

    public String getIdentifierReturns() {
        return this.identifierReturns;
    }

    public LinkedList<String> getTempLinkedList() {
        return this.tempLinkedList;
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

    public void resetTempLinkedList() {
        this.tempLinkedList = new LinkedList<String>();
    }
}