package lexicalAnalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * write tokens to file
 */
public class Writer {

  /**
   * save tokens as json format
   *
   * @param tokenList tokens that are tokenized from input file
   * @param filename  fileName to save
   * @throws IOException
   */
  public static void writeToken2Json(List<Token> tokenList, String filename) throws IOException {
    File file = new File(filename);
    FileWriter fileWriter = new FileWriter(file);

    fileWriter.write("{");
    fileWriter.write("\"tokenCounts\":" + tokenList.size());

    fileWriter.write(",\"data\":[");
    for (Token token : tokenList) {
      fileWriter.write("{");
      fileWriter.write("\"token\":\"" + token.key + "\"");

      if (token.value != null)
        fileWriter.write(",\"lexeme\":\"" + token.value + "\"");

      fileWriter.write("}");

      if (tokenList.indexOf(token) != tokenList.size() - 1) fileWriter.write(",");
    }
    fileWriter.write("]}");
    fileWriter.flush();
    fileWriter.close();

    System.out.println("\n[SUCCESS] token file generated in " + filename);
  }


  /**
   * save tokens as txt format
   *
   * @param tokenList tokens that are tokenized from input file
   * @param filename  file name to save
   * @throws IOException
   */
  public static void writeToken2Txt(List<Token> tokenList, String filename) throws IOException {
    File file = new File(filename);
    FileWriter fileWriter = new FileWriter(file);

    for (Token token : tokenList) {
      fileWriter.write(token.key);

      fileWriter.write("/" + token.lineNum);
      if (token.value != null)
        fileWriter.write("/" + token.value);
      fileWriter.write("\n");
    }
    fileWriter.flush();
    fileWriter.close();

    System.out.println("\n[SUCCESS] token file generated in " + filename);
  }
}
