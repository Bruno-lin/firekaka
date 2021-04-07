package css;

import java.util.ArrayList;

public class Stylesheet {
    ArrayList<Rule> rules;

    public Stylesheet(ArrayList<Rule> rules) {
        this.rules = rules;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Rule rule : rules) {
            ArrayList<String> list = new ArrayList<>();
            for (Selector selector : rule.selectors) {

                StringBuilder sb1 = new StringBuilder();

                if (!selector.tag_name.equals("")) {
                    sb1.append(selector.tag_name);
                }

                if (!selector.id.equals("")) {
                    sb1.append("#").append(selector.id);
                }

                if (selector.class_array.size() != 0) {
                    for (String class_array : selector.class_array) {
                        sb1.append('.').append(class_array);
                    }
                }
                if (!String.valueOf(sb1).equals("")) {
                    list.add(sb1.toString());
                }
            }
            sb.append(String.join(", ", list));
            sb.append(" {\n");

            for (Declaration declaration : rule.declarations) {
                sb.append("  ").append(declaration.name).append(": ").append(declaration.value.toString()).append(";\n");
            }
            sb.append("}\n\n");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }
}
