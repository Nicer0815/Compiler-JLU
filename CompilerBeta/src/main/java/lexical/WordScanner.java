package lexical;

import lexical.Token.StateType;
import lexical.Token.WordType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WordScanner {
	private ArrayList<String> readList = null;
	private ArrayList<Token> tokenList = new ArrayList<Token>();
	/* Token序列中的token数目 */
	private int tokenNum = 0;

	private int line = 0;		// 行号,从0开始
	private int row = 0;		// 行中的下标,从0开始
	private int resLength = 0;	// 当前行的实际长度
	private char[] lineString = null;	// 当前行的字符串
	private boolean EOF_flag = false;	// 文件结束标志
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
			System.out.println("读取文件" + filename + "失败");
		}
		return readList;
	}

	public char getNextChar() {
		char ch = 0;
		if (row <= resLength - 1) {// 该行没有结束
			ch = lineString[row++];
			return ch;
		}
		// 下一行
		if (line <= readList.size() - 2) {// 文件没有结尾
			row = 0;
			line++;
			lineString = (readList.get(line) + "\n").toCharArray();
			resLength = lineString.length;
			ch = lineString[row++];
		} else {
			/* 已经到源代码文件末尾,设置EOF_flag标志为TRUE */
			EOF_flag = true;
			return (char) 3; /* 函数返回EOF */
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

		line = 0;// 行号,从0开始
		row = 0;// 行中的下标,从0开始
		resLength = 0;// 当前行的实际长度
		lineString = null;// 当前行的字符串
		EOF_flag = false;// 文件结束标志
		Error = false;

		tokenList.removeAll(tokenList);
		readList = (ArrayList<String>) readlist;
		for (String str : readList) {
			System.out.println(str);
		}
		// 在构造函数中初始化变量值：
		lineString = (readList.get(0) + "\n").toCharArray();
		resLength = lineString.length;

		Token currentToken = null;
		String tokenString = "";
		do {
			/* 当前状态标志state,始终都是以START作为开始 */
			StateType state = StateType.START;

			/* tokenString的存储标志save,整数类型 * 决定当前识别字符是否存入当前识别单词词元存储区tokenString */
			boolean save;
			tokenString = "";
			currentToken = new Token();
			/* 当前确定性有限自动机DFA状态state不是完成状态DONE */
			while (state != StateType.DONE) {
				/* 从源代码文件中获取下一个字符,送入变量c作为当前字符 */
				char c = getNextChar();
				/* 当前正识别字符的存储标志save初始为true */
				save = true;
				switch (state) {
				/* 当前DFA状态state为开始状态START,DFA处于当前单词开始位置 */
				case START:
					/*
					 * 当前字符c为数字,当前DFA状态state设置为数字状态 INNUM * 确定性有限自动机DFA处于数字类型单词中
					 */
					if (isDigit(c))
						state = StateType.INNUM;
					/*
					 * 当前字符c为字母,当前DFA状态state设置为标识符状态INID * 确定性有限自动机DFA处于标识符类型单词中
					 */
					else if (isChar(c))
						state = StateType.INID;
					/*
					 * 当前字符c为冒号,当前DFA状态state设置为赋值状态INASSIGN *
					 * 确定性有限自动机DFA处于赋值类型单词中
					 */
					else if (c == ':')
						state = StateType.INASSIGN;
					/* 当前字符c为.,当前DFA状态state设置为数组下标界限状态 */
					/* INRANGE，确定性有限自动机DFA处于数组下标界限类型单词中 */
					else if (c == '.')
						state = StateType.INRANGE;

					else if (c == '\'') {
						save = false;
						state = StateType.INCHAR;
					}
					/*
					 * 当前字符c为空白(空格,制表符,换行符),字符存储标志save设置为false *
					 * 当前字符为分隔符,不需要产生单词,无须存储
					 */
					else if ((c == ' ') || (c == '\t') || (c == '\n'))
						save = false;
					/*
					 * 当前字符c为左括号,字符存储标志save设置为false *
					 * 当前DFA状态state设置为注释状态INCOMMENT *
					 * 确定性有限自动机DFA处于注释中,不生成单词,无需存储
					 */
					else if (c == '{') {
						save = false;
						state = StateType.INCOMMENT;
					}
					/*
					 * 当前字符c为其它字符,当前DFA状态state设置为完成状态DONE *
					 * 确定性有限自动机DFA处于单词的结束位置,需进一步分类处理
					 */
					else {
						state = StateType.DONE;
						switch (c) {
						/*
						 * 当前字符c为EOF,字符存储标志save设置为false,无需存储 *
						 * 当前识别单词返回值currentToken设置为文件结束单词ENDFILE
						 */
						case (char) 3:
							save = false;
							currentToken.type = WordType.ENDFILE;
							break;

						/* 当前字符c为"=",当前识别单词返回值currentToken设置为等号单词EQ */
						case '=':
							currentToken.type = WordType.EQ;
							break;

						/* 当前字符c为"<",当前识别单词返回值currentToken设置为小于单词LT */
						case '<':
							currentToken.type = WordType.LT;
							break;

						/* 当前字符c为"+",当前识别单词返回值currentToken设置为加号单词PLUS */
						case '+':
							currentToken.type = WordType.PLUS;
							break;

						/* 当前字符c为"-",当前识别单词返回值currentToken设置为减号单词MINUS */
						case '-':
							currentToken.type = WordType.MINUS;
							break;

						/* 当前字符c为"*",当前识别单词返回值currentToken设置为乘号单词TIMES */
						case '*':
							currentToken.type = WordType.TIMES;
							break;

						/* 当前字符c为"/",当前识别单词返回值currentToken设置为除号单词OVER */
						case '/':
							currentToken.type = WordType.OVER;
							break;

						/* 当前字符c为"(",当前识别单词返回值currentToken设置为左括号单词LPAREN */
						case '(':
							currentToken.type = WordType.LPAREN;
							break;

						/* 当前字符c为")",当前识别单词返回值currentToken设置为右括号单词RPAREN */
						case ')':
							currentToken.type = WordType.RPAREN;
							break;

						/* 当前字符c为";",当前识别单词返回值currentToken设置为分号单词SEMI */
						case ';':
							currentToken.type = WordType.SEMI;
							break;
						/* 当前字符c为",",当前识别单词返回值currentToken设置为逗号单词COMMA */
						case ',':
							currentToken.type = WordType.COMMA;
							break;
						/* 当前字符c为"[",当前识别单词返回值currentToken设置为左中括号单词LMIDPAREN */
						case '[':
							currentToken.type = WordType.LMIDPAREN;
							break;

						/* 当前字符c为"]",当前识别单词返回值currentToken设置为右中括号单词RMIDPAREN */
						case ']':
							currentToken.type = WordType.RMIDPAREN;
							break;

						/* 当前字符c为其它字符,当前识别单词返回值currentToken设置为错误单词ERROR1 */
						default:
							currentToken.type = WordType.ERROR;
							Error = true;
							break;
						}
					}
					break;
				/********** 当前状态为开始状态START的处理结束 **********/

				/* 当前DFA状态state为注释状态INCOMMENT,确定性有限自动机DFA处于注释位置 */
				case INCOMMENT:
					/* 当前字符存储状态save设置为false,注释中内容不生成单词,无需存储 */
					save = false;
					/*
					 * 当前字符c为EOF,当前DFA状态state设置为完成状态DONE,当前单词识别结束 *
					 * 当前识别单词返回值currentToken设置为文件结束单词ENDFILE
					 */
					if (c == (char) 3) {
						state = StateType.DONE;
						currentToken.type = WordType.ENDFILE;
					}

					/* 当前字符c为"}",注释结束.当前DFA状态state设置为开始状态START */
					else if (c == '}')
						state = StateType.START;
					break;

				/* 当前DFA状态state为赋值状态INASSIGN,确定性有限自动机DFA处于赋值单词位置 */
				case INASSIGN:
					/* 当前DFA状态state设置为完成状态DONE,赋值单词结束 */
					state = StateType.DONE;
					/* 当前字符c为"=",当前识别单词返回值currentToken设置为赋值单词ASSIGN */
					if (c == '=')
						currentToken.type = WordType.ASSIGN;
					/*
					 * 当前字符c为其它字符,即":"后不是"=",在输入行缓冲区中回退一个字符 *
					 * 字符存储状态save设置为false,当前识别单词返回值currentToken设置为ERROR1
					 */
					else {
						ungetNextChar();
						save = false;
						currentToken.type = WordType.COLON;
					}
					break;

				case INRANGE:
					/* 当前DFA状态state设置为完成状态DONE,赋值单词结束 */
					state = StateType.DONE;
					/* 当前字符c为".",当前识别单词返回值currentToken设置为下标界UNDERANGE */
					if (c == '.')
						currentToken.type = WordType.UNDERANGE;
					/*
					 * 当前字符c为其它字符,即"."后不是".",在输入行缓冲区中回退一个字符 *
					 * 字符存储状态save设置为false,当前识别单词返回值currentToken设置为ERROR1
					 */
					else {
						ungetNextChar();
						save = false;
						currentToken.type = WordType.DOT;
					}
					break;
				/* 当前DFA状态state为数字状态INNUM,确定性有限自动机处于数字单词位置 */
				case INNUM:
					/*
					 * 当前字符c不是数字,则在输入行缓冲区源中回退一个字符 *
					 * 字符存储标志设置为false,当前DFA状态state设置为DONE,数字单词识别完成 *
					 * 当前识别单词返回值currentToken设置为数字单词NUM
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
				/* 当前DFA状态state为标识符状态INID,确定性有限自动机DFA处于标识符单词位置 */
				case INID:
					/*
					 * 当前字符c不是字母,则在输入行缓冲区源中回退一个字符 *
					 * 字符存储标志设置为false,当前DFA状态state设置为DONE,标识符单词识别完成 *
					 * 当前识别单词返回值currentToken设置为标识符单词ID
					 */
					if (!isNumChar(c)) {
						ungetNextChar();
						save = false;
						state = StateType.DONE;
						currentToken.type = WordType.ID;
					}
					break;

				/* 当前DFA状态state为完成状态DONE,确定性有限自动机DFA处于单词结束位置 */
				case DONE:
					break;

				/* 当前DFA状态state为其它状态,此种情况不应发生 */
				default:
					/*
					 * 将词法扫描器产生错误的状态state写入列表文件listing * 当前DFA状态state设置为完成状态DONE
					 * * 当前识别单词返回值currentToken设置为错误单词ERROR1
					 */
					state = StateType.DONE;
					currentToken.type = WordType.ERROR;
					Error = true;
					break;
				}

				/*
				 * 当前字符存储状态save为true,且当前正识别单词已经识别部分未超过单词最大长度 *
				 * 将当前字符c写入当前正识别单词词元存储区tokenString
				 */
				if (save)
					tokenString += c;

				if (state == StateType.DONE) {
					/*
					 * 当前DFA状态state为完成状态DONE,单词识别完成 *
					 * 当前识别单词词元存储区tokenString加上结束标志
					 */
					/* 当前单词currentToken为标识符单词类型,查看其是否为保留字单词 */
					if (currentToken.type == WordType.ID) {
						currentToken.type = reservedLookup(tokenString);
					}
				}

			}
			/* 将行号信息存入Token */
			currentToken.line = line + 1;
			currentToken.context = tokenString;
			/* 将单词的语义信息存入Token */
			tokenList.add(currentToken);
			tokenNum++; /* Token总数目加1 */

		}
		/* 直到处理完表示文件结束的Token:ENDFILE，说明处理完所有的Token */
		while ((currentToken.type) != WordType.ENDFILE);

		/* 词法分析函数结束 */

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
				writer.println("源文件有错误");
			else {
				writer.println("源文件正确");
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
			System.out.println("向文件" + tofile + "中写入token 失败");
		}
	}

	public void printToken(String tofile) {
		try {
			File file = new File(tofile);
			FileOutputStream fous = new FileOutputStream(file);
			OutputStreamWriter fos = new OutputStreamWriter(fous);
			PrintWriter writer = new PrintWriter(fos);
			if (Error) {
				writer.println("****************源文件有错误****************");
			} else {
				writer.println("****************源文件正确！没有错误****************");
			}
			/* 对函数参数token给定单词进行分类处理 */
			for (Token token : tokenList) {
				writer.print(token.getLine() + ":  ");
				switch (token.type) {
				/* 单词token为保留字,将保留字词元以指定格式写入列表文件listing */
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
					/* 后来添加的 */
				case INTEGER:
				case CHAR:
					writer.println(token.getContext());
					break;

				/* 特殊符号 */

				/* 单词token为特殊符号：ASSIGN (赋值)，将":="写入文件listing */
				case ASSIGN:
					writer.println(":=");
					break;

				/* 单词token为特殊符号：LT (小于)，将"<"写入文件listing */
				case LT:
					writer.println("<");
					break;

				/* 单词token为特殊符号：EQ (等于)，将"="写入文件listing */
				case EQ:
					writer.println("=");
					break;

				/* 单词token为特殊符号：PLUS (加号)，将"+"写入文件listing */
				case PLUS:
					writer.println("+");
					break;

				/* 单词token为特殊符号；MINUS (减号)，将"-"写入文件listing */
				case MINUS:
					writer.println("-");
					break;

				/* 单词token为特殊符号：TIMES (乘号)，将"*"写入文件listing */
				case TIMES:
					writer.println("*");
					break;

				/* 单词token为特殊符号：OVER (除号)，将"/"写入文件listing */
				case OVER:
					writer.println("/");
					break;

				/* 单词token为特殊符号：LPAREN (左括号)，将"("写入文件listing */
				case LPAREN:
					writer.println("(");
					break;

				/* 单词token为特殊符号：RPAREN (右括号)，将")"写入文件listing */
				case RPAREN:
					writer.println(")");
					break;

				/* 单词token为特殊符号：LMIDPAREN (左中括号)，将"["写入文件listing */
				case LMIDPAREN:
					writer.println("[");
					break;

				/* 单词token为特殊符号：RMIDPAREN (右中括号)，将"]"写入文件listing */
				case RMIDPAREN:
					writer.println("]");
					break;

				/* 单词token为特殊符号：SEMI (分号)，将";"写入文件listing */
				case SEMI:
					writer.println(";");
					break;

				/* 单词token为特殊符号：COLON (冒号)，将":"写入文件listing */
				case COLON:
					writer.println(":");
					break;

				/* 单词token为特殊符号：COMMA (逗号)，将","写入文件listing */
				case COMMA:
					writer.println(",");
					break;

				/* 单词token为特殊符号：DOT (程序结束符)，将"."写入文件listing */
				case DOT:
					writer.println(".");
					break;

				/* 单词token为特殊符号：UNDERANGE (下标界限)，将".."写入文件listing */
				case UNDERANGE:
					writer.println(";");
					break;

				/* 单词token为簿记单词符号：ENDFILE (文件结尾)，将EOF写入文件listing */
				case ENDFILE:
					writer.println("EOF");
					break;

				/* 单词token为多字符单词符号：INTC (数字)，将数值写入文件listing */
				case INTC:
					writer.println("INTC, val= " + token.getContext());
					break;

				/* 单词token为多字符单词符号：CHARC (字符)，将字符写入文件listing */
				case CHARC:
					writer.println("CHARC, letter=" + token.getContext());
					break;

				/* 单词token为多字符单词符号：ID (标识符)，将标识符名写入文件listing */
				case ID:
					writer.println("ID, name= " + token.getContext());

					break;

				/* 单词token为簿记单词符号：ERROR (错误)，将错误信息写入文件listing */
				case ERROR:
					writer.println("ERROR:" + token.getContext());
					break;

				/* 单词token为其他未知单词，未知信息写入文件listing,此种情况不应发生 */
				default:

					writer.println("Unknown token: " + token.getContext());

				}
			}
			writer.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("向文件" + tofile + "中写入token 失败");
		}

	}
}
