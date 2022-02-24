package com.example.appsocket.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.appsocket.databinding.ActivityLoginBinding
import com.example.appsocket.ui.activities.main.Container
import com.example.appsocket.utils.ConnectionLiveData
import com.example.appsocket.utils.NetworkHelper
import com.example.appsocket.utils.SocketCreate
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.auth.FirebaseAuth
import com.shasin.notificationbanner.Banner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class LoginActivity : AppCompatActivity() {


    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private var emailUser = ""
    private var idUser = ""
    private lateinit var sh: SharedPreferences
    private lateinit var app: SocketCreate
    private var mSocket: Socket? = null

    /*******************************************/
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        app = application as SocketCreate
        mSocket = app.getSocket()

        binding.edEmailLogin.addTextChangedListener(generalTextWatcher)
        binding.edPasswordLogin.addTextChangedListener(generalTextWatcher)


        sh = applicationContext.getSharedPreferences("Login data", Context.MODE_PRIVATE)

        readData()

        binding.btnLogin.setOnClickListener {

            connectionLiveData()

            if (NetworkHelper.isNetworkConnected(this)) {
                binding.progressBar.visibility = View.GONE

                login()

            } else {
                binding.progressBar.visibility = View.VISIBLE

            }
        }



        binding.registerNow.setOnClickListener {

            goToRegisterActivity()

        }


        mSocket!!.connect()


    }


    private val generalTextWatcher: TextWatcher = object : TextWatcher {

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val email = binding.edEmailLogin.text.toString()
            val password = binding.edPasswordLogin.text.toString()

            binding.btnLogin.isEnabled = email.isNotEmpty() && password.isNotEmpty()

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {}

    }

    private fun login() {

        GlobalScope.launch(Dispatchers.Default) {

            val email = binding.edEmailLogin.text.toString()
            val password = binding.edPasswordLogin.text.toString()


            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userEmail = auth.currentUser!!.email.toString()
                    val id = auth.currentUser!!.uid

                    emailUser = userEmail
                    idUser = id

                    userLogin()


                    val edit = sh.edit()
                    edit.putString("email", email)
                    edit.putString("password", password)
                    edit.apply()

                    val i = Intent(applicationContext, Container::class.java)
                    startActivity(i)
                    finish()


                } else {

                    Banner.make(
                        binding.root,
                        this@LoginActivity,
                        Banner.ERROR,
                        "Email or Password incorrect",
                        Banner.TOP,
                        3500
                    ).show()


                }
            }

        }
    }


    private fun readData() {

        binding.edEmailLogin.setText(sh.getString("email", ""))
        binding.edPasswordLogin.setText(sh.getString("password", ""))

    }


    private fun userLogin() {

        val user = JSONObject().apply {
            put("email", emailUser)
            put("id", idUser)
            put("isOnline", true)

        }
        mSocket!!.emit("login", user)

    }

    private fun goToRegisterActivity() {

        val i = Intent(this, RegisterActivity::class.java)
        startActivity(i)

    }

    private fun connectionLiveData() {
        val cld = ConnectionLiveData(this)
        cld.observe(this, {

            if (it) {
                binding.progressBar.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.VISIBLE

            }
        })

    }


}