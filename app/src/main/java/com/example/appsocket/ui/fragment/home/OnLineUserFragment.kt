package com.example.appsocket.ui.fragment.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsocket.R
import com.example.appsocket.adapter.OnLineUserAdapter
import com.example.appsocket.databinding.FragmentOnLineUserBinding
import com.example.appsocket.model.User
import com.example.appsocket.utils.SocketCreate
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.reflect.Type


class OnLineUserFragment : Fragment() {


    lateinit var app: SocketCreate
    private var mSocket: Socket? = null
    private val adapterUser = OnLineUserAdapter


    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        mSocket!!.emit("updateIsOnline", JSONObject().apply {
            put("id", auth.currentUser!!.uid)
            put("isOnline", true)
        })
    }

    /******************************************************/
    private lateinit var binding: FragmentOnLineUserBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnLineUserBinding.inflate(inflater, container, false)

        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        app = requireActivity().application as SocketCreate
        mSocket = app.getSocket()







        setUpRecycler()
        //  checkStatusInternet()
        adapterUser.setOnItemClickListener(object : OnLineUserAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {


                val email =
                    (binding.rvOnlineUser.findViewHolderForAdapterPosition(position)!!.itemView.findViewById(
                        R.id.tv_name
                    ) as TextView).text.toString()


                val bundle = Bundle()
                bundle.putString("userEmail", email)


                val navOptions =
                    NavOptions.Builder().setPopUpTo(R.id.onLineUserFragment, true).build()
                findNavController().navigate(
                    R.id.action_containerFragment_to_chatFragment,
                    bundle,
                    navOptions
                )

            }

        })


        mSocket!!.emit("aLLUser", true)
        mSocket!!.on("aLLUser") { args ->

            GlobalScope.launch(Dispatchers.Main) {
                try {


                    val usersList: Type = object : TypeToken<List<User>>() {}.type
                    val userList = Gson().fromJson<List<User>>(args[0].toString(), usersList)

                    adapterUser.list.clear()
                    userList.forEach { users ->
                        if (users.email != auth.currentUser!!.email.toString()) {
                            adapterUser.list.add(users)
                            adapterUser.list.distinct()
                            adapterUser.notifyDataSetChanged()

                        }

                    }


                } catch (e: Exception) {
                    Log.e("ListUser", e.message.toString())
                }
            }


        }


        mSocket!!.connect()

    }

    override fun onDestroy() {

        if (mSocket != null)
            mSocket!!.emit("updateIsOnline", JSONObject().apply {
                put("id", auth.currentUser!!.uid)
                put("isOnline", false)
            })

        super.onDestroy()

    }

    private fun setUpRecycler() {
        binding.rvOnlineUser.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvOnlineUser.adapter = adapterUser
    }



}