package danvim.qrsign.db.dao

import androidx.room.*
import danvim.qrsign.db.entities.KeyPair

@Dao
interface KeyPairDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertKeyPair(keyPair: KeyPair)

    @Update
    fun updateKeyPair(keyPair: KeyPair)

    @Delete
    fun deleteKeyPair(keyPair: KeyPair)

    @Query("SELECT * FROM KeyPair WHERE name == :name")
    fun getKeyPairsByName(name: String): List<KeyPair>

    @Query("SELECT * FROM KeyPair")
    fun getKeyPairs(): List<KeyPair>
}