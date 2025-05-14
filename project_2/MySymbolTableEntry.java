public class MySymbolTableEntry {
    String id;
    String type;
    int scope;
    String belongsTo;

    public MySymbolTableEntry(String identifier, String type, int scope, String belongsTo) {
        this.id = identifier;
        this.type = type;
        this.scope = scope;
        this.belongsTo = belongsTo;
    }
}
