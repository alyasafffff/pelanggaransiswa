package com.example.pelanggaransiswa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.pelanggaransiswa.databinding.ActivityMainBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // 1. Setup Binding dan Firebase
    private lateinit var binding: ActivityMainBinding
    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Auth dan Credential Manager
        // (Meniru todolist/MainActivity.kt)
        credentialManager = CredentialManager.create(this)
        auth = Firebase.auth

        // Memasang event listener
        setupEvents()
    }

    // 2. Setup Tombol Klik
    fun setupEvents() {
        // ID tombol disesuaikan dengan layout pelanggaransiswa
        // (activity_main.xml)
        binding.btnSignInGoogle.setOnClickListener {
            updateUI(true) // Tampilkan loading
            loginWithGoogle()
        }
    }

    // 3. Logika Login (Sama seperti todolist/MainActivity.kt)
    //
    fun loginWithGoogle() {
        val request = prepareGoogleSigninRequest()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = request
                )
                handleSignin(credential = result.credential)
            } catch (e: GetCredentialException) {
                updateUI(false) // Sembunyikan loading
                Log.e("GOOGLE_SIGNIN", "Gagal mendapatkan kredensial: ${e.localizedMessage}")
            }
        }
    }

    // 4. Persiapan Request Google Signin
    fun prepareGoogleSigninRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            // Gunakan ID Klien dari strings.xml Anda
            //
            .setServerClientId(getString(R.string.clientId))
            .setFilterByAuthorizedAccounts(false)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    // 5. Menangani Hasil Signin (Sama seperti todolist/MainActivity.kt)
    //
    private fun handleSignin(credential: Credential) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val credentialGoogle = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

        auth.signInWithCredential(credentialGoogle)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                    goToSiswaActivity() // Pindah ke halaman Siswa
                } else {
                    updateUI(false) // Sembunyikan loading
                    Toast.makeText(this, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 6. Pindah Halaman (Disesuaikan ke SiswaActivity)
    fun goToSiswaActivity() {
        // Arahkan ke SiswaActivity, bukan TodoActivity
        val intent = Intent(this, SiswaActivity::class.java)
        startActivity(intent)
        finish() // Tutup MainActivity
    }

    // 7. Cek Login (Sama seperti todolist/MainActivity.kt)
    //
    override fun onStart() {
        super.onStart()
        // Cek apakah user sudah login
        if (auth.currentUser != null) {
            goToSiswaActivity()
        }
    }

    // 8. Helper untuk Loading UI
    private fun updateUI(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSignInGoogle.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnSignInGoogle.visibility = View.VISIBLE
        }
    }
}