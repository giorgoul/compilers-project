/* Test the lexer's functionality before creating a grammar.
 * Used combined with the 'standalone' option on the lexer.
 */
public class sym {
    public static final int EOF = 0;
    public static final int PLUS = 1;
    public static final int LPAREN = 2;
    public static final int RPAREN = 3;
    public static final int SEMI = 4;
    public static final int LBRACK = 5;
    public static final int RBRACK = 6;
    public static final int IF = 7;
    public static final int ELSE = 8;
    public static final int EQUAL = 9;
    public static final int COMMA = 10;
    public static final int PREFIX = 11;
    public static final int SUFFIX = 12;
    public static final int STRING_LITERAL = 13;
    public static final int IDENTIFIER = 14;
}