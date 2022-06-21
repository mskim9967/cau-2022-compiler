package syntaxAnalyzer;

import java.util.*;
import java.util.stream.Collectors;

import static syntaxAnalyzer.State2.*;


public class Parser {

  public Parser() {
    initLRTABLE();
  }

  private class Cfg {
    State2 left;
    List<State2> right;

    public Cfg(State2 left, List<State2> right) {
      this.left = left;
      this.right = right;
    }
  }

  private class LR {
    char action;
    int number;

    public LR(char action, int number) {
      this.action = action;
      this.number = number;
    }
  }

  private final List<Cfg> SLR = new ArrayList<>() {{
    add(new Cfg(State2.CCODE, Arrays.asList(CODE)));

    add(new Cfg(CODE, Arrays.asList(VDECL, CODE)));
    add(new Cfg(CODE, Arrays.asList(FDECL, CODE)));
    add(new Cfg(CODE, Arrays.asList(CDECL, CODE)));
    add(new Cfg(CODE, Arrays.asList()));

    add(new Cfg(VDECL, Arrays.asList(Vtype, Id, Semi)));
    add(new Cfg(VDECL, Arrays.asList(Vtype, ASSIGN, Semi)));

    add(new Cfg(ASSIGN, Arrays.asList(Id, Assign, RHS)));

    add(new Cfg(RHS, Arrays.asList(EXPR)));
    add(new Cfg(RHS, Arrays.asList(Literal)));
    add(new Cfg(RHS, Arrays.asList(Character)));
    add(new Cfg(RHS, Arrays.asList(Boolstr)));

    add(new Cfg(EXPR, Arrays.asList(Addsub, EXPR)));
    add(new Cfg(EXPR, Arrays.asList(S1, Addsub, EXPR)));
    add(new Cfg(EXPR, Arrays.asList(S1)));

    add(new Cfg(S1, Arrays.asList(S2, Multdiv, S1)));
    add(new Cfg(S1, Arrays.asList(S2)));

    add(new Cfg(S2, Arrays.asList(Lparen, EXPR, Rparen)));
    add(new Cfg(S2, Arrays.asList(Id)));
    add(new Cfg(S2, Arrays.asList(Num)));

    add(new Cfg(FDECL, Arrays.asList(Vtype, Id, Lparen, ARG, Rparen, Lbrace, BLOCK, RETURN, Rbrace)));

    add(new Cfg(ARG, Arrays.asList(Vtype, Id, MOREARGS)));
    add(new Cfg(ARG, Arrays.asList()));

    add(new Cfg(MOREARGS, Arrays.asList(Comma, Vtype, Id, MOREARGS)));
    add(new Cfg(MOREARGS, Arrays.asList()));

    add(new Cfg(BLOCK, Arrays.asList(STMT, BLOCK)));
    add(new Cfg(BLOCK, Arrays.asList()));

    add(new Cfg(STMT, Arrays.asList(VDECL)));
    add(new Cfg(STMT, Arrays.asList(ASSIGN, Semi)));
    add(new Cfg(STMT, Arrays.asList(If, Lparen, COND, Rparen, Lbrace, BLOCK, Rbrace, ELSE)));
    add(new Cfg(STMT, Arrays.asList(While, Lparen, COND, Rparen, Lbrace, BLOCK, Rbrace)));

    add(new Cfg(COND, Arrays.asList(Boolstr, S3)));
    add(new Cfg(S3, Arrays.asList(Comp, COND)));
    add(new Cfg(S3, Arrays.asList()));

    add(new Cfg(ELSE, Arrays.asList(Else, Lbrace, BLOCK, Rbrace)));
    add(new Cfg(ELSE, Arrays.asList()));

    add(new Cfg(RETURN, Arrays.asList(Return, RHS, Semi)));

    add(new Cfg(CDECL, Arrays.asList(Class, Id, Lbrace, ODECL, Rbrace)));

    add(new Cfg(ODECL, Arrays.asList(VDECL, ODECL)));
    add(new Cfg(ODECL, Arrays.asList(FDECL, ODECL)));
    add(new Cfg(ODECL, Arrays.asList()));
  }};

  private final Map<State2, List<State2>> FOLLOW = new HashMap<>() {{
    put(CCODE, Arrays.asList(End));
    put(CODE, Arrays.asList(End));
    put(VDECL, Arrays.asList(End, Vtype, Class, Return, Id, If, While, Rbrace));
    put(ASSIGN, Arrays.asList(Semi));
    put(RHS, Arrays.asList(Semi));
    put(EXPR, Arrays.asList(Semi, Rparen));
    put(S1, Arrays.asList(Addsub, Semi, Rparen));
    put(S2, Arrays.asList(Multdiv, Addsub, Semi, Rparen));
    put(FDECL, Arrays.asList(End, Vtype, Class, Rbrace));
    put(ARG, Arrays.asList(Rparen));
    put(MOREARGS, Arrays.asList(Rparen));
    put(BLOCK, Arrays.asList(Return, Rbrace));
    put(STMT, Arrays.asList(Return, Vtype, Id, If, While, Rbrace));
    put(COND, Arrays.asList(Rparen));
    put(S3, Arrays.asList(Rparen));
    put(ELSE, Arrays.asList(Return, Vtype, Id, If, While, Rbrace));
    put(RETURN, Arrays.asList(Rbrace));
    put(CDECL, Arrays.asList(End, Vtype, Class));
    put(ODECL, Arrays.asList(Rbrace));
  }};

  private LR[][] LRTABLE = new LR[100][State2.values().length];

  private void initLRTABLE() {
    LRTABLE[0][Vtype.ordinal()] = new LR('s', 5);
    LRTABLE[0][Class.ordinal()] = new LR('s', 6);
    LRTABLE[0][End.ordinal()] = new LR('r', 4);
    LRTABLE[0][CODE.ordinal()] = new LR('g', 1);
    LRTABLE[0][VDECL.ordinal()] = new LR('g', 2);
    LRTABLE[0][FDECL.ordinal()] = new LR('g', 3);
    LRTABLE[0][CDECL.ordinal()] = new LR('g', 4);

    LRTABLE[1][End.ordinal()] = new LR('r', 0);

    LRTABLE[2][Vtype.ordinal()] = new LR('s', 5);
    LRTABLE[2][Class.ordinal()] = new LR('s', 5);
    LRTABLE[2][End.ordinal()] = new LR('r', 4);
    LRTABLE[2][CODE.ordinal()] = new LR('g', 7);
    LRTABLE[2][VDECL.ordinal()] = new LR('g', 2);
    LRTABLE[2][FDECL.ordinal()] = new LR('g', 3);
    LRTABLE[2][CDECL.ordinal()] = new LR('g', 4);

    LRTABLE[3][Vtype.ordinal()] = new LR('s', 5);
    LRTABLE[3][Class.ordinal()] = new LR('s', 5);
    LRTABLE[3][End.ordinal()] = new LR('r', 4);
    LRTABLE[3][CODE.ordinal()] = new LR('g', 8);
    LRTABLE[3][VDECL.ordinal()] = new LR('g', 2);
    LRTABLE[3][FDECL.ordinal()] = new LR('g', 3);
    LRTABLE[3][CDECL.ordinal()] = new LR('g', 4);

    LRTABLE[4][Vtype.ordinal()] = new LR('s', 5);
    LRTABLE[4][Class.ordinal()] = new LR('s', 5);
    LRTABLE[4][End.ordinal()] = new LR('r', 4);
    LRTABLE[4][CODE.ordinal()] = new LR('g', 9);
    LRTABLE[4][VDECL.ordinal()] = new LR('g', 2);
    LRTABLE[4][FDECL.ordinal()] = new LR('g', 3);
    LRTABLE[4][CDECL.ordinal()] = new LR('g', 4);

    LRTABLE[5][Id.ordinal()] = new LR('s', 10);
    LRTABLE[5][ASSIGN.ordinal()] = new LR('g', 11);

    LRTABLE[6][Id.ordinal()] = new LR('s', 12);

    LRTABLE[7][End.ordinal()] = new LR('r', 1);

    LRTABLE[8][End.ordinal()] = new LR('r', 2);

    LRTABLE[9][End.ordinal()] = new LR('r', 3);

    LRTABLE[10][Semi.ordinal()] = new LR('s', 13);
    LRTABLE[10][Assign.ordinal()] = new LR('s', 15);
    LRTABLE[10][Lparen.ordinal()] = new LR('s', 14);

    LRTABLE[11][Semi.ordinal()] = new LR('s', 16);

    LRTABLE[12][Lbrace.ordinal()] = new LR('s', 17);

    LRTABLE[13][Vtype.ordinal()] = new LR('r', 5);
    LRTABLE[13][Id.ordinal()] = new LR('r', 5);
    LRTABLE[13][Rbrace.ordinal()] = new LR('r', 5);
    LRTABLE[13][If.ordinal()] = new LR('r', 5);
    LRTABLE[13][While.ordinal()] = new LR('r', 5);
    LRTABLE[13][Return.ordinal()] = new LR('r', 5);
    LRTABLE[13][Class.ordinal()] = new LR('r', 5);
    LRTABLE[13][End.ordinal()] = new LR('r', 5);

    LRTABLE[14][Vtype.ordinal()] = new LR('s', 19);
    LRTABLE[14][Rparen.ordinal()] = new LR('r', 22);
    LRTABLE[14][ARG.ordinal()] = new LR('g', 18);

    LRTABLE[15][Id.ordinal()] = new LR('s', 29);
    LRTABLE[15][Literal.ordinal()] = new LR('s', 22);
    LRTABLE[15][Character.ordinal()] = new LR('s', 23);
    LRTABLE[15][Boolstr.ordinal()] = new LR('s', 24);
    LRTABLE[15][Addsub.ordinal()] = new LR('s', 25);
    LRTABLE[15][Lparen.ordinal()] = new LR('s', 28);
    LRTABLE[15][Num.ordinal()] = new LR('s', 30);
    LRTABLE[15][RHS.ordinal()] = new LR('g', 20);
    LRTABLE[15][EXPR.ordinal()] = new LR('g', 21);
    LRTABLE[15][S1.ordinal()] = new LR('g', 26);
    LRTABLE[15][S2.ordinal()] = new LR('g', 27);

    LRTABLE[16][Vtype.ordinal()] = new LR('r', 6);
    LRTABLE[16][Id.ordinal()] = new LR('r', 6);
    LRTABLE[16][Rbrace.ordinal()] = new LR('r', 6);
    LRTABLE[16][If.ordinal()] = new LR('r', 6);
    LRTABLE[16][While.ordinal()] = new LR('r', 6);
    LRTABLE[16][Return.ordinal()] = new LR('r', 6);
    LRTABLE[16][Class.ordinal()] = new LR('r', 6);
    LRTABLE[16][End.ordinal()] = new LR('r', 6);

    LRTABLE[17][Vtype.ordinal()] = new LR('s', 5);
    LRTABLE[17][Rbrace.ordinal()] = new LR('r', 40);
    LRTABLE[17][VDECL.ordinal()] = new LR('g', 32);
    LRTABLE[17][FDECL.ordinal()] = new LR('g', 33);
    LRTABLE[17][ODECL.ordinal()] = new LR('g', 31);

    LRTABLE[18][Rparen.ordinal()] = new LR('s', 34);

    LRTABLE[19][Id.ordinal()] = new LR('s', 35);

    LRTABLE[20][Semi.ordinal()] = new LR('r', 7);

    LRTABLE[21][Semi.ordinal()] = new LR('r', 8);

    LRTABLE[22][Semi.ordinal()] = new LR('r', 9);

    LRTABLE[23][Semi.ordinal()] = new LR('r', 10);

    LRTABLE[24][Semi.ordinal()] = new LR('r', 11);

    LRTABLE[25][Id.ordinal()] = new LR('s', 29);
    LRTABLE[25][Addsub.ordinal()] = new LR('s', 25);
    LRTABLE[25][Lparen.ordinal()] = new LR('s', 28);
    LRTABLE[25][Num.ordinal()] = new LR('s', 30);
    LRTABLE[25][EXPR.ordinal()] = new LR('g', 36);
    LRTABLE[25][S1.ordinal()] = new LR('g', 26);
    LRTABLE[25][S2.ordinal()] = new LR('g', 27);

    LRTABLE[26][Semi.ordinal()] = new LR('r', 14);
    LRTABLE[26][Addsub.ordinal()] = new LR('s', 37);
    LRTABLE[26][Rparen.ordinal()] = new LR('r', 14);

    LRTABLE[27][Semi.ordinal()] = new LR('r', 16);
    LRTABLE[27][Addsub.ordinal()] = new LR('r', 16);
    LRTABLE[27][Multdiv.ordinal()] = new LR('s', 38);
    LRTABLE[27][Rparen.ordinal()] = new LR('r', 16);

    LRTABLE[28][Id.ordinal()] = new LR('s', 29);
    LRTABLE[28][Addsub.ordinal()] = new LR('s', 25);
    LRTABLE[28][Lparen.ordinal()] = new LR('s', 28);
    LRTABLE[28][Num.ordinal()] = new LR('s', 30);
    LRTABLE[28][EXPR.ordinal()] = new LR('g', 39);
    LRTABLE[28][S1.ordinal()] = new LR('g', 26);
    LRTABLE[28][S2.ordinal()] = new LR('g', 27);

    LRTABLE[29][Semi.ordinal()] = new LR('r', 18);
    LRTABLE[29][Addsub.ordinal()] = new LR('r', 18);
    LRTABLE[29][Multdiv.ordinal()] = new LR('r', 18);
    LRTABLE[29][Rparen.ordinal()] = new LR('r', 18);

    LRTABLE[30][Semi.ordinal()] = new LR('r', 19);
    LRTABLE[30][Addsub.ordinal()] = new LR('r', 19);
    LRTABLE[30][Multdiv.ordinal()] = new LR('r', 19);
    LRTABLE[30][Rparen.ordinal()] = new LR('r', 19);

    LRTABLE[31][Rbrace.ordinal()] = new LR('s', 40);

    LRTABLE[32][Vtype.ordinal()] = new LR('s', 5);
    LRTABLE[32][Rbrace.ordinal()] = new LR('r', 40);
    LRTABLE[32][VDECL.ordinal()] = new LR('g', 32);
    LRTABLE[32][FDECL.ordinal()] = new LR('g', 33);
    LRTABLE[32][ODECL.ordinal()] = new LR('g', 41);

    LRTABLE[33][Vtype.ordinal()] = new LR('s', 5);
    LRTABLE[33][Rbrace.ordinal()] = new LR('r', 40);
    LRTABLE[33][VDECL.ordinal()] = new LR('g', 32);
    LRTABLE[33][FDECL.ordinal()] = new LR('g', 33);
    LRTABLE[33][ODECL.ordinal()] = new LR('g', 42);

    LRTABLE[34][Lbrace.ordinal()] = new LR('s', 43);

    LRTABLE[35][Rparen.ordinal()] = new LR('r', 24);
    LRTABLE[35][Comma.ordinal()] = new LR('s', 45);
    LRTABLE[35][MOREARGS.ordinal()] = new LR('g', 44);

    LRTABLE[36][Semi.ordinal()] = new LR('r', 12);
    LRTABLE[36][Rparen.ordinal()] = new LR('r', 12);

    LRTABLE[37][Id.ordinal()] = new LR('s', 29);
    LRTABLE[37][Addsub.ordinal()] = new LR('s', 25);
    LRTABLE[37][Lparen.ordinal()] = new LR('s', 28);
    LRTABLE[37][Num.ordinal()] = new LR('s', 30);
    LRTABLE[37][EXPR.ordinal()] = new LR('g', 46);
    LRTABLE[37][S1.ordinal()] = new LR('g', 26);
    LRTABLE[37][S2.ordinal()] = new LR('g', 27);

    LRTABLE[38][Id.ordinal()] = new LR('s', 29);
    LRTABLE[38][Lparen.ordinal()] = new LR('s', 28);
    LRTABLE[38][Num.ordinal()] = new LR('s', 30);
    LRTABLE[38][S1.ordinal()] = new LR('g', 47);
    LRTABLE[38][S2.ordinal()] = new LR('g', 27);

    LRTABLE[39][Rparen.ordinal()] = new LR('s', 48);

    LRTABLE[40][Vtype.ordinal()] = new LR('r', 37);
    LRTABLE[40][Class.ordinal()] = new LR('r', 37);
    LRTABLE[40][End.ordinal()] = new LR('r', 37);

    LRTABLE[41][Rbrace.ordinal()] = new LR('r', 38);

    LRTABLE[42][Rbrace.ordinal()] = new LR('r', 39);

    LRTABLE[43][Vtype.ordinal()] = new LR('s', 55);
    LRTABLE[43][Id.ordinal()] = new LR('s', 56);
    LRTABLE[43][Rbrace.ordinal()] = new LR('r', 26);
    LRTABLE[43][If.ordinal()] = new LR('s', 53);
    LRTABLE[43][While.ordinal()] = new LR('s', 54);
    LRTABLE[43][Return.ordinal()] = new LR('r', 26);
    LRTABLE[43][VDECL.ordinal()] = new LR('g', 51);
    LRTABLE[43][ASSIGN.ordinal()] = new LR('g', 52);
    LRTABLE[43][BLOCK.ordinal()] = new LR('g', 49);
    LRTABLE[43][STMT.ordinal()] = new LR('g', 50);

    LRTABLE[44][Rparen.ordinal()] = new LR('r', 21);

    LRTABLE[45][Vtype.ordinal()] = new LR('s', 57);

    LRTABLE[46][Semi.ordinal()] = new LR('r', 13);
    LRTABLE[46][Rparen.ordinal()] = new LR('r', 13);

    LRTABLE[47][Semi.ordinal()] = new LR('r', 15);
    LRTABLE[47][Addsub.ordinal()] = new LR('r', 15);
    LRTABLE[47][Rparen.ordinal()] = new LR('r', 15);

    LRTABLE[48][Semi.ordinal()] = new LR('r', 17);
    LRTABLE[48][Addsub.ordinal()] = new LR('r', 17);
    LRTABLE[48][Multdiv.ordinal()] = new LR('r', 17);
    LRTABLE[48][Rparen.ordinal()] = new LR('r', 15);

    LRTABLE[49][Return.ordinal()] = new LR('s', 59);
    LRTABLE[49][RETURN.ordinal()] = new LR('g', 58);

    LRTABLE[50][Vtype.ordinal()] = new LR('s', 55);
    LRTABLE[50][Id.ordinal()] = new LR('s', 56);
    LRTABLE[50][Rbrace.ordinal()] = new LR('r', 26);
    LRTABLE[50][If.ordinal()] = new LR('s', 53);
    LRTABLE[50][While.ordinal()] = new LR('s', 54);
    LRTABLE[50][Return.ordinal()] = new LR('r', 26);
    LRTABLE[50][VDECL.ordinal()] = new LR('g', 51);
    LRTABLE[50][ASSIGN.ordinal()] = new LR('g', 52);
    LRTABLE[50][BLOCK.ordinal()] = new LR('g', 60);
    LRTABLE[50][STMT.ordinal()] = new LR('g', 50);

    LRTABLE[51][Vtype.ordinal()] = new LR('r', 27);
    LRTABLE[51][Id.ordinal()] = new LR('r', 27);
    LRTABLE[51][Rbrace.ordinal()] = new LR('r', 27);
    LRTABLE[51][If.ordinal()] = new LR('r', 27);
    LRTABLE[51][While.ordinal()] = new LR('r', 27);
    LRTABLE[51][Return.ordinal()] = new LR('r', 27);

    LRTABLE[52][Semi.ordinal()] = new LR('s', 61);

    LRTABLE[53][Lparen.ordinal()] = new LR('s', 62);

    LRTABLE[54][Lparen.ordinal()] = new LR('s', 63);

    LRTABLE[55][Id.ordinal()] = new LR('s', 64);
    LRTABLE[55][ASSIGN.ordinal()] = new LR('g', 11);

    LRTABLE[56][Assign.ordinal()] = new LR('s', 15);

    LRTABLE[57][Id.ordinal()] = new LR('s', 65);

    LRTABLE[58][Rbrace.ordinal()] = new LR('s', 66);

    LRTABLE[59][Id.ordinal()] = new LR('s', 29);
    LRTABLE[59][Literal.ordinal()] = new LR('s', 22);
    LRTABLE[59][Character.ordinal()] = new LR('s', 23);
    LRTABLE[59][Boolstr.ordinal()] = new LR('s', 24);
    LRTABLE[59][Addsub.ordinal()] = new LR('s', 25);
    LRTABLE[59][Lparen.ordinal()] = new LR('s', 28);
    LRTABLE[59][Num.ordinal()] = new LR('s', 30);
    LRTABLE[59][RHS.ordinal()] = new LR('g', 67);
    LRTABLE[59][EXPR.ordinal()] = new LR('g', 21);
    LRTABLE[59][S1.ordinal()] = new LR('g', 26);
    LRTABLE[59][S2.ordinal()] = new LR('g', 27);

    LRTABLE[60][Rbrace.ordinal()] = new LR('r', 25);
    LRTABLE[60][Return.ordinal()] = new LR('r', 25);

    LRTABLE[61][Vtype.ordinal()] = new LR('r', 28);
    LRTABLE[61][Id.ordinal()] = new LR('r', 28);
    LRTABLE[61][Rbrace.ordinal()] = new LR('r', 28);
    LRTABLE[61][If.ordinal()] = new LR('r', 28);
    LRTABLE[61][While.ordinal()] = new LR('r', 28);
    LRTABLE[61][Return.ordinal()] = new LR('r', 28);

    LRTABLE[62][Boolstr.ordinal()] = new LR('s', 69);
    LRTABLE[62][COND.ordinal()] = new LR('g', 68);

    LRTABLE[63][Boolstr.ordinal()] = new LR('s', 69);
    LRTABLE[63][COND.ordinal()] = new LR('g', 70);

    LRTABLE[64][Semi.ordinal()] = new LR('s', 13);
    LRTABLE[64][Assign.ordinal()] = new LR('s', 15);

    LRTABLE[65][Rparen.ordinal()] = new LR('r', 24); /////////////////////////////////////
    LRTABLE[65][Comma.ordinal()] = new LR('s', 45);
    LRTABLE[65][MOREARGS.ordinal()] = new LR('g', 71);

    LRTABLE[66][Vtype.ordinal()] = new LR('r', 20);
    LRTABLE[66][Rbrace.ordinal()] = new LR('r', 20);
    LRTABLE[66][Class.ordinal()] = new LR('r', 20);
    LRTABLE[66][End.ordinal()] = new LR('r', 20);

    LRTABLE[67][Semi.ordinal()] = new LR('s', 72);

    LRTABLE[68][Rparen.ordinal()] = new LR('s', 73);

    LRTABLE[69][Rparen.ordinal()] = new LR('r', 33);
    LRTABLE[69][Comp.ordinal()] = new LR('s', 75);
    LRTABLE[69][S3.ordinal()] = new LR('g', 74);

    LRTABLE[70][Rparen.ordinal()] = new LR('s', 76);

    LRTABLE[71][Rparen.ordinal()] = new LR('r', 23);

    LRTABLE[72][Rbrace.ordinal()] = new LR('r', 36);

    LRTABLE[73][Lbrace.ordinal()] = new LR('s', 77);

    LRTABLE[74][Rparen.ordinal()] = new LR('r', 31);

    LRTABLE[75][Boolstr.ordinal()] = new LR('s', 69);
    LRTABLE[75][COND.ordinal()] = new LR('g', 78);

    LRTABLE[76][Lbrace.ordinal()] = new LR('s', 79);

    LRTABLE[77][Vtype.ordinal()] = new LR('s', 55);
    LRTABLE[77][Id.ordinal()] = new LR('s', 56);
    LRTABLE[77][Rbrace.ordinal()] = new LR('r', 26);
    LRTABLE[77][If.ordinal()] = new LR('s', 53);
    LRTABLE[77][While.ordinal()] = new LR('s', 54);
    LRTABLE[77][Return.ordinal()] = new LR('r', 26);
    LRTABLE[77][VDECL.ordinal()] = new LR('g', 51);
    LRTABLE[77][ASSIGN.ordinal()] = new LR('g', 52);
    LRTABLE[77][BLOCK.ordinal()] = new LR('g', 80);
    LRTABLE[77][STMT.ordinal()] = new LR('g', 50);

    LRTABLE[78][Rparen.ordinal()] = new LR('r', 32);

    LRTABLE[79][Vtype.ordinal()] = new LR('s', 55);
    LRTABLE[79][Id.ordinal()] = new LR('s', 56);
    LRTABLE[79][Rbrace.ordinal()] = new LR('r', 26);
    LRTABLE[79][If.ordinal()] = new LR('s', 53);
    LRTABLE[79][While.ordinal()] = new LR('s', 54);
    LRTABLE[79][Return.ordinal()] = new LR('r', 26);
    LRTABLE[79][VDECL.ordinal()] = new LR('g', 51);
    LRTABLE[79][ASSIGN.ordinal()] = new LR('g', 52);
    LRTABLE[79][BLOCK.ordinal()] = new LR('g', 81);
    LRTABLE[79][STMT.ordinal()] = new LR('g', 50);

    LRTABLE[80][Rbrace.ordinal()] = new LR('s', 82);

    LRTABLE[81][Rbrace.ordinal()] = new LR('s', 83);

    LRTABLE[82][Vtype.ordinal()] = new LR('r', 35);
    LRTABLE[82][Id.ordinal()] = new LR('r', 35);
    LRTABLE[82][Rbrace.ordinal()] = new LR('r', 35);
    LRTABLE[82][If.ordinal()] = new LR('r', 35);
    LRTABLE[82][While.ordinal()] = new LR('r', 35);
    LRTABLE[82][Else.ordinal()] = new LR('s', 85);
    LRTABLE[82][Return.ordinal()] = new LR('r', 35);
    LRTABLE[82][ELSE.ordinal()] = new LR('g', 84);

    LRTABLE[83][Vtype.ordinal()] = new LR('r', 30);
    LRTABLE[83][Id.ordinal()] = new LR('r', 30);
    LRTABLE[83][Rbrace.ordinal()] = new LR('r', 30);
    LRTABLE[83][If.ordinal()] = new LR('r', 30);
    LRTABLE[83][While.ordinal()] = new LR('r', 30);
    LRTABLE[83][Return.ordinal()] = new LR('r', 30);

    LRTABLE[84][Vtype.ordinal()] = new LR('r', 29);
    LRTABLE[84][Id.ordinal()] = new LR('r', 29);
    LRTABLE[84][Rbrace.ordinal()] = new LR('r', 29);
    LRTABLE[84][If.ordinal()] = new LR('r', 29);
    LRTABLE[84][While.ordinal()] = new LR('r', 29);
    LRTABLE[84][Return.ordinal()] = new LR('r', 29);

    LRTABLE[85][Lbrace.ordinal()] = new LR('s', 86);

    LRTABLE[86][Vtype.ordinal()] = new LR('s', 55);
    LRTABLE[86][Id.ordinal()] = new LR('s', 56);
    LRTABLE[86][Rbrace.ordinal()] = new LR('r', 26);
    LRTABLE[86][If.ordinal()] = new LR('s', 53);
    LRTABLE[86][While.ordinal()] = new LR('s', 54);
    LRTABLE[86][Return.ordinal()] = new LR('r', 26);
    LRTABLE[86][VDECL.ordinal()] = new LR('g', 51);
    LRTABLE[86][ASSIGN.ordinal()] = new LR('g', 52);
    LRTABLE[86][BLOCK.ordinal()] = new LR('g', 87);
    LRTABLE[86][STMT.ordinal()] = new LR('g', 50);

    LRTABLE[87][Rbrace.ordinal()] = new LR('s', 88);

    LRTABLE[88][Vtype.ordinal()] = new LR('r', 34);
    LRTABLE[88][Id.ordinal()] = new LR('r', 34);
    LRTABLE[88][Rbrace.ordinal()] = new LR('r', 34);
    LRTABLE[88][If.ordinal()] = new LR('r', 34);
    LRTABLE[88][While.ordinal()] = new LR('r', 34);
    LRTABLE[88][Return.ordinal()] = new LR('r', 34);
  }


  public boolean isValid(List<Token2> argSymbols) {
    List<Token2> symbols = argSymbols.stream().map(Token2::clone).collect(Collectors.toCollection(LinkedList::new));
    symbols.add(new Token2(End));
    Stack<Integer> stack = new Stack<>() {{
      push(0);
    }};
    int pos = 0;

    while (pos != symbols.size()) {

      LR lr = LRTABLE[stack.peek()][symbols.get(pos).key.ordinal()];
      System.out.println("[LOG]");
      System.out.println("Current state: " + stack.peek());
      System.out.print("Next Input symbol: " + symbols.get(pos).key);
      try {
        System.out.println(symbols.get(pos).value.value.isBlank() ? "" : " (" + symbols.get(pos).value.value + ")");
      } catch (NullPointerException e) {
        System.out.println();
      }

      if (lr == null) {
        System.out.println("[ERROR] " + "invalid syntax in line " + symbols.get(pos).value.lineNum);
        return false;
      }

      if (lr.action == 's') {
        System.out.println("Shift and Goto " + lr.number);
        stack.push(lr.number);
        pos++;
      }
      else if (lr.action == 'r') {
        System.out.println("Reduce by " + lr.number + " (" + SLR.get(lr.number).left.toString() + "->" + SLR.get(lr.number).right.toString() + ")");

        System.out.print("Pop ");
        for (State2 state : SLR.get(lr.number).right) {
          symbols.remove(--pos);
          System.out.print(stack.peek() + " ");
          stack.pop();
        }
        System.out.println();
        symbols.add(pos, new Token2(SLR.get(lr.number).left));
        pos++;

        if (symbols.get(pos - 1).key == CCODE) return true;

        lr = LRTABLE[stack.peek()][SLR.get(lr.number).left.ordinal()];
        stack.push(lr.number);
        System.out.println("Goto(Push) " + lr.number);
      }
      else if (lr.action == 'g') {
        System.out.println("Goto " + lr.number);
        stack.push(lr.number);
      }

      System.out.println("Stack: " + stack);
      System.out.print("Left substring: ");
      for (int i = 0; i < pos; i++) System.out.print(symbols.get(i).key + " ");
      System.out.println("\n");
    }
    return false;
  }
}
