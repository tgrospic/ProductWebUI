package livelygig.client

import livelygig.client.services.TodoStore
import japgolly.scalajs.react.{ReactDOM, React}
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._

import org.scalajs.dom
import livelygig.client.components.{Icon, GlobalStyles}
import livelygig.client.css.AppCSS
import livelygig.client.logger._
import livelygig.client.modules._
import livelygig.client.modules.CreateAgent.Props
import livelygig.client.css.HeaderCSS
import livelygig.client.css.FooterCSS
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scalacss.mutable.GlobalRegistry
import livelygig.client.models.UserModel
@JSExport("LGMain")
object LGMain extends js.JSApp {
  // Define the locations (pages) used in this application
  sealed trait Loc
  case object DashboardLoc extends Loc
  case object TodoLoc extends Loc
  case object CreateAgentLoc extends  Loc
  case object EmailValidationLoc extends Loc
  case object AgentLoginLoc extends Loc
  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._
    (staticRoute(root, DashboardLoc) ~> renderR(ctl => Dashboard.component(ctl))
//      |staticRoute("#CreateNewAgent", CreateNewAgentLoc) ~> renderR(ctl => CreateNewAgent.component(ctl))
//      renderR(ctl => Todo(TodoStore)(ctl))
      |staticRoute("#addnewagent", CreateAgentLoc) ~> render(CreateAgent.component(Unit))
      |staticRoute("#emailvalidation", EmailValidationLoc) ~> renderR(ctl => EmailValidation.component(ctl))
      |staticRoute("#agentlogin", AgentLoginLoc) ~> renderR(ctl => AgentLogin.component(ctl))

      ).notFound(redirectToPage(DashboardLoc)(Redirect.Replace))
  }.renderWith(layout)

  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.id:="naviContainer" , HeaderCSS.Style.naviContainer ,^.className := "navbar navbar-fixed-top")(
        <.div()(
          <.div (^.className:="navbar-header")(
            <.button(^.className:="navbar-toggle","data-toggle".reactAttr := "collapse" , "data-target".reactAttr:="#navi-collapse")(
              <.span(Icon.thList)
            ),
            c.link(DashboardLoc)(HeaderCSS.Style.logoContainer,^.className := "navbar-header",<.img(HeaderCSS.Style.imgLogo, ^.src := "./assets/images/logo-symbol.png"))
          ),
          <.div(^.id:="navi-collapse", ^.className := "collapse navbar-collapse")(
            MainMenu(MainMenu.Props(c, r.page)),
            <.div(HeaderCSS.Style.LoginInMenuItem)(
              <.div(HeaderCSS.Style.displayInline)(<.span(Icon.bell)),
              <.div(HeaderCSS.Style.displayInline) ("Dale Steyn"),
              c.link(CreateAgentLoc)(HeaderCSS.Style.displayInline)( c.link(DashboardLoc)(HeaderCSS.Style.logoContainer,<.img(HeaderCSS.Style.imgLogo, ^.src := "./assets/images/profile.jpg")))
              // Todo(Todo.Props(r.page,))
               //AddNewAgent(AddNewAgent.Props(c, r.page))
            )
          )
        )
      ),
      // currently active module is shown in this container
      //added
      <.div(^.id:="middelNaviContainer",HeaderCSS.Style.middelNaviContainer)(
        <.div(^.className :="row")(
          <.div(^.className:="col-md-12 col-sm-12 col-xs-12")(
            <.div(^.className:="btn-group")(
              <.button(HeaderCSS.Style.projectCreateBtn, ^.className:="btn dropdown-toggle","data-toggle".reactAttr := "dropdown")("Recommended Matches ")(
                <.span(^.className:="caret")
              ),
              <.ul(HeaderCSS.Style.dropdownMenuWidth, ^.className:="dropdown-menu")(
                <.li()(<.a(^.href:="#")("Suggested Matches")),
                <.li()(<.a(^.href:="#")("Available")),
                <.li()(<.a(^.href:="#")("Active Unavailable")),
                <.li()(<.a(^.href:="#")("Inactive")),
                <.li()(<.a(^.href:="#")("Suppressed")),
                <.li(^.className:="divider")(),
                <.li()(<.a(^.href:="#")("Customized View 1")),
                <.li()(<.a(^.href:="#")("Customize..."))
              )
            ),
            <.button(HeaderCSS.Style.createNewProjectBtn, ^.className:="btn")("Create New Project")()
          )
        )
      ),   //recommended matches

      <.div()(r.render()),

      <.nav(^.id:="footerContainer", FooterCSS.Style.footerContainer)(
        <.div(^.className:="row")(
          <.div(^.className:="col-md-4 col-sm-4 col-xs-3")(
            <.div(FooterCSS.Style.footGlyphContainer)(
              <.div(FooterCSS.Style.displayInline)(
                <.a(FooterCSS.Style.displayInlineGlyph)(^.href:="https://github.com/LivelyGig", "data-toggle".reactAttr := "tooltip", "title".reactAttr :="View our GitHub repositories" )(<.span()(Icon.github))),
              <.div(FooterCSS.Style.displayInline)(
                <.a(FooterCSS.Style.displayInlineGlyph)(^.href:="https://twitter.com/LivelyGig", "data-toggle".reactAttr := "tooltip", "title".reactAttr :="View our GitHub repositories" )(
                  <.span()(Icon.twitter))),
              <.div(FooterCSS.Style.displayInline)(
                <.a(FooterCSS.Style.displayInlineGlyph)(^.href:="https://facebook.com/LivelyGig", "data-toggle".reactAttr := "tooltip", "title".reactAttr :="View our GitHub repositories" )(
                  <.span()(Icon.facebook))),
              <.div(FooterCSS.Style.displayInline)(
                <.a(FooterCSS.Style.displayInlineGlyph)(^.href:="https://google.com/LivelyGig", "data-toggle".reactAttr := "tooltip", "title".reactAttr :="View our GitHub repositories" )(
                  <.span()(Icon.googlePlus))),
              <.div(FooterCSS.Style.displayInline)(
                <.a(FooterCSS.Style.displayInlineGlyph)(^.href:="https://youtube.com/LivelyGig", "data-toggle".reactAttr := "tooltip", "title".reactAttr :="View our GitHub repositories" )(
                  <.span()(Icon.youtube))),
              <.div(FooterCSS.Style.displayInline)(
                <.a(FooterCSS.Style.displayInlineGlyph)(^.href:="https://linkedin.com/LivelyGig", "data-toggle".reactAttr := "tooltip", "title".reactAttr :="View our GitHub repositories" )(
                  <.span()(Icon.linkedin))),
              <.div(FooterCSS.Style.displayInline)(
                <.a(FooterCSS.Style.displayInlineGlyph)(^.href:="https://slack.com/LivelyGig", "data-toggle".reactAttr := "tooltip", "title".reactAttr :="View our GitHub repositories" )(
                  <.span()(Icon.slack)))
            )
          ),
          <.div(^.className:="col-md-8 col-sm-8 col-xs-9")(
            Footer(Footer.Props(c, r.page))
          )
        )
      ),

        <.div(^.className:="col-md-8 col-sm-8 col-xs-9")(
//      CreateNewAgent(CreateNewAgent.Props(c, r.page))
    )

    )
  }
  @JSExport
  def main(): Unit = {
    log.warn("Application starting")
    // send log messages also to the server
    log.enableServerLogging("/logging")
    log.info("This message goes to server as well")
    // create stylesheet
    GlobalStyles.addToDocument()
    AppCSS.load
    GlobalRegistry.addToDocumentOnRegistration()
    // create the router
    val router = Router(BaseUrl(dom.window.location.href.takeWhile(_ != '#')), routerConfig)
    // tell React to render the router in the document body
    //ReactDOM.render(router(), dom.document.getElementById("root"))
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }
}