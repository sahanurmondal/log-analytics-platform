package design.hard;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Design Simple Compiler
 *
 * Description: Design a compiler that supports:
 * - Lexical analysis (tokenization)
 * - Syntax parsing with AST generation
 * - Semantic analysis and type checking
 * - Code generation or interpretation
 * 
 * Constraints:
 * - Support basic language constructs
 * - Handle variables, expressions, control flow
 * - Error reporting and recovery
 *
 * Follow-up:
 * - How to optimize generated code?
 * - Support for functions and scoping?
 * 
 * Time Complexity: O(n) for parsing, O(n) for execution
 * Space Complexity: O(n) for AST and symbol table
 * 
 * Company Tags: Compiler companies, Language designers
 */
public class DesignCompiler {

    enum TokenType {
        NUMBER, IDENTIFIER, PLUS, MINUS, MULTIPLY, DIVIDE, ASSIGN,
        EQUALS, LESS_THAN, GREATER_THAN, SEMICOLON, LPAREN, RPAREN,
        LBRACE, RBRACE, IF, ELSE, WHILE, PRINT, EOF, NEWLINE
    }

    class Token {
        TokenType type;
        String value;
        int line;
        int column;

        Token(TokenType type, String value, int line, int column) {
            this.type = type;
            this.value = value;
            this.line = line;
            this.column = column;
        }

        @Override
        public String toString() {
            return String.format("Token[%s: '%s' at %d:%d]", type, value, line, column);
        }
    }

    class Lexer {
        private String input;
        private int position;
        private int line;
        private int column;
        private Map<String, TokenType> keywords;

        Lexer(String input) {
            this.input = input;
            this.position = 0;
            this.line = 1;
            this.column = 1;
            initializeKeywords();
        }

        private void initializeKeywords() {
            keywords = new HashMap<>();
            keywords.put("if", TokenType.IF);
            keywords.put("else", TokenType.ELSE);
            keywords.put("while", TokenType.WHILE);
            keywords.put("print", TokenType.PRINT);
        }

        List<Token> tokenize() {
            List<Token> tokens = new ArrayList<>();

            while (position < input.length()) {
                char current = input.charAt(position);

                if (Character.isWhitespace(current)) {
                    handleWhitespace();
                } else if (Character.isDigit(current)) {
                    tokens.add(tokenizeNumber());
                } else if (Character.isLetter(current) || current == '_') {
                    tokens.add(tokenizeIdentifier());
                } else {
                    tokens.add(tokenizeOperator());
                }
            }

            tokens.add(new Token(TokenType.EOF, "", line, column));
            return tokens;
        }

        private void handleWhitespace() {
            char current = input.charAt(position);
            if (current == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            position++;
        }

        private Token tokenizeNumber() {
            int start = position;
            int startColumn = column;

            while (position < input.length() &&
                    (Character.isDigit(input.charAt(position)) || input.charAt(position) == '.')) {
                position++;
                column++;
            }

            String value = input.substring(start, position);
            return new Token(TokenType.NUMBER, value, line, startColumn);
        }

        private Token tokenizeIdentifier() {
            int start = position;
            int startColumn = column;

            while (position < input.length() &&
                    (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
                position++;
                column++;
            }

            String value = input.substring(start, position);
            TokenType type = keywords.getOrDefault(value, TokenType.IDENTIFIER);
            return new Token(type, value, line, startColumn);
        }

        private Token tokenizeOperator() {
            char current = input.charAt(position);
            int currentColumn = column;
            position++;
            column++;

            switch (current) {
                case '+':
                    return new Token(TokenType.PLUS, "+", line, currentColumn);
                case '-':
                    return new Token(TokenType.MINUS, "-", line, currentColumn);
                case '*':
                    return new Token(TokenType.MULTIPLY, "*", line, currentColumn);
                case '/':
                    return new Token(TokenType.DIVIDE, "/", line, currentColumn);
                case '=':
                    if (position < input.length() && input.charAt(position) == '=') {
                        position++;
                        column++;
                        return new Token(TokenType.EQUALS, "==", line, currentColumn);
                    }
                    return new Token(TokenType.ASSIGN, "=", line, currentColumn);
                case '<':
                    return new Token(TokenType.LESS_THAN, "<", line, currentColumn);
                case '>':
                    return new Token(TokenType.GREATER_THAN, ">", line, currentColumn);
                case ';':
                    return new Token(TokenType.SEMICOLON, ";", line, currentColumn);
                case '(':
                    return new Token(TokenType.LPAREN, "(", line, currentColumn);
                case ')':
                    return new Token(TokenType.RPAREN, ")", line, currentColumn);
                case '{':
                    return new Token(TokenType.LBRACE, "{", line, currentColumn);
                case '}':
                    return new Token(TokenType.RBRACE, "}", line, currentColumn);
                default:
                    throw new RuntimeException(
                            "Unexpected character: " + current + " at " + line + ":" + currentColumn);
            }
        }
    }

    // Abstract Syntax Tree nodes
    abstract class ASTNode {
        abstract Object evaluate(Map<String, Object> variables);
    }

    class NumberNode extends ASTNode {
        double value;

        NumberNode(double value) {
            this.value = value;
        }

        @Override
        Object evaluate(Map<String, Object> variables) {
            return value;
        }

        @Override
        public String toString() {
            return "Number(" + value + ")";
        }
    }

    class IdentifierNode extends ASTNode {
        String name;

        IdentifierNode(String name) {
            this.name = name;
        }

        @Override
        Object evaluate(Map<String, Object> variables) {
            if (!variables.containsKey(name)) {
                throw new RuntimeException("Undefined variable: " + name);
            }
            return variables.get(name);
        }

        @Override
        public String toString() {
            return "Identifier(" + name + ")";
        }
    }

    class BinaryOpNode extends ASTNode {
        ASTNode left;
        Token operator;
        ASTNode right;

        BinaryOpNode(ASTNode left, Token operator, ASTNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        Object evaluate(Map<String, Object> variables) {
            Object leftVal = left.evaluate(variables);
            Object rightVal = right.evaluate(variables);

            if (!(leftVal instanceof Number) || !(rightVal instanceof Number)) {
                throw new RuntimeException("Type error: expected numbers for arithmetic operation");
            }

            double l = ((Number) leftVal).doubleValue();
            double r = ((Number) rightVal).doubleValue();

            switch (operator.type) {
                case PLUS:
                    return l + r;
                case MINUS:
                    return l - r;
                case MULTIPLY:
                    return l * r;
                case DIVIDE:
                    if (r == 0)
                        throw new RuntimeException("Division by zero");
                    return l / r;
                case EQUALS:
                    return l == r ? 1.0 : 0.0;
                case LESS_THAN:
                    return l < r ? 1.0 : 0.0;
                case GREATER_THAN:
                    return l > r ? 1.0 : 0.0;
                default:
                    throw new RuntimeException("Unknown operator: " + operator.type);
            }
        }

        @Override
        public String toString() {
            return "BinaryOp(" + left + " " + operator.value + " " + right + ")";
        }
    }

    class AssignmentNode extends ASTNode {
        String variable;
        ASTNode expression;

        AssignmentNode(String variable, ASTNode expression) {
            this.variable = variable;
            this.expression = expression;
        }

        @Override
        Object evaluate(Map<String, Object> variables) {
            Object value = expression.evaluate(variables);
            variables.put(variable, value);
            return value;
        }

        @Override
        public String toString() {
            return "Assignment(" + variable + " = " + expression + ")";
        }
    }

    class PrintNode extends ASTNode {
        ASTNode expression;

        PrintNode(ASTNode expression) {
            this.expression = expression;
        }

        @Override
        Object evaluate(Map<String, Object> variables) {
            Object value = expression.evaluate(variables);
            System.out.println(value);
            return value;
        }

        @Override
        public String toString() {
            return "Print(" + expression + ")";
        }
    }

    class BlockNode extends ASTNode {
        List<ASTNode> statements;

        BlockNode(List<ASTNode> statements) {
            this.statements = statements;
        }

        @Override
        Object evaluate(Map<String, Object> variables) {
            Object lastValue = null;
            for (ASTNode statement : statements) {
                lastValue = statement.evaluate(variables);
            }
            return lastValue;
        }

        @Override
        public String toString() {
            return "Block(" + statements + ")";
        }
    }

    class IfNode extends ASTNode {
        ASTNode condition;
        ASTNode thenBranch;
        ASTNode elseBranch;

        IfNode(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        Object evaluate(Map<String, Object> variables) {
            Object condValue = condition.evaluate(variables);

            if (!(condValue instanceof Number)) {
                throw new RuntimeException("Type error: condition must be a number");
            }

            double value = ((Number) condValue).doubleValue();
            if (value != 0.0) {
                return thenBranch.evaluate(variables);
            } else if (elseBranch != null) {
                return elseBranch.evaluate(variables);
            }

            return null;
        }

        @Override
        public String toString() {
            return "If(" + condition + " then " + thenBranch +
                    (elseBranch != null ? " else " + elseBranch : "") + ")";
        }
    }

    class Parser {
        private List<Token> tokens;
        private int position;

        Parser(List<Token> tokens) {
            this.tokens = tokens;
            this.position = 0;
        }

        ASTNode parse() {
            List<ASTNode> statements = new ArrayList<>();

            while (!isAtEnd()) {
                if (currentToken().type == TokenType.EOF) {
                    break;
                }

                ASTNode statement = parseStatement();
                if (statement != null) {
                    statements.add(statement);
                }
            }

            return new BlockNode(statements);
        }

        private ASTNode parseStatement() {
            Token current = currentToken();

            switch (current.type) {
                case IF:
                    return parseIfStatement();
                case PRINT:
                    return parsePrintStatement();
                case IDENTIFIER:
                    if (peekNext().type == TokenType.ASSIGN) {
                        return parseAssignment();
                    }
                    // Fall through to expression
                default:
                    ASTNode expr = parseExpression();
                    consume(TokenType.SEMICOLON, "Expected ';' after expression");
                    return expr;
            }
        }

        private ASTNode parseIfStatement() {
            consume(TokenType.IF, "Expected 'if'");
            consume(TokenType.LPAREN, "Expected '(' after 'if'");
            ASTNode condition = parseExpression();
            consume(TokenType.RPAREN, "Expected ')' after condition");

            ASTNode thenBranch = parseStatement();
            ASTNode elseBranch = null;

            if (match(TokenType.ELSE)) {
                elseBranch = parseStatement();
            }

            return new IfNode(condition, thenBranch, elseBranch);
        }

        private ASTNode parsePrintStatement() {
            consume(TokenType.PRINT, "Expected 'print'");
            consume(TokenType.LPAREN, "Expected '(' after 'print'");
            ASTNode expression = parseExpression();
            consume(TokenType.RPAREN, "Expected ')' after expression");
            consume(TokenType.SEMICOLON, "Expected ';' after print statement");

            return new PrintNode(expression);
        }

        private ASTNode parseAssignment() {
            Token identifier = consume(TokenType.IDENTIFIER, "Expected identifier");
            consume(TokenType.ASSIGN, "Expected '='");
            ASTNode expression = parseExpression();
            consume(TokenType.SEMICOLON, "Expected ';' after assignment");

            return new AssignmentNode(identifier.value, expression);
        }

        private ASTNode parseExpression() {
            return parseComparison();
        }

        private ASTNode parseComparison() {
            ASTNode expr = parseAddition();

            while (match(TokenType.EQUALS, TokenType.LESS_THAN, TokenType.GREATER_THAN)) {
                Token operator = previousToken();
                ASTNode right = parseAddition();
                expr = new BinaryOpNode(expr, operator, right);
            }

            return expr;
        }

        private ASTNode parseAddition() {
            ASTNode expr = parseMultiplication();

            while (match(TokenType.PLUS, TokenType.MINUS)) {
                Token operator = previousToken();
                ASTNode right = parseMultiplication();
                expr = new BinaryOpNode(expr, operator, right);
            }

            return expr;
        }

        private ASTNode parseMultiplication() {
            ASTNode expr = parsePrimary();

            while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
                Token operator = previousToken();
                ASTNode right = parsePrimary();
                expr = new BinaryOpNode(expr, operator, right);
            }

            return expr;
        }

        private ASTNode parsePrimary() {
            if (match(TokenType.NUMBER)) {
                return new NumberNode(Double.parseDouble(previousToken().value));
            }

            if (match(TokenType.IDENTIFIER)) {
                return new IdentifierNode(previousToken().value);
            }

            if (match(TokenType.LPAREN)) {
                ASTNode expr = parseExpression();
                consume(TokenType.RPAREN, "Expected ')' after expression");
                return expr;
            }

            throw new RuntimeException("Unexpected token: " + currentToken());
        }

        private boolean match(TokenType... types) {
            for (TokenType type : types) {
                if (check(type)) {
                    advance();
                    return true;
                }
            }
            return false;
        }

        private boolean check(TokenType type) {
            return !isAtEnd() && currentToken().type == type;
        }

        private Token advance() {
            if (!isAtEnd())
                position++;
            return previousToken();
        }

        private boolean isAtEnd() {
            return position >= tokens.size() || currentToken().type == TokenType.EOF;
        }

        private Token currentToken() {
            return position < tokens.size() ? tokens.get(position) : new Token(TokenType.EOF, "", 0, 0);
        }

        private Token previousToken() {
            return tokens.get(position - 1);
        }

        private Token peekNext() {
            return position + 1 < tokens.size() ? tokens.get(position + 1) : new Token(TokenType.EOF, "", 0, 0);
        }

        private Token consume(TokenType type, String message) {
            if (check(type))
                return advance();
            throw new RuntimeException(message + ". Got: " + currentToken());
        }
    }

    class Interpreter {
        private Map<String, Object> variables;

        Interpreter() {
            variables = new HashMap<>();
        }

        void interpret(ASTNode program) {
            try {
                program.evaluate(variables);
            } catch (Exception e) {
                System.err.println("Runtime error: " + e.getMessage());
            }
        }

        Map<String, Object> getVariables() {
            return new HashMap<>(variables);
        }
    }

    public void compile(String source) {
        try {
            // Lexical analysis
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.tokenize();

            System.out.println("=== Tokens ===");
            tokens.forEach(System.out::println);

            // Syntax analysis
            Parser parser = new Parser(tokens);
            ASTNode ast = parser.parse();

            System.out.println("\n=== AST ===");
            System.out.println(ast);

            // Interpretation
            System.out.println("\n=== Execution ===");
            Interpreter interpreter = new Interpreter();
            interpreter.interpret(ast);

            System.out.println("\n=== Variables ===");
            System.out.println(interpreter.getVariables());

        } catch (Exception e) {
            System.err.println("Compilation error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        DesignCompiler compiler = new DesignCompiler();

        String program = "x = 10;\n" +
                "y = 20;\n" +
                "sum = x + y;\n" +
                "print(sum);\n" +
                "if (sum > 25)\n" +
                "    print(1);\n" +
                "else\n" +
                "    print(0);\n" +
                "result = sum * 2 - 5;\n" +
                "print(result);";

        System.out.println("=== Source Code ===");
        System.out.println(program);
        System.out.println();

        compiler.compile(program);
    }
}
