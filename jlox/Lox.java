package com.craftinginterpeters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.craftinginterpreters.lox.Scanner.*;

/**
 * Various exit codes from Unix sysexits.h:
 *
 * https://man.openbsd.org/sysexits
 */
class ExitCode {
    /**
     * Command line usage error.  The command was used incorrectly, e.g. with
     * the wrong number of arguments, a bad flag, bad syntax in a parameter, or
     * whatever.
     */
    static final int EX_USAGE = 64;

    /**
     * Data format error.  The input data was incorrect in some way.  This
     * should only be used for user's data & not ystem files.
     */
    static final int EX_DATAERR = 65;
}

public class Lox {
    /**
     * Whether we have detected an error in source code.
     */
    static boolean hadError = false;

    /**
     * TODO: Document me.
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(ExitCode.EX_USAGE);
        } else if (1 == args.length) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    /**
     * Read a Lox source file and execute it.
     *
     * @param path A path to a Lox source file.
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        // Indicate an error in the exit code.
        if (hadError) {
            System.exit(ExitCode.EX_DATAERR);
        }
    }

    /**
     * An interactive prompt for REPL.
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (null == line) {
                break;
            }
            run(line);
            hadError = false;
        }
    }

    /**
     * Execute Lox source code.
     *
     * @param source A string of Lox source code.
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        // For now, just print the tokens.
        for (Token tk : tokens) {
            System.out.println(tk);
        }
    }

    /**
     * Report an error.
     *
     * @param line The line number where an error occurs.
     * @param message A custom message about the error.
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Report an error.
     *
     * @param line The line number where an error occurs.
     * @param where *What is this parameter supposed to do?*
     * @param message A custom message about the error.
     */
    private static void report(int line, String where, String message) {
        String msg = "[line " + line + "] Error" + where + ": " + message;
        System.err.println(msg);
    }
}
