package css;

import java.util.ArrayList;

public class Rule {
    ArrayList<Selector> selectors;
    ArrayList<Declaration> declarations;

    public Rule(ArrayList<Selector> selectors, ArrayList<Declaration> declarations) {
        this.selectors = selectors;
        this.declarations = declarations;
    }
}
