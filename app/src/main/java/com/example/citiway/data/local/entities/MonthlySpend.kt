package com.example.citiway.data.local.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// ==================== MONTHLY SPEND ====================
@Entity(
    tableName = "monthlyspend",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MonthlySpendEntity(
    @PrimaryKey(autoGenerate = true)
    val spend_id: Int = 0,
    val user_id: Int? = null,
    val month: String? = null,
    val total_amount: Double? = null
)