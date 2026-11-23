/**
 * Node untuk Singly Linked List (materi ASD-02).
 * Field 1 = data (indeks vertex mata kuliah), field 2 = pointer ke node berikutnya.
 */
public class Node {
    int data;
    Node pointer;

    Node(int data) {
        this.data = data;
        this.pointer = null;
    }
}
