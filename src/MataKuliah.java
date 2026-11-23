/**
 * Data yang disimpan pada setiap vertex graph: satu baris dari matakuliah.csv.
 */
public class MataKuliah {
    String kode;
    String nama;
    int sks;
    int semester;
    String penawaran; // "Ganjil", "Genap", atau "Keduanya"

    MataKuliah(String kode, String nama, int sks, int semester, String penawaran) {
        this.kode = kode;
        this.nama = nama;
        this.sks = sks;
        this.semester = semester;
        this.penawaran = penawaran;
    }

    /** True jika MK ditawarkan pada semester (ganjil/genap) yang diberikan. */
    boolean ditawarkanPada(String semesterBerikutnya) {
        return penawaran.equalsIgnoreCase("Keduanya")
                || penawaran.equalsIgnoreCase(semesterBerikutnya);
    }
}
