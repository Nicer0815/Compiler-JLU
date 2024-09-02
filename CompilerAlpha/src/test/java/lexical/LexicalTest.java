package lexical;

import org.junit.Test;
import parsing.ParsingAnalysis;
import parsing.TreeNode;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Vector;

public class LexicalTest {
    @Test
    public void test01(){
        File f = new File("D://SNLTest//test01.txt");

        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            byte[] b = new byte[(int)f.length()];
            in.read(b);
            String testProgram = new String(b);
            LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
            List<Token> list = lexicalAnalysis.getToken(testProgram);
            for (Token token : list) {
                System.out.println(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test02(){
        File f = new File("D://SNLTest//test02.txt");

        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            byte[] b = new byte[(int)f.length()];
            in.read(b);
            String testProgram = new String(b);
            LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
            List<Token> list = lexicalAnalysis.getToken(testProgram);
            for (Token token : list) {
                System.out.println(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void test03(){
        File f = new File("D://SNLTest//test03.txt");

        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            byte[] b = new byte[(int)f.length()];
            in.read(b);
            String testProgram = new String(b);
            LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
            List<Token> list = lexicalAnalysis.getToken(testProgram);
            for (Token token : list) {
                System.out.println(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
