package css;

import java.util.ArrayList;

public class Selector {
    String tag_name;
    String id;
    ArrayList<String> class_array;

    public Selector(String tag_name, String id, ArrayList<String> class_array) {
        this.tag_name = tag_name;
        this.id = id;
        this.class_array = class_array;
    }

    public int specificity() {
        int a = 0;
        int b = 0;
        int c = 0;
        if (id != "") {
            a += 3;
        }

        if (class_array.size() != 0) {
            b += 2;
        }

        if (tag_name != "") {
            c += 1;
        }
        return a + b + c;
    }
}
