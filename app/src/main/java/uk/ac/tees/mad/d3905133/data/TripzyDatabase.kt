package uk.ac.tees.mad.d3905133.data

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.ac.tees.mad.d3905133.domain.Recents

@Database(entities = [Recents::class], version = 1)
abstract class TripzyDatabase: RoomDatabase() {
    abstract fun getTripzyDao(): TripzyDao
}