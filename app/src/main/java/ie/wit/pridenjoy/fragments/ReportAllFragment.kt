package ie.wit.pridenjoy.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.adapters.EventsAdapter
import ie.wit.pridenjoy.adapters.EventsListener
import ie.wit.pridenjoy.main.MainApp
import ie.wit.pridenjoy.models.EventModel
import ie.wit.pridenjoy.utils.createLoader
import ie.wit.pridenjoy.utils.hideLoader
import ie.wit.pridenjoy.utils.showLoader
import ie.wit.utils.*
import kotlinx.android.synthetic.main.card_events.*
import kotlinx.android.synthetic.main.card_events.view.*
import kotlinx.android.synthetic.main.content_placemark_maps.*
import kotlinx.android.synthetic.main.fragment_create.view.*
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.fragment_report.view.*

import org.jetbrains.anko.info

class ReportAllFragment : ReportFragment(), EventsListener {

    lateinit var ft: FragmentTransaction

    override lateinit var app: MainApp
    override lateinit var loader : AlertDialog
    override lateinit var root: View


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
        activity?.title = getString(R.string.all_upcoming_events_title)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        setSwipeRefresh()

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ReportAllFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    override fun setSwipeRefresh() {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllUsersEvents()
            }
        })
    }

    override fun onEventClick(event: EventModel) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrameLayout, HostProfileFragment.newInstance(event))
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        getAllUsersEvents()
    }

    fun getAllUsersEvents() {
        loader = createLoader(requireActivity())
        showLoader(loader, "Retrieving User Events from Firebase")
        val eventsList = ArrayList<EventModel>()
        app.database.child("events")
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
                            EventsAdapter(eventsList, this@ReportAllFragment, true)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        checkSwipeRefresh()

                        app.database.child("events").removeEventListener(this)
                    }
                }
            })

    }
}