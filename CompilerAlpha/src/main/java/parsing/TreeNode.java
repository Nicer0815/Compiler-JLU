package parsing;
import java.util.ArrayList;


/*
参见59页的语法树结点的数据结构
 */

public class TreeNode {
    public enum NodeKind {
        PROC_K, PHEAD_K, TYPE_K, VAR_K, PROC_DEC_K, STM_L_K,
        DEC_K, STMT_K, EXP_K
    }
    public enum Kind {
        ARRAY_DEC, CHAR_DEC, INTEGER_DEC, RECORD_DEC, ID_DEC,
        IF_STMT, WHILE_STMT,ASSIGN_STMT,READ_STMT, WRITE_STMT, CALL_STMT, RETURN_STMT,
        OP_EXP,CONST_EXP,ID_EXP
    }
    public TreeNode[] child;
    public TreeNode sibling;
    int lineno;

    public NodeKind nodeKind;
    Kind kind;
    public ArrayList<String> idString;
    ArrayList<String> typeArray;
    ArrayList<String> addr;
    public TreeNode() {
        child = new TreeNode[3];
        addr = new ArrayList<String>();
        idString = new ArrayList<String>();
        typeArray = new ArrayList<String>();
        sibling = null;
    }
    public void printTree(TreeNode root,int space) {
        if(root==null) {
            return;
        }
        for(int i=0;i<space;i++)
            System.out.print("    ");
        System.out.println(root.nodeKind.toString()+root.idString);
        for(int i=0;i<3;i++) {
            if(root.child[i]!=null)
                printTree(root.child[i],space+1);
        }
        if(root.sibling !=null) {
            printTree(root.sibling,space);
        }
    }
    public String getTree(TreeNode root, int space) {
        StringBuilder output = new StringBuilder();
        if(root == null)
            return "";
        output.append("    ".repeat(Math.max(0, space)));
        output.append(root.nodeKind.toString()).append(root.idString).append("\n");
        for(int i=0;i<3;i++)
            if(root.child[i]!=null)
                output.append(root.child[i].getTree(root.child[i], space + 1));
        if(root.sibling !=null)
            output.append(root.sibling.getTree(root.sibling, space));
        return output.toString();
    }

    public TreeNode getSibling() {
        return sibling;
    }
}
