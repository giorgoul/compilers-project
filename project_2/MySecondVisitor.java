import java.util.Iterator;
import java.util.LinkedList;

import syntaxtree.*;
import utils.MySymbolTable;
import utils.MySymbolTableEntry;
import utils.VisitorContext;
import visitor.*;


class MySecondVisitor extends GJDepthFirst<String, Void>{
    MySymbolTable table;

    // Context so that inner visitors (e.g. AssignmentStatement) "know"
    // where they are. Similar to MySymbolTable.

    VisitorContext context;
    
    public MySecondVisitor(MySymbolTable table) {
        this.table = table;
        context = new VisitorContext();
    }

    /**
        * f0 -> "class"
        * f1 -> Identifier()
        * f2 -> "{"
        * f3 -> "public"
        * f4 -> "static"
        * f5 -> "void"
        * f6 -> "main"
        * f7 -> "("
        * f8 -> "String"
        * f9 -> "["
        * f10 -> "]"
        * f11 -> Identifier()
        * f12 -> ")"
        * f13 -> "{"
        * f14 -> ( VarDeclaration() )*
        * f15 -> ( Statement() )*
        * f16 -> "}"
        * f17 -> "}"
    */
    @Override
    public String visit(MainClass n, Void argu) throws Exception {
        context.setIdentifierReturns("string");
        context.setCurrentClass(n.f1.accept(this, null));
        context.setCurrentMethod("main");
        context.incrementScope();
        context.incrementScope();
        context.setIdentifierReturns("type");
        n.f15.accept(this, argu);
        context.setIdentifierReturns("string");
        context.decrementScope();
        context.decrementScope();
        context.setCurrentMethod("");
        context.setCurrentClass("");
        return null;
    }

    /**
        * f0 -> Block()
        *       | AssignmentStatement()
        *       | ArrayAssignmentStatement()
        *       | IfStatement()
        *       | WhileStatement()
        *       | PrintStatement()
    */
    @Override
    public String visit(Statement n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
        * f0 -> "{"
        * f1 -> ( Statement() )*
        * f2 -> "}"
    */
    @Override
    public String visit(Block n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return null;
    }

    /**
        * f0 -> Identifier()
        * f1 -> "="
        * f2 -> Expression()
        * f3 -> ";"
    */
    @Override
    public String visit(AssignmentStatement n, Void argu) throws Exception {
        this.context.setIdentifierReturns("string");
        String identifier = n.f0.accept(this, argu);
        String type1 = "";
        MySymbolTableEntry varEntry = this.table.findVar(this.context.getCurrentClass(), this.context.getCurrentMethod(), identifier);
        if (varEntry != null) {
            type1 = varEntry.getType();
        } else {
            MySymbolTableEntry fieldEntry = this.table.findField(this.context.getCurrentClass(), identifier);
            if (fieldEntry != null) type1 = fieldEntry.getType();
        }
        if (type1.equals("")) throw new Exception("Semantic error: Identifier doesn't exist");
        this.context.setIdentifierReturns("type");
        String type2 = n.f2.accept(this, argu);

        if (!type1.equals(type2)) {
            // Handle other cases first
            if ((type1.equals("int") && !type2.equals("int")) || (!type1.equals("int") && type2.equals("int"))) {
                throw new Exception("Semantic error: Incompatible type when assigning to variable");
            }
            if ((type1.equals("int[]") && !type2.equals("int[]")) || (!type1.equals("int[]") && type2.equals("int[]"))) {
                throw new Exception("Semantic error: Incompatible type when assigning to variable");
            }
            if ((type1.equals("boolean") && !type2.equals("boolean")) || (!type1.equals("boolean") && type2.equals("boolean"))) {
                throw new Exception("Semantic error: Incompatible type when assigning to variable");
            }
            if ((type1.equals("boolean[]") && !type2.equals("boolean[]")) || (!type1.equals("boolean[]") && type2.equals("boolean[]"))) {
                throw new Exception("Semantic error: Incompatible type when assigning to variable");
            }
            // Check for inheritance, is f0's type a parent of f2's type?
            // Go up the chain of inheritances until type is found (success)
            // or until there isn't another parent class (fail)
            // TODO: change to isSubclass call
            String classToFind = type2;
            boolean done = false;
            while (!done) {
                for (MySymbolTableEntry entry : this.table.getSymbolTable()) {
                    if (entry.getKind().equals("class") && entry.getIdentifier().equals(classToFind)) {
                        if (entry.getExtend().equals("-")) {
                            throw new Exception("Semantic error: Incompatible type when assigning to variable");
                        }
                        if (entry.getExtend().equals(type1)) {
                            done = true;
                            break;
                        }
                        classToFind = entry.getExtend();
                        break;
                    }
                }
            }
        }
        n.f3.accept(this, argu);
        return null;
    }

    /**
        * f0 -> Identifier()
        * f1 -> "["
        * f2 -> Expression()
        * f3 -> "]"
        * f4 -> "="
        * f5 -> Expression()
        * f6 -> ";"
    */
    @Override
    public String visit(ArrayAssignmentStatement n, Void argu) throws Exception {
        this.context.setIdentifierReturns("string");
        String identifier = n.f0.accept(this, argu);
        String type1 = "";
        MySymbolTableEntry varEntry = this.table.findVar(this.context.getCurrentClass(), this.context.getCurrentMethod(), identifier);
        if (varEntry != null) {
            type1 = varEntry.getType();
        } else {
            MySymbolTableEntry fieldEntry = this.table.findField(this.context.getCurrentClass(), identifier);
            if (fieldEntry != null) type1 = fieldEntry.getType();
        }
        if (type1.equals("")) throw new Exception("Semantic Error: Identifier doesn't exist");

        if (!type1.equals("int[]") && !type1.equals("boolean[]")) {
            throw new Exception("Semantic Error: Assigning value to non-array");
        }
        String type2 = n.f2.accept(this, argu);
        if (!type2.equals("int")) {
            throw new Exception("Semantic error: Indices must be of type int");
        }
        String type3 = n.f5.accept(this, argu);
        if ((type1.equals("int[]") && !type3.equals("int")) || (type1.equals("boolean[]") && !type3.equals("boolean"))) {
            throw new Exception("Semantic error: Incompatible type when assigning to array member");
        }
        return null;
    }

    /**
        * f0 -> "if"
        * f1 -> "("
        * f2 -> Expression()
        * f3 -> ")"
        * f4 -> Statement()
        * f5 -> "else"
        * f6 -> Statement()
    */
    @Override
    public String visit(IfStatement n, Void argu) throws Exception {

        this.context.setIdentifierReturns("type");
        String type = n.f2.accept(this, argu);
        if (!type.equals("boolean")) {
            throw new Exception("Semantic Error: If condition is not boolean");
        }
        n.f4.accept(this, argu);
        n.f6.accept(this, argu);
        return null;
    }

    /**
        * f0 -> "while"
        * f1 -> "("
        * f2 -> Expression()
        * f3 -> ")"
        * f4 -> Statement()
    */
    @Override
    public String visit(WhileStatement n, Void argu) throws Exception {
        this.context.setIdentifierReturns("type");
        String type = n.f2.accept(this, argu);
        if (!type.equals("boolean")) {
            throw new Exception("Semantic Error: While condition is not boolean");
        }
        n.f4.accept(this, argu);
        return null;
    }

    /**
        * f0 -> "System.out.println"
        * f1 -> "("
        * f2 -> Expression()
        * f3 -> ")"
        * f4 -> ";"
    */
    @Override
    public String visit(PrintStatement n, Void argu) throws Exception {
        this.context.setIdentifierReturns("type");
        String type = n.f2.accept(this, argu);
        if (!type.equals("int")) {
            throw new Exception("Semantic Error: Can only print int");
        }
        return null;
    }

    /**
        * f0 -> AndExpression()
        *       | CompareExpression()
        *       | PlusExpression()
        *       | MinusExpression()
        *       | TimesExpression()
        *       | ArrayLookup()
        *       | ArrayLength()
        *       | MessageSend()
        *       | Clause()
    */
    // Returns the resulting type of the expression
    public String visit(Expression n, Void argu) throws Exception {
        return n.f0.accept(this, null);
    }

    /**
        * f0 -> Clause()
        * f1 -> "&&"
        * f2 -> Clause()
    */
    @Override
    public String visit(AndExpression n, Void argu) throws Exception {
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = n.f2.accept(this, argu);
        if (!type1.equals("boolean") || !type2.equals("boolean")) {
            throw new Exception("Semantic error: Logical and expression must have boolean clauses");
        }
        // Result of logical and is a boolean
        return "boolean";
    }

    /**
        * f0 -> PrimaryExpression()
        * f1 -> "<"
        * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(CompareExpression n, Void argu) throws Exception {
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = n.f2.accept(this, argu);
        if (!type1.equals("int") || !type2.equals("int")) {
            throw new Exception("Semantic error: Can only compare ints");
        }
        return "boolean";
    }

    /**
        * f0 -> PrimaryExpression()
        * f1 -> "+"
        * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(PlusExpression n, Void argu) throws Exception {
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = n.f2.accept(this, argu);
        if (!type1.equals("int") || !type2.equals("int")) {
            throw new Exception("Semantic error: Can only add ints");
        }
        return "int";
    }

    /**
        * f0 -> PrimaryExpression()
        * f1 -> "-"
        * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(MinusExpression n, Void argu) throws Exception {
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = n.f2.accept(this, argu);
        if (!type1.equals("int") || !type2.equals("int")) {
            throw new Exception("Semantic error: Can only subtract ints");
        }
        return "int";
    }

    /**
        * f0 -> PrimaryExpression()
        * f1 -> "*"
        * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(TimesExpression n, Void argu) throws Exception {
        String type1 = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String type2 = n.f2.accept(this, argu);
        if (!type1.equals("int") || !type2.equals("int")) {
            throw new Exception("Semantic error: Can only multiply ints");
        }
        return "int";
    }

    /**
        * f0 -> PrimaryExpression()
        * f1 -> "["
        * f2 -> PrimaryExpression()
        * f3 -> "]"
    */
    @Override
    public String visit(ArrayLookup n, Void argu) throws Exception {
        this.context.setIdentifierReturns("type");
        String type1 = n.f0.accept(this, argu);
        if (!type1.equals("int[]") && !type1.equals("boolean[]")) {
            throw new Exception("Semantic error: Array access on non-array");
        }
        String type2 = n.f2.accept(this, argu);
        if (!type2.equals("int")) {
            throw new Exception("Semantic error: Indices must be of type int");
        }
        if (type1.equals("int[]")) {
            return "int";
        }
        return "boolean";
    }

    /**
        * f0 -> PrimaryExpression()
        * f1 -> "."
        * f2 -> "length"
    */
    @Override
    public String visit(ArrayLength n, Void argu) throws Exception {
        this.context.setIdentifierReturns("type");
        String type = n.f0.accept(this, argu);
        if (!type.equals("int[]") && !type.equals("boolean[]")) {
            throw new Exception("Semantic Error: Can't use .length on non-array");
        }
        return "int";
    }

    /**
        * f0 -> PrimaryExpression()
        * f1 -> "."
        * f2 -> Identifier()
        * f3 -> "("
        * f4 -> ( ExpressionList() )?
        * f5 -> ")"
    */
    @Override
    public String visit(MessageSend n, Void argu) throws Exception {
        this.context.setIdentifierReturns("type");
        String type = n.f0.accept(this, argu);
        if (type.equals("int") || type.equals("boolean") || type.equals("int[]") || type.equals("boolean[]")) {
            throw new Exception("Semantic Error: Primitive types don't have methods");
        }
        this.context.setIdentifierReturns("string");

        // Check whether method exists within class type (or any of type's parent classes)
        String methodname = n.f2.accept(this, argu);
        String returns = "";
        String parameterTypes = "";
        MySymbolTableEntry methodEntry = this.table.findMethod(type, methodname);
        if (methodEntry == null) {
            throw new Exception("Semantic Error: Method doesn't exist within class or its parents");
        }
        returns = methodEntry.getType();
        parameterTypes = this.table.getParameters(methodEntry);

        // Check if arguments of call match method parameters
        String argTypes = n.f4.present() ? n.f4.accept(this, argu) : "";

        // For debugging
        System.out.println("Found method: " + methodname);
        System.out.println("Method found has parameters: " + parameterTypes);
        System.out.println("Method call has arguments: " + argTypes);

        if (!parameterTypes.equals(argTypes)) {
            // Compare each type one-by-one
            String[] parameterTypesSplit = parameterTypes.split(", ");
            String[] argTypesSplit = argTypes.split(", ");
            if (parameterTypesSplit.length != argTypesSplit.length) {
                throw new Exception("Semantic Error: Different number of call arguments and method parameters");
            }
            for (int i = 0; i < parameterTypesSplit.length; i++) {
                // Same initial checks as with AssignmentStatements
                if ((parameterTypesSplit[i].equals("int") && !argTypesSplit[i].equals("int")) || (!parameterTypesSplit[i].equals("int") && argTypesSplit[i].equals("int"))) {
                    throw new Exception("Semantic error: Incompatible type during method call");
                }
                if ((parameterTypesSplit[i].equals("int[]") && !argTypesSplit[i].equals("int[]")) || (!parameterTypesSplit[i].equals("int[]") && argTypesSplit[i].equals("int[]"))) {
                    throw new Exception("Semantic error: Incompatible type during method call");
                }
                if ((parameterTypesSplit[i].equals("boolean") && !argTypesSplit[i].equals("boolean")) || (!parameterTypesSplit[i].equals("boolean") && argTypesSplit[i].equals("boolean"))) {
                    throw new Exception("Semantic error: Incompatible type during method call");
                }
                if ((parameterTypesSplit[i].equals("boolean[]") && !argTypesSplit[i].equals("boolean[]")) || (!parameterTypesSplit[i].equals("boolean[]") && argTypesSplit[i].equals("boolean[]"))) {
                    throw new Exception("Semantic error: Incompatible type during method call");
                }
                if (parameterTypesSplit[i].equals(argTypesSplit[i])) continue;
                if (!this.table.isSubclass(argTypesSplit[i], parameterTypesSplit[i])) {
                    throw new Exception("Semantic error: Incompatible type during method call");
                }
            }
        }
        return returns;
    }

    /**
        * f0 -> Expression()
        * f1 -> ExpressionTail()
    */
    // Return ExpressionLists as comma-separated types, same as with FormalParameters.
    @Override
    public String visit(ExpressionList n, Void argu) throws Exception {
        this.context.setIdentifierReturns("type");
        String types = n.f0.accept(this, argu);
        if (n.f1 != null) {
            types += n.f1.accept(this, argu);
        }
        return types;
    }

    /**
        * f0 -> ( ExpressionTerm() )*
    */
    @Override
    public String visit(ExpressionTail n, Void argu) throws Exception {
        String types = "";
        for (Node node : n.f0.nodes) {
            types += ", " + node.accept(this, null);
        }
        return types;
    }

    /**
        * f0 -> ","
        * f1 -> Expression()
    */
    @Override
    public String visit(ExpressionTerm n, Void argu) throws Exception {
        this.context.setIdentifierReturns("type");
        String type = n.f1.accept(this, argu);
        return type;
    }

    /**
        * f0 -> NotExpression()
        *       | PrimaryExpression()
    */
    @Override
    public String visit(Clause n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
        * f0 -> "!"
        * f1 -> Clause()
    */
    @Override
    public String visit(NotExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String type = n.f1.accept(this, argu);
        if (!type.equals("boolean")) {
            throw new Exception("Semantic error: Clause doesn't evaluate to boolean type");
        }
        return type;
    }

    /**
        * f0 -> IntegerLiteral()
        *       | TrueLiteral()
        *       | FalseLiteral()
        *       | Identifier()
        *       | ThisExpression()
        *       | ArrayAllocationExpression()
        *       | AllocationExpression()
        *       | BracketExpression()
    */
    @Override
    public String visit(PrimaryExpression n, Void argu) throws Exception {
        String type = n.f0.accept(this, argu);
        return type;
    }

    /**
        * f0 -> <INTEGER_LITERAL>
    */
    @Override
    public String visit(IntegerLiteral n, Void argu) throws Exception {
        return "int";
    }

    /**
        * f0 -> "true"
    */
    @Override
    public String visit(TrueLiteral n, Void argu) throws Exception {
        return "boolean";
    }

    /**
        * f0 -> "false"
    */
    public String visit(FalseLiteral n, Void argu) throws Exception {
        return "boolean";
    }

    /**
        * f0 -> <IDENTIFIER>
    */
    // Returns string representation of identifier OR its type
    // depending on the context
    @Override
    public String visit(Identifier n, Void argu) throws Exception {
        String identifier = n.f0.toString();
        if (this.context.getIdentifierReturns().equals("string")) {
            return identifier;
        } else if (this.context.getIdentifierReturns().equals("type")) {
            // Find the type based on current context
            MySymbolTableEntry varEntry = this.table.findVar(this.context.getCurrentClass(), this.context.getCurrentMethod(), identifier);
            if (varEntry != null) return varEntry.getType();
            MySymbolTableEntry fieldEntry = this.table.findField(this.context.getCurrentClass(), identifier);
            if (fieldEntry != null) return fieldEntry.getType();
            MySymbolTableEntry methodEntry = this.table.findMethod(this.context.getCurrentClass(), identifier);
            if (methodEntry != null) return methodEntry.getType();
            throw new Exception("Semantic Error: Identifier doesn't exist");
        }
        return null;
    }

    /**
        * f0 -> "this"
    */
    public String visit(ThisExpression n, Void argu) throws Exception {
        // "this" refers to the current class, so just return currentClass
        return this.context.getCurrentClass();
    }

    /**
        * f0 -> BooleanArrayAllocationExpression()
        *       | IntegerArrayAllocationExpression()
    */
    @Override
    public String visit(ArrayAllocationExpression n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
        * f0 -> "new"
        * f1 -> "boolean"
        * f2 -> "["
        * f3 -> Expression()
        * f4 -> "]"
    */
    @Override
    public String visit(BooleanArrayAllocationExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        String type = n.f3.accept(this, argu);
        if (!type.equals("int")) {
            throw new Exception("Semantic error: Indices must be of type int");
        }
        n.f4.accept(this, argu);
        return "boolean[]";
    }

    /**
        * f0 -> "new"
        * f1 -> "int"
        * f2 -> "["
        * f3 -> Expression()
        * f4 -> "]"
    */
    @Override
    public String visit(IntegerArrayAllocationExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        String type = n.f3.accept(this, argu);
        if (!type.equals("int")) {
            throw new Exception("Semantic error: Indices must be of type int");
        }
        n.f4.accept(this, argu);
        return "int[]";
    }

    /**
        * f0 -> "new"
        * f1 -> Identifier()
        * f2 -> "("
        * f3 -> ")"
    */
    @Override
    public String visit(AllocationExpression n, Void argu) throws Exception {
        // Search class that matches f1
        this.context.setIdentifierReturns("string");
        String identifier = n.f1.accept(this, argu);
        MySymbolTableEntry classEntry = this.table.findClass(identifier);
        if (classEntry != null) return identifier;
        // If class doesn't exist, throw exception
        throw new Exception("Semantic error: Class " + identifier + " doesn't exist");
    }

    /**
        * f0 -> "("
        * f1 -> Expression()
        * f2 -> ")"
    */
    @Override
    public String visit(BracketExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String type = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return type;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public String visit(ClassDeclaration n, Void argu) throws Exception {
        this.context.setIdentifierReturns("string");
        String classname = n.f1.accept(this, argu);
        this.context.incrementScope();
        this.context.setCurrentClass(classname);
        n.f4.accept(this, argu);
        this.context.setCurrentClass("");
        this.context.decrementScope();

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
        this.context.setIdentifierReturns("string");
        String classname = n.f1.accept(this, null);
        this.context.incrementScope();
        this.context.setCurrentClass(classname);
        n.f6.accept(this, argu);
        this.context.setCurrentClass("");
        this.context.decrementScope();
        return null;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Void argu) throws Exception {
        String _ret=null;
        String type = n.f0.accept(this, argu);
        // For consistency
        this.context.setIdentifierReturns("string");
        String var = n.f1.accept(this, argu);
        
        return _ret;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public String visit(MethodDeclaration n, Void argu) throws Exception {
        this.context.setIdentifierReturns("string");
        String returns = n.f1.accept(this, null);
        String methodname = n.f2.accept(this, null);
        this.context.incrementScope();
        this.context.setCurrentMethod(methodname);

        n.f4.accept(this, null);

        n.f7.accept(this, null);
        n.f8.accept(this, null);
        this.context.setIdentifierReturns("type");
        String type = n.f10.accept(this, null);
        // TODO: Check whether type is a subclass of returns 
        if (!type.equals(returns)) {
            throw new Exception("Semantic Error: Method type and return expression mismatch");
        }
        this.context.decrementScope();
        this.context.setCurrentMethod("");
        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, Void argu) throws Exception {
        String ret = n.f0.accept(this, null);

        if (n.f1 != null) {
            ret += n.f1.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterTerm n, Void argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public String visit(FormalParameterTail n, Void argu) throws Exception {
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, Void argu) throws Exception{
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);
        return type + " " + name;
    }

    @Override
    public String visit(ArrayType n, Void argu) {
        return "int[]";
    }

    public String visit(BooleanType n, Void argu) {
        return "boolean";
    }

    public String visit(IntegerType n, Void argu) {
        return "int";
    }
}