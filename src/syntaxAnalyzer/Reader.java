package syntaxAnalyzer;

import lexicalAnalyzer.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Reader {
  /**
   * read token file and return tokens
   * @param fileName token file name
   * @return list of token
   * @throws IOException
   */
  public static List<Token> file2TokenList(String fileName) throws IOException {
    BufferedReader reader = null;
    List<Token> tokenList = new ArrayList<>();

    try {
      Path filePath = Paths.get(System.getProperty("user.dir"), fileName);
      reader = new BufferedReader(new FileReader(filePath.toString()));
    } catch (IOException e) {
      System.out.println("[Error] file isn't exist");
    }

    String line = reader.readLine();
    while (line != null) {
      String splits[] = line.split("/", 3);
      tokenList.add(new Token(splits[0], splits.length == 3 ? splits[2] : null, Integer.parseInt(splits[1])));
      line = reader.readLine();
    }

    reader.close();
    return tokenList;
  }
}
