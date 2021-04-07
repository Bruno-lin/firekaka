package css;

public class Declaration {
    public String name;
    public Value value;

    public Declaration(String name, String value){
        this.name = name;
        this.value = new Value(value);
    }
}
