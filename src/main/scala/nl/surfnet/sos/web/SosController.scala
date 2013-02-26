package nl.surfnet.sos.web

import org.scalatra.ScalatraServlet
import nl.surfnet.sos.OpenSocialStore
import nl.surfnet.sos.Group
import scala.xml.Xhtml

class SosController extends ScalatraServlet {

  get("/") {
    contentType = "text/html"

    Xhtml.toXhtml(
      <html>
        <head>
          <link rel="stylesheet" type="text/css" href="css/main.css" />
          <script src="/js/jquery-1.9.1.min.js"></script>
          <script src="/js/main.js"></script>
        </head>
        <body>
          <div id="messages">
          </div>
          <h1>Users</h1>
          <ul id="users">
          { for (user <- OpenSocialStore.users) yield
            <li data-nameid={ user._1 }>
              <a href={"/social/rest/groups/" + user._1}>{user._1}</a>
              <ul class="buttons">
                <li>
                  { groupButton(user._2, OpenSocialStore.nocEngineerId, "NOC Engineer") ++
                    groupButton(user._2, OpenSocialStore.appManagerId, "App Manager") }
                </li>
                <li>{ for (group <- user._2; if group.id != OpenSocialStore.nocEngineerId; if group.id != OpenSocialStore.appManagerId) yield
                      groupButton(user._2, group.id, group.description)
                    }
                </li>
              </ul>
            </li>
          }
          </ul>
        </body>
      </html>
    )
  }

  def groupButton(groups: Set[Group], groupId: String, name: String) =
    <a href="#" data-roleid={ groupId } data-desc={name} class={ if(groups.map(g => g.id).contains(groupId)) "btn remove" else "btn add" }>{name}</a>

}