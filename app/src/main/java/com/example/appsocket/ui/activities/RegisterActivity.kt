package com.example.appsocket.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.appsocket.R
import com.example.appsocket.databinding.ActivityRegisterBinding
import com.example.appsocket.utils.ConnectionLiveData
import com.example.appsocket.utils.NetworkHelper
import com.google.firebase.auth.FirebaseAuth
import com.shasin.notificationbanner.Banner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {


    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    /*****************************************************/
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkEmail()

        checkPassword()


        binding.already.setOnClickListener {
            goToLoginActivity()
        }


        binding.btnRegister.setOnClickListener {

            connectionLiveData()

            if (NetworkHelper.isNetworkConnected(this)) {

                binding.progressBar.visibility = View.GONE

                signUp()

            } else {
                binding.progressBar.visibility = View.VISIBLE
            }

        }


    }


    private fun signUp() {

        GlobalScope.launch(Dispatchers.Main) {

            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edEmail.error = getString(R.string.req)
                }
                password.isEmpty() -> {
                    binding.edEmail.error = getString(R.string.req)

                }
                else -> {

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->

                            GlobalScope.launch(Dispatchers.Main) {
                                if (task.isSuccessful) {

                                    Banner.make(
                                        binding.root, this@RegisterActivity, Banner.SUCCESS,
                                        "RegisterActivity succeeded!", Banner.TOP, 3000
                                    ).show()

                                    goToLoginActivity()


                                } else {
                                    Banner.make(
                                        binding.root, this@RegisterActivity, Banner.ERROR,
                                        "RegisterActivity Failed!", Banner.TOP, 2000
                                    ).show()
                                }

                            }
                        }
                }
            }
        }


    }

    private fun checkEmail() {

        binding.edEmail.setOnFocusChangeListener { _, focused ->

            if (!focused) {

                binding.lEmail.helperText = validEmail()
            }

        }

    }

    private fun validEmail(): String? {
        val emailText = binding.edEmail.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid Email Address"

        }
        return null
    }

    private fun checkPassword() {

        binding.edPassword.setOnFocusChangeListener { _, focused ->

            if (!focused) {

                binding.lPassword.helperText = validPassword()
            }


        }

    }

    private fun validPassword(): String? {
        val passwordText = binding.edEmail.text.toString()

        if (passwordText.length < 8) {
            return "Minimum 8 Character Password"

        }
        return null
    }


    private fun goToLoginActivity() {

        val i = Intent(this, LoginActivity::class.java)
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