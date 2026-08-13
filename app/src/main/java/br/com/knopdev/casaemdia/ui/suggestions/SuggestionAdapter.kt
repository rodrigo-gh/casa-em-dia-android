package br.com.knopdev.casaemdia.ui.suggestions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.knopdev.casaemdia.R
import br.com.knopdev.casaemdia.model.Suggestion
import br.com.knopdev.casaemdia.ui.common.displayName
import com.google.android.material.button.MaterialButton

class SuggestionAdapter(
    private val onAdd: (Suggestion) -> Unit
) : ListAdapter<Suggestion, SuggestionAdapter.SuggestionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        return SuggestionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion, parent, false),
            onAdd
        )
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) = holder.bind(getItem(position))

    class SuggestionViewHolder(itemView: View, private val onAdd: (Suggestion) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(suggestion: Suggestion) {
            itemView.findViewById<TextView>(R.id.suggestion_category).text = suggestion.category.displayName(itemView.context)
            itemView.findViewById<TextView>(R.id.suggestion_title).text = suggestion.title
            itemView.findViewById<TextView>(R.id.suggestion_description).text = suggestion.description
            itemView.findViewById<MaterialButton>(R.id.add_suggestion_button).setOnClickListener { onAdd(suggestion) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Suggestion>() {
        override fun areItemsTheSame(oldItem: Suggestion, newItem: Suggestion) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Suggestion, newItem: Suggestion) = oldItem == newItem
    }
}
