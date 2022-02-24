package com.example.appsocket.ui.fragment.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appsocket.ui.fragment.dialog.BottomSheetFragment
import com.example.appsocket.R
import com.example.appsocket.databinding.FragmentGroupListBinding
import com.example.appsocket.adapter.GroupAdapter
import com.example.appsocket.model.Group
import com.example.appsocket.utils.SocketCreate
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.reflect.Type


class GroupListFragment : Fragment() {


    private lateinit var app: SocketCreate
    private var mSocket: Socket? = null


    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val groupUser = GroupAdapter(arrayListOf(), object : GroupAdapter.OnClickItem {
        override fun onClick(group: Group) {
            //   Toast.makeText(requireContext(), "${group.id}", Toast.LENGTH_SHORT).show()


            val bundle = Bundle()
            bundle.putParcelable("group", group)
            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.groupListFragment, true).build()
            findNavController().navigate(
                R.id.action_containerFragment_to_chatGroupFragment,
                bundle,
                navOptions
            )

        }

    })

    /*****************************************************/
    private lateinit var binding: FragmentGroupListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGroupListBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        app = requireActivity().application as SocketCreate
        mSocket = app.getSocket()

        setUpRecycler()



        mSocket!!.emit("allGroup", true)
        mSocket!!.on("allGroup") { args ->

            GlobalScope.launch(Dispatchers.Main) {
                try {


                    val groupList: Type = object : TypeToken<List<Group>>() {}.type
                    val group = Gson().fromJson<List<Group>>(args[0].toString(), groupList)
                    groupUser.data.clear()
                    group.forEach { groups ->

                        groups.emailUser.map { emailUser ->
                            if (auth.currentUser!!.email.toString() == emailUser) {
                                groupUser.apply {
                                    data.add(groups)
                                    notifyDataSetChanged()
                                }

                            }

                        }

                    }


                } catch (e: Exception) {
                    Log.e("AllG", e.message.toString())
                }
            }
        }

        mSocket!!.connect()

        binding.btnCreateGroup.setOnClickListener {

            val bottomSheetDialog = BottomSheetFragment()
            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)

        }


    }


    private fun setUpRecycler() {
        binding.rcDataGroup.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rcDataGroup.adapter = groupUser
    }


}