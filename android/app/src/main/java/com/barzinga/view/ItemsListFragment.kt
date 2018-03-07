package com.barzinga.view

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.barzinga.R
import com.barzinga.model.Item
import java.text.FieldPosition

/**
 * Created by diego.santos on 18/10/17.
 */
class ItemsListFragment : Fragment() {
    private var adapterItems: ArrayAdapter<Item>? = null
    private var lvItems: ListView? = null

    private var listener: OnItemSelectedListener? = null

    interface OnItemSelectedListener {
        fun onItemSelected(i: Item)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (activity is OnItemSelectedListener) {
            listener = activity
        } else {
            throw ClassCastException(activity!!.toString() + " must implement ItemsListFragment.OnItemSelectedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create arraylist from item fixtures
//        val items = Item.getItems()

        val items = arguments.getSerializable("items") as ArrayList<Item>

        adapterItems = ArrayAdapter(activity,
                R.layout.item_category, items)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate view
        val view = inflater!!.inflate(com.barzinga.R.layout.fragment_items_list, container,
                false)
        // Bind adapter to ListView
        lvItems = view.findViewById<View>(com.barzinga.R.id.lvItems) as ListView
        lvItems!!.adapter = adapterItems
        lvItems!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, item, position, rowId ->
            // Retrieve item based on position
            val i = adapterItems!!.getItem(position)
            // Fire selected event for item
            listener!!.onItemSelected(i)
        }

        lvItems!!.post(Runnable {
            lvItems!!.setItemChecked(0, true)
        })

        setActivateOnItemClick(true)

        return view
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    fun setActivateOnItemClick(activateOnItemClick: Boolean) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        lvItems!!.choiceMode = if (activateOnItemClick)
            ListView.CHOICE_MODE_SINGLE
        else
            ListView.CHOICE_MODE_NONE
    }

    companion object {

        // ItemDetailFragment.newInstance(item)
        fun newInstance(items: ArrayList<Item>): ItemsListFragment {
            val fragmentDemo = ItemsListFragment()
            val args = Bundle()
            args.putSerializable("items", items)
            fragmentDemo.arguments = args
            return fragmentDemo
        }
    }
}