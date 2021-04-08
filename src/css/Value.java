package css;

public class Value {
    public String color;
    public String text;
    public int px;

    /**
     * 支持css中一部分的值
     */

    /**
     * Value可能有三种
     *
     * @param value
     */
    public Value(String value) {

        if (value.startsWith("#")) {
            int i = Integer.parseInt(value.substring(1), 16);
            if (value.substring(1).length() <= 6 && value.substring(1).length() >= 3 && i >= 0) {
                color = value.substring(1);
            } else {
                color = "";
            }
        } else if (value.endsWith("px")) {
            px = (int) Double.parseDouble(value.substring(0, value.length() - 2));
        } else {
            text = value;
        }

    }

    @Override
    public String toString() {

        if (color != null) {
            if (!color.equals("")) {
                return "#" + color;
            } else {
                return "initial";
            }
        }

        if (text != null) {
            return text;
        }
        return px + "px";
    }

    public int to_px() {
        if (text == null || !text.equals("auto")) {
            return px;
        }
        return 0;
    }
}
