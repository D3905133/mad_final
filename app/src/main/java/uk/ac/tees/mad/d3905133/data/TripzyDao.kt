package uk.ac.tees.mad.d3905133.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.d3905133.domain.Recents

@Dao
interface TripzyDao {

    @Query("select * from recent_search")
    fun getAllRecentSearch(): Flow<List<Recents>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecentSearch(recent: Recents)

    @Query("delete from recent_search where id=:recent")
    suspend fun deleteRecentSearch(recent: Int)
}