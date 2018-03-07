package com.barzinga.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.barzinga.R
import com.barzinga.model.Item

/**
 * Created by diego.santos on 18/10/17.
 */
class ItemDetailFragment : Fragment() {
    private var item: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = arguments.getSerializable("item") as Item
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_item_detail,
                container, false)
        val tvTitle = view.findViewById<View>(R.id.tvTitle) as TextView
        val tvBody = view.findViewById<View>(R.id.tvBody) as TextView
        tvTitle.text = item!!.title
        tvBody.text = item!!.body
        return view
    }

    companion object {

        // ItemDetailFragment.newInstance(item)
        fun newInstance(item: Item): ItemDetailFragment {
            val fragmentDemo = ItemDetailFragment()
            val args = Bundle()
            args.putSerializable("item", item)
            fragmentDemo.arguments = args
            return fragmentDemo
        }
    }
}