package lexical;

import java.util.List;

public class LexicalAnalysis {

	public static void main(String[] args) {
		WordScanner wordScanner = new WordScanner();
		String filename = "D:SNLTest//test01.txt";
		wordScanner.readFile(filename);
		if (wordScanner.getError())
			System.out.println("Դ�ļ��д���");
		List<Token> list = wordScanner.getTokenList(wordScanner.getReadList());
		for (Token token : list) {
			System.out.println(token);
		}
		System.out.println("�ʷ���������");
	}
}
