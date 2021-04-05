package css;

public class Value {
    String keyword;
    String px;
    String color;

    /**
     * 支持css中一部分的值
     */

    public Value(String val) {
        if (val.startsWith("#")){
            color = val;
        }else if (val.endsWith("px")) {
            px = val;
        }else {
            keyword = val;
        }
    }

}
