package com.example.citiway.data.local.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.citiway.data.local.entities.User


// ==================== MONTHLY SPEND ====================
@Entity(
    tableName = "monthlyspend",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MonthlySpend(
    @PrimaryKey(autoGenerate = true)
    val spend_id: Int = 0,
    val user_id: Int? = null,
    val month: String? = null,
    val total_amount: Double? = null
)