package musicplayer.cs371m.musicplayer

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository()
    private  var songResources = repository.fetchData()

    var player: MediaPlayer = MediaPlayer.create(
        getApplication<Application>().applicationContext,
        getCurrentSongResourceId()
    ).apply {
        setOnCompletionListener {
            if (loopMode) {
                seekTo(0) // Restart the current song
                start()
                //songsPlayed++ // Increment when the song restarts due to looping
            } else {
                nextSong() // Move to the next song if not looping
            }
            onSongFinished?.invoke() // Notify UI that the song finished
        }
    }

    var currentIndex = 0
    var loopMode = false
    var songsPlayed = 0
    var isPlaying = false
    // Callback to notify PlayerFragment when the song finishes
    var onSongFinished: (() -> Unit)? = null
    var hasStarted = false

    fun shuffleAndReturnCopyOfSongInfo(): MutableList<SongInfo> {
        val currentSong = songResources[currentIndex]
        val shuffledList = songResources.toMutableList().apply {
            removeAt(currentIndex) // Remove the current song
            shuffle() // Shuffle the remaining songs
            add(currentIndex, currentSong) // Add the current song back at its position
        }
        songResources = shuffledList
        return shuffledList
    }
    fun getSongResources(): List<SongInfo> = songResources.toList()

    // Get the name of the current song
    fun getCurrentSongName(): String {
        return songResources[currentIndex].name
    }

    // Get the name of the next song
    fun getNextSongName(): String {
        val nextIndex = (currentIndex + 1) % songResources.size
        return songResources[nextIndex].name
    }

    // Calculate the next index
    private fun nextIndex(): Int {
        return (currentIndex + 1) % songResources.size
    }

    fun nextSong() {
        // User manually pressed next, so override loop mode
        currentIndex = nextIndex()
        //loopMode = false // Disable loop mode when user manually skips
        player.reset()
        player.setDataSource(
            getApplication<Application>().applicationContext.resources.openRawResourceFd(getCurrentSongResourceId())
        )
        player.prepare()
        if (isPlaying) {
            player.start()
            songsPlayed++ // Increment only when the song actually starts
        }
    }

    fun prevSong() {
        // User manually pressed previous, so override loop mode
        currentIndex = if (currentIndex == 0) songResources.size - 1 else currentIndex - 1
        //loopMode = false // Disable loop mode when user manually skips
        player.reset()
        player.setDataSource(
            getApplication<Application>().applicationContext.resources.openRawResourceFd(getCurrentSongResourceId())
        )
        player.prepare()
        if (isPlaying) {
            player.start()
            songsPlayed++ // Increment only when the song actually starts
        }
    }

    // Get the resource ID of the current song
    fun getCurrentSongResourceId(): Int {
        return songResources[currentIndex].rawId
    }

    // Play a specific song by index
    fun playSong(songIndex: Int) {
        currentIndex = songIndex
        player.reset()
        player.setDataSource(
            getApplication<Application>().applicationContext.resources.openRawResourceFd(getCurrentSongResourceId())
        )
        player.prepare()
        player.start() // Start playback immediately
        isPlaying = true
        songsPlayed++
    }


    // Pause the player
    fun pause() {
        player.pause()
        isPlaying = false
    }

    // Start or resume playback
    fun play() {
        player.start()
        isPlaying = true
        if (!hasStarted) {
            songsPlayed++
            hasStarted = true
        }
    }

}
