package com.example.tgsp13pppb_firebasecrud

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tgsp13pppb_firebasecrud.adapter.UserAdapter
import com.example.tgsp13pppb_firebasecrud.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAdd: FloatingActionButton
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val list: MutableList<User> = mutableListOf()
    private lateinit var userAdapter: UserAdapter
    private lateinit var progressBar: ProgressBar

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        btnAdd = findViewById(R.id.btn_add)
        progressBar = findViewById(R.id.progress_bar)

        userAdapter = UserAdapter(this, list)

        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        val decoration = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(decoration)
        recyclerView.adapter = userAdapter

        db.collection("users")
            .orderBy("creationTime", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                list.clear()
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val user = User(
                            id = document.id,
                            name = document.getString("name"),
                            email = document.getString("email"),
                            phone = document.getString("phone")
                        )
                        list.add(user)
                    }
                    userAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(applicationContext, "Data gagal di ambil!", Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility = View.GONE
            }

        btnAdd.setOnClickListener { v ->
            startActivity(Intent(applicationContext, EditorActivity::class.java))
        }
    }

    private fun showProgressBar(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
