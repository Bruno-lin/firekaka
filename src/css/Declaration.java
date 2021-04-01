package css;

public class Declaration {
    String name;
    Value value;

    public Declaration(String name, String value){
        this.name = name;
        this.value = new Value(value);
    }
}
