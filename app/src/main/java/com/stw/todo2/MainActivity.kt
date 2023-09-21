package com.stw.todo2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager.OnActivityResultListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.stw.todo2.databinding.ActivityMainBinding
import java.util.Locale


@Suppress("DEPRECATION")
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
            btnPricaj.setOnClickListener{
                getVoiceToText();

            }
        }

    }

    val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->if(result.resultCode == Activity.RESULT_OK){
        val results = result.data?.getStringArrayListExtra(
            RecognizerIntent.EXTRA_RESULTS
        ) as ArrayList<String>

        binding.etZadatak.setText(results[0])

    }
    }

    private fun getVoiceToText(){

        if(!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this,"Nema dozvole",Toast.LENGTH_LONG).show();
        } else{
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            //Locale.English() ili odredjeni jezik, get default uzima od sistema
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Kazi...");

            result.launch(i)
        }
    }
}