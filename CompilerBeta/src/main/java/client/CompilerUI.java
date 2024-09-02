package client;

import lexical.LexicalAnalysis;
import lexical.Token;
import lexical.WordScanner;
import parsing.ParsingAnalysis;
import parsing.TreeNode;
import semantic.Semantic;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class CompilerUI {


    JTextArea messageTextArea;
    JTextArea codeTextArea;
    String fileContext = null;
    WordScanner scanner = new WordScanner();
    ParsingAnalysis analyzer = null;
    TreeNode treeRoot;
    JTextArea treeTextArea;

    private JFrame compilerFrame;
    private JTable tokenTable;


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CompilerUI ui = new CompilerUI();
                    ui.compilerFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public CompilerUI() {
        initialize();
    }

    private void initialize() {
        compilerFrame = new JFrame();

        compilerFrame.setTitle("CompilerBeta");
        compilerFrame.setBounds(100, 100, 491, 337);
        compilerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        compilerFrame.getContentPane().setLayout(new BorderLayout(0, 0));
        //水平垂直都最大化
        compilerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JMenuBar menuBar = new JMenuBar();
        compilerFrame.getContentPane().add(menuBar, BorderLayout.NORTH);

        JMenu compiler = new JMenu("编译");
        menuBar.add(compiler);

        JMenuItem wordScannerBtn = new JMenuItem("词法分析");
        wordScannerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //调用词法分析程序
                fileContext = codeTextArea.getText();
                String [] temp = fileContext.split("\n");
                List<String> readList = new ArrayList<>(Arrays.asList(temp));
                //System.out.println("aaa"+scanner.getTokenList(readList));
                List<Token> token = scanner.getTokenList(readList);
                //Vector<Token> token = (Vector<Token>) scanner.getTokenList(readList);
                AbstractTableModel dataModel = new AbstractTableModel() {
                    public int getColumnCount() { return 3; }
                    public int getRowCount() { return token.size();}
                    public Object getValueAt(int row, int col) {
                        if(col==0)
                            return token.get(row).type.toString()+":"+token.get(row).type.ordinal();
                        else if(col==1)
                            return  token.get(row).context;
                        else
                            return Integer.toString(token.get(row).line);
                    }
                    public String getColumnName(int col) {
                        if(col==0)
                            return "type";
                        else if(col==1)
                            return "context";
                        return "line";
                    }
                };
                tokenTable.setModel(dataModel);
                tokenTable.updateUI();
            }
        });
        compiler.add(wordScannerBtn);

        JMenuItem analyzerBtn = new JMenuItem("语法分析");
        analyzerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileContext = codeTextArea.getText();
                String [] temp = fileContext.split("\n");
                List<String> readList = new ArrayList<>(Arrays.asList(temp));
                //System.out.println("aaa"+scanner.getTokenList(readList));
                List<Token> token = scanner.getTokenList(readList);
                //Vector<Token> token = (Vector<Token>) scanner.getTokenList(readList);
                AbstractTableModel dataModel = new AbstractTableModel() {
                    public int getColumnCount() { return 3; }
                    public int getRowCount() { return token.size();}
                    public Object getValueAt(int row, int col) {
                        if(col==0)
                            return token.get(row).type.toString()+":"+token.get(row).type.ordinal();
                        else if(col==1)
                            return  token.get(row).context;
                        else
                            return Integer.toString(token.get(row).line);
                    }
                    public String getColumnName(int col) {
                        if(col==0)
                            return "type";
                        else if(col==1)
                            return "context";
                        return "line";
                    }
                };
                tokenTable.setModel(dataModel);
                tokenTable.updateUI();
                analyzer = new ParsingAnalysis();
                analyzer.setTokenList(token);
                treeRoot = analyzer.parse();
                String result =	analyzer.getErrorStr();
                if(result.equals(""))
                    messageTextArea.setText("语法分析成功，没有错误");
                else
                    messageTextArea.setText(analyzer.getErrorStr());

                analyzer.printTree(treeRoot);
                StringBuilder str = new StringBuilder();
                for (String s : analyzer.getParsingList()) {
                    System.out.println(s);
                    str.append(s+"\n");
                }
                System.out.println(str);
                treeTextArea.setText(String.valueOf(str));
                treeTextArea.updateUI();
                messageTextArea.updateUI();
            }
        });
        compiler.add(analyzerBtn);

        JMenuItem semanticBtn = new JMenuItem("语义分析");
        semanticBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileContext = codeTextArea.getText();
                String [] temp = fileContext.split("\n");
                List<String> readList = new ArrayList<>(Arrays.asList(temp));
                //System.out.println("aaa"+scanner.getTokenList(readList));
                List<Token> token = scanner.getTokenList(readList);
                //Vector<Token> token = (Vector<Token>) scanner.getTokenList(readList);
                AbstractTableModel dataModel = new AbstractTableModel() {
                    public int getColumnCount() { return 3; }
                    public int getRowCount() { return token.size();}
                    public Object getValueAt(int row, int col) {
                        if(col==0)
                            return token.get(row).type.toString()+":"+token.get(row).type.ordinal();
                        else if(col==1)
                            return  token.get(row).context;
                        else
                            return Integer.toString(token.get(row).line);
                    }
                    public String getColumnName(int col) {
                        if(col==0)
                            return "type";
                        else if(col==1)
                            return "context";
                        return "line";
                    }
                };
                tokenTable.setModel(dataModel);
                tokenTable.updateUI();
                analyzer = new ParsingAnalysis();
                analyzer.setTokenList(token);
                treeRoot = analyzer.parse();
                String result =	analyzer.getErrorStr();


                analyzer.printTree(treeRoot);
                StringBuilder str = new StringBuilder();
                for (String s : analyzer.getParsingList()) {
                    System.out.println("s:=====>"+s);
                    str.append(s+"\n");
                }
                System.out.println(str);
                String messageText = "";

                if(result.equals(""))
                    messageText+="语法分析成功，没有错误";
                else
                    messageText+=analyzer.getErrorStr();

                Semantic semantic = new Semantic();

                semantic.analyze(treeRoot);
                System.out.println("Semantic    done");

                semantic.PrintSymbTable();
                System.out.println("printsymbtable    done");

                List<String> strList = semantic.getList();
                String table = "";
                for (String t : strList) {
                    System.out.println("t=====>"+t);
                    table+=t;
                    System.out.println(t);
                }
                System.out.println("inTester:"+semantic.getTempStr());
                //TODO ----
                String[] errorMessage = semantic.getErrorMessage().split("\n");
                if(errorMessage.length>2) {
                    messageText += errorMessage[1];
                }else{
                    messageText += "\n语义分析成功，没有错误";
                }
                messageTextArea.setText(messageText);
                treeTextArea.setText(str +"\n\n\n\n"+table);
                treeTextArea.updateUI();
                messageTextArea.updateUI();
            }
        });
        compiler.add(semanticBtn);



        JPanel codePanel = new JPanel(new BorderLayout());
        JLabel codeLabel = new JLabel("coding here");
        codeLabel.setBackground(new Color(59, 55, 50, 169));
        codePanel.add(codeLabel,BorderLayout.NORTH);

        JScrollPane codeScrollPane = new JScrollPane();
        codeScrollPane.getVerticalScrollBar().setUI(new DemoScrollBarUI());
        codeScrollPane.setRowHeaderView(new LineNumberHeaderView());

        codeTextArea = new JTextArea();
        codeScrollPane.setViewportView(codeTextArea);

        codePanel.add(codeScrollPane,BorderLayout.CENTER);
        codePanel.setVisible(true);
        compilerFrame.add(codePanel,BorderLayout.CENTER);

        JPanel tokenPanel = new JPanel(new BorderLayout());
        JLabel tokenLabel = new JLabel("tokens");
        tokenLabel.setBackground(new Color(59, 55, 50, 169));
        tokenPanel.add(tokenLabel,BorderLayout.NORTH);


        JScrollPane tokenScrollPane = new JScrollPane();
        tokenScrollPane.getVerticalScrollBar().setUI(new DemoScrollBarUI());
        tokenPanel.add(tokenScrollPane,BorderLayout.CENTER);
        compilerFrame.add(tokenPanel,BorderLayout.WEST);
        tokenTable = new JTable();
        tokenTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tokenTable.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "type", "context","line"
                }
        ));
        tokenScrollPane.setViewportView(tokenTable);


        JPanel messagePanel = new JPanel(new BorderLayout());
        JLabel messageLabel = new JLabel("message analysis out ");
        messageLabel.setBackground(new Color(59, 55, 50, 169));
        messagePanel.add(messageLabel,BorderLayout.NORTH);


        JScrollPane messageScrollPane = new JScrollPane();
        tokenScrollPane.getVerticalScrollBar().setUI(new DemoScrollBarUI());

        messagePanel.add(messageScrollPane,BorderLayout.CENTER);
        //compilerFrame.add(messagePanel,BorderLayout.SOUTH);
        codePanel.add(messagePanel,BorderLayout.SOUTH);
        messageTextArea = new JTextArea("\n\n\n\n\n\n\n\n");

        messageTextArea.setEditable(false);
        messageScrollPane.setViewportView(messageTextArea);

        JPanel treePanel = new JPanel(new BorderLayout());
        JLabel treeLabel = new JLabel("parse tree   " +
                "                                          " +
                "                                          ");
        treeLabel.setBackground(new Color(59, 55, 50, 169));
        treePanel.add(treeLabel,BorderLayout.NORTH);
        treePanel.setMinimumSize(new Dimension(400,40));



        JScrollPane treeScrollPane = new JScrollPane();
        treeScrollPane.getVerticalScrollBar().setUI(new DemoScrollBarUI());
        treePanel.add(treeScrollPane,BorderLayout.CENTER);
        compilerFrame.add(treePanel,BorderLayout.EAST);

        treeTextArea = new JTextArea();
        treeTextArea.setEditable(false);
        treeScrollPane.setViewportView(treeTextArea);

    }

}