package com.example.david.songlek

import android.view.View
import android.widget.ListView
import org.jetbrains.anko.*

//Describes ProfileActivity UI
class ProfileUI(val ProfileAdapter: ProfileAdapter) : AnkoComponent<ProfileActivity> {
    override fun createView(ui: AnkoContext<ProfileActivity>): View = with(ui) {
        return relativeLayout {
            var todoList : ListView? =null

            //layout to display ListView
            verticalLayout {
                todoList=listView {
                    adapter = ProfileAdapter
                }
            }.lparams {
                margin = dip(5)
            }

        }

    }

}