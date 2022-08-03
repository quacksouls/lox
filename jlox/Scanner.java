package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

/**
 * Scan the source file and output a bunch of tokens.  A lexeme consists of a
 * sequence of characters from the source code.  The whole sequence matches the
 * pattern for a token.  A token in turn is a string that thas a defined
 * meaning.  A token is structured as a name/value pair.  The token name
 * specifies the type of a token, the category to which a token belongs.  The
 * token value is an instance or example of a token.  Here are some common
 * token names together with sample token values.
 *
 * (1) Identifier.  A name chosen by the programmer.
 *     Examples: myName, last_name, hello.
 * (2) Keyword.  A reserved name in the programming language.
 *     Examples: for, if, return.
 * (3) Separator.  One or more characters that serve as punctuation characters
 *     or paired-delimiters.
 *     Examples: {, ;, (
 * (4) Operator.  A symbol that operates on operands, producing a result.
 *     Examples: +, >, -
 * (5) Literal.  A numeric, logical, string, or reference literal.
 *     Examples: false, 3.1415, "apple"
 * (6) Comment.  A line or block comment, ignored by the compiler.
 *     Examples:
 *     // A line comment.
 *     This class documentation is a block comment.
 */
class Scanner {
    /**
     * A string representing the source code.
     */
    private final String source;
    /**
     * An array of tokens generated from the source code.
     */
    private final List<Token> token = new ArrayList<>();
    /**
     * An index to the first character in the lexeme being scanned.
     */
    private int start = 0;
    /**
     * An index to the charcter being scanned.
     */
    private int current = 0;
    /**
     * The current line in the source code.
     */
    private int line = 1;

    /**
     * Constructor to create a scanner object.
     *
     * @param source A string representation of the source code.
     */
    Scanner(String source) {
        this.source = source;
    }

    /**
     * Scan all tokens found in a source scode.
     */
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }
        token.add(new Token(EOF, "", null, line));
        return token;
    }

    /**
     * Scan a token.
     */
    private void scanToken() {
        char c = advance();
        switch (c) {
        case '(':
            addToken(LEFT_PAREN);
            break;
        case ')':
            addToken(RIGHT_PAREN);
            break;
        case '{':
            addToken(LEFT_BRACE);
            break;
        case '}':
            addToken(RIGHT_BRACE);
            break;
        case ',':
            addToken(COMMA);
            break;
        case '.':
            addToken(DOT);
            break;
        case '-':
            addToken(MINUS);
            break;
        case '+':
            addToken(PLUS);
            break;
        case ';':
            addToken(SEMICOLON);
            break;
        case '*':
            addToken(STAR);
            break;
        default:
            Lox.error(line, "Unexpected character.");
            break;
        }
    }

    /**
     * Whether we have reached the end of the source code, i.e. we have
     * scanned all characters.
     *
     * @return true if we have scanned all characters; false otherwise.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Advance to the next character in the source code.
     *
     * @return The next character in the source code.
     */
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    /**
     * Add a token to the list of tokens.
     *
     * @param type A token type.
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Add a token to the list of tokens.
     *
     * @param type A token type.
     * @param literal A literal value.
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        token.add(new Token(type, text, literal, line));
    }
}
