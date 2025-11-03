package com.example.pelanggaransiswa.usecase


import com.example.pelanggaransiswa.entity.Siswa
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

import kotlinx.coroutines.tasks.await

class SiswaUsecase {
    private val db: FirebaseFirestore = Firebase.firestore
    private val collectionRef = db.collection("siswa")

    // CREATE (dan UPDATE)
    // Kita pakai NIS sebagai ID, jadi kita pakai .set() bukan .add()
    suspend fun createOrUpdateSiswa(siswa: Siswa) {
        try {
            // Gunakan NIS sebagai ID dokumen unik
            collectionRef.document(siswa.nis).set(siswa).await()
        } catch (exc: Exception) {
            throw Exception("Gagal menyimpan data siswa: ${exc.message}")
        }
    }

    // READ (Get All)
    suspend fun getAllSiswa(): List<Siswa> {
        try {
            val data = collectionRef
                .orderBy("nama_lengkap", Query.Direction.ASCENDING)
                .get()
                .await()

            // Konversi setiap dokumen ke data class Siswa
            return data.documents.map {
                it.toObject(Siswa::class.java)!!
                // 'id' tidak perlu di-copy karena ID-nya adalah NIS
            }
        } catch (exc: Exception) {
            throw Exception("Gagal mengambil data siswa: ${exc.message}")
        }
    }

    // READ (Get One by NIS)
    suspend fun getSiswa(nis: String): Siswa? {
        val data = collectionRef.document(nis).get().await()
        return data.toObject(Siswa::class.java)
    }

    // DELETE
    suspend fun deleteSiswa(nis: String) {
        try {
            collectionRef.document(nis).delete().await()
        } catch (exc: Exception) {
            throw Exception("Gagal menghapus siswa: ${exc.message}")
        }
    }
}