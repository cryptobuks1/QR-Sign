package danvim.qrsign.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import danvim.qrsign.utils.KeyType
import java.util.Date

@Entity
data class KeyPair (
    @PrimaryKey val uid: String,
    val publicKey: String,
    val privateKey: String,
    val name: String,
    val keyType: KeyType,
    val keyLocation: String,
    val updatedAt: Date = Date(System.currentTimeMillis()),
    val createdAt: Date = Date(System.currentTimeMillis())
)