package syntaxAnalyzer;

import lexicalAnalyzer.State;

/**
 * New states in state redefinition table
 */
public enum State2 {
  Vtype, Num, Character, Boolstr, Literal, Id, If, Else, While, Return, Class, Addsub, Multdiv, Assign, Comp, Semi, Comma, Lparen, Rparen, Lbrace, Rbrace, CCODE, CODE, VDECL, ASSIGN, RHS, EXPR, S1, S2, FDECL, ARG, MOREARGS, BLOCK, STMT, COND, S3, ELSE, RETURN, CDECL, ODECL, End;


  /**
   * convert project #1's state to project #2's state
   * @param state project #1's state
   * @return project #2's state
   */
  public static State2 convert(State state) {
    if (state.equals(State.INT) || state.equals(State.CHAR) || state.equals(State.STRING) || state.equals(State.BOOLEAN))
      return Vtype;

    if (state.equals(State.V_INT)) return Num;

    if (state.equals(State.V_CHAR)) return Character;

    if (state.equals(State.V_BOOLEAN)) return Boolstr;

    if (state.equals(State.V_STRING)) return Literal;

    if (state.equals(State.ID)) return Id;

    if (state.equals(State.IF)) return If;

    if (state.equals(State.ELSE)) return Else;

    if (state.equals(State.WHILE)) return While;

    if (state.equals(State.RETURN)) return Return;

    if (state.equals(State.CLASS)) return Class;

    if (state.equals(State.PLUS) || state.equals(State.MINUS)) return Addsub;

    if (state.equals(State.MUL) || state.equals(State.DIV)) return Multdiv;

    if (state.equals(State.ASSIGN)) return Assign;

    if (state.equals(State.LESS) || state.equals(State.LESS_EQ) || state.equals(State.GREAT) || state.equals(State.GREAT_EQ) || state.equals(State.EQUAL) || state.equals(State.UNEQUAL))
      return Comp;

    if (state.equals(State.SEMICOLON)) return Semi;

    if (state.equals(State.COMMA)) return Comma;

    if (state.equals(State.L_S)) return Lparen;
    if (state.equals(State.R_S)) return Rparen;
    if (state.equals(State.L_B)) return Lbrace;
    if (state.equals(State.R_B)) return Rbrace;

    return null;
  }
}
