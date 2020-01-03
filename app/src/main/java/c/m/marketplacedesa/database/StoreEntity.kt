package c.m.marketplacedesa.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "store_table")
data class StoreEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val idContentTable: Long = 0L,
    @ColumnInfo(name = "uid")
    val uid: String? = "",
    @ColumnInfo(name = "name")
    val name: String? = "",
    @ColumnInfo(name = "address")
    val address: String? = "",
    @ColumnInfo(name = "phone")
    val phone: String? = "",
    @ColumnInfo(name = "owner_uid")
    val ownerUID: String? = "",
    @ColumnInfo(name = "store_latitude")
    val storeLatitude: Double? = 0.0,
    @ColumnInfo(name = "store_longitude")
    val storeLongitude: Double? = 0.0
) : Parcelable