package parser;

import java.util.function.Predicate;

public class Parser {
    public int pos;        //当前位置
    public String input;       //输入的html文本

    public Parser() {
        pos = 0; //初始化位置
    }


    /**
     * 读取当前字符而不consume它
     */
    public char next_char() {
        return input.charAt(pos);
    }

    /**
     * 下一个字符是否以给定的字符串开始
     */
    public boolean start_with(int n, char[] c) {
        return input.substring(pos, pos + n).equals(String.valueOf(c));
    }

    /**
     * 如果读完了所有输入的字符，则返回true。
     */
    public boolean eof() {
        return pos >= input.length() - 1;
    }

    /**
     * 返回当前字符，并将 input的位置 推进到下一个字符。
     */
    public char consume_char() {
        pos++;
        return input.charAt(pos - 1);
    }

    /**
     * 消耗字符，直到`test`返回false为止
     *
     */
    public String consume_while(Predicate<Character> predicate, char c) {
        StringBuilder sb = new StringBuilder();
        while (!eof() && predicate.test(c)) {
            sb.append(consume_char());
        }
        return sb.toString();
    }

    /**
     * 读取多个（也可能是零个）空白字符
     */
    public void consume_whitespace() {
        if (eof()) {
            return;
        }
        while (Character.isWhitespace(next_char())) {
            consume_char();
        }
    }
}
