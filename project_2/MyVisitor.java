import syntaxtree.*;
import utils.MySymbolTable;
import visitor.*;


class MyVisitor extends GJDepthFirst<String, Void>{
    MySymbolTable symbolTable = new MySymbolTable();


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
        String classname = n.f1.accept(this, null);
        String argsname = n.f11.accept(this, null);

        this.symbolTable.insert(classname, "mainclass", "-", "-");
        // We're within a method, i.e. scope 2
        // Current path is main -> classname
        this.symbolTable.getContext().addToPath(classname);
        this.symbolTable.getContext().addToPath("main");
        this.symbolTable.getContext().incrementScope();
        this.symbolTable.getContext().incrementScope();
        this.symbolTable.insert(argsname, "param", "-", "String[]");
        n.f14.accept(this, argu);
        // Revert path done so far and scope
        this.symbolTable.getContext().decrementScope();
        this.symbolTable.getContext().decrementScope();
        this.symbolTable.getContext().removeLastFromPath();
        this.symbolTable.getContext().removeLastFromPath();
        return null;
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
        n.f0.accept(this, argu);
        
        String classname = n.f1.accept(this, argu);
        this.symbolTable.insert(classname, "class", "-", "-");

        n.f2.accept(this, argu);

        this.symbolTable.getContext().incrementScope();
        this.symbolTable.getContext().addToPath(classname);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        this.symbolTable.getContext().removeLastFromPath();
        this.symbolTable.getContext().decrementScope();

        n.f5.accept(this, argu);

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
        n.f0.accept(this, argu);

        String classname = n.f1.accept(this, null);

        n.f2.accept(this, argu);
        String extendsname = n.f3.accept(this, argu);

        this.symbolTable.insert(classname, "class", extendsname, "-");

        n.f4.accept(this, argu);

        this.symbolTable.getContext().incrementScope();
        this.symbolTable.getContext().addToPath(classname);
        n.f5.accept(this, argu);

        n.f6.accept(this, argu);

        this.symbolTable.getContext().removeLastFromPath();
        this.symbolTable.getContext().decrementScope();
        
        n.f7.accept(this, argu);

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
        String var = n.f1.accept(this, argu);
        // Class field
        if (this.symbolTable.getContext().getScope() == 1)
            this.symbolTable.insert(var, "field", "-", type);
        // Method local variable
        if (this.symbolTable.getContext().getScope() == 2)
            this.symbolTable.insert(var, "var", "-", type);
        
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
        String myType = n.f1.accept(this, null);
        String myName = n.f2.accept(this, null);

        this.symbolTable.getContext().incrementScope();
        this.symbolTable.insert(myName, "method", "-", myType);
        this.symbolTable.getContext().addToPath(myName);

        if (n.f4.present()) {
            n.f4.accept(this, null);
        }

        n.f7.accept(this, null);

        this.symbolTable.getContext().removeLastFromPath();
        this.symbolTable.getContext().decrementScope();

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
        this.symbolTable.insert(name, "param", "-", type);
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

    @Override
    public String visit(Identifier n, Void argu) {
        return n.f0.toString();
    }
}