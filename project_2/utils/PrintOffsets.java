package utils;

import java.util.HashMap;

class VTable {
    HashMap<String, Integer> methodOffset;

    VTable() {
        methodOffset = new HashMap<String, Integer>();
    }

    // For extended classes
    VTable(HashMap<String, Integer> methodOffsets) {
        methodOffset = new HashMap<String, Integer>(methodOffsets);
    }
}

public class PrintOffsets {
    public static void print(MySymbolTable symbolTable) {
        // Offset for class fields
        int currentOffset = 0;
        String currentClass = "";
        // Use a map for each class' v-table
        HashMap<String, VTable> classVtables = new HashMap<>();
        // To print "---Methods---" only once
        boolean printMethodString = true;
        String extend = "-";

        for (MySymbolTableEntry entry : symbolTable.getSymbolTable()) {
            if (entry.getKind().equals("class")) {
                // Reset flags and values
                printMethodString = true;
                extend = "-";
                currentOffset = 0;

                currentClass = entry.getIdentifier();
                classVtables.put(currentClass, new VTable());

                // If there's a parent class, copy its v-table over
                if (!entry.getExtend().equals("-")) {
                    extend = entry.getExtend();
                    VTable parentClassVtable = classVtables.get(extend);
                    HashMap<String, Integer> methodOffsets = parentClassVtable.methodOffset;
                    classVtables.put(entry.getIdentifier(), new VTable(methodOffsets));
                }

                System.out.println("-----------Class " + currentClass + "-----------");
                System.out.println("---Variables---");
            } else if (entry.getKind().equals("field")) {
                System.out.println(currentClass + "." + entry.getIdentifier() + " : " + currentOffset);
                if (entry.getType().equals("int")) {
                    currentOffset += 4;
                } else if (entry.getType().equals("boolean")) {
                    currentOffset += 1;
                } else {
                    currentOffset += 8;
                }
            } else if (entry.getKind().equals("method")) {
                // If method exists in parent, use the same offset and don't print it
                int offset = 0;
                boolean printEntry = true;
                if (!extend.equals("-")) {
                    VTable parentVtable = classVtables.get(extend);
                    if (parentVtable.methodOffset.containsKey(entry.getIdentifier())) {
                        offset = parentVtable.methodOffset.get(entry.getIdentifier());
                        printEntry = false;
                    } else {
                        // If it doesn't exist, place it right next to the last method
                        // Find max value in the HashMap first
                        HashMap<String, Integer> offsets = classVtables.get(currentClass).methodOffset;
                        // The first added method has offset 0 (no max found)
                        int max = -8;
                        for (Integer val : offsets.values()) {
                            if (val.intValue() > max) {
                                max = val.intValue();
                            }
                        }
                        offset = max + 8;
                    }
                } else {
                    // Same process, it's essentially a new method
                    HashMap<String, Integer> offsets = classVtables.get(currentClass).methodOffset;
                    int max = -8;
                    for (Integer val : offsets.values()) {
                        if (val.intValue() > max) {
                            max = val.intValue();
                        }
                    }
                    offset = max + 8;  
                }
                if (printMethodString) {printMethodString = false; System.out.println("---Methods---");}
                classVtables.get(currentClass).methodOffset.put(entry.getIdentifier(), offset);
                if (printEntry) System.out.println(currentClass + "." + entry.getIdentifier() + " : " + offset);
            }
        }
    }
}
