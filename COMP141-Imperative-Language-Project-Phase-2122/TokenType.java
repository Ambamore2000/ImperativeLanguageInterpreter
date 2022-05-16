public enum TokenType {

    IDENTIFIER("IDENTIFIER"),
    NUMBER("NUMBER"),
    SYMBOL("SYMBOL"),
    KEYWORD("KEYWORD"),
    NULL("NULL");

    private String s;
    TokenType(String s) { this.s = s; }

    public String toString() { return s; }
    
}