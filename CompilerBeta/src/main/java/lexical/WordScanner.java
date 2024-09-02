package lexical;

import lexical.Token.StateType;
import lexical.Token.WordType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WordScanner {
	private ArrayList<String> readList = null;
	private ArrayList<Token> tokenList = new ArrayList<Token>();
	/* Token�����е�token��Ŀ */
	private int tokenNum = 0;

	private int line = 0;		// �к�,��0��ʼ
	private int row = 0;		// ���е��±�,��0��ʼ
	private int resLength = 0;	// ��ǰ�е�ʵ�ʳ���
	private char[] lineString = null;	// ��ǰ�е��ַ���
	private boolean EOF_flag = false;	// �ļ�������־
	private boolean Error = false;
	public static String[] reservedWords = {
			"program", "type", "var", "procedure", "begin", "end",
			"array", "of", "record", "if", "then", "else", "fi",
			"while", "do", "endwh", "read", "write","return",
			"integer", "char"
	};

	public int getTokenNum() {
		return tokenNum;
	}

	public void setTokenNum(int tokenNum) {
		this.tokenNum = tokenNum;
	}

	public boolean getError() {
		return Error;
	}

	public ArrayList<Token> getTokenList() {
		return tokenList;
	}

	public void setTokenList(ArrayList<Token> tokens) {
		this.tokenList = tokens;
	}

	public ArrayList<String> getReadList() {
		return readList;
	}

	public void setReadList(ArrayList<String> realist) {
		this.readList = realist;
	}

	public List<String> readFile(String filename) {
		readList = new ArrayList<String>();
		try {
			File file = new File(filename);
			FileInputStream fin = new FileInputStream(file);
			InputStreamReader fis = new InputStreamReader(fin);
			BufferedReader buffered = new BufferedReader(fis);
			String line = null;
			while ((line = buffered.readLine()) != null) {
				readList.add(line);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("��ȡ�ļ�" + filename + "ʧ��");
		}
		return readList;
	}

	public char getNextChar() {
		char ch = 0;
		if (row <= resLength - 1) {// ����û�н���
			ch = lineString[row++];
			return ch;
		}
		// ��һ��
		if (line <= readList.size() - 2) {// �ļ�û�н�β
			row = 0;
			line++;
			lineString = (readList.get(line) + "\n").toCharArray();
			resLength = lineString.length;
			ch = lineString[row++];
		} else {
			/* �Ѿ���Դ�����ļ�ĩβ,����EOF_flag��־ΪTRUE */
			EOF_flag = true;
			return (char) 3; /* ��������EOF */
		}

		return ch;
	}

	public boolean isDigit(char ch) {
		if (ch > '9' || ch < '0')
			return false;
		else
			return true;
	}

	public boolean isChar(char ch) {
		if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
			return true;
		else
			return false;
	}

	public void ungetNextChar() {
		if (!EOF_flag)
			row--;
	}

	public WordType reservedLookup(String s) {
		for (String reservedWord : reservedWords) {
			if (s.equals(reservedWord)) {
				return WordType.valueOf(reservedWord.toUpperCase());
			}
		}
		return WordType.ID;
	}

	public boolean isNumChar(char ch) {
		if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z')
				|| (ch >= 'A' && ch <= 'Z'))
			return true;
		return false;
	}

	public List<Token> getTokenList(List<String> readlist) {

		line = 0;// �к�,��0��ʼ
		row = 0;// ���е��±�,��0��ʼ
		resLength = 0;// ��ǰ�е�ʵ�ʳ���
		lineString = null;// ��ǰ�е��ַ���
		EOF_flag = false;// �ļ�������־
		Error = false;

		tokenList.removeAll(tokenList);
		readList = (ArrayList<String>) readlist;
		for (String str : readList) {
			System.out.println(str);
		}
		// �ڹ��캯���г�ʼ������ֵ��
		lineString = (readList.get(0) + "\n").toCharArray();
		resLength = lineString.length;

		Token currentToken = null;
		String tokenString = "";
		do {
			/* ��ǰ״̬��־state,ʼ�ն�����START��Ϊ��ʼ */
			StateType state = StateType.START;

			/* tokenString�Ĵ洢��־save,�������� * ������ǰʶ���ַ��Ƿ���뵱ǰʶ�𵥴ʴ�Ԫ�洢��tokenString */
			boolean save;
			tokenString = "";
			currentToken = new Token();
			/* ��ǰȷ���������Զ���DFA״̬state�������״̬DONE */
			while (state != StateType.DONE) {
				/* ��Դ�����ļ��л�ȡ��һ���ַ�,�������c��Ϊ��ǰ�ַ� */
				char c = getNextChar();
				/* ��ǰ��ʶ���ַ��Ĵ洢��־save��ʼΪtrue */
				save = true;
				switch (state) {
				/* ��ǰDFA״̬stateΪ��ʼ״̬START,DFA���ڵ�ǰ���ʿ�ʼλ�� */
				case START:
					/*
					 * ��ǰ�ַ�cΪ����,��ǰDFA״̬state����Ϊ����״̬ INNUM * ȷ���������Զ���DFA�����������͵�����
					 */
					if (isDigit(c))
						state = StateType.INNUM;
					/*
					 * ��ǰ�ַ�cΪ��ĸ,��ǰDFA״̬state����Ϊ��ʶ��״̬INID * ȷ���������Զ���DFA���ڱ�ʶ�����͵�����
					 */
					else if (isChar(c))
						state = StateType.INID;
					/*
					 * ��ǰ�ַ�cΪð��,��ǰDFA״̬state����Ϊ��ֵ״̬INASSIGN *
					 * ȷ���������Զ���DFA���ڸ�ֵ���͵�����
					 */
					else if (c == ':')
						state = StateType.INASSIGN;
					/* ��ǰ�ַ�cΪ.,��ǰDFA״̬state����Ϊ�����±����״̬ */
					/* INRANGE��ȷ���������Զ���DFA���������±�������͵����� */
					else if (c == '.')
						state = StateType.INRANGE;

					else if (c == '\'') {
						save = false;
						state = StateType.INCHAR;
					}
					/*
					 * ��ǰ�ַ�cΪ�հ�(�ո�,�Ʊ��,���з�),�ַ��洢��־save����Ϊfalse *
					 * ��ǰ�ַ�Ϊ�ָ���,����Ҫ��������,����洢
					 */
					else if ((c == ' ') || (c == '\t') || (c == '\n'))
						save = false;
					/*
					 * ��ǰ�ַ�cΪ������,�ַ��洢��־save����Ϊfalse *
					 * ��ǰDFA״̬state����Ϊע��״̬INCOMMENT *
					 * ȷ���������Զ���DFA����ע����,�����ɵ���,����洢
					 */
					else if (c == '{') {
						save = false;
						state = StateType.INCOMMENT;
					}
					/*
					 * ��ǰ�ַ�cΪ�����ַ�,��ǰDFA״̬state����Ϊ���״̬DONE *
					 * ȷ���������Զ���DFA���ڵ��ʵĽ���λ��,���һ�����ദ��
					 */
					else {
						state = StateType.DONE;
						switch (c) {
						/*
						 * ��ǰ�ַ�cΪEOF,�ַ��洢��־save����Ϊfalse,����洢 *
						 * ��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�ļ���������ENDFILE
						 */
						case (char) 3:
							save = false;
							currentToken.type = WordType.ENDFILE;
							break;

						/* ��ǰ�ַ�cΪ"=",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�Ⱥŵ���EQ */
						case '=':
							currentToken.type = WordType.EQ;
							break;

						/* ��ǰ�ַ�cΪ"<",��ǰʶ�𵥴ʷ���ֵcurrentToken����ΪС�ڵ���LT */
						case '<':
							currentToken.type = WordType.LT;
							break;

						/* ��ǰ�ַ�cΪ"+",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�Ӻŵ���PLUS */
						case '+':
							currentToken.type = WordType.PLUS;
							break;

						/* ��ǰ�ַ�cΪ"-",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���ŵ���MINUS */
						case '-':
							currentToken.type = WordType.MINUS;
							break;

						/* ��ǰ�ַ�cΪ"*",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�˺ŵ���TIMES */
						case '*':
							currentToken.type = WordType.TIMES;
							break;

						/* ��ǰ�ַ�cΪ"/",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���ŵ���OVER */
						case '/':
							currentToken.type = WordType.OVER;
							break;

						/* ��ǰ�ַ�cΪ"(",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�����ŵ���LPAREN */
						case '(':
							currentToken.type = WordType.LPAREN;
							break;

						/* ��ǰ�ַ�cΪ")",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�����ŵ���RPAREN */
						case ')':
							currentToken.type = WordType.RPAREN;
							break;

						/* ��ǰ�ַ�cΪ";",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�ֺŵ���SEMI */
						case ';':
							currentToken.type = WordType.SEMI;
							break;
						/* ��ǰ�ַ�cΪ",",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���ŵ���COMMA */
						case ',':
							currentToken.type = WordType.COMMA;
							break;
						/* ��ǰ�ַ�cΪ"[",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�������ŵ���LMIDPAREN */
						case '[':
							currentToken.type = WordType.LMIDPAREN;
							break;

						/* ��ǰ�ַ�cΪ"]",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�������ŵ���RMIDPAREN */
						case ']':
							currentToken.type = WordType.RMIDPAREN;
							break;

						/* ��ǰ�ַ�cΪ�����ַ�,��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���󵥴�ERROR1 */
						default:
							currentToken.type = WordType.ERROR;
							Error = true;
							break;
						}
					}
					break;
				/********** ��ǰ״̬Ϊ��ʼ״̬START�Ĵ������ **********/

				/* ��ǰDFA״̬stateΪע��״̬INCOMMENT,ȷ���������Զ���DFA����ע��λ�� */
				case INCOMMENT:
					/* ��ǰ�ַ��洢״̬save����Ϊfalse,ע�������ݲ����ɵ���,����洢 */
					save = false;
					/*
					 * ��ǰ�ַ�cΪEOF,��ǰDFA״̬state����Ϊ���״̬DONE,��ǰ����ʶ����� *
					 * ��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�ļ���������ENDFILE
					 */
					if (c == (char) 3) {
						state = StateType.DONE;
						currentToken.type = WordType.ENDFILE;
					}

					/* ��ǰ�ַ�cΪ"}",ע�ͽ���.��ǰDFA״̬state����Ϊ��ʼ״̬START */
					else if (c == '}')
						state = StateType.START;
					break;

				/* ��ǰDFA״̬stateΪ��ֵ״̬INASSIGN,ȷ���������Զ���DFA���ڸ�ֵ����λ�� */
				case INASSIGN:
					/* ��ǰDFA״̬state����Ϊ���״̬DONE,��ֵ���ʽ��� */
					state = StateType.DONE;
					/* ��ǰ�ַ�cΪ"=",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ��ֵ����ASSIGN */
					if (c == '=')
						currentToken.type = WordType.ASSIGN;
					/*
					 * ��ǰ�ַ�cΪ�����ַ�,��":"����"=",�������л������л���һ���ַ� *
					 * �ַ��洢״̬save����Ϊfalse,��ǰʶ�𵥴ʷ���ֵcurrentToken����ΪERROR1
					 */
					else {
						ungetNextChar();
						save = false;
						currentToken.type = WordType.COLON;
					}
					break;

				case INRANGE:
					/* ��ǰDFA״̬state����Ϊ���״̬DONE,��ֵ���ʽ��� */
					state = StateType.DONE;
					/* ��ǰ�ַ�cΪ".",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�±��UNDERANGE */
					if (c == '.')
						currentToken.type = WordType.UNDERANGE;
					/*
					 * ��ǰ�ַ�cΪ�����ַ�,��"."����".",�������л������л���һ���ַ� *
					 * �ַ��洢״̬save����Ϊfalse,��ǰʶ�𵥴ʷ���ֵcurrentToken����ΪERROR1
					 */
					else {
						ungetNextChar();
						save = false;
						currentToken.type = WordType.DOT;
					}
					break;
				/* ��ǰDFA״̬stateΪ����״̬INNUM,ȷ���������Զ����������ֵ���λ�� */
				case INNUM:
					/*
					 * ��ǰ�ַ�c��������,���������л�����Դ�л���һ���ַ� *
					 * �ַ��洢��־����Ϊfalse,��ǰDFA״̬state����ΪDONE,���ֵ���ʶ����� *
					 * ��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���ֵ���NUM
					 */
					if (!isDigit(c)) {
						ungetNextChar();
						save = false;
						state = StateType.DONE;
						currentToken.type = WordType.INTC;
					}
					break;
				case INCHAR:
					if (isNumChar(c)) {
						int c1 = getNextChar();
						if (c1 == '\'') {
							save = true;
							state = StateType.DONE;
							currentToken.type = WordType.CHARC;
						} else {
							ungetNextChar();
							ungetNextChar();
							// save = true;
							state = StateType.DONE;
							currentToken.type = WordType.ERROR;
							Error = true;
						}
					} else {
						ungetNextChar();
						// save = true;
						state = StateType.DONE;
						currentToken.type = WordType.ERROR;
						Error = true;
					}
					break;
				/* ��ǰDFA״̬stateΪ��ʶ��״̬INID,ȷ���������Զ���DFA���ڱ�ʶ������λ�� */
				case INID:
					/*
					 * ��ǰ�ַ�c������ĸ,���������л�����Դ�л���һ���ַ� *
					 * �ַ��洢��־����Ϊfalse,��ǰDFA״̬state����ΪDONE,��ʶ������ʶ����� *
					 * ��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ��ʶ������ID
					 */
					if (!isNumChar(c)) {
						ungetNextChar();
						save = false;
						state = StateType.DONE;
						currentToken.type = WordType.ID;
					}
					break;

				/* ��ǰDFA״̬stateΪ���״̬DONE,ȷ���������Զ���DFA���ڵ��ʽ���λ�� */
				case DONE:
					break;

				/* ��ǰDFA״̬stateΪ����״̬,���������Ӧ���� */
				default:
					/*
					 * ���ʷ�ɨ�������������״̬stateд���б��ļ�listing * ��ǰDFA״̬state����Ϊ���״̬DONE
					 * * ��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���󵥴�ERROR1
					 */
					state = StateType.DONE;
					currentToken.type = WordType.ERROR;
					Error = true;
					break;
				}

				/*
				 * ��ǰ�ַ��洢״̬saveΪtrue,�ҵ�ǰ��ʶ�𵥴��Ѿ�ʶ�𲿷�δ����������󳤶� *
				 * ����ǰ�ַ�cд�뵱ǰ��ʶ�𵥴ʴ�Ԫ�洢��tokenString
				 */
				if (save)
					tokenString += c;

				if (state == StateType.DONE) {
					/*
					 * ��ǰDFA״̬stateΪ���״̬DONE,����ʶ����� *
					 * ��ǰʶ�𵥴ʴ�Ԫ�洢��tokenString���Ͻ�����־
					 */
					/* ��ǰ����currentTokenΪ��ʶ����������,�鿴���Ƿ�Ϊ�����ֵ��� */
					if (currentToken.type == WordType.ID) {
						currentToken.type = reservedLookup(tokenString);
					}
				}

			}
			/* ���к���Ϣ����Token */
			currentToken.line = line + 1;
			currentToken.context = tokenString;
			/* �����ʵ�������Ϣ����Token */
			tokenList.add(currentToken);
			tokenNum++; /* Token����Ŀ��1 */

		}
		/* ֱ���������ʾ�ļ�������Token:ENDFILE��˵�����������е�Token */
		while ((currentToken.type) != WordType.ENDFILE);

		/* �ʷ������������� */

		for (Token tt : tokenList) {
			System.out.println("  " + tt.getLine() + " \t" + tt.getContext()
					+ "\t" + tt.getType());
		}
		return tokenList;
	}

	public void chainTofile(String tofile) {
		try {
			File file = new File(tofile);
			FileOutputStream fous = new FileOutputStream(file);
			OutputStreamWriter fos = new OutputStreamWriter(fous);
			PrintWriter writer = new PrintWriter(fos);
			if (Error)
				writer.println("Դ�ļ��д���");
			else {
				writer.println("Դ�ļ���ȷ");
			}
			for (Token tt : tokenList) {
				writer.print(tt.getLine() + "\t\r ");
				writer.print(tt.getType() + "\t\r ");
				writer.println(tt.getContext());
				writer.flush(); // ///////////
			}
			writer.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("���ļ�" + tofile + "��д��token ʧ��");
		}
	}

	public void printToken(String tofile) {
		try {
			File file = new File(tofile);
			FileOutputStream fous = new FileOutputStream(file);
			OutputStreamWriter fos = new OutputStreamWriter(fous);
			PrintWriter writer = new PrintWriter(fos);
			if (Error) {
				writer.println("****************Դ�ļ��д���****************");
			} else {
				writer.println("****************Դ�ļ���ȷ��û�д���****************");
			}
			/* �Ժ�������token�������ʽ��з��ദ�� */
			for (Token token : tokenList) {
				writer.print(token.getLine() + ":  ");
				switch (token.type) {
				/* ����tokenΪ������,�������ִ�Ԫ��ָ����ʽд���б��ļ�listing */
				case PROGRAM:
				case PROCEDURE:
				case TYPE:
				case VAR:
				case IF:
				case THEN:
				case ELSE:
				case FI:
				case WHILE:
				case DO:
				case ENDWH:
				case BEGIN:
				case END:
				case READ:
				case WRITE:
				case ARRAY:
				case OF:
				case RECORD:
				case RETURN:
					/* ������ӵ� */
				case INTEGER:
				case CHAR:
					writer.println(token.getContext());
					break;

				/* ������� */

				/* ����tokenΪ������ţ�ASSIGN (��ֵ)����":="д���ļ�listing */
				case ASSIGN:
					writer.println(":=");
					break;

				/* ����tokenΪ������ţ�LT (С��)����"<"д���ļ�listing */
				case LT:
					writer.println("<");
					break;

				/* ����tokenΪ������ţ�EQ (����)����"="д���ļ�listing */
				case EQ:
					writer.println("=");
					break;

				/* ����tokenΪ������ţ�PLUS (�Ӻ�)����"+"д���ļ�listing */
				case PLUS:
					writer.println("+");
					break;

				/* ����tokenΪ������ţ�MINUS (����)����"-"д���ļ�listing */
				case MINUS:
					writer.println("-");
					break;

				/* ����tokenΪ������ţ�TIMES (�˺�)����"*"д���ļ�listing */
				case TIMES:
					writer.println("*");
					break;

				/* ����tokenΪ������ţ�OVER (����)����"/"д���ļ�listing */
				case OVER:
					writer.println("/");
					break;

				/* ����tokenΪ������ţ�LPAREN (������)����"("д���ļ�listing */
				case LPAREN:
					writer.println("(");
					break;

				/* ����tokenΪ������ţ�RPAREN (������)����")"д���ļ�listing */
				case RPAREN:
					writer.println(")");
					break;

				/* ����tokenΪ������ţ�LMIDPAREN (��������)����"["д���ļ�listing */
				case LMIDPAREN:
					writer.println("[");
					break;

				/* ����tokenΪ������ţ�RMIDPAREN (��������)����"]"д���ļ�listing */
				case RMIDPAREN:
					writer.println("]");
					break;

				/* ����tokenΪ������ţ�SEMI (�ֺ�)����";"д���ļ�listing */
				case SEMI:
					writer.println(";");
					break;

				/* ����tokenΪ������ţ�COLON (ð��)����":"д���ļ�listing */
				case COLON:
					writer.println(":");
					break;

				/* ����tokenΪ������ţ�COMMA (����)����","д���ļ�listing */
				case COMMA:
					writer.println(",");
					break;

				/* ����tokenΪ������ţ�DOT (���������)����"."д���ļ�listing */
				case DOT:
					writer.println(".");
					break;

				/* ����tokenΪ������ţ�UNDERANGE (�±����)����".."д���ļ�listing */
				case UNDERANGE:
					writer.println(";");
					break;

				/* ����tokenΪ���ǵ��ʷ��ţ�ENDFILE (�ļ���β)����EOFд���ļ�listing */
				case ENDFILE:
					writer.println("EOF");
					break;

				/* ����tokenΪ���ַ����ʷ��ţ�INTC (����)������ֵд���ļ�listing */
				case INTC:
					writer.println("INTC, val= " + token.getContext());
					break;

				/* ����tokenΪ���ַ����ʷ��ţ�CHARC (�ַ�)�����ַ�д���ļ�listing */
				case CHARC:
					writer.println("CHARC, letter=" + token.getContext());
					break;

				/* ����tokenΪ���ַ����ʷ��ţ�ID (��ʶ��)������ʶ����д���ļ�listing */
				case ID:
					writer.println("ID, name= " + token.getContext());

					break;

				/* ����tokenΪ���ǵ��ʷ��ţ�ERROR (����)����������Ϣд���ļ�listing */
				case ERROR:
					writer.println("ERROR:" + token.getContext());
					break;

				/* ����tokenΪ����δ֪���ʣ�δ֪��Ϣд���ļ�listing,���������Ӧ���� */
				default:

					writer.println("Unknown token: " + token.getContext());

				}
			}
			writer.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("���ļ�" + tofile + "��д��token ʧ��");
		}

	}
}
