from parser import parse

stack = []
map = []

def evaluate(input_string, output_string):
    global stack
    global map

    tree = parse(input_string, output_string)
    
    output_file = open(output_string, "a")

    push_evaluator(tree)

    output_file.write("Output: " + stack[0])

    output_file.close()

    return

def push_evaluator(node):
    if (node == None):
        return
    
    push_to_stack(node.value)

    push_evaluator(node.left)

    if (node.middle != None):
        push_evaluator(node.middle)
    
    push_evaluator(node.right)

def reevaluate():
    global stack
    is_evaluatable = len(stack) >= 3
    while (is_evaluatable == True):
        first_val = stack[len(stack) - 1]
        second_val = stack[len(stack) - 2]
        third_val = stack[len(stack) - 3]

        try:
            int(second_val)
            int(first_val)
        except ValueError:
            break

        if (third_val == "+"):
            stack.pop()
            stack.pop()
            stack.pop()
            stack.append(str(int(second_val) + int(first_val)))
        elif (third_val == "*"):
            stack.pop()
            stack.pop()
            stack.pop()
            stack.append(str(int(second_val) * int(first_val)))
        elif (third_val == "-"):
            stack.pop()
            stack.pop()
            stack.pop()
            stack.append(str( int(second_val) - int(first_val)))
        elif (third_val == "/"):
            stack.pop()
            stack.pop()
            stack.pop()
            stack.append(str(int(int(second_val) / int(first_val))))
        
        if (len(stack) < 3):
            break

def push_to_stack(value):
    global stack
    stack.append(value)
    reevaluate()    