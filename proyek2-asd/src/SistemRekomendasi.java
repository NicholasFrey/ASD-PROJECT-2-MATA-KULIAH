import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * PROYEK 2 ALGORITMA DAN STRUKTUR DATA
 * Sistem rekomendasi pengambilan mata kuliah semester berikutnya.
 *
 * Input  : matakuliah.csv, prasyarat.csv, mhs_xx.txt
 * Output : daftar MK yang bisa diambil dan daftar MK yang belum bisa diambil beserta alasannya.
 *
 * Struktur data utama: Graph berarah dengan adjacency list (buatan sendiri),
 * dibantu Hash Table (separate chaining) dan Singly Linked List (buatan sendiri).
 * Tidak memakai struktur data bawaan Java (ArrayList, HashMap, TreeMap, dll).
 *
 * Cara menjalankan:
 *   javac -d out src/*.java
 *   java -cp out SistemRekomendasi data/matakuliah.csv data/prasyarat.csv data/mhs_01.txt
 */
public class SistemRekomendasi {

    /** Kode MK Skripsi: hanya bisa diambil setelah semua MK wajib diambil (lulus). */
    static final String KODE_SKRIPSI = "SKR";

    private final GraphMataKuliah graph = new GraphMataKuliah();
    private boolean[] lulus;
    private String semesterBerikutnya;

    public static void main(String[] args) {
        String fileMK = "data/matakuliah.csv";
        String filePrasyarat = "data/prasyarat.csv";
        String fileMhs = "data/mhs_01.txt";

        if (args.length == 3) {
            fileMK = args[0];
            filePrasyarat = args[1];
            fileMhs = args[2];
        } else if (args.length == 1) {
            fileMhs = args[0];
        } else if (args.length != 0) {
            System.out.println("Penggunaan:");
            System.out.println("  java -cp out SistemRekomendasi <matakuliah.csv> <prasyarat.csv> <mhs_xx.txt>");
            System.out.println("  java -cp out SistemRekomendasi <mhs_xx.txt>   (csv default di folder data/)");
            System.exit(1);
        }

        SistemRekomendasi sistem = new SistemRekomendasi();
        try {
            sistem.bacaMataKuliah(fileMK);
            sistem.bacaPrasyarat(filePrasyarat);
            sistem.bacaMahasiswa(fileMhs);
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.println("Format file tidak valid: " + e.getMessage());
            System.exit(1);
        }
        sistem.prosesDanCetak();
    }

    // ===================================================================
    // 1. MEMBACA FILE INPUT
    // ===================================================================

    /** Membuka file teks UTF-8. */
    private static BufferedReader buka(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
    }

    /** Membersihkan baris: hapus BOM (jika ada) dan spasi/CR di awal-akhir. */
    private static String bersihkan(String baris) {
        if (baris.length() > 0 && baris.charAt(0) == '﻿') {
            baris = baris.substring(1);
        }
        return baris.trim();
    }

    /** matakuliah.csv -> setiap baris menjadi satu vertex graph. */
    void bacaMataKuliah(String path) throws IOException {
        BufferedReader br = buka(path);
        String baris;
        boolean header = true;
        int nomorBaris = 0;
        while ((baris = br.readLine()) != null) {
            nomorBaris++;
            baris = bersihkan(baris);
            if (baris.isEmpty()) {
                continue;
            }
            if (header) { // baris pertama: Kode;Nama;sks;semester;ganjil/genap
                header = false;
                continue;
            }
            String[] f = baris.split(";");
            if (f.length < 5) {
                br.close();
                throw new IllegalArgumentException(path + " baris " + nomorBaris + ": kolom kurang dari 5");
            }
            String penawaran = f[4].trim();
            if (!penawaran.equalsIgnoreCase("Ganjil") && !penawaran.equalsIgnoreCase("Genap")
                    && !penawaran.equalsIgnoreCase("Keduanya")) {
                br.close();
                throw new IllegalArgumentException(path + " baris " + nomorBaris
                        + ": kolom ganjil/genap harus Ganjil, Genap, atau Keduanya");
            }
            MataKuliah mk;
            try {
                mk = new MataKuliah(f[0].trim().toUpperCase(), f[1].trim(),
                        Integer.parseInt(f[2].trim()), Integer.parseInt(f[3].trim()), penawaran);
            } catch (NumberFormatException e) {
                br.close();
                throw new IllegalArgumentException(path + " baris " + nomorBaris + ": sks/semester bukan angka");
            }
            if (graph.addVertex(mk) == -1) {
                System.err.println("Peringatan: kode MK " + mk.kode + " ganda, baris " + nomorBaris + " diabaikan.");
            }
        }
        br.close();
    }

    /** prasyarat.csv -> setiap baris "prasyarat;matakuliah" menjadi edge (matakuliah, prasyarat). */
    void bacaPrasyarat(String path) throws IOException {
        BufferedReader br = buka(path);
        String baris;
        boolean header = true;
        int nomorBaris = 0;
        while ((baris = br.readLine()) != null) {
            nomorBaris++;
            baris = bersihkan(baris);
            if (baris.isEmpty()) {
                continue;
            }
            if (header) { // baris pertama: prasyarat;matakuliah
                header = false;
                continue;
            }
            String[] f = baris.split(";");
            if (f.length < 2) {
                System.err.println("Peringatan: " + path + " baris " + nomorBaris + " tidak valid, diabaikan.");
                continue;
            }
            String kodePrasyarat = f[0].trim().toUpperCase();
            String kodeMK = f[1].trim().toUpperCase();
            int u = graph.indexOf(kodePrasyarat); // cari indeks lewat hash table
            int v = graph.indexOf(kodeMK);
            if (u == -1 || v == -1) {
                System.err.println("Peringatan: " + path + " baris " + nomorBaris
                        + " memuat kode MK yang tidak ada di matakuliah.csv, diabaikan.");
                continue;
            }
            graph.addEdge(v, u);                  // edge: MK -> prasyarat
        }
        br.close();
    }

    /** mhs_xx.txt -> semester berikutnya (ganjil/genap) dan daftar MK yang sudah lulus. */
    void bacaMahasiswa(String path) throws IOException {
        lulus = new boolean[graph.jumlahVertex()];
        BufferedReader br = buka(path);
        String baris;
        while ((baris = br.readLine()) != null) {
            baris = bersihkan(baris);
            int sama = baris.indexOf('=');
            if (sama == -1) {
                continue;
            }
            String kunci = baris.substring(0, sama).trim().toLowerCase();
            String nilai = baris.substring(sama + 1).trim();

            if (kunci.equals("semester_berikutnya")) {
                semesterBerikutnya = nilai.toLowerCase();
            } else if (kunci.equals("lulus")) {
                String[] daftar = nilai.split(",");
                for (int i = 0; i < daftar.length; i++) {
                    String kode = daftar[i].trim().toUpperCase();
                    if (kode.isEmpty()) {
                        continue;
                    }
                    int idx = graph.indexOf(kode);
                    if (idx == -1) {
                        System.err.println("Peringatan: MK lulus \"" + kode + "\" tidak ada di matakuliah.csv, diabaikan.");
                    } else {
                        lulus[idx] = true;
                    }
                }
            }
        }
        br.close();
        if (semesterBerikutnya == null
                || (!semesterBerikutnya.equals("ganjil") && !semesterBerikutnya.equals("genap"))) {
            throw new IllegalArgumentException(path + ": semester_berikutnya harus ganjil atau genap");
        }
    }

    // ===================================================================
    // 2. MENENTUKAN MK YANG BISA / BELUM BISA DIAMBIL
    // ===================================================================

    void prosesDanCetak() {
        int n = graph.jumlahVertex();
        int[] bisa = new int[n];
        int jumlahBisa = 0;
        int[] belum = new int[n];
        SinglyLinkedList[] alasan = new SinglyLinkedList[n]; // alasan[v] = MK yang belum lulus
        int jumlahBelum = 0;
        int jumlahLulus = 0;

        for (int v = 0; v < n; v++) {
            if (lulus[v]) {
                jumlahLulus++;
            }
        }

        for (int v = 0; v < n; v++) {
            MataKuliah mk = graph.getVertex(v);

            // Syarat 1: belum lulus MK tersebut dan MK ditawarkan semester berikutnya
            if (lulus[v] || !mk.ditawarkanPada(semesterBerikutnya)) {
                continue;
            }

            // Syarat 2: semua prasyarat sudah lulus.
            // DFS menelusuri rantai prasyarat dan mengumpulkan yang belum lulus.
            SinglyLinkedList belumLulus = new SinglyLinkedList();
            boolean[] visited = new boolean[n];
            visited[v] = true;
            graph.dfsPrasyaratBelumLulus(v, lulus, visited, belumLulus);

            // Syarat khusus: Skripsi hanya bisa diambil setelah semua MK wajib diambil.
            if (mk.kode.equals(KODE_SKRIPSI)) {
                belumLulus = new SinglyLinkedList();
                for (int u = 0; u < n; u++) {
                    if (u != v && !lulus[u]) {
                        belumLulus.addLast(u);
                    }
                }
            }

            if (belumLulus.isEmpty()) {
                bisa[jumlahBisa++] = v;
            } else {
                belum[jumlahBelum] = v;
                alasan[v] = belumLulus;
                jumlahBelum++;
            }
        }

        insertionSortSemester(bisa, jumlahBisa);
        insertionSortSemester(belum, jumlahBelum);

        // ---------------- Cetak hasil ----------------
        System.out.println("Semester berikutnya : " + kapital(semesterBerikutnya));
        System.out.println("Jumlah MK lulus     : " + jumlahLulus + " MK");
        System.out.println();

        System.out.println("Daftar MK yang bisa diambil:");
        if (jumlahBisa == 0) {
            System.out.println("(tidak ada)");
        } else {
            int lebarKode = 4;
            int lebarNama = 0;
            for (int i = 0; i < jumlahBisa; i++) {
                MataKuliah mk = graph.getVertex(bisa[i]);
                lebarKode = Math.max(lebarKode, mk.kode.length());
                lebarNama = Math.max(lebarNama, mk.nama.length());
            }
            int totalSks = 0;
            for (int i = 0; i < jumlahBisa; i++) {
                MataKuliah mk = graph.getVertex(bisa[i]);
                System.out.printf("%2d. %-" + lebarKode + "s %-" + lebarNama + "s %d SKS%n",
                        i + 1, mk.kode, mk.nama, mk.sks);
                totalSks += mk.sks;
            }
            System.out.println("Total: " + totalSks + " SKS");
        }

        System.out.println();
        System.out.println("Daftar MK yang belum bisa diambil semester depan:");
        if (jumlahBelum == 0) {
            System.out.println("(tidak ada)");
        } else {
            for (int i = 0; i < jumlahBelum; i++) {
                int v = belum[i];
                MataKuliah mk = graph.getVertex(v);
                String teks = mk.kode + ": belum lulus ";
                Node p = alasan[v].getHead();
                while (p != null) {
                    teks += graph.getVertex(p.data).kode;
                    if (p.pointer != null) {
                        teks += ", ";
                    }
                    p = p.pointer;
                }
                if (mk.kode.equals(KODE_SKRIPSI)) {
                    teks += " (Skripsi hanya bisa diambil setelah semua MK wajib diambil)";
                }
                System.out.println(teks);
            }
        }
    }

    /**
     * Insertion sort (materi ASD-12) berdasarkan semester MK, ascending.
     * Stabil: MK dengan semester sama tetap berurutan seperti di matakuliah.csv.
     */
    private void insertionSortSemester(int[] a, int jumlah) {
        for (int i = 1; i < jumlah; i++) {
            int temp = a[i];
            int j = i - 1;
            while (j >= 0 && graph.getVertex(a[j]).semester > graph.getVertex(temp).semester) {
                a[j + 1] = a[j]; // geser ke belakang
                j--;
            }
            a[j + 1] = temp;
        }
    }

    private static String kapital(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
