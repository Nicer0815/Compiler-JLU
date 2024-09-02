package lexical;

import java.util.List;
import java.util.Vector;

public class LexicalAnalysis { //词法分析
    public List<Token> getToken(String source) {
        WordScanner ws = new WordScanner(source);
        Vector<Token> tokenList = new Vector<>();
        Token token = ws.getNextWord();
        while(token != null) {
            tokenList.add(token);
            token=ws.getNextWord();
        }
        tokenList.add(new Token(WordType.EOF,"End of File",ws.line));
        return tokenList;
    }

    public static void main(String[] args) {
        String testProgram = "program p\n" +
                "type t1 = integer;\n" +
                "var integer v1,v2;\n" +
                "procedure\n" +
                "q(integer i);\n" +
                "var integer a;\n" +
                "begin\n" +
                "a:=i;\n" +
                "write(a)\n" +
                "end\n" +
                "begin\n" +
                "read(v1);\n" +
                "if v1<10\n" +
                "then v1:=v1+10\n" +
                "else v1:=v1-10\n" +
                "fi;\n" +
                "q(v1)\n" +
                "end.";
        /*
        第二种测试方法
        Scanner scanner = new Scanner(System.in);
        testProgram = scanner.next();
        */
        LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
        List<Token> list = lexicalAnalysis.getToken(testProgram);
        for (Token token : list) {
            System.out.println(token);
        }
    }
}
