package ar.edu.unq.pdes.myprivateblog.data

import android.content.Context
import android.graphics.Color
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.Serializable


typealias EntityID = Int

val MIGRATION_1_2: Migration =
    object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE BlogEntries "
                        + "ADD COLUMN is_synced INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

@Database(entities = [BlogEntry::class], version = 2)
@TypeConverters(ThreeTenTimeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun blogEntriesDao(): BlogEntriesDao

    companion object {
        fun generateDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "myprivateblog.db"
        ).addMigrations(MIGRATION_1_2).build()
    }

}

@Entity(
    tableName = "BlogEntries"
)
data class BlogEntry(

    @PrimaryKey(autoGenerate = true)
    var uid: EntityID = 0,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "bodyPath")
    var bodyPath: String? = null,

    @ColumnInfo(name = "imagePath")
    val imagePath: String? = null,

    @ColumnInfo(name = "is_deleted")
    var deleted: Boolean = false,

    @ColumnInfo(name = "is_synced")
    var synced: Boolean = false,

    @ColumnInfo(name = "date")
    val date: OffsetDateTime? = null,

    @ColumnInfo(name = "cardColor")
    var cardColor: Int = Color.WHITE

) : Serializable

@Dao
interface BlogEntriesDao {
    @Query("SELECT * FROM BlogEntries ORDER BY date DESC")
    fun getAll(): Flowable<List<BlogEntry>>

    @Query("""
        SELECT * FROM BlogEntries
        WHERE (:deleted IS NULL OR is_deleted = :deleted)
        AND (:synced IS NULL OR is_synced = :synced)
        ORDER BY date DESC
    """)
    fun getAll(
        deleted: Boolean? = null,
        synced:  Boolean? = null
    ): Flowable<List<BlogEntry>>

    @Query("SELECT * FROM BlogEntries WHERE uid = :entryId LIMIT 1")
    fun loadById(entryId: EntityID): Flowable<BlogEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entries: List<BlogEntry>): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entries: BlogEntry): Single<Long>

    @Update
    fun updateAll(entries: List<BlogEntry>): Completable

    @Update
    fun update(entry: BlogEntry): Completable


    @Delete
    fun delete(entry: BlogEntry): Completable

    @Delete
    fun deleteAll(entries: List<BlogEntry>): Completable
}

object ThreeTenTimeTypeConverters {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }
}
