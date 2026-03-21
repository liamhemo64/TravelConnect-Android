package com.idz.travelconnect.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.idz.travelconnect.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserById(uid: String): LiveData<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: List<User>)
}
