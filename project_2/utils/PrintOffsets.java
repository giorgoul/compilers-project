package utils;

public class PrintOffsets {
    public static void print(MySymbolTable symbolTable) {
        int currentOffset = 0;
        String currentClass = "";
        for (MySymbolTableEntry entry : symbolTable.getSymbolTable()) {
            if (entry.getKind().equals("class")) {
                currentClass = entry.getIdentifier();
                System.out.println("-----------Class " + currentClass + "-----------");
                System.out.println("---Variables---");
            } else if (entry.getKind().equals("field")) {
                System.out.println(currentClass + "." + entry.getIdentifier() + ": " + currentOffset);
                if (entry.getType().equals("int")) {
                    currentOffset += 4;
                } else if (entry.getType().equals("boolean")) {
                    currentOffset += 1;
                } else {
                    currentOffset += 8;
                }
            }
        }
    }
}
