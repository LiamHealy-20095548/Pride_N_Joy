package ie.wit.pridenjoy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import ie.wit.pridenjoy.fragments.ReportFragment
import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.main.MainApp
import ie.wit.pridenjoy.models.EventModel
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.card_events.view.*

import kotlinx.android.synthetic.main.fragment_host_profile.view.cardViewCategory
import kotlinx.android.synthetic.main.fragment_host_profile.view.cardViewDate
import kotlinx.android.synthetic.main.fragment_host_profile.view.cardViewEventDetails
import kotlinx.android.synthetic.main.fragment_host_profile.view.cardViewEventTitle
import kotlinx.android.synthetic.main.fragment_host_profile.view.cardViewImageIcon
import kotlinx.android.synthetic.main.fragment_host_profile.view.cardViewLatitude
import kotlinx.android.synthetic.main.fragment_host_profile.view.cardViewLongitude
import kotlinx.android.synthetic.main.fragment_host_profile.view.cardViewTime


interface EventsListener {
    fun onEventClick(event: EventModel)
}

class EventsAdapter(
        var events: ArrayList<EventModel>,
        private val listener: ReportFragment, reportall: Boolean)
    : RecyclerView.Adapter<EventsAdapter.MainHolder>() {

    val reportAll = reportall

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_events,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val event = events[holder.adapterPosition]
        holder.bind(event,listener, reportAll)
    }

    override fun getItemCount(): Int = events.size

    fun removeAt(position: Int) {
        events.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(event: EventModel, listener: ReportFragment, reportAll: Boolean) {
            itemView.tag = event

            itemView.cardViewCategory.text = event.categorytype

            itemView.cardViewDate.text = event.date
            itemView.cardViewTime.text = event.time
            itemView.cardViewEventTitle.text = event.eventTitle
            itemView.cardViewEventDetails.text = event.eventDetails

            itemView.cardViewLatitude.text = event.latitude.toString()
            itemView.cardViewLongitude.text = event.longitude.toString()

            if(event.isfavourite) itemView.imagefavourite.setImageResource(R.drawable.ic_favourite_on)


            if(reportAll) {
                if (event.isfavourite)
                    itemView.imagefavourite.visibility = View.VISIBLE
                else
                    itemView.imagefavourite.visibility = View.INVISIBLE
            }

            //Set to if(!reportAll) to disable click on the card view of the Report All Fragment
            if(reportAll)
                itemView.setOnClickListener {
                    listener.onEventClick(event)
                }
            if(!reportAll)
                itemView.setOnClickListener {
                    listener.onEventClick(event)
                }

            if(!event.profilepic.isEmpty()) {
                Picasso.get().load(event.profilepic.toUri())
                    //.resize(180, 180)
                    .transform(CropCircleTransformation())
                    .into(itemView.cardViewImageIcon)
            }
            else
                itemView.cardViewImageIcon.setImageResource(R.drawable.pindrop_rainbow)
        }

    }
}