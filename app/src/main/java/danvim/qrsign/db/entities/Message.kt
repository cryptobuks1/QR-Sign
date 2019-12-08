package danvim.qrsign.db.entities

import androidx.room.*
import danvim.qrsign.db.DateTypeConverter
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = KeyPair::class,
        parentColumns = ["uid"],
        childColumns = ["keyPairId"]
    )]
)
data class Message(
    @PrimaryKey val uid: String,
    val keyPairId: String,
    val content: String,
    val date: String,
    val createdAt: Date
)