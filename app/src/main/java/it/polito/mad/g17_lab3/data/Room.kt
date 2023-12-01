package it.polito.mad.g17_lab3.data

import androidx.room.*
import com.google.firebase.firestore.DocumentReference


@Entity(tableName = "users")
data class User(
    @PrimaryKey()
    val id: String = "",

    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "surname")
    var surname: String,
    @ColumnInfo(name = "username")
    var username: String,
    @ColumnInfo(name = "gender")
    var gender: String = "",
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "bio")
    var bio: String = "",
    @ColumnInfo(name = "birthday")
    var birthday: String = "",
    @ColumnInfo(name = "phone")
    var phone: String = "",
    @ColumnInfo(name = "profileImage")
    var profileImage: String = "",
    @ColumnInfo(name = "football_rating")
    var football_rating: Long = 0L,
    @ColumnInfo(name = "basket_rating")
    var basket_rating: Long = 0L,
    @ColumnInfo(name = "tennis_rating")
    var tennis_rating: Long = 0L,
    @ColumnInfo(name = "visible_name")
    var visibleName: Boolean = true,
    @ColumnInfo(name = "visible_mail")
    var visibleMail: Boolean = true,
    @ColumnInfo(name = "visible_sports")
    var visibleSports: Boolean = false,
    @ColumnInfo(name = "visible_phone")
    var visiblePhone: Boolean = false,
    @ColumnInfo(name = "visible_birthday")
    var visibleBirthday: Boolean = false,
    @ColumnInfo(name = "auth_id")
    var auth_id: String = "",

    )
@Entity(tableName = "court")
data class Court(
    @PrimaryKey(autoGenerate = false)
    val id: String = "0",

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "address")
    var address: String,

    @ColumnInfo(name = "time_start")
    val opening: String = "9.00",

    @ColumnInfo(name = "time_finish")
    val closing: String,

    @ColumnInfo(name = "days")
    val days: String = "",

    )

@Entity(tableName = "rating_court")
data class RatingCourt(
    @PrimaryKey(autoGenerate = false)
    val id: String = "0",

    @ColumnInfo(name = "rating")
    val rating: Long,

    @ColumnInfo(name = "author_id")
    val authorId: String,

    @ColumnInfo(name = "court_id")
    val courtId: String,

    @ColumnInfo(name = "review")
    val review: String
)

@Entity(tableName = "court_slots")
data class CourtSlot(
    @PrimaryKey
    var id: DocumentReference? = null,

    @ColumnInfo(name = "court_id")
    var court_id: DocumentReference,

    @ColumnInfo(name = "time_start")
    var time_start: String,

    @ColumnInfo(name = "time_finish")
    var time_finish: String,

    @ColumnInfo(name = "reservation_id")
    var reservation_id: DocumentReference? =null,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "sport")
    var sport: String

)

@Entity(tableName = "reservation")
data class Reservation(

    @PrimaryKey
    var id: DocumentReference? = null,

    @ColumnInfo(name = "author_id")
    var author_id: String = "",

    @ColumnInfo(name = "court_id")
    var court_id: DocumentReference? =null,

    @ColumnInfo(name = "court_name")
    var court_name: String = "",

    @ColumnInfo(name = "sport")
    var sport: String = "",

    @ColumnInfo(name = "date")
    var date: String = "",

    @ColumnInfo(name = "time")
    var time: String = "",

    @ColumnInfo(name = "player_number")
    var player_number: Int = 0,

    @ColumnInfo(name = "rented_equipment")
    var rented_equipment: Int = 0,

    @ColumnInfo(name = "current_number")
    var current_number: Int = 0,

    @ColumnInfo(name = "participants")
    var participants: ArrayList<String> = arrayListOf<String>(),

    @ColumnInfo(name = "confirmed_participants")
    var confirmed_participants: ArrayList<String> = arrayListOf<String>()


)