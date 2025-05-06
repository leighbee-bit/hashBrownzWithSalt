package com.example.hashbrownzwithsalt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EnterActivity : AppCompatActivity() {

    private lateinit var userDao: UserDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.enter_page)

        userDao = HashBrownzWithSalt.db.userDao()

        val logOut = findViewById<Button>(R.id.logOutBtn)
        val welcome = findViewById<TextView>(R.id.welcomeTv)
        val hashTv = findViewById<TextView>(R.id.hashTv)
        var currentUser: UserEntity

        lifecycleScope.launch(Dispatchers.IO) {
            for(user in userDao.getAllUsers()) {
                if (user.isCurrent) {
                    currentUser = user
                    withContext(Dispatchers.Main) {
                        welcome.text = "welcome ${currentUser.username}"
                        hashTv.text = currentUser.password
                    }
                }
                break
            }
        }

        logOut.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }


    }

}