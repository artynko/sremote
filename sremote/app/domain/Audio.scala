package domain

import javax.persistence.GeneratedValue
import com.fasterxml.jackson.annotation.JsonAutoDetect
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Transient

object Audio {
  def apply(containerId: String, audioId: String, title: String, artist: String, thumbnailUrl: String) = {
    val v = new Audio
    v.title = title
    v.artist = artist
    v.thumbnailUrl = thumbnailUrl
    v.audioId = audioId
    v.containerId = containerId
    v.links = Array(CommandLink("play", s"http://192.168.0.190:9000/playback/play/audio/$containerId/$audioId"),
      CommandLink("queue", s"http://192.168.0.190:9000/playback/queue/audio/$containerId/$audioId"),
      CommandLink("addFavorite", s"http://192.168.0.190:9000/mediabrowser/audio/favorites/$containerId/$audioId"),
      CommandLink("removeFavorite", s"http://192.168.0.190:9000/mediabrowser/audio/favorites/$containerId/$audioId"))
    v
  }
}

@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class Audio() {
  var containerId: String = _
  var audioId: String = _
  var title: String = _
  var artist: String = _
  var thumbnailUrl: String = _
  @Transient var links: Array[CommandLink] = _

  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
  var id: Long = _

}