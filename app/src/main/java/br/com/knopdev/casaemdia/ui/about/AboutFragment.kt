package br.com.knopdev.casaemdia.ui.about

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.knopdev.casaemdia.BuildConfig
import br.com.knopdev.casaemdia.R
import com.google.android.material.appbar.MaterialToolbar

class AboutFragment : Fragment(R.layout.fragment_about) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialToolbar>(R.id.about_toolbar).apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationContentDescription(R.string.back)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
        view.findViewById<TextView>(R.id.version_text).text = getString(R.string.version_label, BuildConfig.VERSION_NAME)
    }
}
