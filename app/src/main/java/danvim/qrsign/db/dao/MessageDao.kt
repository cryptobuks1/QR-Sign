package danvim.qrsign.db.dao

import androidx.room.*
import danvim.qrsign.db.entities.Message

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertMessage(message: Message)

    @Update
    fun updateMessage(message: Message)

    @Delete
    fun deleteMessage(message: Message)

    @Query("SELECT * FROM Message WHERE content LIKE '%'||:content||'%'")
    fun getMessagesByContent(content: String): List<Message>

    @Query("SELECT * FROM Message")
    fun getMessages(): List<Message>
}