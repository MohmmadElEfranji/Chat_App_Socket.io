package com.example.appsocket.ui.fragment.chatgroup

import com.example.appsocket.adapter.ChatAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsocket.R
import com.example.appsocket.utils.SocketCreate
import com.example.appsocket.databinding.FragmentChatGroupBinding
import com.example.appsocket.model.Group
import com.example.appsocket.model.Messages
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class ChatGroupFragment : Fragment() {

    private lateinit var app: SocketCreate
    private var mSocket: Socket? = null

    private var sender: String = ""
    private lateinit var group: Group
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private var chatMessagesList: MutableList<Messages> = ArrayList()

    /*****************************************************/
    private lateinit var binding: FragmentChatGroupBinding
    private val chatAdapter: ChatAdapter by lazy {
        ChatAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatGroupBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        app = requireActivity().application as SocketCreate
        mSocket = app.getSocket()

        setUpRecycler()


        val arg = this.arguments
        group = arg?.getParcelable("group")!!


        sender = auth.currentUser!!.email.toString()


        mSocket!!.on("messageGroup", chattingGroup)



        binding.senderBtn.setOnClickListener {
            sender()

        }

        binding.btnBack.setOnClickListener {

            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.chatGroupFragment, true).build()
            findNavController().navigate(
                R.id.action_chatGroupFragment_to_containerFragment,
                null,
                navOptions
            )


        }

        binding.tvName.text = group.name


        mSocket!!.connect()
    }


    private val chattingGroup = Emitter.Listener { args ->
        val message = args[1] as JSONObject
        GlobalScope.launch(Dispatchers.IO) {
            try {

                if (args[0].toString() == group.id) {

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

                Log.e("MsgG", e.message.toString())

            }
        }
    }

    private fun sender() {
        val message = binding.sender.text.toString()
        val chatGroup = JSONObject()

        if (message.isNotEmpty()) {
            val msg = Messages.MsgSender()
            msg.msgOutGoing = message
            msg.date = Calendar.getInstance().time


            chatGroup.put("sender", sender)
            chatGroup.put("message", message)


            chatMessagesList.add(msg)


            binding.rvChatRecycler.postDelayed({
                binding.rvChatRecycler.smoothScrollToPosition(chatMessagesList.indexOf(msg))
            }, 0)


            mSocket!!.emit("messageGroup", group.id, chatGroup)
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