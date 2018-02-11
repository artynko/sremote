package domain

import javax.persistence.GeneratedValue
import com.fasterxml.jackson.annotation.JsonAutoDetect
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Transient

object Video {
  def apply(containerId: String, videoId: String, title: String, thumbnailUrl: String) = {
    val v = new Video
    v.title = title
    v.thumbnailUrl  = thumbnailUrl
    v.videoId = videoId
    v.containerId = containerId
    v.links = Array(CommandLink("play", s"http://192.168.0.190:9000/playback/play/video/$containerId/$videoId"))
    v
  }
}

@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class Video() {
  var containerId: String = _
  var videoId: String = _
  var title: String = _
  var thumbnailUrl: String = _
  @Transient var links: Array[CommandLink] = _

  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
  var id: Long = _

}