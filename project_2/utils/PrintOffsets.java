package utils;

import java.util.HashMap;

class VTable {
    HashMap<String, Integer> method_offset;

    VTable() {
        method_offset = new HashMap<String, Integer>();
    }

    // For extended classes
    VTable(HashMap<String, Integer> method_offsets) {
        method_offset = new HashMap<String, Integer>(method_offsets);
    }
}

public class PrintOffsets {
    public static void print(MySymbolTable symbolTable) {
        // Offset for class fields
        int currentOffset = 0;
        String currentClass = "";
        // Use a map for each class' v-table
        HashMap<String, VTable> class_vtables = new HashMap<>();
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
                class_vtables.put(currentClass, new VTable());

                // If there's a parent class, copy its v-table over
                if (!entry.getExtend().equals("-")) {
                    extend = entry.getExtend();
                    VTable originalClassVtable = class_vtables.get(extend);
                    HashMap<String, Integer> method_offsets = originalClassVtable.method_offset;
                    class_vtables.put(entry.getIdentifier(), new VTable(method_offsets));
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
                boolean print = true;
                if (!extend.equals("-")) {
                    VTable parentVtable = class_vtables.get(extend);
                    if (parentVtable.method_offset.containsKey(entry.getIdentifier())) {
                        offset = parentVtable.method_offset.get(entry.getIdentifier());
                        print = false;
                    } else {
                        // If it doesn't exist, place it right next to the last method
                        // Find max value in the HashMap first
                        HashMap<String, Integer> offsets = class_vtables.get(currentClass).method_offset;
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
                    HashMap<String, Integer> offsets = class_vtables.get(currentClass).method_offset;
                    int max = -8;
                    for (Integer val : offsets.values()) {
                        if (val.intValue() > max) {
                            max = val.intValue();
                        }
                    }
                    offset = max + 8;  
                }
                if (printMethodString) {printMethodString = false; System.out.println("---Methods---");}
                class_vtables.get(currentClass).method_offset.put(entry.getIdentifier(), offset);
                if (print) System.out.println(currentClass + "." + entry.getIdentifier() + " : " + offset);
            }
        }
    }
}
