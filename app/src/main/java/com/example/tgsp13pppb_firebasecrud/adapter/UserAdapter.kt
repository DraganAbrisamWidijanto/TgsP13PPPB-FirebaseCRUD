package com.example.tgsp13pppb_firebasecrud.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tgsp13pppb_firebasecrud.R
import com.example.tgsp13pppb_firebasecrud.model.User
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.example.tgsp13pppb_firebasecrud.EditorActivity

class UserAdapter(
    private val context: Activity,
    private val list: MutableList<User>) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    private var dialog: Dialog? = null
    private val db = FirebaseFirestore.getInstance()

    interface Dialog {
        fun onClick(pos: Int)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_user, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = list[position]

        holder.name.text = currentItem.name
        holder.email.text = currentItem.email
        holder.phone.text = currentItem.phone

        holder.itemView.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle("Pilih opsi untuk '${currentItem.name}'")
                setItems(R.array.items_option) { dialog, which ->
                    when (which) {
                        0 -> {
                            // Ubah
                            val intent = Intent(context, EditorActivity::class.java).apply {
                                putExtra("EXTRA_FULLNAME", currentItem.name)
                                putExtra("EXTRA_EMAIL", currentItem.email)
                                // Anda perlu menambahkan field 'phone' ke model User Anda
                                putExtra("EXTRA_PHONE", currentItem.phone)
                                putExtra("EXTRA_ID", currentItem.id)
                            }
                            context.startActivity(intent)
                        }
                        1 -> {
                            // Hapus
                            val itemId = currentItem.id
                            if (itemId != null) {
                                db.collection("users").document(itemId).delete()
                                    .addOnSuccessListener {
                                        // Dokumen berhasil dihapus
                                        list.removeAt(position)
                                        notifyDataSetChanged()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(context, "ID tidak ditemukan", Toast.LENGTH_SHORT).show()
                            }
                        }
                        2 -> {
                            // Batal
                            dialog.dismiss()
                        }
                    }
                }
                setCancelable(false)
                show()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var email: TextView = itemView.findViewById(R.id.email)
        var phone: TextView = itemView.findViewById(R.id.phone)

        init {
            itemView.setOnClickListener { view ->
                dialog?.onClick(layoutPosition)
            }
        }
    }
}
