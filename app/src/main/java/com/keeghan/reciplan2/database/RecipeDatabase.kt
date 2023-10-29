package com.keeghan.reciplan2.database

import android.content.Context
import androidx.room.Database
import androidx.room.*
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.RecipeUrls.Companion.adaakwa
import com.keeghan.reciplan2.database.RecipeUrls.Companion.banku
import com.keeghan.reciplan2.database.RecipeUrls.Companion.bankye_kakro
import com.keeghan.reciplan2.database.RecipeUrls.Companion.fante_fante
import com.keeghan.reciplan2.database.RecipeUrls.Companion.fish_Stew
import com.keeghan.reciplan2.database.RecipeUrls.Companion.fried_Rice
import com.keeghan.reciplan2.database.RecipeUrls.Companion.ga_kenkey
import com.keeghan.reciplan2.database.RecipeUrls.Companion.hausa_Kooko
import com.keeghan.reciplan2.database.RecipeUrls.Companion.jollof_Rice
import com.keeghan.reciplan2.database.RecipeUrls.Companion.kelewele
import com.keeghan.reciplan2.database.RecipeUrls.Companion.kontomire_stew
import com.keeghan.reciplan2.database.RecipeUrls.Companion.koose
import com.keeghan.reciplan2.database.RecipeUrls.Companion.missing0
import com.keeghan.reciplan2.database.RecipeUrls.Companion.missing1
import com.keeghan.reciplan2.database.RecipeUrls.Companion.missing2
import com.keeghan.reciplan2.database.RecipeUrls.Companion.mputomputo
import com.keeghan.reciplan2.database.RecipeUrls.Companion.nkate_nkwan
import com.keeghan.reciplan2.database.RecipeUrls.Companion.nkatie_Cake
import com.keeghan.reciplan2.database.RecipeUrls.Companion.omo_Tuo
import com.keeghan.reciplan2.database.RecipeUrls.Companion.rice_water
import com.keeghan.reciplan2.database.RecipeUrls.Companion.tatale
import com.keeghan.reciplan2.database.RecipeUrls.Companion.waakye
import com.keeghan.reciplan2.database.RecipeUrls.Companion.yam_Balls
import com.keeghan.reciplan2.utils.Constants.BREAKFAST
import com.keeghan.reciplan2.utils.Constants.DINNER
import com.keeghan.reciplan2.utils.Constants.LUNCH
import com.keeghan.reciplan2.utils.Constants.SNACK

@Database(entities = [Recipe::class, Day::class], version = 2, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                //Add a field to store whether recipe was created by user
                db.execSQL("ALTER TABLE recipe_table ADD COLUMN userCreated INTEGER NOT NULL DEFAULT 0")
                //Add a field to store user descriptions
                db.execSQL("ALTER TABLE recipe_table ADD COLUMN userDirection TEXT NOT NULL DEFAULT ''")
                //Add a field to store user Ingredients
                db.execSQL("ALTER TABLE recipe_table ADD COLUMN userIngredient TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(
            context: Context, scope: CoroutineScope
        ): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, RecipeDatabase::class.java, "word_database"
                ).addCallback(WordDatabaseCallback(scope)).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.recipeDao())
                }
            }
        }

        //Images used here are not guaranteed under fair-use policy and as such
        //may violate copyright laws if this app is published
        suspend fun populateDatabase(recipeDao: RecipeDao) {
            //clear database first
            recipeDao.insert(
                Recipe(
                    0,
                    "missing0",
                    R.string.no_directions,
                    0,
                    0,
                    missing0,
                    collection = false,
                    favorite = false,
                    type = "Missing Meal Plan",
                )
            ) //make invisible
            recipeDao.insert(
                Recipe(
                    1,
                    "missing1",
                    R.string.no_directions,
                    0,
                    0,
                    missing1,
                    collection = false,
                    favorite = false,
                    type = "Missing Meal Plan",
                )
            ) //make invisible
            recipeDao.insert(
                Recipe(
                    2,
                    "missing2",
                    R.string.no_directions,
                    0,
                    0,
                    missing2,
                    collection = false,
                    favorite = false,
                    type = "Missing Meal Plan",
                )
            ) //make invisible
            recipeDao.insert(
                Recipe(
                    3,
                    "Hausa Kooko",
                    R.array.hausa_kooko_array,
                    6,
                    15,
                    hausa_Kooko,
                    collection = false,
                    favorite = false,
                    type = BREAKFAST,
                )
            )
            recipeDao.insert(
                Recipe(
                    4,
                    "Koose (Spicy Bean Cake)",
                    R.array.koose_array,
                    7,
                    60,
                    koose,
                    collection = false,
                    favorite = false,
                    type = BREAKFAST,
                )
            )
            recipeDao.insert(
                Recipe(
                    5,
                    "Yam Balls",
                    R.array.yam_balls_array,
                    7,
                    25,
                    yam_Balls,
                    collection = false,
                    favorite = false,
                    type = SNACK,
                )
            )
            recipeDao.insert(
                Recipe(
                    6,
                    "Kelewele",
                    R.array.kelewele_array,
                    5,
                    25,
                    kelewele,
                    collection = false,
                    favorite = false,
                    type = SNACK,
                )
            )
            recipeDao.insert(
                Recipe(
                    7,
                    "Fish Stew",
                    R.array.fish_stew_array,
                    8,
                    40,
                    fish_Stew,
                    collection = false,
                    favorite = false,
                    type = DINNER,
                )
            )
            recipeDao.insert(
                Recipe(
                    8,
                    "Omo Tuo",
                    R.array.omo_tuo_array,
                    3,
                    25,
                    omo_Tuo,
                    collection = false,
                    favorite = false,
                    type = DINNER,
                )
            )
            recipeDao.insert(
                Recipe(
                    9,
                    "BanKye (Agble) Krakro",
                    R.array.bankye_krakro_array,
                    4,
                    20,
                    bankye_kakro,
                    collection = false,
                    favorite = false,
                    type = SNACK,
                )
            )
            recipeDao.insert(
                Recipe(
                    10,
                    "Nkatie Cake",
                    R.array.nkatie_cake_array,
                    2,
                    15,
                    nkatie_Cake,
                    collection = false,
                    favorite = false,
                    type = SNACK,
                )
            )
            recipeDao.insert(
                Recipe(
                    11,
                    "Adaakwa (Zowey)",
                    R.array.adaakwa_array,
                    5,
                    15,
                    adaakwa,
                    collection = false,
                    favorite = false,
                    type = SNACK,
                )
            )
            recipeDao.insert(
                Recipe(
                    12,
                    "Fante Fante",
                    R.array.fante_fante_array,
                    11,
                    52,
                    fante_fante,
                    collection = false,
                    favorite = false,
                    type = DINNER,
                )
            )
            recipeDao.insert(
                Recipe(
                    13,
                    "Ga Kenkey",
                    R.array.ga_kenkeny_array,
                    5,
                    145,
                    ga_kenkey,
                    collection = false,
                    favorite = false,
                    type = DINNER,
                )
            )
            recipeDao.insert(
                Recipe(
                    14,
                    "Fried Rice",
                    R.array.fried_rice_array,
                    12,
                    43,
                    fried_Rice,
                    collection = false,
                    favorite = false,
                    type = LUNCH,
                )
            )
            recipeDao.insert(
                Recipe(
                    15,
                    "Jollof Rice",
                    R.array.jollof_array,
                    13,
                    70,
                    jollof_Rice,
                    collection = false,
                    favorite = false,
                    type = LUNCH,
                )
            )
            recipeDao.insert(
                Recipe(
                    16,
                    "Waakye",
                    R.array.waakye_array,
                    4,
                    72,
                    waakye,
                    collection = false,
                    favorite = false,
                    type = LUNCH,
                )
            )
            recipeDao.insert(
                Recipe(
                    17,
                    "Rice Water",
                    R.array.rice_water_array,
                    6,
                    30,
                    rice_water,
                    collection = false,
                    favorite = false,
                    type = BREAKFAST,
                )
            )
            recipeDao.insert(
                Recipe(
                    18,
                    "Tatale",
                    R.array.tatale_array,
                    8,
                    60,
                    tatale,
                    collection = false,
                    favorite = false,
                    type = SNACK,
                )
            )//no img
            recipeDao.insert(
                Recipe(
                    19,
                    "Nkate Nkwan",
                    R.array.nkate_nkwan_array,
                    12,
                    50,
                    nkate_nkwan,
                    collection = true,
                    favorite = false,
                    type = DINNER,
                )
            )
            recipeDao.insert(
                Recipe(
                    20,
                    "Banku",
                    R.array.banku_array,
                    3,
                    30,
                    banku,
                    collection = false,
                    favorite = false,
                    type = DINNER,
                )
            )
            recipeDao.insert(
                Recipe(
                    21,
                    "Mputomputo",
                    R.array.mputomputo_array,
                    11,
                    40,
                    mputomputo,
                    collection = false,
                    favorite = false,
                    type = LUNCH,
                )
            ) //no img
            recipeDao.insert(
                Recipe(
                    22,
                    "Kontomire Stew",
                    R.array.kontomire_stew_array,
                    11,
                    45,
                    kontomire_stew,
                    collection = false,
                    favorite = false,
                    type = DINNER,
                )
            )

            //Day Inserts
            recipeDao.insert(Day(1, "Sunday", 3, 14, 22))
            recipeDao.insert(Day(2, "Monday", 0, 1, 2))
            recipeDao.insert(Day(3, "Tuesday", 0, 1, 2))
            recipeDao.insert(Day(4, "Wednesday", 0, 1, 2))
            recipeDao.insert(Day(5, "Thursday", 0, 1, 2))
            recipeDao.insert(Day(6, "Friday", 0, 14, 2))
            recipeDao.insert(Day(7, "Saturday", 0, 1, 2))
        }
    }

}

class RecipeUrls {
    companion object {
        const val missing0 =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fafro.webp?alt=media&token=a04d933c-1356-49ec-91c8-e8b2990bb3cb"
        const val missing1 =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fafro.webp?alt=media&token=a04d933c-1356-49ec-91c8-e8b2990bb3cb"
        const val missing2 =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fafro.webp?alt=media&token=a04d933c-1356-49ec-91c8-e8b2990bb3cb"
        const val hausa_Kooko =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fhausa_kooko.webp?alt=media&token=c136b2b0-8cfd-42b4-b601-0f21b0ac3ab8"
        const val koose =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fkoose.webp?alt=media&token=e77a9327-afd0-41e3-9758-a17fe80266c1"
        const val yam_Balls =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fyam_balls.webp?alt=media&token=0ceff29c-3fd8-459d-9863-7da2295031ab"
        const val kelewele =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fkelewele.webp?alt=media&token=464a84dc-e349-4c3a-a6dc-6e2d4ffc8821"
        const val fish_Stew =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Ffish_stew.webp?alt=media&token=993bfc4d-b1ea-44de-a1f7-867749530529"
        const val omo_Tuo =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fomo_tuo.webp?alt=media&token=c84632a3-a21d-49f0-b4c3-4ee96314ba9b"
        const val bankye_kakro =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fbankyekk.webp?alt=media&token=61446344-9041-4f33-85f1-fdca8e2c5d44"
        const val nkatie_Cake =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fnkatie_cake.webp?alt=media&token=7dc4553c-59b4-4b5f-8542-0e0f9aca57b6"
        const val adaakwa =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fadaakwa.webp?alt=media&token=c1faf3b2-0745-4dfe-9c3c-10af9014bfbe"
        const val fante_fante =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Ffante_fante.webp?alt=media&token=cdcbcc2d-4366-4657-bbe1-2ecaa2f170d1"
        const val ga_kenkey =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fga_kenkey.jpg?alt=media&token=bc5bd614-292e-45d2-9a72-60ddff464124"
        const val fried_Rice =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Ffried_rice.webp?alt=media&token=c22f71b8-80bc-4c20-8acd-c0f030c0d8a7"
        const val jollof_Rice =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fjollof.webp?alt=media&token=036742d4-d059-4335-b147-b64a30ede499"
        const val waakye =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fwaakye.webp?alt=media&token=de72f820-0880-47a8-9724-a5a9b38b7e8b"
        const val rice_water =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Frice%20water.webp?alt=media&token=0c199a2c-019a-4833-a360-901db8932947"
        const val tatale =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fsmiling.webp?alt=media&token=ad46ec29-4195-47b3-bab0-a610b3ef3e5a"
        const val nkate_nkwan =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fnkate_nkwan.webp?alt=media&token=06914fe2-5188-4656-9a65-cb59bd24b4dd"
        const val banku =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fbanku.webp?alt=media&token=8677c604-81bd-41c0-a85d-79fcd02b8c93"
        const val mputomputo =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fsmiling.webp?alt=media&token=ad46ec29-4195-47b3-bab0-a610b3ef3e5a"
        const val kontomire_stew =
            "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fkontomire.webp?alt=media&token=06b881c0-9c1f-4e7f-b574-cfd9465a3620"
    }
}