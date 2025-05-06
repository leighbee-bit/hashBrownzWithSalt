package com.example.hashbrownzwithsalt

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UserDao {

    @Insert
    fun insertUser(user: UserEntity)

    @Delete
    fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): List<UserEntity>

    @Query("DELETE FROM user_table")
    fun deleteAllUsers()

}
