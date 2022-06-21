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
   * read file from args[0], tokenize file and save file as txt and json fo   rmat
   *
   * @param args args[0]: file name to tokenize
   * @throws IOException input file execption handler
   */
  public static void main(String[] args) throws IOException {
    String fileName;

    try {
      fileName = args[0];
    } catch (Exception e) {
      fileName = "input.txt.output.txt";
    }

    // read file and tokenize
    List<Token> tokenList = Reader.file2TokenList(fileName);
    List<Token2> symbols = new ArrayList<>();
    tokenList.forEach(token -> {
      symbols.add(new Token2(State2.convert(State.valueOf(token.key)), token));
    });

    Parser syntaxAnalyzer = new Parser();
    boolean isValid = syntaxAnalyzer.isValid(symbols);
    System.out.println("\n[LOG] " + fileName + (isValid ? " Accepted" : " Rejected"));
  }
}
