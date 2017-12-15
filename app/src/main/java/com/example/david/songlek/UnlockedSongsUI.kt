package com.example.david.songlek

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ListView
import org.jetbrains.anko.*

//Describes ProfileActivity UI
class UnlockedSongsUI(val UnlockedSongsAdapter: UnlockedSongsAdapter) : AnkoComponent<UnlockedSongsActivity> {
    override fun createView(ui: AnkoContext<UnlockedSongsActivity>): View = with(ui) {
        return relativeLayout {
            var todoList : ListView? =null

            //layout to display ListView
            verticalLayout {
                todoList=listView {
                    adapter = UnlockedSongsAdapter
                    // onItemClick go to the corresponding YouTube link
                    onItemClick { _, _, i, _ ->
                        alert("Do you want to watch the music video on Youtube?") {
                            yesButton {
                                val peanut : String = adapter.getItem(i) as String
                                val uri = Uri.parse(peanut)
                                val i = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(i)
                            }
                            noButton {}
                        }.show()
                    }
                }
            }.lparams {
                margin = dip(5)
            }

        }

    }

}