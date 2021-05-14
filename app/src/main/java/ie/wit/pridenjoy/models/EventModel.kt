package ie.wit.pridenjoy.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class EventModel(
    var uid: String? = "",
    var categorytype: String = "",

    var id: Long = 0,
    var eventTitle: String = "",
    var eventDetails: String = "",
    var date: String = "",
    var time: String = "",

    var profilepic: String = "",
    var eventimage: String = "",

    var image: String = "",

    var isfavourite: Boolean = false,

    var latitude: Double = 0.0,
    var longitude: Double = 0.0,

    var lat : Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f,

    var username: String = "",
    var email: String? = "joe@bloggs.com")
    : Parcelable {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "categorytype" to categorytype,

            "id" to id,

            "eventTitle" to eventTitle,
            "eventDetails" to eventDetails,
            "date" to date,
            "time" to time,

            "lat" to lat,
            "lng" to lng,
            "zoom" to zoom,

            "profilepic" to profilepic,
                "eventimage" to eventimage,

            "image" to image,

            "isfavourite" to isfavourite,

            "latitude" to latitude,
            "longitude" to longitude,

            "username" to username,
            "email" to email
        )
    }
}


@IgnoreExtraProperties
@Parcelize
data class UserPhotoModel(
    var uid: String? = "",
    var profilepic: String = "",
    var eventimage: String = "")
    : Parcelable
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "profilepic" to profilepic,
            "eventimage" to eventimage
        )
    }
}

@IgnoreExtraProperties
@Parcelize
data class Location(
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f) : Parcelable
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "lat" to lat,
            "lng" to lng,
            "zoom" to zoom,
        )
    }
}