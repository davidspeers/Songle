package com.example.david.songlek

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        val guessSong = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        guessSong.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val input = EditText(this)
            input.setInputType(InputType.TYPE_CLASS_TEXT)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            builder.setView(input)
            builder.setMessage("Guess the Song Title")
                    .setPositiveButton("Guess", DialogInterface.OnClickListener { dialog, id ->
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)}).setNegativeButton("Blarg", DialogInterface.OnClickListener {dialog, id ->
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)})
            builder.show()
        }
    }
}
