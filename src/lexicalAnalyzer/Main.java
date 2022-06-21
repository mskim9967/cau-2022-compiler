package lexicalAnalyzer;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * entry class
 */
public class Main {
  /**
   * entry point
   * <p>
   * read file from args[0], tokenize file and save file as txt and json format
   *
   * @param args args[0]: file name to tokenize
   * @throws IOException input file execption handler
   */
  public static void main(String[] args) throws IOException {
    String fileName;

    try {
      fileName = args[0];
    } catch (ArrayIndexOutOfBoundsException e) {
      fileName = "input.txt";
    }
    Path filePath = Paths.get(System.getProperty("user.dir"), fileName);

    // read file and tokenize
    Tokenizer tokenizer = new Tokenizer();
    List<Token> tokenList = tokenizer.tokenize(new FileReader(filePath.toString()));
    if (tokenList == null) return;

    // write file as txt and json format
    Writer.writeToken2Json(tokenList, fileName + ".output.json");
    Writer.writeToken2Txt(tokenList, fileName + ".output.txt");
  }
}
