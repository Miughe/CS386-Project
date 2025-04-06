package musicplayer.cs371m.musicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import musicplayer.cs371m.musicplayer.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment() {
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI with current state
        binding.settingsSongsPlayedText.text = viewModel.songsPlayed.toString()
        binding.settingsLoopSwitch.isChecked = viewModel.loopMode

        // Handle OK button click
        binding.settingsOkButton.setOnClickListener {
            viewModel.loopMode = binding.settingsLoopSwitch.isChecked
            findNavController().popBackStack() // Navigate back
        }

        // Handle Cancel button click
        binding.settingsCancelButton.setOnClickListener {
            findNavController().popBackStack() // Navigate back without saving
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
