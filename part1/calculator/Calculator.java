package part1.calculator;
import java.io.InputStream;
import java.io.IOException;

/*
    +----------+-----+-----+-----+-----+-----+------+-----+
    |          |  (  |  )  |  +  |  -  |  ** | 0..9 |  $  |
    +----------+-----+-----+-----+-----+-----+------+-----+
    | exp      |  1  | err | err | err | err |  1   | err |
    | exp2     | err |  ε  |  2  |  3  | err | err  |  ε  |
    | term     |  5  | err | err | err | err |  5   | err |
    | term2    | err |  ε  |  ε  |  ε  |  6  | err  |  ε  |
    | factor   |  9  | err | err | err | err |  8   | err |
    | num      | err | err | err | err | err |  10  | err |
    | num_rest | err |  ε  |  ε  |  ε  |  ε  |  11  |  ε  |
    | digit    | err | err | err | err | err |  13  | err |
    +----------+-----+-----+-----+-----+-----+------+-----+

    (view the `LL1_grammar` folder for each rule and FIRST+, FOLLOW sets)
*/


class Calculator {
    private final InputStream in;

    private int lookahead;

    public Calculator(InputStream in) throws IOException {
        this.in = in;
        lookahead = in.read();
    }

    private void consume(int symbol) throws IOException, ParseError {
        if (lookahead == symbol)
            lookahead = in.read();
        else
            throw new ParseError();
    }

    private boolean isDigit(int c) {
        return '0' <= c && c <= '9';
    }

    private int evalDigit(int c) {
        return c - '0';
    }

    public int eval() throws IOException, ParseError {
        int value = exp();

        if (lookahead != -1 && lookahead != '\n')
            throw new ParseError();

        return value;
    }

    private int exp() throws IOException, ParseError {
        int first_term = term();
        int result = exp2(first_term);
        return result;
    }

    private int exp2(int sofar) throws IOException, ParseError {
        if (lookahead == '+') {
            consume('+');
            int first_term = term();
            int result = exp2(sofar + first_term);
            return result;
        } else if (lookahead == '-') {
            consume('-');
            int first_term = term();
            int result = exp2(sofar - first_term);
            return result;
        } else if (lookahead == ')' || lookahead == '\n') {
            return sofar;
        }

        throw new ParseError();
    }

    private int term() throws IOException, ParseError {
        int num1 = factor();
        int result = term2(num1);
        // Handles ε rules
        return result == Integer.MIN_VALUE ? num1 : result;
    }

    private int factor() throws IOException, ParseError {
        if (isDigit(lookahead)) {
            int num = num();
            return num;
        } else if (lookahead == '(') {
            consume('(');
            int result = exp();
            consume(')');
            return result;
        }

        throw new ParseError();
    }

    private int term2(int num1) throws IOException, ParseError {
        if (lookahead == '*') {
            consume('*');
            consume('*');
            int factor = factor();
            int term2 = term2(factor);
            if (term2 == Integer.MIN_VALUE) {
                // For ε rule
                return (int)Math.round(Math.pow(num1, factor));
            }
            return (int)Math.round(Math.pow(factor, term2));
        } else if (lookahead == ')' || lookahead == '+' || lookahead == '-' || lookahead == '\n') {
            return Integer.MIN_VALUE;
        }

        throw new ParseError();
    }

    private int num() throws IOException, ParseError {
        if (isDigit(lookahead)) {
            // ASCII -> digit
            int digit = evalDigit(lookahead);
            // digit -> str(digit)
            String s = String.valueOf(digit);
            consume(lookahead);
            String s1 = num_rest("");
            return Integer.parseInt(s + s1);
        }

        throw new ParseError();
    }

    private String num_rest(String s) throws IOException, ParseError {
        if (isDigit(lookahead)) {
            int digit = evalDigit(lookahead);
            String s1 = s + String.valueOf(digit);
            s1 = s1 + num_rest(s1);
            return s1;
        } else if (lookahead == ')' || lookahead == '+' || lookahead == '-' || lookahead == '*' || lookahead == '\n') {
            // Again, we do nothing
            return s;
        }

        throw new ParseError();
    }
}
