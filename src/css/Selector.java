package css;

import java.util.ArrayList;
import java.util.Objects;

public class Selector {
    public String tag_name;
    public String id;
    public ArrayList<String> class_array;

    public Selector(String tag_name, String id, ArrayList<String> class_array) {
        this.tag_name = tag_name;
        this.id = Objects.requireNonNullElse(id, "");
        this.class_array = Objects.requireNonNullElseGet(class_array, ArrayList::new);
    }

    public int specificity() {
        int a = 0;
        int b = 0;
        int c = 0;
        if (!id.equals("")) {
            a += 3;
        }

        if (class_array.size() != 0) {
            b += 2;
        }

        if (!tag_name.equals("")) {
            c += 1;
        }
        return a + b + c;
    }
}
