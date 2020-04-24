package com.github.aoreshin.connectivity.room

import androidx.room.*
import io.reactivex.*

@Dao
interface ConnectionDao {
    @Query("SELECT * FROM CONNECTION")
    fun all(): Observable<List<Connection>>

    @Query("SELECT * FROM CONNECTION WHERE id=:id")
    fun find(id: Int): Observable<Connection>

    @Delete
    fun delete(connection: Connection)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(connection: Connection)
}