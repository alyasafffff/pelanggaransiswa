package com.example.pelanggaransiswa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pelanggaransiswa.adapter.SiswaAdapter
import com.example.pelanggaransiswa.databinding.ActivitySiswaBinding
import com.example.pelanggaransiswa.entity.Siswa
import com.example.pelanggaransiswa.usecase.SiswaUsecase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class SiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySiswaBinding
    private lateinit var siswaAdapter: SiswaAdapter
    private lateinit var siswaUsecase: SiswaUsecase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        siswaUsecase = SiswaUsecase()
        auth = Firebase.auth

        setupRecyclerView()
        registerEvents()
    }

    override fun onStart() {
        super.onStart()
        loadDataSiswa()
    }

    private fun registerEvents() {
        binding.fabTambahSiswa.setOnClickListener {
            val intent = Intent(this, CreateSiswaActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            // Tampilkan dialog konfirmasi
            AlertDialog.Builder(this@SiswaActivity)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya, Logout") { dialog, _ ->
                    // Panggil fungsi logout
                    prosesLogout()
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun prosesLogout() {
        // Sign out dari Firebase
        auth.signOut()

        // Pindah kembali ke MainActivity (halaman login)
        val intent = Intent(this, MainActivity::class.java)
        // Tambahkan flags untuk membersihkan riwayat activity
        // sehingga pengguna tidak bisa kembali ke SiswaActivity dengan tombol "Back"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Tutup SiswaActivity
    }

    private fun setupRecyclerView() {
        siswaAdapter = SiswaAdapter(mutableListOf(), object : SiswaAdapter.SiswaItemEvents {

            // Event Hapus
            override fun onDelete(siswa: Siswa) {
                AlertDialog.Builder(this@SiswaActivity)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Yakin ingin menghapus siswa ${siswa.nama_lengkap}?")
                    .setPositiveButton("Ya") { dialog, _ ->
                        lifecycleScope.launch {
                            try {
                                siswaUsecase.deleteSiswa(siswa.nis)
                                displayMessage("Siswa berhasil dihapus")
                                loadDataSiswa() // Muat ulang data
                            } catch (exc: Exception) {
                                displayMessage("Gagal menghapus: ${exc.message}")
                            }
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            // Event Edit
            override fun onEdit(siswa: Siswa) {
                val intent = Intent(this@SiswaActivity, UpdateSiswaActivity::class.java)
                // Kirim NIS sebagai key untuk di-edit
                intent.putExtra("SISWA_NIS", siswa.nis)
                startActivity(intent)
            }
        })
        binding.rvSiswa.apply {
            adapter = siswaAdapter
            layoutManager = LinearLayoutManager(this@SiswaActivity)
        }
    }

    private fun loadDataSiswa() {
        binding.rvSiswa.visibility = View.GONE
        binding.loadingSiswa.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val data = siswaUsecase.getAllSiswa()
                siswaAdapter.updateDataShet(data)
            } catch (exc: Exception) {
                displayMessage("Error: ${exc.message}")
            } finally {
                binding.rvSiswa.visibility = View.VISIBLE
                binding.loadingSiswa.visibility = View.GONE
            }
        }
    }

    private fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}