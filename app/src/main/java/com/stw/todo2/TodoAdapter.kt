package com.stw.todo2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.stw.todo2.databinding.ItemViewBinding


class TodoAdapter(
    private val zadaci : MutableList<Zadatak>
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    var db = Firebase.firestore

    class TodoViewHolder(private val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root){

        var db = Firebase.firestore

        fun bind(zad: Zadatak){
            binding.tvItem.text = zad.Tekst
            binding.cbItem.isChecked = zad.Cekiran
            binding.cbItem.setOnCheckedChangeListener { _, isChecked ->
                db.collection("zadaci").whereEqualTo("tekst", zad.Tekst).get()
                    .onSuccessTask {docs ->
                        db.collection("zadaci").document(docs.first().id).update("cekiran", isChecked)
                    }
                zad.Cekiran = isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    fun Dodaj(z: Zadatak) {
        zadaci.add(z);
        notifyItemInserted(zadaci.size - 1)
    }

    fun Brisi() {
        db.collection("zadaci").whereEqualTo("cekiran", true).get()
            .addOnSuccessListener {documents->
                for (document in documents){
                    db.collection("zadaci").document(document.id).delete()
                }
            }
            .addOnFailureListener {

            }
        zadaci.removeAll { it -> it.Cekiran }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val trenZad = zadaci[position]

        holder.bind(trenZad)
    }

    override fun getItemCount(): Int {
        return zadaci.size
    }
}