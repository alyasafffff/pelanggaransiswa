package com.example.pelanggaransiswa

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
// Pastikan nama binding ini sesuai dengan file XML Anda
// (activity_update_siswa.xml -> ActivityUpdateSiswaBinding)
import com.example.pelanggaransiswa.databinding.ActivityUpdateSiswaBinding
import com.example.pelanggaransiswa.entity.Siswa
import com.example.pelanggaransiswa.usecase.SiswaUsecase
import kotlinx.coroutines.launch

class UpdateSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateSiswaBinding
    private lateinit var siswaUsecase: SiswaUsecase
    private var currentNis: String? = null // Untuk menyimpan NIS yang akan diedit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        siswaUsecase = SiswaUsecase()

        // 1. Ambil NIS yang dikirim dari SiswaActivity
        currentNis = intent.getStringExtra("SISWA_NIS")

        if (currentNis == null) {
            // Jika tidak ada NIS, tampilkan error dan tutup
            displayMessage("Error: NIS Siswa tidak ditemukan")
            finish()
            return
        }

        // 2. Muat data siswa berdasarkan NIS
        loadDataSiswa(currentNis!!)

        // 3. Pasang event listener untuk tombol simpan
        setupEvents()
    }

    private fun loadDataSiswa(nis: String) {
        updateUI(true) // Tampilkan loading
        lifecycleScope.launch {
            try {
                // Panggil usecase getSiswa
                val siswa = siswaUsecase.getSiswa(nis)

                if (siswa != null) {
                    // Masukkan data ke form
                    binding.etNis.setText(siswa.nis)
                    binding.etNama.setText(siswa.nama_lengkap)
                    binding.etKelas.setText(siswa.kelas)
                } else {
                    displayMessage("Error: Data siswa tidak ditemukan")
                    finish()
                }
            } catch (exc: Exception) {
                displayMessage("Gagal memuat data: ${exc.message}")
                finish()
            } finally {
                updateUI(false) // Sembunyikan loading
            }
        }
    }

    private fun setupEvents() {
        binding.btnSimpanPerubahan.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        // Ambil data dari form
        // NIS diambil dari currentNis, BUKAN dari EditText, karena field-nya disabled
        val nama = binding.etNama.text.toString().trim()
        val kelas = binding.etKelas.text.toString().trim()

        // Validasi
        if (nama.isEmpty() || kelas.isEmpty()) {
            displayMessage("Nama dan Kelas wajib diisi")
            return
        }

        updateUI(true) // Tampilkan loading

        // Buat objek Siswa yang sudah diperbarui
        val updatedSiswa = Siswa(
            nis = currentNis!!, // Pakai NIS yang asli
            nama_lengkap = nama,
            kelas = kelas
        )

        // Simpan data (gunakan fungsi yang sama dengan create)
        lifecycleScope.launch {
            try {
                // Panggil createOrUpdateSiswa
                siswaUsecase.createOrUpdateSiswa(updatedSiswa)
                displayMessage("Data siswa berhasil diperbarui")
                finish() // Tutup activity dan kembali ke daftar
            } catch (exc: Exception) {
                updateUI(false)
                displayMessage("Gagal menyimpan: ${exc.message}")
            }
        }
    }

    // Helper untuk loading
    private fun updateUI(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarUpdate.visibility = View.VISIBLE
            binding.btnSimpanPerubahan.visibility = View.GONE
        } else {
            binding.progressBarUpdate.visibility = View.GONE
            binding.btnSimpanPerubahan.visibility = View.VISIBLE
        }
    }

    // Helper untuk Toast
    private fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}