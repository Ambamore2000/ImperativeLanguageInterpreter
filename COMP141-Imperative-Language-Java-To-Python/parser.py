from token_type import TokenType

class PTNode:

    def __init__(self, v, t, l=None, r=None, m=None):
        self.value = v
        self.type = t
        self.left = l
        self.right = r
        self.middle = m

from scanner import scan

token_list = []
token_index = 0

next_token = None
next_token_type = TokenType.none

output_file = None

def parse(input_string, output_string):
    global token_list
    global output_file
    global token_index

    token_list = scan(input_string, output_string)
    
    output_file = open(output_string, "w")

    output_file.write("Tokens:\n\n")

    for token in token_list:
        output_file.write(token + "\n")
        
    output_file.write("\nAST:\n\n")

    consume_token()
    tree = parseExpression()

    print_preorder(tree, 0)

    output_file.close()

    return tree

def print_preorder(node, tab):
    global output_file

    if (node == None):
        return
    
    for x in range(int(tab)):
        output_file.write("\t")
    
    if (node.type == ""):
        output_file.write(node.value + "\n")
    else:
        output_file.write(node.value + " : " + str(node.type) + "\n")
    
    print_preorder(node.left, tab + 1)

    if (node.middle != None):
        print_preorder(node.middle, tab + 1)

    print_preorder(node.right, tab + 1)

def consume_token():
    global token_list
    global token_index
    global next_token
    global next_token_type

    if (token_index == len(token_list)):
        return

    tokens_split = token_list[token_index].split(" : ")
    
    next_token = tokens_split[0]
    if (tokens_split[1] == "IDENTIFIER"):
        next_token_type = TokenType.identifier
    elif (tokens_split[1] == "NUMBER"):
        next_token_type = TokenType.number
    elif (tokens_split[1] == "SYMBOL"):
        next_token_type = TokenType.symbol
    elif (tokens_split[1] == "KEYWORD"):
        next_token_type = TokenType.keyword
    
    token_index += 1

def parseStatement():
    global next_token

    tree = parseBaseStatement()

    while (next_token == ";"):
        consume_token()
        tree = PTNode(";", "SYMBOL", tree, parseBaseStatement())

    return tree

def parseBaseStatement():
    global next_token

    if (next_token == "if"):
        tree = parseIfStatement()
    elif (next_token == "while"):
        tree = parseWhileStatement()
    elif (next_token == "skip"):
        consume_token()
        tree = PTNode("skip", "SKIP")
    else:
        tree = parseAssignment()
    
    return tree

def parseAssignment():
    global next_token

    tree = PTNode(next_token, next_token_type)
    consume_token()

    if (next_token == ":="):
        consume_token()

        tree = PTNode(":=", "SYMBOL", tree, parseExpression())
        tree.left.type = "IDENTIFIER"
        
    return tree

def parseIfStatement():
    global next_token
    
    tree = None

    if (next_token == "if"):
        consume_token()
        expression = parseExpression()

        if (next_token == "then"):
            consume_token()
            thenStatement = parseStatement()

            if (next_token == "else"):
                consume_token()
                elseStatement = parseStatement()

                if (next_token == "endif"):
                    tree = PTNode("IF-STATEMENT", "", expression, elseStatement, thenStatement,)
    return tree

def parseWhileStatement():
    global next_token

    tree = None

    if (next_token == "while"):
        consume_token()
        expression = parseExpression()

        if (next_token == "do"):
            consume_token()
            statement = parseStatement()

            if (next_token == "endwhile"):
                tree = PTNode("WHILE-LOOP", "", expression, statement)
    
    return tree

def parseExpression():
    global next_token

    tree = parseTerm()

    while (next_token == "+"):
        consume_token()
        tree = PTNode("+", "SYMBOL", tree, parseTerm())
    
    return tree

def parseTerm():
    global next_token

    tree = parseFactor()

    while (next_token == "-"):
        consume_token()
        tree = PTNode("-", "SYMBOL", tree, parseFactor())
    
    return tree

def parseFactor():
    global next_token

    tree = parsePiece()

    while (next_token == "/"):
        consume_token()
        tree = PTNode("/", "SYMBOL", tree, parsePiece())
    
    return tree

def parsePiece():
    global next_token

    tree = parseElement()

    while (next_token == "*"):
        consume_token()
        tree = PTNode("*", "SYMBOL", tree, parseElement())
    
    return tree

def parseElement():
    global next_token

    if (next_token == "("):
        consume_token()
        tree = parseExpression()
        if (next_token == ")"):
            consume_token()
            return tree
        else:
            print("PARSER: Could not parse '" + next_token + "'")
            exit(5)
    elif (next_token_type == TokenType.identifier):
        return parseIdentifier()
    elif (next_token_type == TokenType.number):
        return parseNumber()
    else:
        print("PARSER: Could not parse '" + next_token + "'")
        exit(5)

def parseIdentifier():
    n = next_token
    consume_token()
    tree = PTNode(n, "IDENTIFIER")
    return tree

def parseNumber():
    n = next_token
    consume_token()
    tree = PTNode(n, "NUMBER")
    return tree

