# Proyek 2 Algoritma dan Struktur Data
Sistem rekomendasi pengambilan mata kuliah semester berikutnya (Teknik Informatika FILKOM UB).

Program membaca `matakuliah.csv`, `prasyarat.csv`, dan `mhs_xx.txt`, lalu menampilkan:
1. Daftar MK yang **bisa diambil** semester berikutnya.
2. Daftar MK yang **belum bisa diambil** beserta alasannya (prasyarat yang belum lulus).

Sebuah MK bisa diambil jika belum lulus, ditawarkan pada semester berikutnya (Ganjil/Genap),
dan semua prasyaratnya sudah lulus. Khusus Skripsi (SKR): hanya bisa diambil setelah semua MK wajib diambil.

## Struktur Data (dibuat sendiri, tanpa koleksi bawaan Java)
| File | Isi |
|---|---|
| `src/GraphMataKuliah.java` | Graph berarah, representasi **adjacency list**, plus DFS penelusuran prasyarat |
| `src/HashTable.java` | Hash table **separate chaining** (kode MK -> indeks vertex) |
| `src/SinglyLinkedList.java`, `src/Node.java` | Singly linked list (adjacency list & daftar alasan) |
| `src/MataKuliah.java` | Data tiap vertex (kode, nama, sks, semester, penawaran) |
| `src/SistemRekomendasi.java` | Program utama: baca file, proses aturan, insertion sort, cetak |

Tidak memakai `ArrayList`, `HashMap`, `TreeMap`, `TreeSet`, `LinkedList` bawaan Java, maupun library graph.

## Cara Menjalankan
Membutuhkan JDK 8 atau lebih baru.
```bash
javac -d out src/*.java
java -cp out SistemRekomendasi data/matakuliah.csv data/prasyarat.csv data/mhs_01.txt
java -cp out SistemRekomendasi data/matakuliah.csv data/prasyarat.csv data/mhs_02.txt
java -cp out SistemRekomendasi data/matakuliah.csv data/prasyarat.csv data/mhs_03.txt
```
Bisa juga hanya memberi file mahasiswa (CSV diambil dari folder `data/`):
`java -cp out SistemRekomendasi data/mhs_02.txt`

## Contoh Output (mhs_01.txt)
```
Semester berikutnya : Genap
Jumlah MK lulus     : 7 MK

Daftar MK yang bisa diambil:
 1. BD   Basis Data         3 SKS
 2. PLAN Pemrograman Lanjut 5 SKS
 3. SO   Sistem Operasi     5 SKS
 4. MD   Matematika Diskret 2 SKS
 5. AL   Aljabar Linear     2 SKS
 6. EP   Etika Profesi      2 SKS
 7. BING Bahasa Inggris     2 SKS
 8. PANC Pancasila          2 SKS
 9. KWU  Kewirausahaan      2 SKS
10. KWN  Kewarganegaraan    2 SKS
11. CAP  Capstone Project   3 SKS
Total: 30 SKS

Daftar MK yang belum bisa diambil semester depan:
PAW: belum lulus SO, IMK
DAA: belum lulus PLAN, ASD
KI: belum lulus SO, JK
KALJ: belum lulus AL, KA
SKR: belum lulus BD, PLAN, SO, MD, AL, EP, BING, APS, ASD, JK, KA, MN, IMK, PAW, DAA, KI, KALJ, PANC, KWU, KWN, PAPB, JN, KG, JST, SI, MPPI, BIND, CAP, PKL (Skripsi hanya bisa diambil setelah semua MK wajib diambil)
```
Output lengkap ketiga mahasiswa ada di folder `contoh_output/`.

## Laporan
`laporan/Laporan_Proyek2_ASD.pdf` (3 halaman): detail struktur data dan detail algoritma.

## Struktur Folder
```
proyek2-asd/
├── src/            source code Java
├── data/           matakuliah.csv, prasyarat.csv, mhs_01..03.txt
├── contoh_output/  hasil program untuk tiap mahasiswa
├── laporan/        laporan PDF
└── README.md
```
