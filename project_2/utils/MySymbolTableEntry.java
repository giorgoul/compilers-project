package utils;
import java.util.LinkedList;

public class MySymbolTableEntry {
    String identifier;
    String kind;
    String extend;
    String type;
    int scope;
    LinkedList<String> belongsTo;

    public MySymbolTableEntry(String identifier, String kind, String extend, String type, int scope, LinkedList<String> belongsTo) {
        this.identifier = identifier;
        this.kind = kind;
        this.extend = extend;
        this.type = type;
        this.scope = scope;
        this.belongsTo = belongsTo;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public LinkedList<String> getBelongsTo() {
        return this.belongsTo;
    }

    public String getKind() {
        return this.kind;
    }

    public String getExtend() {
        return this.extend;
    }
}
