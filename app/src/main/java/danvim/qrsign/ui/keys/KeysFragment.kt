package danvim.qrsign.ui.keys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import danvim.qrsign.R
class KeysFragment : Fragment() {

    private lateinit var keysViewModel: KeysViewModel
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        keysViewModel =
            ViewModelProviders.of(this).get(KeysViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_keys, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        keysViewModel.text.observe(this, Observer {
            textView.text = it
        })
        fab = activity!!.findViewById(R.id.fab)
        return root
    }

    override fun onStart() {
        super.onStart()
        fab.show()
    }

    override fun onStop() {
        super.onStop()
        fab.hide()
    }
}