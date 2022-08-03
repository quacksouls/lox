package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;
import static com.craftinginterpreters.lox.Lox.*;

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
public class Scanner {
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
     * Reserved words, i.e. keywords, of the Lox language.
     */
    private static final Map<String, TokenType> keyword;
    static {
        keyword = new HashMap<>();
        keyword.put("and",    AND);
        keyword.put("class",  CLASS);
        keyword.put("else",   ELSE);
        keyword.put("false",  FALSE);
        keyword.put("for",    FOR);
        keyword.put("fun",    FUN);
        keyword.put("if",     IF);
        keyword.put("nil",    NIL);
        keyword.put("or",     OR);
        keyword.put("print",  PRINT);
        keyword.put("return", RETURN);
        keyword.put("super",  SUPER);
        keyword.put("this",   THIS);
        keyword.put("true",   TRUE);
        keyword.put("var",    VAR);
        keyword.put("while",  WHILE);
    }

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
        case '!':
            addToken(match('=') ? BANG_EQUAL : BANG);
            break;
        case '=':
            addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            break;
        case '<':
            addToken(match('=') ? LESS_EQUAL : LESS);
            break;
        case '>':
            addToken(match('=') ? GREATER_EQUAL : GREATER);
            break;
        case '/':
            if (match('/')) {
                // Ignore the rest of the line because a line comment takes
                // one line.
                while ((peek() != '\n') && !isAtEnd()) {
                    advance();
                }
            } else {
                addToken(SLASH);
            }
            break;
        case ' ':
        case '\r':
        case '\t':
            // Ignore whitespace.
            break;
        case '\n':
            line++;
            break;
        case '"':
            string();
            break;
        default:
            if (isDigit(c)) {
                number();
            } else if (isAlpha(c)) {
                identifier();
            } else {
                Lox.error(line, "Unexpected character.");
            }
            break;
        }
    }

    /**
     * Scan an identifier.  This can be an identifier, i.e. a name chosen by
     * the programmer, or a reserved word (i.e. a keyword).
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        String text = source.substring(start, current);
        TokenType type = keyword.get(text);
        if (type == null) {
            type = IDENTIFIER;
        }
        addToken(type);
    }

    /**
     * Scan a number literal.
     */
    private void number() {
        while (isDigit(peek())) {
            advance();
        }
        // Look for a fractional part.
        if ((peek() == '.') && isDigit(peekNext())) {
            // Consume the decimal point ".".
            advance();
            while (isDigit(peek())) {
                advance();
            }
        }
        addToken(NUMBER,
                 Double.parseDouble(source.substring(start, current)));
    }

    /**
     * Peek at the next character without advancing the character pointer.
     * This is essentially looking two characters ahead.
     */
    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    /**
     * Whether a character represents an alphabetic character.  For our
     * purposes, we count the underscore character "_" as an alphabetic
     * character.
     *
     * @param c A character.
     * @return true is the given character represents an alphabetic character;
     *     false otherwise.
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z')
            || (c >= 'A' && c <= 'Z')
            || (c == '_');
    }

    /**
     * Whether a character represents an alphabetic character or a numeric
     * digit.
     *
     * @param c A character.
     * @return true if the given character is an alpha-numeric character;
     *     false otherwise.
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Scan a string literal.
     */
    private void string() {
        while ((peek() != '"') && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }
        // The closing ".
        advance();
        // The the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    /**
     * Whether the current character being scanned is what we expect.
     *
     * @param expected The character we want.
     * @return true if the current character being scanned is the expected
     *     character; false otherwise.
     */
    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    /**
     * Peek at the current character without advancing the character index.
     * This is essentially looking ahead at one character.
     */
    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    /**
     * Whether a character represents a decimal digit.
     *
     * @param c A character to process.
     * @return true if the given character represents a decimal digit;
     *     false otherwise.
     */
    private boolean isDigit(char c) {
        return (c >= '0') && (c <= '9');
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
