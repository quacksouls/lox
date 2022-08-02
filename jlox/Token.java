package com.craftinginterpreters.lox;

/**
 * A class to represent a token in the Lox language.
 */
class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    /**
     * Constructor a Token object.
     */
    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    /**
     * A string representation of a token.
     */
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
