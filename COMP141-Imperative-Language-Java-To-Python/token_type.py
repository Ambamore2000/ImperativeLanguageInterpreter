import enum
import sys

class TokenType(enum.Enum):
    identifier = "IDENTIFIER"
    number = "NUMBER"
    symbol = "SYMBOL"
    keyword = "KEYWORD"
    whitespace = "WHITESPACE"
    none = None