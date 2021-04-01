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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getPx() {
        return px;
    }

    public void setPx(String px) {
        this.px = px;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
