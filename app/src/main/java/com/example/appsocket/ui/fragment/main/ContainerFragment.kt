package com.example.appsocket.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appsocket.R
import com.example.appsocket.databinding.FragmentContainerBinding
import com.example.appsocket.adapter.TabLayoutAdapter
import com.google.android.material.tabs.TabLayoutMediator

class ContainerFragment : Fragment() {

    private lateinit var adapter: TabLayoutAdapter

    /******************************************/

    private lateinit var binding: FragmentContainerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentContainerBinding.inflate(inflater, container, false)

        setupTabLayout()

        return binding.root
    }

    private fun setupTabLayout() {
        adapter = TabLayoutAdapter(requireActivity())
        binding.vpMyViewPager2.adapter = adapter

        val tabLayoutMediator = TabLayoutMediator(
            binding.tlMyTabLayout, binding.vpMyViewPager2
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Chats"

                    tab.setIcon(R.drawable.ic_chat)

                }
                1 -> {
                    tab.text = "Group"
                    tab.setIcon(R.drawable.ic_group)

                }
            }
        }
        tabLayoutMediator.attach()
    }


}