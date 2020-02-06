package c.m.lapaksembakodonorojo.database

import androidx.room.*

@Dao
interface StoreDao {
    // Insert data from remote repository
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContent(contentEntity: List<StoreEntity>)

    // Delete data from local repository for refresh data
    @Query("DELETE FROM store_table")
    fun deleteContent()

    // transaction refresh data for local repository
    @Transaction
    fun updateContent(contentEntity: List<StoreEntity>) {
        deleteContent()
        insertContent(contentEntity)
    }

    // Search data from local repository
    @Query("SELECT * FROM store_table WHERE name LIKE '%' || :searchKeyword || '%'")
    fun searchContent(searchKeyword: String): List<StoreEntity>
}