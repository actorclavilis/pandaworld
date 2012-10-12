/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

/**
 *
 * @author haro
 */
public final class PrettyPrint {
    private PrettyPrint(){}
    
    public static int tabWidth(StringBuffer sb) {
        return sb.length() - sb.lastIndexOf("\n");
    }
    
    public static void tab(StringBuffer sb) {
        int num = tabWidth(sb);
        sb.append("\n");
        while(num --> 0)
            sb.append(" ");
    }
}