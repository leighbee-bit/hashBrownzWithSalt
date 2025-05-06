package com.example.hashbrownzwithsalt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2KtResult
import com.lambdapioneer.argon2kt.Argon2Mode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.SecureRandom


class CreateUserActivity : AppCompatActivity() {

    //Bringing in the database
    private lateinit var userDao: UserDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.new_user_page)

        //The object to create the hash!
        val argon = Argon2Kt()

        //Ground Zero: Get some salt!

        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }


        //1: Initialize variables

        val createUser = findViewById<EditText>(R.id.createUsername)
        val createPass = findViewById<EditText>(R.id.createPassword)
        val confirmPass = findViewById<EditText>(R.id.confirmPassword)
        val create = findViewById<Button>(R.id.confirmBtn)
        val returnBtn = findViewById<Button>(R.id.returnBtn)
        val clear = findViewById<Button>(R.id.clearBtn)
        var canCreate: Boolean

        //Toasts (a loaf of bread)

        val dontMatch = makeText(this, "Passwords do not match", Toast.LENGTH_LONG)
        val success = makeText(this, "User created! Welcome to [redacted]!", Toast.LENGTH_LONG)
        val taken = makeText(this, "Username already taken", Toast.LENGTH_LONG)
        val tooShort = makeText(this, "Username is too short", Toast.LENGTH_LONG)

        //Database initialization

        userDao = HashBrownzWithSalt.db.userDao()


        create.setOnClickListener {
            //Check if username is long enough
            if (createUser.text.length >= 5) {
                canCreate = true
                //Check if username already exists
                lifecycleScope.launch(Dispatchers.IO) {
                    for(user in userDao.getAllUsers()) {
                        if (user.username == createUser.text.toString()) {
                            //Meaning the user cannot be added to the database
                            canCreate = false

                            taken.show()
                        }
                    }

                        //Check if passwords match
                        if ((canCreate) and (createPass.text.toString() == confirmPass.text.toString())) {
                            //Initialize username and password in this context
                            val username: String = createUser.text.toString()
                            val password: String = createPass.text.toString()

                            //Hashed the password
                            val hashResult: Argon2KtResult = argon.hash(
                                Argon2Mode.ARGON2_I,
                                password.toByteArray(),
                                salt,
                                5,
                                65536
                            )

                            //Inserts the user into the database
                            userDao.insertUser(
                                UserEntity(
                                    0,
                                    username,
                                    hashResult.encodedOutputAsString(),
                                    false
                                )
                            )

                            //Show success
                            success.show()
                            //Reset variable
                            canCreate = true

                            //Back to the home screen
                            withContext(Dispatchers.Main) {

                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)

                            }
                            Log.d("Total Users (DB)", "${userDao.getAllUsers()}")
                        }

                    if((createPass.text.toString() != confirmPass.text.toString())) {
                        canCreate = false
                        dontMatch.show()
                    }
                }
            }
            else {
                tooShort.show()
            }
        }

        returnBtn.setOnClickListener{
            val context: Context = this

            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

        clear.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO) {
                userDao.deleteAllUsers()
            }

        }

    }

}