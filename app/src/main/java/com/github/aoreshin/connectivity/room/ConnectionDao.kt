package com.github.aoreshin.connectivity.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Observable

@Dao
interface ConnectionDao {
    @Query("SELECT * FROM CONNECTION")
    fun all(): Observable<List<Connection>>

    @Query("SELECT * FROM CONNECTION WHERE id=:id")
    fun find(id: Int): Observable<Connection>

    @Query("DELETE FROM CONNECTION WHERE id = :id")
    fun delete(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(connection: Connection)
}