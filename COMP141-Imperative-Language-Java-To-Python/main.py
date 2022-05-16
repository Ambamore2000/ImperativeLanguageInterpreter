import sys

from scanner import scan
from parser import parse
from evaluator import evaluate

if __name__ == "__main__":
    
    if (len(sys.argv) == 3):
        input_string = sys.argv[1]
        output_string = sys.argv[2]
    else:
        input_string = "input.txt"
        output_string = "output.txt"

    evaluate(input_string, output_string)