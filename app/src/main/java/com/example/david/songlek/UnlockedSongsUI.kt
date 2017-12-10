package com.example.david.songlek

/**
 * Created by David on 10/12/2017.
 */
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ListView
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.floatingActionButton

class UnlockedSongsUI(val UnlockedSongsAdapter: UnlockedSongsAdapter) : AnkoComponent<UnlockedSongsActivity> {
    override fun createView(ui: AnkoContext<UnlockedSongsActivity>): View = with(ui) {
        return relativeLayout {
            var todoList : ListView? =null

            //layout to display ListView
            verticalLayout {
                todoList=listView {
                    adapter = UnlockedSongsAdapter
                    onItemClick { adapterView, view, i, l ->
                        val peanut : String = adapter.getItem(i) as String
                        val uri = Uri.parse(peanut)
                        val i = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(i)
                    }
                }
            }.lparams {
                margin = dip(5)
            }

        }

    }

    //function to get total number of items in list
    fun getTotalListItems(list: ListView?) = list?.adapter?.count ?: 0
}