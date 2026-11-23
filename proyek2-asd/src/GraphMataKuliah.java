/**
 * Directed graph (digraph) mata kuliah, direpresentasikan dengan ADJACENCY LIST (materi ASD-11).
 *
 * - Vertex : mata kuliah (disimpan di array vertexList, indeks 0..nVerts-1).
 * - Edge   : (v, u) artinya "MK v membutuhkan prasyarat MK u".
 *            Contoh: baris "KAL;AL" pada prasyarat.csv -> edge (AL, KAL).
 * - adjList[v] : singly linked list berisi indeks seluruh prasyarat langsung dari v.
 * - indeksKode : hash table (separate chaining) untuk mencari indeks vertex dari kode MK
 *                dalam waktu rata-rata O(1).
 *
 * Kapasitas array diperbesar dengan array doubling jika penuh.
 */
public class GraphMataKuliah {
    private MataKuliah[] vertexList;    // data setiap vertex
    private SinglyLinkedList[] adjList; // adjacency list: prasyarat langsung tiap vertex
    private int nVerts;                 // jumlah vertex
    private HashTable indeksKode;       // kode MK -> indeks vertex

    public GraphMataKuliah() {
        vertexList = new MataKuliah[10];
        adjList = new SinglyLinkedList[10];
        nVerts = 0;
        indeksKode = new HashTable();
    }

    /** Penambahan vertex. Mengembalikan indeks vertex, atau -1 jika kode sudah ada. */
    public int addVertex(MataKuliah mk) {
        if (indeksKode.containsKey(mk.kode)) {
            return -1;
        }
        if (nVerts == vertexList.length) {
            arrayDoubling();
        }
        vertexList[nVerts] = mk;
        adjList[nVerts] = new SinglyLinkedList();
        indeksKode.put(mk.kode, nVerts);
        nVerts++;
        return nVerts - 1;
    }

    /**
     * Penambahan edge berarah: MK v membutuhkan prasyarat MK u.
     * Edge ganda diabaikan. Mengembalikan false jika edge tidak ditambahkan.
     */
    public boolean addEdge(int v, int u) {
        if (v == u || adjList[v].contains(u)) {
            return false;               // self-loop / edge ganda diabaikan
        }
        adjList[v].addLast(u);          // edge: v -> u (v membutuhkan u)
        return true;
    }

    /** Mencari indeks vertex dari kode MK (lewat hash table). -1 jika tidak ada. */
    public int indexOf(String kode) {
        return indeksKode.get(kode);
    }

    public MataKuliah getVertex(int v) {
        return vertexList[v];
    }

    public SinglyLinkedList getPrasyarat(int v) {
        return adjList[v];
    }

    public int jumlahVertex() {
        return nVerts;
    }

    /**
     * DEPTH FIRST SEARCH (rekursif) dari vertex v menelusuri rantai prasyarat
     * untuk mengumpulkan semua MK prasyarat yang BELUM lulus.
     *
     * - Vertex yang sudah lulus tidak ditelusuri lebih dalam (prasyaratnya dianggap terpenuhi).
     * - Array visited mencegah vertex dikunjungi dua kali (dan aman terhadap siklus).
     * - Vertex dimasukkan ke hasil SETELAH prasyaratnya ditelusuri (postorder),
     *   sehingga urutan hasil dimulai dari prasyarat paling dasar.
     *   Contoh: PAW -> IMK -> SO  menghasilkan "SO, IMK".
     */
    public void dfsPrasyaratBelumLulus(int v, boolean[] lulus, boolean[] visited,
                                       SinglyLinkedList hasil) {
        Node p = adjList[v].getHead();
        while (p != null) {
            int u = p.data;
            if (!visited[u]) {
                visited[u] = true;                                    // tandai
                if (!lulus[u]) {
                    dfsPrasyaratBelumLulus(u, lulus, visited, hasil); // telusuri lebih dalam
                    hasil.addLast(u);                                 // post-order
                }
            }
            p = p.pointer;
        }
    }

    /** Array doubling (materi Stack/Queue) untuk memperbesar kapasitas vertex. */
    private void arrayDoubling() {
        MataKuliah[] oldVertex = vertexList;
        SinglyLinkedList[] oldAdj = adjList;
        vertexList = new MataKuliah[oldVertex.length * 2];
        adjList = new SinglyLinkedList[oldAdj.length * 2];
        for (int i = 0; i < nVerts; i++) {
            vertexList[i] = oldVertex[i];
            adjList[i] = oldAdj[i];
        }
    }
}
