/**
 * Hash table buatan sendiri dengan Open Hashing / Separate Chaining (materi ASD-14).
 * Key   : kode mata kuliah (String), misal "ASD"
 * Value : indeks vertex mata kuliah pada graph (int)
 *
 * Setiap sel tabel berisi pointer ke sebuah linked list (rantai) HashEntry.
 * Tabel di-rehash ke ukuran prima berikutnya jika load factor > 1 (aturan open hashing).
 */
public class HashTable {

    /** Node rantai (chaining): menyimpan pasangan key-value dan pointer next. */
    private static class HashEntry {
        String key;
        int value;
        HashEntry next;

        HashEntry(String key, int value, HashEntry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private HashEntry[] table;
    private int currentSize = 0;

    public HashTable() {
        table = new HashEntry[11]; // ukuran awal bilangan prima
    }

    /**
     * Fungsi hash untuk String (materi ASD-14):
     * hashVal = (hashVal * 31 + charAt(i)) % tableSize, modulo setiap langkah
     * agar tidak overflow. Hasil selalu di rentang [0, tableSize-1].
     */
    private int hash(String key, int tableSize) {
        int hashVal = 0;
        for (int i = 0; i < key.length(); i++) {
            hashVal = (hashVal * 31 + key.charAt(i)) % tableSize;
        }
        return hashVal;
    }

    /** Insert / update. Jika key sudah ada, nilai diperbarui (key harus unik). */
    public void put(String key, int value) {
        int pos = hash(key, table.length);
        HashEntry p = table[pos];
        while (p != null) {
            if (p.key.equals(key)) {
                p.value = value;
                return;
            }
            p = p.next;
        }
        // tambahkan di depan rantai (seperti addFirst)
        table[pos] = new HashEntry(key, value, table[pos]);
        currentSize++;
        if (currentSize > table.length) { // load factor > 1
            rehash();
        }
    }

    /** Find: mengembalikan value, atau -1 jika key tidak ditemukan. */
    public int get(String key) {
        int pos = hash(key, table.length);
        HashEntry p = table[pos];
        while (p != null) {
            if (p.key.equals(key)) {
                return p.value;
            }
            p = p.next;
        }
        return -1;
    }

    public boolean containsKey(String key) {
        return get(key) != -1;
    }

    public int size() {
        return currentSize;
    }

    /** Rehash: buat tabel baru berukuran prima >= 2 x ukuran lama lalu masukkan ulang. */
    private void rehash() {
        HashEntry[] oldTable = table;
        table = new HashEntry[nextPrime(2 * oldTable.length)];
        currentSize = 0;
        for (int i = 0; i < oldTable.length; i++) {
            HashEntry p = oldTable[i];
            while (p != null) {
                put(p.key, p.value);
                p = p.next;
            }
        }
    }

    private static int nextPrime(int n) {
        while (!isPrime(n)) {
            n++;
        }
        return n;
    }

    private static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
