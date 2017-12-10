package com.example.david.songlek

/**
 * Created by David on 10/12/2017.
 */
import android.graphics.Typeface
import android.graphics.Typeface.DEFAULT_BOLD
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout.HORIZONTAL
import org.jetbrains.anko.*
import java.util.*

class UnlockedSongsAdapter(val list: ArrayList<String> = ArrayList<String>()) : BaseAdapter() {
    override fun getView(i : Int, v : View?, parent : ViewGroup?) : View {
        return with(parent!!.context) {
            //taskNum will serve as the S.No. of the list starting from 1
            var taskNum: Int = i +1

            //Layout for a list view item
            linearLayout {
                id = 0
                lparams(width = matchParent, height = wrapContent)
                padding = dip(10)
                orientation = HORIZONTAL

                textView {
                    id = 0
                    text=""+taskNum
                    textSize = 16f
                    typeface = Typeface.MONOSPACE
                    padding =dip(5)
                }

                textView {
                    id = 0
                    text=list.get(i)
                    textSize = 16f
                    typeface = DEFAULT_BOLD
                    padding =dip(5)
                }
            }
        }
    }

    override fun getItem(position : Int) : String {
        return list[position]
    }

    override fun getCount() : Int {
        return list.size
    }

    override fun getItemId(position : Int) : Long {
        //can be used to return the item's ID column of table
        return 0L
    }

    //function to add an item to the list
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