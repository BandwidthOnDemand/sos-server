import org.scalatra.LifeCycle
import javax.servlet.ServletContext
import nl.surfnet.sos.web.OpenSocialServerController
import nl.surfnet.sos.web.SabNgController
import nl.surfnet.sos.web.SosController

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {

    context mount (new SosController, "/*")
    context mount (new OpenSocialServerController, "/os/*")
    context mount (new SabNgController, "/sabng/*")
  }
}