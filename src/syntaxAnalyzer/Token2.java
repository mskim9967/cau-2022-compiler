package syntaxAnalyzer;

import lexicalAnalyzer.Token;

/**
 * Define token2
 */
public class Token2 implements Cloneable {
  public State2 key;
  public Token value;

  public Token2(State2 key) {
    this.key = key;
    this.value = null;
  }

  public Token2(State2 key, Token value) {
    this.key = key;
    this.value = value;
  }


  @Override
  public Token2 clone() {
    Token2 clone = null;
    try {
      clone = (Token2) super.clone();
      clone.value = value.clone();

    } catch (CloneNotSupportedException e) {
    }
    return clone;
  }

}

