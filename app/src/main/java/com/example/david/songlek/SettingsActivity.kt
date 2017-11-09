package com.example.david.songlek

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        resetProgressButton.setOnClickListener() {
            alert("Are You Sure You Want To Delete All Your Progress?") {
                yesButton { toast("Yess!!!") }
                noButton { }
            }.show()
        }
    }
}
