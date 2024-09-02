package lexical;

public class Token {
    public WordType type;      //类型
    public String context;  //内容
    public int line;        //行号
    Token(WordType type, String context) {
        this.type = type;
        this.context = context;
    }
    Token(WordType type, String context, int line) {
        this.type = type;
        this.context = context;
        this.line = line;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", context='" + context + '\'' +
                ", line=" + line +
                '}';
    }
}