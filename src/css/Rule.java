package css;

import java.util.ArrayList;

public class Rule {
    ArrayList<Selector> selectors;
    ArrayList<Declaration> declarations;

    public Rule(ArrayList<Selector> selectors, ArrayList<Declaration> declarations) {
        this.selectors = selectors;
        this.declarations = declarations;
    }

    public ArrayList<Selector> getSelectors() {
        return selectors;
    }

    public ArrayList<Declaration> getDeclarations() {
        return declarations;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "selectors=" + selectors +
                ", declarations=" + declarations +
                '}';
    }
}
