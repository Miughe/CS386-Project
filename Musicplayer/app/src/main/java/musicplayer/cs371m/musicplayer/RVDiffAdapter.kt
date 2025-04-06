package musicplayer.cs371m.musicplayer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import musicplayer.cs371m.musicplayer.databinding.SongRowBinding

class RVDiffAdapter(
    private val viewModel: MainViewModel,
    private val clickListener: (songIndex: Int) -> Unit
) : ListAdapter<SongInfo, RVDiffAdapter.ViewHolder>(Diff()) {

    companion object {
        val TAG = "RVDiffAdapter"
    }

    inner class ViewHolder(val songRowBinding: SongRowBinding) :
        RecyclerView.ViewHolder(songRowBinding.root) {
        init {
            // Set click listener for the row
            songRowBinding.root.setOnClickListener {
                clickListener(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the song row layout
        val inflater = LayoutInflater.from(parent.context)
        val binding = SongRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind song data to the row
        val song = getItem(position)
        holder.songRowBinding.songNameText.text = song.name
        holder.songRowBinding.songDurationText.text = song.time

        // Highlight the selected song
        if (viewModel.currentIndex == position) {
            holder.songRowBinding.root.setBackgroundColor(Color.YELLOW)
        } else {
            holder.songRowBinding.root.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    class Diff : DiffUtil.ItemCallback<SongInfo>() {
        // Item identity
        override fun areItemsTheSame(oldItem: SongInfo, newItem: SongInfo): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: SongInfo, newItem: SongInfo): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.rawId == newItem.rawId &&
                    oldItem.time == newItem.time
        }
    }
}