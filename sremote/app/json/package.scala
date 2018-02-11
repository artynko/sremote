import play.api.mvc.Result
import play.api.mvc.Controller
import com.fasterxml.jackson.databind.ObjectMapper

package object json {

  private val objectMapper = new ObjectMapper()

  object Ok extends Controller {
    def apply(a: Any): Result = {
      val response = a match {
        case list: Traversable[Any] => objectMapper.writeValueAsString(list.toArray)
        case _ => objectMapper.writeValueAsString(a)
      }
      Ok(response).as(JSON)
    }
  }
}