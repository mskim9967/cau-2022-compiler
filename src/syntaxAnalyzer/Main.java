package syntaxAnalyzer;

import lexicalAnalyzer.State;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.Tokenizer;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * entry class
 */
public class Main {
  /**
   * entry point
   * read file from args[0], parsing tokenized file and determine acceptable
   *
   * @param args args[0]: tokenized file name
   * @throws IOException tokenized file exception handler
   */
  public static void main(String[] args) throws IOException {
    String fileName;

    try {
      fileName = args[0];
    } catch (Exception e) {
      // default file name
      fileName = "input.txt.output.txt";
    }

    // read tokenized file
    List<Token> tokenList = Reader.file2TokenList(fileName);
    List<Token2> symbols = new ArrayList<>();

    // convert token format from project#1 to project#2
    tokenList.forEach(token -> {
      symbols.add(new Token2(State2.convert(State.valueOf(token.key)), token));
    });

    // determine acceptable
    boolean isValid = new Parser().isValid(symbols);
    System.out.println("\n[LOG] " + fileName + (isValid ? " Accepted" : " Rejected"));
  }
}


