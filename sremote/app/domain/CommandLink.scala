package domain

import com.fasterxml.jackson.annotation.JsonAutoDetect

object CommandLink {
  def apply(name: String, value: String) = {
    val n = new CommandLink()
    n.name = name;
    n.value = value;
    n
  }
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class CommandLink {
  var name: String = _
  var value: String = _

}