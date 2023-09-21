package com.stw.todo2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.stw.todo2.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var adapter : TodoAdapter;
    private lateinit var binding: ActivityMainBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adapter = TodoAdapter(mutableListOf())

        //Retrive data from database
        db.collection("zadaci")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tekst = document.data["tekst"].toString()
                    val cekiran = document.data["cekiran"].toString() == "true"
                    var nZad = Zadatak(tekst, cekiran)
                    adapter.Dodaj(nZad)
                    //Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener {

            }




        binding.rvZadaci.adapter = adapter
        binding.rvZadaci.layoutManager = LinearLayoutManager(this)

        binding.apply {

            btnDodaj.setOnClickListener {
                val inputText = etZadatak.text.toString();

                if (inputText.isNotEmpty()) {
                    val z: Zadatak = Zadatak(inputText, false)
                    adapter.Dodaj(z);
                    etZadatak.text.clear()

                    //Add to database
                    val hmZad = hashMapOf(
                        "tekst" to inputText,
                        "cekiran" to false
                    )

                    db.collection("zadaci").add(hmZad)/*
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }*/
                }
            }
            btnBrisi.setOnClickListener {
                adapter.Brisi()
            }
        }
    }
}