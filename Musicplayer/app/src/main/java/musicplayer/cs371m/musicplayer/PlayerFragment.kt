package musicplayer.cs371m.musicplayer

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import musicplayer.cs371m.musicplayer.databinding.PlayerFragmentBinding
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.floor

class PlayerFragment : Fragment() {
    private val userModifyingSeekBar = AtomicBoolean(false)
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: RVDiffAdapter

    private var _binding: PlayerFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL
        )
        rv.addItemDecoration(dividerItemDecoration)
    }


    private fun updateDisplay() {
        if (_binding == null) return

        // Update current and next song text
        binding.playerCurrentSongText.text = viewModel.getCurrentSongName()
        binding.playerNextSongText.text = viewModel.getNextSongName()
        // Update play/pause button icon
        if (viewModel.isPlaying) {
            binding.playerPlayPauseButton.setImageResource(R.drawable.ic_pause_black_24dp)
        } else {
            binding.playerPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }

        // Update loop indicator
        if (viewModel.loopMode) {
            binding.loopIndicator.setBackgroundColor(Color.RED)
        } else {
            binding.loopIndicator.setBackgroundColor(Color.WHITE)
        }

        // Update seek bar and time displays
        binding.playerSeekBar.max = viewModel.player.duration
        binding.playerSeekBar.progress = viewModel.player.currentPosition
        binding.playerTimePassedText.text = convertTime(viewModel.player.currentPosition)
        binding.playerTimeRemainingText.text =
            convertTime(viewModel.player.duration - viewModel.player.currentPosition)

        // Notify adapter that the current song has changed and update the highlight
        adapter.notifyDataSetChanged()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        adapter = RVDiffAdapter(viewModel) { songIndex ->
            viewModel.playSong(songIndex)
            updateDisplay()
        }
        viewModel.onSongFinished = {
            updateDisplay()
        }
        binding.playerRV.adapter = adapter
        binding.playerRV.layoutManager = LinearLayoutManager(requireContext())
        initRecyclerViewDividers(binding.playerRV)

        // Ensure the initial list is displayed
        adapter.submitList(viewModel.getSongResources())


        // Set button click listeners
        binding.playerPlayPauseButton.setOnClickListener {
            if (viewModel.isPlaying) {
                viewModel.pause()
            } else {
                viewModel.play()
            }
            updateDisplay()
        }

        binding.playerSkipForwardButton.setOnClickListener {
            viewModel.nextSong()
            updateDisplay()
        }

        binding.playerSkipBackButton.setOnClickListener {
            viewModel.prevSong()
            updateDisplay()
        }

        // Shuffle button click listener
        binding.btnShuffle.setOnClickListener {
            viewModel.shuffleAndReturnCopyOfSongInfo()  // Shuffle the existing list
            adapter.submitList(viewModel.getSongResources()) // Update the adapter with the shuffled list
            updateDisplay()
        }

        // Loop indicator click listener
        binding.loopIndicator.setOnClickListener {
            viewModel.loopMode = !viewModel.loopMode // Toggle loop mode
            updateDisplay() // Refresh the UI
        }

        // Set seek bar listener
        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.player.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                userModifyingSeekBar.set(true)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                userModifyingSeekBar.set(false)
            }
        })

        updateDisplay()

        // Launch coroutine for updating time display
        val millisec = 100L
        viewLifecycleOwner.lifecycleScope.launch {
            displayTime(millisec)
        }
    }


    private suspend fun displayTime(millisec: Long) {
        while (viewLifecycleOwner.lifecycleScope.coroutineContext.isActive) {
            if (!userModifyingSeekBar.get()) {
                binding.playerSeekBar.progress = viewModel.player.currentPosition
                binding.playerTimePassedText.text = convertTime(viewModel.player.currentPosition)
                binding.playerTimeRemainingText.text =
                    convertTime(viewModel.player.duration - viewModel.player.currentPosition)
            }
            delay(millisec)
        }
    }

    private fun convertTime(milliseconds: Int): String {
        val minutes = floor(milliseconds / 1000.0 / 60.0).toInt()
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
