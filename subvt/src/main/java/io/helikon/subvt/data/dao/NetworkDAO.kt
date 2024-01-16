package io.helikon.subvt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.helikon.subvt.data.model.Network

@Dao
interface NetworkDAO {
    @Query("SELECT * from network")
    fun getAll(): LiveData<List<Network>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(network: Network)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(networks: List<Network>)

    @Query("SELECT * FROM network WHERE id = :id")
    fun findById(id: Long): Network?
}
