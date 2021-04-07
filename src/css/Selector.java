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
        int specificity = 0;
        if (!id.equals("")) {
            specificity += 99999;
        }
        specificity += 3 * class_array.size();

        if (tag_name.equals("*") || tag_name.equals("")) {
            specificity += 1;
        } else {
            specificity += 2;
        }
        return specificity;

    }

    @Override
    public String toString() {
        return "Selector{" +
                "tag_name='" + tag_name + '\'' +
                ", id='" + id + '\'' +
                ", class_array=" + class_array +
                '}';
    }
}
