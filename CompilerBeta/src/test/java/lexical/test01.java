package lexical;

import org.junit.Test;

import java.util.List;

public class test01 {
    @Test
    public void test() {
        WordScanner wordScanner = new WordScanner();
        String filename = "D:SNLTest//test01.txt";
        wordScanner.readFile(filename);

        if (wordScanner.getError())
            System.out.println("源文件有错误");
        List<Token> list = wordScanner.getTokenList(wordScanner.getReadList());
        //wordScanner.printToken("./tokenlist.txt");
        for (Token token : list) {
            System.out.println(token);
        }
        System.out.println("词法分析结束");
    }
}
