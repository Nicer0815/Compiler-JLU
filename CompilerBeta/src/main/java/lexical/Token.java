package lexical;


public class Token {

	public int line;// 行号

	public String context;// 字符

	public WordType type;// 字符类型

	public enum StateType {// 状态表
		START, INASSIGN, INRANGE, INCOMMENT, INNUM, INID, INCHAR, DONE
	}

	public enum WordType {// 单词类型
		ENDFILE, ERROR,
		/* 保留字 */
		PROGRAM, PROCEDURE, TYPE, VAR, IF, THEN, ELSE, FI, WHILE, DO, ENDWH, BEGIN, END, READ, WRITE, ARRAY, OF, RECORD, RETURN,

		INTEGER, CHAR,
		/* 多字符单词符号 */
		ID, INTC, CHARC,
		/* 特殊符号 */
		ASSIGN, EQ, LT, PLUS, MINUS, TIMES, OVER, LPAREN, RPAREN, DOT, COLON, SEMI, COMMA, LMIDPAREN, RMIDPAREN, UNDERANGE
	}

	
	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public WordType getType() {
		return type;
	}

	public void setType(WordType ctype) {
		this.type = ctype;
	}

	@Override
	public String toString() {
		return "Token{" +
				"line=" + line +
				", context='" + context + '\'' +
				", type=" + type +
				'}';
	}
}
