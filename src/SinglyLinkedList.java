/**
 * Singly Linked List buatan sendiri (materi ASD-02) yang menyimpan bilangan bulat.
 * Dipakai sebagai adjacency list pada graph dan sebagai penampung hasil penelusuran.
 * Tidak memakai java.util.LinkedList / ArrayList.
 */
public class SinglyLinkedList {
    private Node head, tail;
    private int size = 0;

    /** Inisialisasi: head = tail = null, list kosong. */
    public SinglyLinkedList() {
        head = tail = null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public Node getHead() {
        return head;
    }

    /** Penambahan dari belakang (addLast). Hanya tail yang bergerak. */
    public void addLast(int data) {
        Node input = new Node(data);
        if (isEmpty()) {
            head = input;
            tail = input;
        } else {
            tail.pointer = input;
            tail = input;
        }
        size++;
    }

    /** Pencarian sequential: true jika data ada di list. */
    public boolean contains(int data) {
        Node p = head;
        while (p != null) {
            if (p.data == data) {
                return true;
            }
            p = p.pointer;
        }
        return false;
    }

    /** Pengaksesan berdasarkan index (method get pada materi ASD-02). */
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index = " + index + " size = " + size);
        }
        Node currentNode = head;
        for (int i = 0; i < index; i++) {
            currentNode = currentNode.pointer;
        }
        return currentNode.data;
    }
}
