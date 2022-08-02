package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

/**
 * Scan the source file and output a bunch of tokens.
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
     * Whether we have reached the end of the source code, i.e. we have
     * scanned all characters.
     *
     * @return true if we have scanned all characters; false otherwise.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }
}
