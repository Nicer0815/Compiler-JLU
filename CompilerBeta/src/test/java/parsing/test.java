package parsing;

import lexical.WordScanner;
import org.junit.Test;

public class test {
    @Test
    public void test01() {
        ParsingAnalysis parsingAnalysis = new ParsingAnalysis();
        System.out.println("start parse");
        String filename = "D:SNLTest//test01.txt";
        WordScanner wordScanner = new WordScanner();
        wordScanner.getTokenList(wordScanner.readFile(filename));
        parsingAnalysis.setTokenList(wordScanner.getTokenList());
        TreeNode head = parsingAnalysis.parse();
        System.out.println("end parse");
        parsingAnalysis.printTree(head);
        for (String str : parsingAnalysis.getParsingList()) {
            System.out.println("  " + str);
        }
        System.out.println(parsingAnalysis.getErrorStr());
    }

    @Test
    public void test02() {
        ParsingAnalysis parsingAnalysis = new ParsingAnalysis();
        System.out.println("start parse");
        String filename = "D:SNLTest//test02.txt";
        WordScanner wordScanner = new WordScanner();
        wordScanner.getTokenList(wordScanner.readFile(filename));
        parsingAnalysis.setTokenList(wordScanner.getTokenList());
        TreeNode head = parsingAnalysis.parse();
        System.out.println("end parse");
        parsingAnalysis.printTree(head);
        for (String str : parsingAnalysis.getParsingList()) {
            System.out.println("  " + str);
        }
        System.out.println(parsingAnalysis.getErrorStr());
    }

    @Test
    public void test03() {
        ParsingAnalysis parsingAnalysis = new ParsingAnalysis();
        System.out.println("start parse");
        String filename = "D:SNLTest//test03.txt";
        WordScanner wordScanner = new WordScanner();
        wordScanner.getTokenList(wordScanner.readFile(filename));
        parsingAnalysis.setTokenList(wordScanner.getTokenList());
        TreeNode head = parsingAnalysis.parse();
        System.out.println("end parse");
        parsingAnalysis.printTree(head);
        for (String str : parsingAnalysis.getParsingList()) {
            System.out.println("  " + str);
        }
        System.out.println(parsingAnalysis.getErrorStr());
    }
}
