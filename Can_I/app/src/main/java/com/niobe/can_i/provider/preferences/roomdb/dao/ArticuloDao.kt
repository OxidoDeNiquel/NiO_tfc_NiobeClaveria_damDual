package com.niobe.can_i.provider.preferences.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niobe.can_i.provider.preferences.roomdb.entities.ArticuloEntity

@Dao
interface ArticuloDao {
    @Query("SELECT * FROM articulos")
    fun getAll(): List<ArticuloEntity>

    @Query("DELETE FROM articulos")
    suspend fun deleteAllArticulos()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'articulos'")
    suspend fun resetArticuloAutoincrement()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(articulos: List<ArticuloEntity>)

    @Query("SELECT * FROM articulos WHERE tipo LIKE :tipo")
    suspend fun getArticuloByType(tipo: String): List<ArticuloEntity>

    @Query("SELECT * FROM articulos WHERE id = :id")
    suspend fun getArticuloById(id: Int): ArticuloEntity?

}
