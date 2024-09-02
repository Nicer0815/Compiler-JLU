package semantic;

import lexical.WordScanner;
import org.junit.Test;
import parsing.ParsingAnalysis;
import parsing.TreeNode;

import java.util.List;

public class test {
    @Test
    public void test01() {
        String filename = "D:SNLTest//test01.txt";
        WordScanner wordScanner = new WordScanner();
        wordScanner.getTokenList(wordScanner.readFile(filename));
        ParsingAnalysis parsingAnalysis = new ParsingAnalysis();
        parsingAnalysis.setTokenList(wordScanner.getTokenList());
        TreeNode head = parsingAnalysis.parse();
        parsingAnalysis.printTree(head);
        Semantic semantic = new Semantic();
        semantic.analyze(head);
        semantic.PrintSymbTable();
        List<String> strList = semantic.getList();
        for (String str : strList) {
            System.out.println(str);
        }
        System.out.println("inTester:"+semantic.getTempStr());
    }
}
