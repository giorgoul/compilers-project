package utils;
import java.util.Iterator;
import java.util.LinkedList;

// Used for checking whether the path of two identifiers with the same
// name is the same
public class CompareLinkedLists {
    public static boolean compare(LinkedList<String> ll1, LinkedList<String> ll2) {
        if (ll1.size() != ll2.size())
            return false;
        Iterator<String> it1 = ll1.iterator();
        Iterator<String> it2 = ll2.iterator();

        while(it1.hasNext()) {
            if (!it1.next().equals(it2.next()))
                return false;
        }

        return true;
    }
}
