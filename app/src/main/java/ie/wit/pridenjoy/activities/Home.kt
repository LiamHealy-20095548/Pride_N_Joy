package ie.wit.pridenjoy.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ie.wit.pridenjoy.fragments.ReportFragment
import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.fragments.*
import ie.wit.pridenjoy.helpers.checkLocationPermissions
import ie.wit.pridenjoy.helpers.isPermissionGranted
import ie.wit.pridenjoy.helpers.setCurrentLocation
import ie.wit.pridenjoy.helpers.showImagePicker
import ie.wit.pridenjoy.main.MainApp
import ie.wit.pridenjoy.utils.checkExistingPhoto
import ie.wit.pridenjoy.utils.readImageUri
import ie.wit.pridenjoy.utils.uploadImageView
import ie.wit.pridenjoy.utils.writeImageRef
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var ft: FragmentTransaction
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        app = application as MainApp

        app.locationClient = LocationServices.getFusedLocationProviderClient(this)

        if(checkLocationPermissions(this)) {
            // todo get the current location
            setCurrentLocation(app)
        }

        val toolbarLayout = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val animationDrawable = toolbarLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(8000)
        animationDrawable.setExitFadeDuration(8000)
        animationDrawable.start()

    //        app.currentLocation = Location("Default").apply {
    //           latitude = 52.245696
    //           longitude = -7.139102
    //       }

        navView.getHeaderView(0).imageView.setOnClickListener {
            showImagePicker(this, 1)
        }

        navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.getHeaderView(0).nav_header_email.text = app.auth.currentUser?.email

        checkExistingPhoto(app, this)

        ft = supportFragmentManager.beginTransaction()

//Sets the landing page once the user is successfully logged in//
        val fragment = ReportFragment.newInstance()
        ft.replace(R.id.homeFrameLayout, fragment)
        ft.commit()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (isPermissionGranted(requestCode, grantResults)) {
            // todo get the current location
            setCurrentLocation(app)
        } else {
            // permissions denied, so use the default location
            app.currentLocation = Location("Default").apply {
                latitude = 52.245696
                longitude = -7.139102
            }
        }
        Log.v("Event", "Home LAT: ${app.currentLocation.latitude} LNG: ${app.currentLocation.longitude}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (data != null) {

                    writeImageRef(app, readImageUri(resultCode, data).toString())

                    Picasso.get().load(readImageUri(resultCode, data).toString())
                        .resize(180, 180)
                        .transform(CropCircleTransformation())
                        .into(navView.getHeaderView(0).imageView, object : Callback {
                            override fun onSuccess() {
                                // Drawable is ready
                                uploadImageView(app, navView.getHeaderView(0).imageView)
                            }

                            override fun onError(e: Exception) {}
                        })
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.nav_createEvent ->
                navigateTo(CreateFragment.newInstance())

            R.id.nav_myEvents ->
                navigateTo(ReportFragment.newInstance())

            R.id.nav_upcomingEvents ->
                navigateTo(ReportAllFragment.newInstance())

            R.id.nav_favourites ->
                navigateTo(FavouritesFragment.newInstance())

            R.id.nav_account ->
                navigateTo(MyProfileFragment.newInstance())

            R.id.nav_settings ->
                toast("Settings Fragment - Coming Soon!")
            //supportFragmentManager.beginTransaction().replace(android.R.id.home, SettingsFragment()).commit()

            R.id.nav_checkUpdates ->
                startActivity(intentFor<ComingSoonActivity>())

            R.id.nav_sign_out ->
                signOutAlertDialog()

            else -> toast("Not Currently Available")
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.item_add ->
                navigateTo(CreateFragment.newInstance())

            R.id.item_map ->
                navigateTo(AllEventsFragment.newInstance())

            R.id.option_reportIssue ->
                startActivity(intentFor<ComingSoonActivity>())

            R.id.option_signOut ->
                signOutAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun signOut()
    {
        app.googleSignInClient.signOut().addOnCompleteListener(this) {
            app.auth.signOut()
            startActivity<Login>()
            finish()
        }
    }

    private fun signOutAlertDialog()
    {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("SIGN OUT")

        builder.setMessage("Are you sure you want to sign out?")

        builder.setPositiveButton("YES"){ dialog, which ->
            Toast.makeText(applicationContext, "User Signed Out", Toast.LENGTH_SHORT).show()
            signOut()
        }

        builder.setNegativeButton("NO"){ dialog, which ->
            //Toast.makeText(applicationContext,"You selected No",Toast.LENGTH_SHORT).show()
        }

        val dialog: AlertDialog = builder.create()

        dialog.show()
    }
}
