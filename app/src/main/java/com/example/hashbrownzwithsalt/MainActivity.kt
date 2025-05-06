package com.example.hashbrownzwithsalt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var userDao: UserDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //1. Initialize variables

        //Bread
        val invalid = makeText(this, "Input is invalid. Please try again.", Toast.LENGTH_LONG)

        //Main Activity

        val username = findViewById<EditText>(R.id.usernameEt)
        val password = findViewById<EditText>(R.id.passwordEt)
        val enter = findViewById<Button>(R.id.enterBtn)
        val signUp = findViewById<Button>(R.id.signUpBtn)


        //2: Create database for user information and hashing algorithm

        userDao = HashBrownzWithSalt.db.userDao()

        val argon = Argon2Kt()

        signUp.setOnClickListener{
            val context: Context = this

            val intent = Intent(context, CreateUserActivity::class.java)
            context.startActivity(intent)

        }

        enter.setOnClickListener {
            //Make sure response isn't empty
            //Iterate through database to see if credentials are correct
            //If correct, proceed to Enter Screen
            //If not, show Toasts
            val userName = username.text.toString()
            val passWord = password.text.toString()

            //If fields aren't empty...
            if (username.text.isNotEmpty() and password.text.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    for (user in userDao.getAllUsers()) {
                        //Compares the User password to user input, returning a Boolean
                        val passMatch = argon.verify(Argon2Mode.ARGON2_I, user.password, passWord.toByteArray())
                        //Boolean for whether or not the usernames match
                        val userMatch = userName == user.username

                        //If both the usernames match and the Boolean is true, proceed to app content
                        if (userMatch and passMatch) {
                            user.isCurrent = true
                            withContext(Dispatchers.Main) {

                                val intent = Intent(applicationContext, EnterActivity::class.java)
                                startActivity(intent)

                            }
                            break
                        }
                        else {
                            invalid.show()
                        }
                    }
                }
            }
        }
    }
}