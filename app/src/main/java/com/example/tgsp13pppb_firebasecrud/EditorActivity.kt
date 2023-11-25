package com.example.tgsp13pppb_firebasecrud

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tgsp13pppb_firebasecrud.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class EditorActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var btnsave: Button
    private lateinit var progressBar: ProgressBar
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        editName = findViewById(R.id.et_fullname)
        editEmail = findViewById(R.id.et_Email)
        editPhone = findViewById(R.id.et_Phone)
        btnsave = findViewById(R.id.btn_save)
        progressBar = findViewById(R.id.progressBar)

        if (intent.hasExtra("EXTRA_FULLNAME")) {
            editName.setText(intent.getStringExtra("EXTRA_FULLNAME"))
            editEmail.setText(intent.getStringExtra("EXTRA_EMAIL"))
            editPhone.setText(intent.getStringExtra("EXTRA_PHONE"))
        }

        btnsave.setOnClickListener { v ->
            if (editName.text.isNotEmpty() && editEmail.text.isNotEmpty() && editPhone.text.isNotEmpty()) {
                showProgressBar(true)
                if (intent.hasExtra("EXTRA_ID")) {
                    updateData(editName.text.toString(), editEmail.text.toString(), editPhone.text.toString(), intent.getStringExtra("EXTRA_ID")!!)
                } else {
                    saveData(editName.text.toString(), editEmail.text.toString(), editPhone.text.toString())
                }
            } else {
                Toast.makeText(applicationContext, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressBar(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun saveData(name: String, email: String, phone: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "creationTime" to FieldValue.serverTimestamp()
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(applicationContext, "Data saved successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@EditorActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                showProgressBar(false)
            }
    }

    private fun updateData(name: String, email: String, phone: String, id: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "creationTime" to FieldValue.serverTimestamp()
        )

        db.collection("users")
            .document(id)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Data updated successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@EditorActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                showProgressBar(false)
            }
    }
}
