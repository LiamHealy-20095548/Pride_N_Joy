package ie.wit.pridenjoy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ie.wit.pridenjoy.R


class MyProfileFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = getString(R.string.my_profile_title)
        return inflater.inflate(R.layout.fragment_my_profile, container, false)

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MyProfileFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}
