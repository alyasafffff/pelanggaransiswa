package com.example.pelanggaransiswa

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pelanggaransiswa.databinding.ActivityCreateSiswaBinding
import com.example.pelanggaransiswa.entity.Siswa
import com.example.pelanggaransiswa.usecase.SiswaUsecase
import kotlinx.coroutines.launch

class CreateSiswaActivity : AppCompatActivity() {

    // Gunakan ViewBinding sesuai nama file XML (activity_create_siswa.xml -> ActivityCreateSiswaBinding)
    private lateinit var binding: ActivityCreateSiswaBinding
    private lateinit var siswaUsecase: SiswaUsecase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup View Binding
        binding = ActivityCreateSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Usecase
        siswaUsecase = SiswaUsecase()

        // Pasang event listener untuk tombol simpan
        setupEvents()
    }

    private fun setupEvents() {
        binding.btnSimpan.setOnClickListener {
            saveDataSiswa()
        }
    }

    private fun saveDataSiswa() {
        // 1. Ambil data dari EditText
        val nis = binding.etNis.text.toString().trim()
        val nama = binding.etNama.text.toString().trim()
        val kelas = binding.etKelas.text.toString().trim()

        // 2. Validasi sederhana
        if (nis.isEmpty() || nama.isEmpty() || kelas.isEmpty()) {
            Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Tampilkan loading
        updateUI(true)

        // 4. Buat objek Siswa
        val siswa = Siswa(
            nis = nis,
            nama_lengkap = nama,
            kelas = kelas
        )

        // 5. Simpan data menggunakan coroutine
        lifecycleScope.launch {
            try {
                // Panggil usecase untuk menyimpan data
                siswaUsecase.createOrUpdateSiswa(siswa)

                // Jika berhasil
                Toast.makeText(this@CreateSiswaActivity, "Siswa berhasil ditambahkan", Toast.LENGTH_SHORT).show()

                // Tutup activity ini dan kembali ke daftar siswa
                finish()

            } catch (exc: Exception) {
                // Jika gagal
                updateUI(false) // Sembunyikan loading
                Toast.makeText(this@CreateSiswaActivity, "Gagal menyimpan: ${exc.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Helper untuk menampilkan/menyembunyikan loading
    private fun updateUI(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarCreate.visibility = View.VISIBLE
            binding.btnSimpan.visibility = View.GONE
        } else {
            binding.progressBarCreate.visibility = View.GONE
            binding.btnSimpan.visibility = View.VISIBLE
        }
    }
}