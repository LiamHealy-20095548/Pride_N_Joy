package ie.wit.pridenjoy.main

import android.app.Application
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.adapters.EventsAdapter
import ie.wit.pridenjoy.models.EventPlacemarkStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var storage: StorageReference
    lateinit var userImage: Uri

    lateinit var mMap : GoogleMap
    lateinit var marker : Marker
    lateinit var currentLocation : Location
    lateinit var locationClient : FusedLocationProviderClient

    lateinit var placemarks: EventPlacemarkStore

    override fun onCreate() {
        super.onCreate()
        info("Pride N' Joy App started")
    }
}