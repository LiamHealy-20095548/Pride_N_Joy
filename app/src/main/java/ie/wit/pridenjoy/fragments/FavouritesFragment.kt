package ie.wit.pridenjoy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ie.wit.pridenjoy.helpers.getAllMyEvents
import ie.wit.pridenjoy.helpers.getFavouriteEvents
import ie.wit.pridenjoy.helpers.setMapMarker
import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.main.MainApp
import kotlinx.android.synthetic.main.fragment_favourites.*

class FavouritesFragment : Fragment() {

    lateinit var app: MainApp
    var viewFavourites = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_favourites, container, false)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.my_events_title)

        imageMapFavourites.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                app.mMap.clear()
                setMapMarker(app)
                if (!viewFavourites) {
                    imageMapFavourites.setImageResource(R.drawable.ic_favourite_on)
                    viewFavourites = true
                    getFavouriteEvents(app)
                    activity?.title = getString(R.string.favourites_title)
                }
                else {
                    imageMapFavourites.setImageResource(R.drawable.ic_favourite_off)
                    viewFavourites = false
                    getAllMyEvents(app)
                    activity?.title = getString(R.string.my_events_title)
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FavouritesFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}