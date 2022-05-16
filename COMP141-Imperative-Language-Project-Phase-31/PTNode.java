public class PTNode {
    String value;
    String type;
    PTNode left;
    PTNode middle;
    PTNode right;

    PTNode(String value) {
        this.value = value;
        right = null;
        middle = null;
        left = null;
    }

    PTNode(String value, PTNode left, PTNode right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    PTNode(String value, PTNode left, PTNode middle, PTNode right) {
        this.value = value;
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    
}