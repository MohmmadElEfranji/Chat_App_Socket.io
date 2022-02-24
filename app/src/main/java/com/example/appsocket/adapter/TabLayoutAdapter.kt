package com.example.appsocket.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.appsocket.ui.fragment.home.GroupListFragment
import com.example.appsocket.ui.fragment.home.OnLineUserFragment

class TabLayoutAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    companion object {
        const val TOTAL_COUNT = 2
    }

    override fun getItemCount(): Int {
        return TOTAL_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()

        when (position) {
            0 -> {
                fragment = OnLineUserFragment()
            }
            1 -> {
                fragment = GroupListFragment()
            }
        }

        return fragment
    }
}