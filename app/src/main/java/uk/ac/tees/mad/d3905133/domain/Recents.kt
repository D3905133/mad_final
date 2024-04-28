package uk.ac.tees.mad.d3905133.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_search")
data class Recents(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recentResult: String
)
