package br.com.knopdev.casaemdia.ui.suggestions

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.knopdev.casaemdia.CasaEmDiaApplication
import br.com.knopdev.casaemdia.R
import br.com.knopdev.casaemdia.ui.tasks.TaskFormFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SuggestionsFragment : Fragment(R.layout.fragment_suggestions) {

    private val viewModel: SuggestionsViewModel by viewModels {
        val app = requireActivity().application as CasaEmDiaApplication
        SuggestionsViewModelFactory(app.container.suggestionRepository)
    }
    private val adapter = SuggestionAdapter { suggestion ->
        findNavController().navigate(
            R.id.action_suggestionsFragment_to_taskFormFragment,
            bundleOf(
                TaskFormFragment.ARG_TASK_ID to 0L,
                TaskFormFragment.ARG_SUGGESTION_TITLE to suggestion.title,
                TaskFormFragment.ARG_SUGGESTION_NOTES to suggestion.description,
                TaskFormFragment.ARG_SUGGESTION_CATEGORY to suggestion.category.name
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialToolbar>(R.id.suggestions_toolbar).apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationContentDescription(R.string.back)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
        view.findViewById<MaterialButton>(R.id.refresh_suggestions_button).setOnClickListener { viewModel.refresh() }
        view.findViewById<RecyclerView>(R.id.suggestions_recycler_view).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SuggestionsFragment.adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        adapter.submitList(state.suggestions)
                        view.findViewById<ProgressBar>(R.id.suggestions_loading).isVisible = state.isLoading && state.suggestions.isEmpty()
                        view.findViewById<TextView>(R.id.suggestions_empty).isVisible = !state.isLoading && state.suggestions.isEmpty()
                    }
                }
                launch {
                    viewModel.events.collect { Snackbar.make(view, R.string.suggestions_offline, Snackbar.LENGTH_LONG).show() }
                }
            }
        }
    }
}
