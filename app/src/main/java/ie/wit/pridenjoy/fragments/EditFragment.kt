package ie.wit.pridenjoy.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import ie.wit.pridenjoy.activities.MapPlacemarkActivity
import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.adapters.EventsAdapter
import ie.wit.pridenjoy.helpers.readImage
import ie.wit.pridenjoy.helpers.readImageFromPath
import ie.wit.pridenjoy.main.MainApp
import ie.wit.pridenjoy.models.EventModel
import ie.wit.pridenjoy.models.Location
import ie.wit.pridenjoy.utils.*
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_edit.view.*
import kotlinx.android.synthetic.main.fragment_report.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var loader : AlertDialog
    lateinit var root: View
    var editEvents: EventModel? = null

    val IMAGE_REQUEST = 1
    val LOCATION_REQUEST = 2
    var location = Location()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            editEvents = it.getParcelable("editevent")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_edit, container, false)
        activity?.title = getString(R.string.action_edit)
        loader = createLoader(requireActivity())

//      root.editCategoryType.text == (editEvents!!.paymenttype.toString())
//      root.editCategoryType.setText(editEvents!!.paymenttype)

        root.editEventTitle.setText(editEvents!!.eventTitle)
        root.editEventDetails.setText(editEvents!!.eventDetails)
        root.editTime.setText(editEvents!!.time)
        root.editDate.setText(editEvents!!.date)
        root.editLatitude.setText(editEvents!!.latitude.toString())
        root.editLongitude.setText(editEvents!!.longitude.toString())

//        root.IV_editEventImage.setImageResource(editEvents!!.image.toInt())
//            IV_editEventImage.setImageBitmap(readImageFromPath(this, editEvents!!.image))

            root.btnEditSave.setOnClickListener {

                if (!validateEdits()) {
                    return@setOnClickListener
                }

                showLoader(loader, "Updating Event on Server...")
                updateEventData()
                updateEvent(editEvents!!.uid, editEvents!!)
                updateUserEvent(app.auth.currentUser!!.uid,
                        editEvents!!.uid, editEvents!!)
                activity?.toast("Event Updated!")
            }

            return root
    }

    companion object {
        @JvmStatic
        fun newInstance(event: EventModel) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("editevent",event)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

       val editCategory =  if (editCategoryType.checkedRadioButtonId == R.id.ArtCategory) "Art / Workshops"
        else
            if (editCategoryType.checkedRadioButtonId == R.id.MusicCategory) "Music / Live Performances"
            else
                if (editCategoryType.checkedRadioButtonId == R.id.SocialCategory) "Social / Networking"
                else
                    if (editCategoryType.checkedRadioButtonId == R.id.CharityCategory) "Charity / Fundraising"
                    else
                        if (editCategoryType.checkedRadioButtonId == R.id.BusinessCategory) "Business / Technology"
                        else
                            if (editCategoryType.checkedRadioButtonId == R.id.FoodCategory) "Food / Trade Shows"
                            else "No Category Selected"

        btnEditImage.setOnClickListener{
            showEditImagePicker(this, IMAGE_REQUEST)
        }
        btnEditDate.setOnClickListener {
            showDatePicker()
        }
        btnEditTime.setOnClickListener {
            showTimePicker()
        }
        btnEditLocation.setOnClickListener {
            val location = Location(editEvents!!.latitude, editEvents!!.longitude, editEvents!!.zoom)

            startActivityForResult(context?.intentFor<MapPlacemarkActivity>()?.putExtra("location", location), LOCATION_REQUEST)
        }
        btnEditDelete.setOnClickListener {
//                deleteEvent(app.auth.currentUser!!.uid)
        }
    }

//    private fun deleteEvent(uid: String?) {
//        app.database.child("events").child(uid!!)
//                .addListenerForSingleValueEvent(
//                        object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                snapshot.ref.removeValue()
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                info("Firebase Communication error : ${error.message}")
//                            }
//                        })
//    }

//    private fun deleteUserEvent(userId: String, uid: String?) {
//        app.database.child("user-events").child(userId).child(uid!!)
//                .addListenerForSingleValueEvent(
//                        object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                snapshot.ref.removeValue()
//                            }
//                            override fun onCancelled(error: DatabaseError) {
//                                info("Firebase Communication error : ${error.message}")
//                            }
//                        })
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            IMAGE_REQUEST -> {
                if (data != null) {
                    editEvents!!.image = data.getData().toString()
                    IV_editEventImage.setImageBitmap(readImage(requireActivity(), resultCode, data))
                }
            }

            LOCATION_REQUEST -> {
                if (data != null) {
                    val location = data.extras?.getParcelable<Location>("location")!!
                    editEvents!!.lat = location.lat
                    editEvents!!.lng = location.lng
                    editEvents!!.zoom = location.zoom

                    editLatitude.text = editEvents!!.lat.toString()
                    editLongitude.text = editEvents!!.lng.toString()
                }
            }
        }
    }


        fun updateEventData() {

//        editEvents!!.paymenttype = root.editCategoryType.toString()

        editEvents!!.eventTitle = root.editEventTitle.text.toString()
        editEvents!!.eventDetails = root.editEventDetails.text.toString()
        editEvents!!.time = root.editTime.text.toString()
        editEvents!!.date = root.editDate.text.toString()
        editEvents!!.latitude = root.editLatitude.text.toString().toDouble()
        editEvents!!.longitude = root.editLongitude.text.toString().toDouble()

    }

    fun updateUserEvent(userId: String, uid: String?, event: EventModel) {
        app.database.child("user-events").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(event)
                        activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFrameLayout, ReportFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Communication error : ${error.message}")
                    }
                })
    }

    fun updateEvent(uid: String?, event: EventModel) {
        app.database.child("events").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(event)
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Communication error : ${error.message}")
                    }
                })
    }

    private fun validateEdits(): Boolean {
        var valid = true

        val eventTitleEdit = editEventTitle.text.toString()
        if (TextUtils.isEmpty(eventTitleEdit)) {
            editEventTitle.error = "Required"
            valid = false
        } else {
            editEventTitle.error = null
        }

        val eventDetailsEdit = editEventDetails.text.toString()
        if (TextUtils.isEmpty(eventDetailsEdit)) {
            editEventDetails.error = "Required"
            valid = false
        } else {
            editEventDetails.error = null
        }

        val eventDateEdit = editDate.text.toString()
        if (TextUtils.isEmpty(eventDateEdit)) {
            editDate.error = "Required"
            valid = false
        } else {
            editDate.error = null
        }

        val eventTimeEdit = editTime.text.toString()
        if (TextUtils.isEmpty(eventTimeEdit)) {
            editTime.error = "Required"
            valid = false
        } else {
            editTime.error = null
        }

        val eventLatEdit = editLatitude.text.toString()
        if (TextUtils.isEmpty(eventLatEdit)) {
            editLatitude.error = "Required"
            valid = false
        } else {
            editLatitude.error = null
        }

        val eventLngEdit = editLongitude.text.toString()
        if (TextUtils.isEmpty(eventLngEdit)) {
            editLongitude.error = "Required"
            valid = false
        } else {
            editLongitude.error = null
        }

        return valid
    }

    private fun showTimePicker() {
        val timePickerFragment = TimePickerFragment(this)
        timePickerFragment.show(requireFragmentManager(), "edittimePicker")
        info("Edit Time Picker started...")
    }

    private fun showDatePicker() {
        val datePickerFragment = DatePickerFragment(this)
        datePickerFragment.show(requireFragmentManager(), "editdatePicker")
        info("Edit Date Picker started...")
    }

    class TimePickerFragment(val timeSelected: EditFragment) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val cal : Calendar = Calendar.getInstance()
            val hour :Int = cal.get(Calendar.HOUR_OF_DAY)
            val minute :Int = cal.get(Calendar.MINUTE)

            return TimePickerDialog(requireContext(), this, hour, minute, (true))

        }

        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            timeSelected.receiveTime(hourOfDay, minute)
            Log.d(ContentValues.TAG, "Time selected...")
        }
    }

    class DatePickerFragment(val dateSelected: EditFragment) : DialogFragment(), DatePickerDialog.OnDateSetListener {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val calendar : Calendar = Calendar.getInstance()
            val year :Int = calendar.get(Calendar.YEAR)
            val month :Int = calendar.get(Calendar.MONTH)
            val dayOfMonth :Int = calendar.get(Calendar.DAY_OF_MONTH)

            return DatePickerDialog(requireContext(), this, year, month, dayOfMonth)

        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            dateSelected.receiveDate(year, month, dayOfMonth)
            Log.d(ContentValues.TAG, "Date selected...")
        }
    }
    fun receiveDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = GregorianCalendar()
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)

        val viewDateFormatter = SimpleDateFormat("dd-MMM-YYYY")
        var viewFormattedDate:String  = viewDateFormatter.format(calendar.time)
        editDate.setText(viewFormattedDate)
    }

    fun receiveTime(hour: Int, minute: Int) {
        val cal = GregorianCalendar()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)

        val viewTimeFormatter = SimpleDateFormat("HH:mm aa")
        var viewFormattedTime:String  = viewTimeFormatter.format(cal.time)
        editTime.setText(viewFormattedTime)
    }
}
