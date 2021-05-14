package ie.wit.pridenjoy.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.adapters.EventsAdapter
import ie.wit.pridenjoy.adapters.EventsListener
import ie.wit.pridenjoy.main.MainApp
import ie.wit.pridenjoy.models.EventModel
import ie.wit.pridenjoy.utils.SwipeToEditCallback
import ie.wit.pridenjoy.utils.createLoader
import ie.wit.pridenjoy.utils.hideLoader
import ie.wit.pridenjoy.utils.showLoader
import ie.wit.utils.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.card_events.*
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.fragment_report.view.*

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

open class ReportFragment : Fragment(), AnkoLogger, EventsListener {

    open lateinit var app: MainApp
    open lateinit var loader : AlertDialog
    open lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = activity?.application as MainApp

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_report, container, false)
        activity?.title = getString(R.string.my_events_title)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireActivity()) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                deleteAlertDialog()


                    val adapter = root.recyclerView.adapter as EventsAdapter
                    adapter.removeAt(viewHolder.adapterPosition)
                    deleteEvent((viewHolder.itemView.tag as EventModel).uid)
                    deleteUserEvent(app.auth.currentUser!!.uid,
                            (viewHolder.itemView.tag as EventModel).uid)

            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(root.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onEventClick(viewHolder.itemView.tag as EventModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(root.recyclerView)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ReportFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    open fun setSwipeRefresh() {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllEvents(app.auth.currentUser!!.uid)
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefresh.isRefreshing) root.swiperefresh.isRefreshing = false
    }

    fun deleteUserEvent(userId: String, uid: String?) {
        app.database.child("user-events").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Communication error : ${error.message}")
                    }
                })
    }

    fun deleteEvent(uid: String?) {
        app.database.child("events").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.removeValue()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Communication error : ${error.message}")
                    }
                })
    }

    override fun onEventClick(donation: EventModel) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrameLayout, EditFragment.newInstance(donation))
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        if(this::class == ReportFragment::class)
            getAllEvents(app.auth.currentUser!!.uid)
    }

    fun getAllEvents(userId: String?) {
        loader = createLoader(requireActivity())
        showLoader(loader, "Downloading Events from Firebase")
        val eventsList = ArrayList<EventModel>()
        app.database.child("user-events").child(userId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Communication error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    hideLoader(loader)
                    val children = snapshot.children
                    children.forEach {
                        val event = it.
                            getValue<EventModel>(EventModel::class.java)

                        eventsList.add(event!!)
                        root.recyclerView.adapter =
                            EventsAdapter(eventsList, this@ReportFragment, false)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("user-events").child(userId)
                            .removeEventListener(this)
                    }
                }
            })
    }

    fun deleteAlertDialog()
    {
        val builder = android.app.AlertDialog.Builder(context)

        builder.setTitle("DELETE")

        builder.setMessage("Are you sure you want to delete this event?")

        builder.setNegativeButton("NO"){dialog,which ->
            //Toast.makeText(applicationContext,"You selected No",Toast.LENGTH_SHORT).show()
        }
        builder.setPositiveButton("YES"){_,_ ->
            Toast.makeText(context,"Event Deleted", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.homeFrameLayout, newInstance())
                .addToBackStack(null)
                .commit()
        }
        val dialog: android.app.AlertDialog = builder.create()
        dialog.show()
    }
}