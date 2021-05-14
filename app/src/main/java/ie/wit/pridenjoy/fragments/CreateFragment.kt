package ie.wit.pridenjoy.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import android.content.Intent
import android.text.TextUtils

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.ValueEventListener

import ie.wit.pridenjoy.activities.MapPlacemarkActivity
import ie.wit.pridenjoy.helpers.readImage
import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.main.MainApp
import ie.wit.pridenjoy.models.EventModel
import ie.wit.pridenjoy.models.Location
import ie.wit.pridenjoy.utils.createLoader
import ie.wit.pridenjoy.utils.hideLoader
import ie.wit.pridenjoy.utils.showEventImagePicker
import ie.wit.pridenjoy.utils.showLoader
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.android.synthetic.main.fragment_create.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*


class CreateFragment : Fragment(), DateSelected, TimeSelected, AnkoLogger {

    lateinit var app: MainApp

    lateinit var loader : AlertDialog
    lateinit var eventListener : ValueEventListener

    var placemark = EventModel()

    var favourite = false

    val IMAGE_REQUEST = 1
    val LOCATION_REQUEST = 2
    var location = Location(52.245696, -7.139102, 15f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_create, container, false)
        loader = createLoader(requireActivity())
        activity?.title = getString(R.string.create_event)

        setFavouriteListener(root)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnAddImage.setOnClickListener{
            showEventImagePicker(this, IMAGE_REQUEST)
        }
        btnAddDate.setOnClickListener{
            showDatePicker()
        }
        btnAddTime.setOnClickListener{
            showTimePicker()
        }
        btnSave.setOnClickListener{
            onSaveButtonPressed()
        }
        btnDelete.setOnClickListener{
            deleteAlertDialog()
        }
        btnAddLocation.setOnClickListener {
            val location = Location(app.currentLocation.latitude, app.currentLocation.longitude)
            if (placemark.zoom != 0f) {
                location.lat = placemark.lat
                location.lng = placemark.lng
                location.zoom = placemark.zoom
            }
            startActivityForResult(context?.intentFor<MapPlacemarkActivity>()?.putExtra("location", location), LOCATION_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                if (data != null) {
                    placemark.image = data.getData().toString()
                    IV_EventImage.setImageBitmap(readImage(requireActivity(), resultCode, data))
                }
            }
            LOCATION_REQUEST -> {
                if (data != null) {
                    val location = data.extras?.getParcelable<Location>("location")!!
                    placemark.lat = location.lat
                    placemark.lng = location.lng
                    placemark.zoom = location.zoom

                    latitudeTV.text = placemark!!.lat.toString()
                    longitudeTV.text = placemark!!.lng.toString()
                }
            }
        }
    }

    fun setFavouriteListener (layout: View) {
        layout.imagefavourite.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (!favourite) {
                    layout.imagefavourite.setImageResource(R.drawable.ic_favourite_on)
                    Toast.makeText(getActivity(), "Event added to favourites!", Toast.LENGTH_LONG ).show()
                    favourite = true
                }
                else {
                    layout.imagefavourite.setImageResource(R.drawable.ic_favourite_off)
                    Toast.makeText(getActivity(), "Event removed from favourites", Toast.LENGTH_LONG ).show()
                    favourite = false
                }
            }
        })
    }

    private fun showTimePicker() {
        val timePickerFragment = TimePickerFragment(this)
        timePickerFragment.show(requireFragmentManager(), "timePicker")
        info("Time Picker started...")
    }

    private fun showDatePicker() {
        val datePickerFragment = DatePickerFragment(this)
        datePickerFragment.show(requireFragmentManager(), "datePicker")
        info("Date Picker started...")
    }

    class TimePickerFragment (val timeSelected: TimeSelected) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val cal :Calendar = Calendar.getInstance()
            val hour :Int = cal.get(Calendar.HOUR_OF_DAY)
            val minute :Int = cal.get(Calendar.MINUTE)

            return TimePickerDialog(requireContext(), this, hour, minute, (true))

        }

        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            timeSelected.receiveTime(hourOfDay, minute)
            Log.d(TAG, "Time selected...")
        }
    }

    class DatePickerFragment (val dateSelected: DateSelected) : DialogFragment(), DatePickerDialog.OnDateSetListener {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val calendar :Calendar = Calendar.getInstance()
            val year :Int = calendar.get(Calendar.YEAR)
            val month :Int = calendar.get(Calendar.MONTH)
            val dayOfMonth :Int = calendar.get(Calendar.DAY_OF_MONTH)

            return DatePickerDialog(requireContext(), this, year, month, dayOfMonth)

        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            dateSelected.receiveDate(year, month, dayOfMonth)
            Log.d(TAG, "Date selected...")
        }
    }



    companion object {
        @JvmStatic
        fun newInstance() =
            CreateFragment().apply {
                arguments = Bundle().apply {}
            }
    }

//Set what happens when the Save button is clicked//
@SuppressLint("ResourceAsColor")
fun onSaveButtonPressed() {

        if (!validateEntries()) {
            return
        }

            val categoryIs = if (addCategoryType.checkedRadioButtonId == R.id.ArtCategory) "Art / Workshops"
            else
                if (addCategoryType.checkedRadioButtonId == R.id.MusicCategory) "Music / Live Performances"
                else
                    if (addCategoryType.checkedRadioButtonId == R.id.SocialCategory) "Social / Networking"
                    else
                        if (addCategoryType.checkedRadioButtonId == R.id.CharityCategory) "Charity / Fundraising"
                        else
                            if (addCategoryType.checkedRadioButtonId == R.id.BusinessCategory) "Business / Technology"
                            else
                                if (addCategoryType.checkedRadioButtonId == R.id.FoodCategory) "Food / Trade Shows"
                                else "No Category Selected"

            writeNewEvent(
                    EventModel(
                            categorytype = categoryIs,
                            time = timeTV.text.toString(),
                            date = dateTV.text.toString(),
                            eventTitle = eventTitle.text.toString(),
                            eventDetails = eventDescription.text.toString(),

                            profilepic = app.userImage.toString(),

                        latitude = placemark.lat,
                        longitude = placemark.lng,

                        //latitude = app.currentLocation.latitude,
                        //longitude = app.currentLocation.longitude,

                        isfavourite = favourite,

                            //lat = placemark.lat,
                            //lng = placemark.lng,

                            image = IV_EventImage.toString(),

                        //    username = fieldUsername.text.toString(),
                            email = app.auth.currentUser?.email))

            activity?.toast("Event Saved!")
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.homeFrameLayout, ReportFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
    }

    private fun deleteAlertDialog()
    {
        val builder = android.app.AlertDialog.Builder(context)

        builder.setTitle("DELETE")

        builder.setMessage("Are you sure you want to delete this event?")

        builder.setNeutralButton("CLEAR ALL"){dialog, which ->
            timeTV.setText("Set a Time")
            dateTV.setText("Set a Date")

            latitudeTV.setText("Latitude")
            longitudeTV.setText("Longitude")

            eventTitle.setText("")
            eventDescription.setText("")

            addCategoryType.clearCheck()

            IV_EventImage.setImageResource(0)
        }
        builder.setNegativeButton("NO"){dialog,which ->
            //Toast.makeText(applicationContext,"You selected No",Toast.LENGTH_SHORT).show()
        }
        builder.setPositiveButton("YES"){_,_ ->
            Toast.makeText(context,"Event Deleted", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.homeFrameLayout, ReportFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        val dialog: android.app.AlertDialog = builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        getCompletedEvent(app.auth.currentUser?.uid)
    }

    override fun onPause() {
        super.onPause()
        app.database.child("user-events")
                    .child(app.auth.currentUser!!.uid)
                    .removeEventListener(eventListener)
    }

    fun writeNewEvent(event: EventModel) {
        // Create new donation at /donations & /donations/$uid
        showLoader(loader, "Adding Event")
        info("Firebase DB Reference : $app.database")
        val uid = app.auth.currentUser!!.uid
        val key = app.database.child("events").push().key
        if (key == null) {
            info("Firebase Error : Key Empty")
            return
        }
        event.uid = key
        val eventValues = event.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/events/$key"] = eventValues
        childUpdates["/user-events/$uid/$key"] = eventValues

        app.database.updateChildren(childUpdates)
        hideLoader(loader)
    }


    //KEEP THIS AS THERE IS AN ERROR WITHOUT IT - INVESTIGATE FURTHER//
    fun getCompletedEvent(userId: String?) {
        eventListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                info("Event error : ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val children = snapshot.children
                children.forEach {
                    val event = it.getValue<EventModel>(EventModel::class.java)

                }
            }
        }

        app.database.child("user-events").child(userId!!)
                .addValueEventListener(eventListener)
    }


    override fun receiveDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = GregorianCalendar()
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)

        val viewDateFormatter = SimpleDateFormat("dd-MMM-YYYY")
        var viewFormattedDate:String  = viewDateFormatter.format(calendar.time)
        dateTV.setText(viewFormattedDate)
    }

    override fun receiveTime(hour: Int, minute: Int) {
        val cal = GregorianCalendar()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)

        val viewTimeFormatter = SimpleDateFormat("HH:mm aa")
        var viewFormattedTime:String  = viewTimeFormatter.format(cal.time)
        timeTV.setText(viewFormattedTime)
    }


    private fun validateEntries(): Boolean {
        var valid = true

        val eventTitleEntry = eventTitle.text.toString()
        if (TextUtils.isEmpty(eventTitleEntry)) {
            eventTitle.error = "Required"
            valid = false
        } else {
            eventTitle.error = null
        }

        val eventDetailsEntry = eventDescription.text.toString()
        if (TextUtils.isEmpty(eventDetailsEntry)) {
            eventDescription.error = "Required"
            valid = false
        } else {
            eventDescription.error = null
        }

        val eventDateEntry = dateTV.text.toString()
        if (TextUtils.isEmpty(eventDateEntry)) {
            dateTV.error = "Required"
            valid = false
        } else {
            dateTV.error = null
        }

        val eventTimeEntry = timeTV.text.toString()
        if (TextUtils.isEmpty(eventTimeEntry)) {
            timeTV.error = "Required"
            valid = false
        } else {
            timeTV.error = null
        }

//        val eventLatEntry = latitudeTV.text.toString()
//        if (TextUtils.isEmpty(eventLatEntry)) {
//            latitudeTV.error = "Required"
//            valid = false
//        } else {
//            latitudeTV.error = null
//        }

//        val eventLngEntry = longitudeTV.text.toString()
//        if (TextUtils.isEmpty(eventLngEntry)) {
//            longitudeTV.error = "Required"
//            valid = false
//        } else {
//            longitudeTV.error = null
//        }

        return valid
    }

}

interface DateSelected{
    fun receiveDate(year: Int, month: Int, dayOfMonth: Int)
}
interface TimeSelected{
    fun receiveTime(hour: Int, minute: Int)
}