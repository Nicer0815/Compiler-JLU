package parsing;
import lexical.LexicalAnalysis;
import lexical.Token;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Vector;
public class ParsingTest {



    @Test
    public void test01(){

        File f = new File("D://SNLTest//test01.txt");
        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            byte[] b = new byte[(int)f.length()];
            in.read(b);
            String testProgram = new String(b);
            //System.out.println(testProgram);
            LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
            List<Token> list = lexicalAnalysis.getToken(testProgram);
            ParsingAnalysis parsingAnalysis = new ParsingAnalysis((Vector<Token>)list);
            TreeNode tree = parsingAnalysis.parse();

            //获取错误结果
            System.out.println(parsingAnalysis.getMessage());
            //获取语法树
            System.out.println(tree.getTree(tree, 0));
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
            //System.out.println(testProgram);
            LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
            List<Token> list = lexicalAnalysis.getToken(testProgram);
            ParsingAnalysis parsingAnalysis = new ParsingAnalysis((Vector<Token>)list);
            TreeNode tree = parsingAnalysis.parse();

            //获取错误结果
            System.out.println(parsingAnalysis.getMessage());
            //获取语法树
            System.out.println(tree.getTree(tree, 0));
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
            //System.out.println(testProgram);
            LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
            List<Token> list = lexicalAnalysis.getToken(testProgram);
            ParsingAnalysis parsingAnalysis = new ParsingAnalysis((Vector<Token>)list);
            TreeNode tree = parsingAnalysis.parse();

            //获取错误结果
                    System.out.println(parsingAnalysis.getMessage());
            //获取语法树
            System.out.println(tree.getTree(tree, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
