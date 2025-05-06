package com.example.hashbrownzwithsalt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey (autoGenerate = true) val id: Long = 0,
    @ColumnInfo (name = "username") val username: String?,
    @ColumnInfo (name = "password") val password: String,
    @ColumnInfo(name = "currentUser") var isCurrent: Boolean
)