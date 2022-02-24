package com.example.appsocket.ui.fragment.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsocket.R
import com.example.appsocket.databinding.FragmentBottomSheetBinding
import com.example.appsocket.adapter.SelectUsersAdapter
import com.example.appsocket.model.User
import com.example.appsocket.utils.SocketCreate
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shasin.notificationbanner.Banner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var app: SocketCreate
    private var mSocket: Socket? = null


    private val adapterSelect = SelectUsersAdapter(arrayListOf())

    val emailUser = ArrayList<String>()
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)

    }

    private lateinit var binding: FragmentBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        app = requireActivity().application as SocketCreate
        mSocket = app.getSocket()

        setUpRecycler()

        emailUser.add(auth.currentUser!!.email.toString())

        adapterSelect.setOnItemClickListener(object : SelectUsersAdapter.OnItemClickListener {

            override fun onItemClick(position: Int) {

                val email =
                    (binding.rvDataUser.findViewHolderForAdapterPosition(position)!!.itemView.findViewById(
                        R.id.checkBox
                    ) as CheckBox)

                if (email.isChecked) {
                    emailUser.add(email.text.toString())

                } else {
                    emailUser.remove(email.text.toString())

                }


            }
        })



        mSocket!!.emit("aLLUser", true)
        mSocket!!.on("aLLUser") { args ->

            GlobalScope.launch(Dispatchers.Main) {
                try {


                    val usersList: Type = object : TypeToken<List<User>>() {}.type
                    val userList = Gson().fromJson<List<User>>(args[0].toString(), usersList)

                    adapterSelect.data.clear()
                       userList.forEach { users ->

                            if (users.email != auth.currentUser!!.email.toString()) {
                                adapterSelect.data.add(users)
                                adapterSelect.data.distinct()
                                adapterSelect.notifyDataSetChanged()

                            }

                        }


                } catch (e: Exception) {
                    Log.e("ListUser", e.message.toString())
                }
            }


        }



        binding.btnCreateGroup.setOnClickListener {

            val nameGroup = binding.txtNameGroup.text.toString()
            if (nameGroup.isNotEmpty()) {


                val dataGroup = JSONObject()
                dataGroup.put("name", nameGroup)
                dataGroup.put("id", UUID.randomUUID().toString())

                val emailUserJson = JSONArray()
                for (email in emailUser) {
                    emailUserJson.put(email)
                }

                if (emailUser.size > 1) {

                    dataGroup.put("emailUser", emailUserJson)

                    mSocket!!.emit("addGroup", dataGroup)

                    dismiss()
                    binding.txtNameGroup.text.clear()
                } else {

                    Banner.make(
                        requireView(),
                        requireActivity(),
                        Banner.WARNING,
                        "Please Select Users!!",
                        Banner.TOP,
                        3000
                    ).show()

                }


            } else {

                Banner.make(
                    requireView(),
                    requireActivity(),
                    Banner.WARNING,
                    "Please Fill Group Name!!",
                    Banner.TOP,
                    3000
                ).show()

            }

        }
        mSocket!!.connect()

    }


    private fun setUpRecycler() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvDataUser.layoutManager = layoutManager
        binding.rvDataUser.adapter = adapterSelect


    }

}