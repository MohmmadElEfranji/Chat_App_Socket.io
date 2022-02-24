package com.example.appsocket.ui.fragment.chatprivate

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsocket.R
import com.example.appsocket.adapter.ChatAdapter
import com.example.appsocket.adapter.OnLineUserAdapter
import com.example.appsocket.databinding.FragmentChatBinding
import com.example.appsocket.model.Messages
import com.example.appsocket.utils.SocketCreate
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*


class ChatPrivateFragment : Fragment() {

    lateinit var app: SocketCreate
    private var mSocket: Socket? = null

    private var sender: String = ""
    private var receiver = ""
    lateinit var sh: SharedPreferences
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private var chatMessagesList: MutableList<Messages> = ArrayList()

    private val chatAdapter: ChatAdapter by lazy {
        ChatAdapter()
    }

    /*****************************************/
    lateinit var binding: FragmentChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        app = requireActivity().application as SocketCreate
        mSocket = app.getSocket()

        sh = requireActivity().getSharedPreferences("s", Context.MODE_PRIVATE)
        binding.sender.setText(sh.getString("autoSave", ""))

        binding.sender.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                val edit = sh.edit()
                edit.putString("autoSave", s.toString())
                edit.apply()
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        setUpRecycler()
        val arg = this.arguments
        val userEmail = arg?.getString("userEmail")
        receiver = userEmail.toString()

        sender = auth.currentUser!!.email.toString()

        mSocket!!.on("message", chattingPrivate)



        binding.senderBtn.setOnClickListener {
            sender()
        }


        for (i in OnLineUserAdapter.list) {

            if (i.email == userEmail) {
                if (i.isOnline) {

                    binding.txtState.visibility = View.VISIBLE

                } else {

                    binding.txtState.setText(R.string.offline)
                }
            }

        }

        binding.tvName.text = userEmail

        mSocket!!.connect()

        binding.btnBack.setOnClickListener {

            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.chatFragment, true).build()
            findNavController().navigate(
                R.id.action_chatFragment_to_containerFragment,
                null,
                navOptions
            )

        }


    }


    private val chattingPrivate = Emitter.Listener { args ->
        val message = args[0] as JSONObject
        GlobalScope.launch(Dispatchers.IO) {
            try {

                if (sender == message.getString("receiver")) {
                    val msg = Messages.MsgReceiver()
                    msg.msgIncoming = message.getString("message").toString()

                    msg.date = Calendar.getInstance().time

                    chatMessagesList.add(msg)

                    binding.rvChatRecycler.postDelayed({
                        binding.rvChatRecycler.smoothScrollToPosition(chatMessagesList.indexOf(msg))
                    }, 0)



                    chatAdapter.setChatList(chatMessagesList)


                }

            } catch (e: Exception) {

                Log.e("MsgP", e.message.toString())
            }


        }


    }


    private fun sender() {
        val message = binding.sender.text.toString()
        val chatPrivate = JSONObject()


        if (message.isNotEmpty()) {
            val msg = Messages.MsgSender()
            msg.msgOutGoing = message
            msg.date = Calendar.getInstance().time

            chatPrivate.put("sender", sender)
            chatPrivate.put("receiver", receiver)
            chatPrivate.put("message", message)


            chatMessagesList.add(msg)


            // Here to scroll to last position in recycler view
            binding.rvChatRecycler.postDelayed({
                binding.rvChatRecycler.smoothScrollToPosition(chatMessagesList.indexOf(msg))
            }, 0)


            mSocket!!.emit("message", chatPrivate)
            chatAdapter.setChatList(chatMessagesList)

            binding.sender.text.clear()
        } else {
            Log.e("onDisconnect", "Socket onDisconnect!")
        }


    }


    private fun setUpRecycler() {
        val lm = LinearLayoutManager(requireActivity())
        binding.rvChatRecycler.layoutManager = lm
        lm.stackFromEnd = true

        binding.rvChatRecycler.adapter = chatAdapter

    }


}