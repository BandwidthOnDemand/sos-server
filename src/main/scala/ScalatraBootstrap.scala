import org.scalatra.LifeCycle
import javax.servlet.ServletContext
import nl.surfnet.sos.web.OpenSocialServerController
import nl.surfnet.sos.web.SabNgController

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {

    context mount (new OpenSocialServerController, "/*")
    context mount (new SabNgController, "/sabng/*")
  }
}