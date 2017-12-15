package com.example.david.songlek

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import org.jetbrains.anko.*
import java.util.*

class ProfileAdapter(val list: ArrayList<String> = ArrayList<String>()) : BaseAdapter() {
    //Describe ListView Layout using Anko
    override fun getView(i : Int, v : View?, parent : ViewGroup?) : View {
        return with(parent!!.context) {

            //Layout for a list view item
            linearLayout {
                id = 0
                lparams(width = matchParent, height = wrapContent)
                padding = dip(10)
                orientation = LinearLayout.HORIZONTAL

                textView {
                    id = 0
                    text=""
                    textSize = 16f
                    typeface = Typeface.MONOSPACE
                    padding =dip(5)
                }

                textView {
                    id = 0
                    text=list.get(i)
                    textSize = 16f
                    typeface = Typeface.DEFAULT_BOLD
                    padding =dip(5)
                }
            }
        }
    }

    //Returns the String stored at position x of the list
    override fun getItem(x : Int) : String {
        return list[x]
    }

    //Returns the length of the list
    override fun getCount() : Int {
        return list.size
    }

    override fun getItemId(position : Int) : Long {
        //can be used to return the item's ID column of table
        return 0L
    }

    //function to add a String to the list
    fun add(text: String) {
        list.add(list.size, text)
        notifyDataSetChanged()
    }

    //function to delete an item from list
    fun delete(i:Int) {
        list.removeAt(i)
        notifyDataSetChanged()
    }

}