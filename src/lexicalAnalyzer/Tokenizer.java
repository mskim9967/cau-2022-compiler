package lexicalAnalyzer;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tokenize input
 */
public class Tokenizer {
  private State[][] dfaTable;

  private State currentState;
  private String lexeme; // store current state's lexeme
  private List<Token> tokenList;


  // final state set
  public final State[] FINAL_STATE = {State.INT, State.IF, State.CLASS, State.CHAR, State.STRING, State.BOOLEAN, State.WHILE, State.ELSE, State.RETURN, State.V_BOOLEAN, State.PLUS, State.MINUS, State.PLUS, State.MINUS, State.MUL, State.DIV, State.LESS, State.LESS_EQ, State.GREAT, State.GREAT_EQ, State.ASSIGN, State.EQUAL, State.EXCLAM, State.UNEQUAL, State.SEMICOLON, State.L_B, State.L_M, State.L_S, State.R_B, State.R_M, State.R_S, State.COMMA, State.V_INT_ZERO, State.V_INT, State.V_STRING, State.V_CHAR};

  // identifier final state set
  private final State[] ID_FINAL_STATE = {State.ID, State.I, State.IN, State.C, State.CL, State.CLA, State.CLAS, State.CH, State.CHA, State.S, State.ST, State.STR, State.STRI, State.STRIN, State.B, State.BO, State.BOO, State.BOOL, State.BOOLE, State.BOOLEA, State.W, State.WH, State.WHI, State.WHIL, State.E, State.EL, State.ELS, State.R, State.RE, State.RET, State.RETU, State.RETUR, State.T, State.TR, State.TRU, State.F, State.FA, State.FAL, State.FALS};

  // final state set that can't be identifier
  private final State[] ID_IMPOSSIBLE_STATE = {State.PLUS, State.MINUS, State.MUL, State.DIV, State.LESS, State.LESS_EQ, State.GREAT, State.GREAT_EQ, State.ASSIGN, State.EQUAL, State.EXCLAM, State.UNEQUAL, State.SEMICOLON, State.L_B, State.L_M, State.L_S, State.R_B, State.R_M, State.R_S, State.COMMA, State.V_INT_ZERO, State.V_STRING, State.V_CHAR};

  // final state set which needs lexeme
  private final State[] LEXEME_STATE = {State.V_INT, State.V_CHAR, State.V_BOOLEAN, State.V_STRING, State.ID};


  public Tokenizer() {
    tokenList = new ArrayList<Token>();

    dfaTable = new State[80][128];

    // initializing dfa table which has possibility to become identifier from current state
    for (State s : State.values()) {
      if (Arrays.asList(ID_IMPOSSIBLE_STATE).contains(s))  // pass state which can't be an identifier
        continue;

      // identifier's input symbol (0-9, a-z, A-Z, _)
      for (int i = '0'; i <= '9'; i++) dfaTable[s.ordinal()][i] = State.ID;
      for (int i = 'a'; i <= 'z'; i++) dfaTable[s.ordinal()][i] = State.ID;
      for (int i = 'A'; i <= 'Z'; i++) dfaTable[s.ordinal()][i] = State.ID;
      dfaTable[s.ordinal()]['_'] = State.ID;
    }

    // initializing dfa table

    /* ------------ variable type ------------ */

    // int
    dfaTable[State.INIT.ordinal()]['i'] = State.I;
    dfaTable[State.I.ordinal()]['n'] = State.IN;
    dfaTable[State.IN.ordinal()]['t'] = State.INT;


    // char
    dfaTable[State.INIT.ordinal()]['c'] = State.C;
    dfaTable[State.C.ordinal()]['h'] = State.CH;
    dfaTable[State.CH.ordinal()]['a'] = State.CHA;
    dfaTable[State.CHA.ordinal()]['r'] = State.CHAR;

    // string
    dfaTable[State.INIT.ordinal()]['s'] = State.S;
    dfaTable[State.S.ordinal()]['t'] = State.ST;
    dfaTable[State.ST.ordinal()]['r'] = State.STR;
    dfaTable[State.STR.ordinal()]['i'] = State.STRI;
    dfaTable[State.STRI.ordinal()]['n'] = State.STRIN;
    dfaTable[State.STRIN.ordinal()]['g'] = State.STRING;

    // boolean
    dfaTable[State.INIT.ordinal()]['b'] = State.B;
    dfaTable[State.B.ordinal()]['o'] = State.BO;
    dfaTable[State.BO.ordinal()]['o'] = State.BOO;
    dfaTable[State.BOO.ordinal()]['l'] = State.BOOL;
    dfaTable[State.BOOL.ordinal()]['e'] = State.BOOLE;
    dfaTable[State.BOOLE.ordinal()]['a'] = State.BOOLEA;
    dfaTable[State.BOOLEA.ordinal()]['n'] = State.BOOLEAN;


    /* ---- keywords for special statements ---- */

    // if
    dfaTable[State.I.ordinal()]['f'] = State.IF;

    // else
    dfaTable[State.INIT.ordinal()]['e'] = State.E;
    dfaTable[State.E.ordinal()]['l'] = State.EL;
    dfaTable[State.EL.ordinal()]['s'] = State.ELS;
    dfaTable[State.ELS.ordinal()]['e'] = State.ELSE;

    // while
    dfaTable[State.INIT.ordinal()]['w'] = State.W;
    dfaTable[State.W.ordinal()]['h'] = State.WH;
    dfaTable[State.WH.ordinal()]['i'] = State.WHI;
    dfaTable[State.WHI.ordinal()]['l'] = State.WHIL;
    dfaTable[State.WHIL.ordinal()]['e'] = State.WHILE;

    // class
    dfaTable[State.C.ordinal()]['l'] = State.CL;
    dfaTable[State.CL.ordinal()]['a'] = State.CLA;
    dfaTable[State.CLA.ordinal()]['s'] = State.CLAS;
    dfaTable[State.CLAS.ordinal()]['s'] = State.CLASS;

    // return
    dfaTable[State.INIT.ordinal()]['r'] = State.R;
    dfaTable[State.R.ordinal()]['e'] = State.RE;
    dfaTable[State.RE.ordinal()]['t'] = State.RET;
    dfaTable[State.RET.ordinal()]['u'] = State.RETU;
    dfaTable[State.RETU.ordinal()]['r'] = State.RETUR;
    dfaTable[State.RETUR.ordinal()]['n'] = State.RETURN;


    /* ---------- boolean string ---------- */

    // true
    dfaTable[State.INIT.ordinal()]['t'] = State.T;
    dfaTable[State.T.ordinal()]['r'] = State.TR;
    dfaTable[State.TR.ordinal()]['u'] = State.TRU;
    dfaTable[State.TRU.ordinal()]['e'] = State.V_BOOLEAN;

    // false
    dfaTable[State.INIT.ordinal()]['f'] = State.F;
    dfaTable[State.F.ordinal()]['a'] = State.FA;
    dfaTable[State.FA.ordinal()]['l'] = State.FAL;
    dfaTable[State.FAL.ordinal()]['s'] = State.FALS;
    dfaTable[State.FALS.ordinal()]['e'] = State.V_BOOLEAN;


    /* ---------- arithmetic operators  ---------- */

    // +
    dfaTable[State.INIT.ordinal()]['+'] = State.PLUS;

    // -
    dfaTable[State.INIT.ordinal()]['-'] = State.MINUS;

    // *
    dfaTable[State.INIT.ordinal()]['*'] = State.MUL;

    // /
    dfaTable[State.INIT.ordinal()]['/'] = State.DIV;


    /* ---------- assignment operators  ---------- */

    // =
    dfaTable[State.INIT.ordinal()]['='] = State.ASSIGN;


    /* ---------- comparison operators  ---------- */

    // <
    dfaTable[State.INIT.ordinal()]['<'] = State.LESS;

    // <=
    dfaTable[State.LESS.ordinal()]['='] = State.LESS_EQ;

    // >
    dfaTable[State.INIT.ordinal()]['>'] = State.GREAT;

    // >=
    dfaTable[State.GREAT.ordinal()]['='] = State.GREAT_EQ;

    // ==
    dfaTable[State.ASSIGN.ordinal()]['='] = State.EQUAL;

    // !=
    dfaTable[State.INIT.ordinal()]['!'] = State.EXCLAM;
    dfaTable[State.EXCLAM.ordinal()]['='] = State.UNEQUAL;


    /* --- a terminating symbol of statements  --- */

    // ;
    dfaTable[State.INIT.ordinal()][';'] = State.SEMICOLON;


    /* a pair of symbols for defining area/scope of variables and functions */

    // {
    dfaTable[State.INIT.ordinal()]['{'] = State.L_B;

    // }
    dfaTable[State.INIT.ordinal()]['}'] = State.R_B;


    /* a pair of symbols for indicating a function/statement */

    // [
    dfaTable[State.INIT.ordinal()]['['] = State.L_M;

    // ]
    dfaTable[State.INIT.ordinal()][']'] = State.R_M;


    /* a pair of symbols for using an array */

    // (
    dfaTable[State.INIT.ordinal()]['('] = State.L_S;

    // )
    dfaTable[State.INIT.ordinal()][')'] = State.R_S;


    /* a symbol for separating input arguments in functions */

    // ,
    dfaTable[State.INIT.ordinal()][','] = State.COMMA;


    /* ----------- integer ----------- */
    dfaTable[State.INIT.ordinal()]['0'] = State.V_INT_ZERO;
    for (int i = 1; i < 10; i++) dfaTable[State.INIT.ordinal()]['0' + i] = State.V_INT;
    for (int i = 0; i < 10; i++) dfaTable[State.V_INT.ordinal()]['0' + i] = State.V_INT;


    /* --------- single character --------- */
    dfaTable[State.INIT.ordinal()]['\''] = State.V_CHAR1;
    for (int i = 32; i < 128; i++) if (i != '\'') dfaTable[State.V_CHAR1.ordinal()][i] = State.V_CHAR2;
    for (int i = 32; i < 128; i++) if (i != '\'') dfaTable[State.V_CHAR2.ordinal()][i] = State.V_CHAR2;
    dfaTable[State.V_CHAR2.ordinal()]['\''] = State.V_CHAR;


    /* --------- literal string --------- */
    dfaTable[State.INIT.ordinal()]['"'] = State.V_STRING1;
    for (int i = 0; i < 128; i++) if (i != '"') dfaTable[State.V_STRING1.ordinal()][i] = State.V_STRING2;
    for (int i = 0; i < 128; i++) if (i != '"') dfaTable[State.V_STRING2.ordinal()][i] = State.V_STRING2;
    dfaTable[State.V_STRING2.ordinal()]['"'] = State.V_STRING;
  }

  /**
   * save token to tokenlist
   */
  public void saveToken(int lineNum) {
    if (currentState == State.V_INT_ZERO) {
      currentState = State.V_INT;
      lexeme = "0";
    }
    else if (Arrays.asList(ID_FINAL_STATE).contains(currentState)) currentState = State.ID;
    else if (!Arrays.asList(LEXEME_STATE).contains(currentState)) lexeme = null;
    else if (currentState == State.V_STRING || currentState == State.V_CHAR)
      lexeme = lexeme.substring(1, lexeme.length() - 1);

    tokenList.add(new Token(currentState.name(), lexeme, lineNum));

    System.out.println("[LOG] token generated: " + currentState.name() + (lexeme == null ? "" : " / " + lexeme));
  }

  /**
   * initialize current state and lexeme
   */
  public void initState() {
    lexeme = "";
    currentState = State.INIT;
  }

  /**
   * check c is whitespace
   *
   * @param c
   * @return return true when c is whitespace
   */
  public boolean isWhitespace(char c) {
    return (c == ' ' || c == '\t' || c == '\n');
  }

  /**
   * tokenize the input file
   *
   * @param fileReader fileReader instance of input file
   * @return token list if success, else null
   * @throws IOException
   */
  public List<Token> tokenize(FileReader fileReader) throws IOException {
    int readerBuf, lineCnt = 1;
    char ch;
    State nextState;

    initState();

    try {
      do {
        // read symbol
        readerBuf = fileReader.read();

        // add whitespace to end of the input file
        ch = (readerBuf != -1 ? (char) readerBuf : ' ');

        if (ch == '\n') lineCnt++;

        // pass whitespace
        if (currentState == State.INIT && isWhitespace(ch)) continue;

        nextState = dfaTable[currentState.ordinal()][ch];

        // can't move to next state
        if (nextState == null) {

          // if current state isn't accepted, it's undefined token
          if (!Arrays.asList(FINAL_STATE).contains(currentState) &&
              !Arrays.asList(ID_FINAL_STATE).contains(currentState))
            throw new Exception("Undefined token { " + lexeme + ch + " } in line " + lineCnt);

          saveToken(lineCnt);
          initState();

          nextState = dfaTable[currentState.ordinal()][ch];

          // if whitespace, read next symbol
          if (isWhitespace(ch)) continue;

          // can't move to next state
          if (nextState == null)
            throw new Exception("Undefined token { " + lexeme + ch + " } in line " + lineCnt);

        }

        // move to next state
        currentState = nextState;
        lexeme += ch;

      } while (readerBuf != -1);

      // last state isn't init state
      if (currentState != State.INIT)
        throw new Exception("Undefined token { " + lexeme + ch + " } in line " + lineCnt);

    } catch (Exception e) {
      System.out.println("[ERROR] " + e.getMessage());
      return null;
    }

    System.out.println("\n[LOG] file read finished: " + tokenList.size() + " tokens are recognized");

    return tokenList;
  }
}
