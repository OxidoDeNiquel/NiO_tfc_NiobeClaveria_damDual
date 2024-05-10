package com.niobe.can_i.provider.preferences.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.niobe.can_i.provider.preferences.roomdb.dao.ArticuloDao
import com.niobe.can_i.provider.preferences.roomdb.entities.ArticuloEntity

@Database(entities = [ArticuloEntity::class], version = 1)
abstract class CanIDatabase: RoomDatabase() {
    abstract fun articuloDao(): ArticuloDao
}