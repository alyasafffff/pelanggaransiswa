package com.example.pelanggaransiswa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pelanggaransiswa.databinding.ItemSiswaBinding
import com.example.pelanggaransiswa.entity.Siswa

class SiswaAdapter(
    private val dataset: MutableList<Siswa>,
    private val events: SiswaItemEvents,
): RecyclerView.Adapter<SiswaAdapter.CustomViewHolder>() {

    // Interface untuk event klik
    interface SiswaItemEvents {
        fun onDelete(siswa: Siswa)
        fun onEdit(siswa: Siswa)
    }

    inner class CustomViewHolder(
        val view: ItemSiswaBinding
    ): RecyclerView.ViewHolder(view.root) {

        fun bindData(data: Siswa) {
            // Sesuaikan dengan ID di item_siswa.xml
            view.tvNamaSiswa.text = data.nama_lengkap
            view.tvNisKelas.text = "${data.nis} - ${data.kelas}"

            // Event Long Click (Hapus)
            view.root.setOnLongClickListener {
                events.onDelete(data)
                true
            }

            // Event Click (Edit)
            view.root.setOnClickListener {
                events.onEdit(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemSiswaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, index: Int) {
        val data = dataset[index]
        holder.bindData(data)
    }

    fun updateDataShet(data: List<Siswa>) {
        dataset.clear()
        dataset.addAll(data)
        notifyDataSetChanged()
    }
}