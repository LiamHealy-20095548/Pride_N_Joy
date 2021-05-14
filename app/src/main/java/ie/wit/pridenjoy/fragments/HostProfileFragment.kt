package ie.wit.pridenjoy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.models.EventModel
import kotlinx.android.synthetic.main.fragment_host_profile.*
import kotlinx.android.synthetic.main.fragment_host_profile.view.*


class HostProfileFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    //    fab.setOnClickListener { view ->
    //        Snackbar.make(view, "Contact Event Host (Coming Soon...)",
    //            Snackbar.LENGTH_LONG).setAction("Action", null).show()
    //    }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = "HOST PROFILE"

        return inflater.inflate(R.layout.fragment_host_profile, container, false)

    }


    companion object {
        @JvmStatic
        fun newInstance(event: EventModel) =
            HostProfileFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}
