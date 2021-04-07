package css;

public class Value {
    public ValueType valueType;
    public String text;

    /**
     * 支持css中一部分的值
     */

    public Value(String val) {
        if (val.startsWith("#")) {
            valueType = ValueType.COLOR;
            text = val.substring(1);
        } else if (val.endsWith("px")) {
            valueType = ValueType.SIZE;
            text = val.substring(0, val.length() - 2);
        } else {
            valueType = ValueType.KEYWORD;
            text = val;
        }
    }

    @Override
    public String toString() {

        if (valueType.equals(ValueType.COLOR) && text != null) {
            if (!text.equals("")) {
                return "#" + text;
            } else {
                return "initial";
            }
        } else if (valueType.equals(ValueType.KEYWORD)) {
            return text;
        } else {
            return text + "px";
        }
    }
}
