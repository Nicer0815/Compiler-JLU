package lexical;


public class Token {

	public int line;// �к�

	public String context;// �ַ�

	public WordType type;// �ַ�����

	public enum StateType {// ״̬��
		START, INASSIGN, INRANGE, INCOMMENT, INNUM, INID, INCHAR, DONE
	}

	public enum WordType {// ��������
		ENDFILE, ERROR,
		/* ������ */
		PROGRAM, PROCEDURE, TYPE, VAR, IF, THEN, ELSE, FI, WHILE, DO, ENDWH, BEGIN, END, READ, WRITE, ARRAY, OF, RECORD, RETURN,

		INTEGER, CHAR,
		/* ���ַ����ʷ��� */
		ID, INTC, CHARC,
		/* ������� */
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
