package ie.wit.pridenjoy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.content_placemark_maps.*

import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.main.MainApp
import ie.wit.pridenjoy.models.EventPlacemarkStore


//THIS SHOULD BE DELETED//
//THIS SHOULD BE DELETED//
//THIS SHOULD BE DELETED//
//THIS SHOULD BE DELETED//
//THIS SHOULD BE DELETED//
//THIS SHOULD BE DELETED//
//THIS SHOULD BE DELETED//
//THIS SHOULD BE DELETED//
//THIS SHOULD BE DELETED//
//THIS SHOULD BE DELETED//

class MapViewAll : AppCompatActivity(), GoogleMap.OnMarkerClickListener{

    lateinit var map: GoogleMap

    lateinit var app: MainApp
    lateinit var database: DatabaseReference

    lateinit var placemarks: EventPlacemarkStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MainApp
        setContentView(R.layout.activity_map_view_all)

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync {

            map = it
            configureMap()

        }
    }

    private fun configureMap() {
        map.setOnMarkerClickListener(this)
        map.uiSettings.setZoomControlsEnabled(true)

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val tag = marker.tag as Long
        val placemark = app.placemarks.findById(tag)
        currentTitle.text = placemark!!.eventTitle
        currentDescription.text = placemark!!.eventDetails
        textDate.text = placemark!!.date
        textTime.text = placemark!!.time
//        currentImage.setImageBitmap(readImageFromPath(this@MapViewAll, placemark.image))
       return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
