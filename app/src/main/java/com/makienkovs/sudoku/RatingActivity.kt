package com.makienkovs.sudoku

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_rating.*
import kotlinx.android.synthetic.main.name_dialog.view.*
import java.util.*

class RatingActivity : AppCompatActivity() {
    private lateinit var keeper: Keeper
    private var records = arrayListOf<Record>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        keeper = Keeper(this)

        edit.setOnClickListener {
            nameDialog()
        }

        val name = loadName()
        if (name == "0") {
            nameDialog()
        } else {
            textViewName.text = name
            val record: Record = readRating(name)
            uploadRating(record)
        }
    }

    private fun readRating(name: String): Record {
        var id = keeper.load("ID").toString()
        if (id == "0") {
            id = UUID.randomUUID().toString()
            keeper.save("ID", id)
        }
        val easy = keeper.load("EASY").toString()
        val normal = keeper.load("NORMAL").toString()
        val hard = keeper.load("HARD").toString()
        return Record(id, name, easy, normal, hard)
    }


    private fun uploadRating(record: Record) {
        val reference = FirebaseDatabase.getInstance().reference
        val idQuery: Query = reference.child("Records").orderByChild("id").equalTo(record.id)
        idQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (idSnapshot in snapshot.children) {
                    idSnapshot.ref.removeValue()
                }
                reference.child("Records").push().setValue(record)

                downloadRating()
            }

            override fun onCancelled(error: DatabaseError) {
                println("!!!!!" + error.toException().message)
            }
        })
    }

    private fun downloadRating() {
        val reference = FirebaseDatabase.getInstance().getReference("Records")
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (records.size > 0) {
                    records.clear()
                }
                for (ds in snapshot.children) {
                    val record = ds.getValue(Record::class.java)
                    if (record != null) {
                        records.add(record)
                    }
                }
                printRating()
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        reference.addValueEventListener(valueEventListener)
    }

    private fun printRating() {

        val builder: StringBuilder = java.lang.StringBuilder()

        for (record in records) {
            if (record.easy == "0") record.easy = "9223372036854775807"
            if (record.normal == "0") record.normal = "9223372036854775807"
            if (record.hard == "0") record.hard = "9223372036854775807"
        }

        records.sortBy { record -> record.easy.toLong() }

        builder.append(getString(R.string.easy)).append("\n").append("\n")

        for (i in 0 until records.size) {
            if (records[i].easy != "9223372036854775807") {
                builder.append(
                    "${i + 1}. ${records[i].name} ${
                        String.format(
                            "%02d:%02d:%02d",
                            records[i].easy.toLong() / 3600 / 1000,
                            records[i].easy.toLong() / 1000 / 60 % 60,
                            records[i].easy.toLong() / 1000 % 60
                        )
                    }"
                ).append("\n")
            }
            if (i == 4) break
        }

        records.sortBy { record -> record.normal.toLong() }

        builder.append("\n").append(getString(R.string.normal)).append("\n").append("\n")

        for (i in 0 until records.size) {
            if (records[i].normal != "9223372036854775807") {
                builder.append(
                    "${i + 1}. ${records[i].name} ${
                        String.format(
                            "%02d:%02d:%02d",
                            records[i].normal.toLong() / 3600 / 1000,
                            records[i].normal.toLong() / 1000 / 60 % 60,
                            records[i].normal.toLong() / 1000 % 60
                        )
                    }"
                ).append("\n")
            }
            if (i == 4) break
        }

        records.sortBy { record -> record.hard.toLong() }

        builder.append("\n").append(getString(R.string.hard)).append("\n").append("\n")

        for (i in 0 until records.size) {
            if (records[i].hard != "9223372036854775807") {
                builder.append(
                    "${i + 1}. ${records[i].name} ${
                        String.format(
                            "%02d:%02d:%02d",
                            records[i].hard.toLong() / 3600 / 1000,
                            records[i].hard.toLong() / 1000 / 60 % 60,
                            records[i].hard.toLong() / 1000 % 60
                        )
                    }"
                ).append("\n")
            }
            if (i == 4) break
        }

        ratingList.text = builder.toString()
    }

    private fun nameDialog() {
        val imm = (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        val view = layoutInflater.inflate(R.layout.name_dialog, null)
        AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(getText(R.string.ok)) { _, _ ->
                run {
                    val name = view.name.text.toString()
                    saveName(name)
                    textViewName.text = name
                    val record: Record = readRating(name)
                    uploadRating(record)
                }
            }
            .setNegativeButton(getText(R.string.cancel)) { _, _ -> }
            .create()
            .show()
    }

    private fun loadName(): String {
        return keeper.load("NAME").toString()
    }

    private fun saveName(name: String) {
        keeper.save("NAME", name)
    }
}