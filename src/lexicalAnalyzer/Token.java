package lexicalAnalyzer;

/**
 * Define token(key-token name, value-lexeme)
 */
public class Token implements Cloneable {
    public String key, value;
    public int lineNum;

    public Token(String key, String value, int lineNum) {
        this.key = key;
        this.value = value;
        this.lineNum = lineNum;
    }

    @Override
    public Token clone() throws CloneNotSupportedException {
        return (Token) super.clone();
    }
}

