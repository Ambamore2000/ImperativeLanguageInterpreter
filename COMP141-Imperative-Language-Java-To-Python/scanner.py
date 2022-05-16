import re

from token_type import TokenType

identifier_regex = "([a-zA-Z])([a-zA-Z] | \d)*"
number_regex = "(\d)+"
symbol_regex = "[\+|\-|\*|/|\(|\)|:=|;]+"
whitespace_regex = "[\s]"

token_list = []

def scan(input_string, output_string):
    global token_list_array
    global token_list_array_index

    current_token = ""
    current_token_type = TokenType.none

    lines = []

    with open(input_string) as input_file:
        lines = input_file.readlines()
    output_file = open(output_string, "w")

    for line in lines:
        output_file.write("Line: " + line)
        for new_char in line:
            is_whitespace = re.search(whitespace_regex, new_char)

            is_char_identifier = re.search(identifier_regex, new_char)
            is_char_number = re.search(number_regex, new_char)
            is_char_symbol = re.search(symbol_regex, new_char)

            is_new_token = False
            is_error = False

            if (is_char_identifier):
                if (current_token_type == TokenType.none):
                    current_token_type = TokenType.identifier
                if (current_token_type != TokenType.identifier):
                    is_new_token = True
            elif (is_char_number):
                if (current_token_type == TokenType.none):
                    current_token_type = TokenType.number
                if (current_token_type != TokenType.number and current_token_type != TokenType.identifier):
                    is_new_token = True
            elif (is_char_symbol):
                if (current_token_type == TokenType.none):
                    current_token_type = TokenType.symbol
                if (current_token_type != TokenType.symbol):
                    is_new_token = True
            elif (is_whitespace and current_token_type):
                is_new_token = True
            else:
                is_error = True

            if (is_whitespace or is_new_token or is_error):
                if (current_token_type != TokenType.whitespace):

                    if (current_token == "if" or current_token == "then" or 
                    current_token == "else" or current_token == "endif" or 
                    current_token == "while" or current_token == "do" or 
                    current_token == "endwhile" or current_token == "skip"):
                        current_token_type = TokenType.keyword

                    token_list.append(current_token + " : " + current_token_type.value)
                    output_file.write(current_token + " : " + current_token_type.value + "\n")
                    current_token = ""
                    current_token_type = TokenType.whitespace
                if (is_error):
                    output_file.write("ERROR READING '" + new_char + "'\n")
                    break
                if (is_whitespace):
                    continue
            
            current_token += new_char

            is_token_identifier = re.search(identifier_regex, current_token)
            is_token_number = re.search(number_regex, current_token)
            is_token_symbol = re.search(symbol_regex, current_token)
            if (is_token_identifier):
                current_token_type = TokenType.identifier
            elif (is_token_number):
                current_token_type = TokenType.number
            elif (is_token_symbol):
                current_token_type = TokenType.symbol
            
        output_file.write("\n")
        
    output_file.close()

    return token_list