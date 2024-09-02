package lexical;
//35页识别SNL单词的DFA表示实现
public class WordScanner {
    //单词扫描器的状态信息
    enum State {
        START, INASSIGN, INCOMMENT, INNUM, INID, INCHAR, INRANGE, DONE
    }
    //保留字
    static String[] reservedWords = {
            "program",
            "type",
            "var",
            "procedure",
            "begin",
            "end",
            "array",
            "of",
            "record",
            "if",
            "then",
            "else",
            "fi",
            "while",
            "do",
            "endwh",
            "read",
            "write",
            "return",
            "integer",
            "char"
    };
    State state;
    String source;

    int next;//下标指针
    int line;//
    public WordScanner(){
        state = State.START;
        next=0;
        line=1;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public WordScanner(String source) {
        this.source = source;
        state = State.START;
        next=0;
        line=1;
    }
    //35页识别SNL单词的DFA表示实现
    public Token getNextWord() {
        this.state = State.START;
        if(this.source==null)
            return null;
        while(true)
            switch(this.state) {
                case START:
                    if(source.length()<=next+1) {
                        if(source.length()==next+1&&source.charAt(next)=='.') {
                            next++;
                            return new Token(WordType.DOT,".",line);
                        }
                        else
                            return null;
                    }
                    //过滤空格、换行，更新next和line号
                    while(source.charAt(next)==' '||source.charAt(next)=='\n'||source.charAt(next)=='\r') {
                        if(source.charAt(next)=='\n')
                            line++;
                        next++;
                        if(next>source.length()-1)
                            return null;
                    }
                    if(isLetter(source.charAt(next)))
                        this.state = State.INID;
                    else if(isNumber(source.charAt(next)))
                        this.state = State.INNUM;
                    else {
                        switch(source.charAt(next++)) {
                            case '+':
                                return new Token(WordType.ADD,"+",line);
                            case '-':
                                return new Token(WordType.SUB,"-",line);
                            case '*':
                                return new Token(WordType.MUL,"*",line);
                            case '/':
                                return new Token(WordType.DIV,"/",line);
                            case '<':
                                return new Token(WordType.LESS,"<",line);
                            case '=':
                                return new Token(WordType.EQUAL,"=",line);
                            case '(':
                                return new Token(WordType.LEFT_PARENT,"(",line);
                            case ')':
                                return new Token(WordType.RIGHT_PARENT,")",line);
                            case '[':
                                return new Token(WordType.LEFT_BRACKET,"[",line);
                            case ']':
                                return new Token(WordType.RIGHT_BRACKET,"]",line);
                            case ';':
                                return new Token(WordType.SEMICOLON,";",line);
                            case ',':
                                return new Token(WordType.COMMA,",",line);
                            case '\''://反斜杠是转义
                                this.state = State.INCHAR;
                                break;
                            case '.'://有两种情况，一种是一个点，一种是两个点，都是保留符号
                                if(source.charAt(next)!='.') {
                                    return new Token(WordType.DOT,".",line);
                                }
                                else {
                                    next++;
                                    return new Token(WordType.TWO_DOT,"..",line);
                                }
                            case ':':
                                this.state = State.INASSIGN;
                                break;
                            case '{':
                                this.state = State.INCOMMENT;
                                break;
                        }
                    }
                    break;
                case INASSIGN:
                    if(source.charAt(next++)=='=')
                        return new Token(WordType.COLON_EQUAL,":=",line);
                    else//TODO 书上对应出错
                        return new Token(WordType.SEMICOLON,":",line);
                    //TODO 没有切换状态
                case INCOMMENT:
                    //{ ... }之间的都是注释，过滤
                    while(source.charAt(next) != '}') {
                        if(source.charAt(next)=='\n')
                            line++;
                        next++;
                    }
                    this.state =State.START;
                    break;
                case INNUM:
                    StringBuilder num = new StringBuilder();
                    num.append(source.charAt(next++));
                    while(isNumber(source.charAt(next))) {
                        num.append(source.charAt(next++));
                    }
                    return new Token(WordType.UNSIGNEDNUMBER, num.toString(),line);
                case INID:
                    StringBuilder id = new StringBuilder();
                    id.append(source.charAt(next++));
                    while(next<source.length()&&(isLetter(source.charAt(next))||isNumber(source.charAt(next)))) {
                        id.append(source.charAt(next++));
                    }
                    Token tmp = getReservedToken(id.toString());
                    if(tmp!=null) {//如果是保留字，返回保留字Token
                        tmp.line=line;
                        return tmp;
                    }
                    //构造标识符的Token并返回
                    return new Token(WordType.IDENTIFIERS, id.toString(),line);
                case INCHAR:
                    if(isLetter(source.charAt(next))||isNumber(source.charAt(next)))
                        return new Token(WordType.CHAR,""+source.charAt(next++),line);
                case INRANGE://TODO 貌似不能进入这个状态，偷懒了。。。
                    next++;
                    this.state =State.START;
                    break;
                case DONE://TODO 貌似不能进入这个状态，偷懒了。。。
                    return null;
            }
    }

    static boolean isLetter(int ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }
    static boolean isNumber(int ch) {
        return ch >= '0' && ch <= '9';
    }
    //如果input是保留字，返回对应保留字的Token
    static Token getReservedToken(String input) {
        for (String reservedWord : reservedWords) {
            if (input.equals(reservedWord)) {
                return new Token(WordType.valueOf(input.toUpperCase()), input);
            }
        }
        return null;
    }
}
