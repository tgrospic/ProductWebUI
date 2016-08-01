package synereo.client.modules

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import synereo.client.sessionitems.SessionItems
import shared.models.{MessagePost, MessagePostContent}
import shared.RootModels.MessagesRootModel
import synereo.client.components._
import synereo.client.css.{DashboardCSS, PostFullViewCSS, SynereoCommanStylesCSS}
import synereo.client.modalpopups.FullPostViewModal

import scalacss.ScalaCssReact._
import scala.scalajs.js
import org.querki.jquery._

import scala.scalajs.js.timers._
import synereo.client.components.Icon

import scala.language.reflectiveCalls
import org.widok.moment.Moment
import synereo.client.services.SYNEREOCircuit


/**
  * Created by Mandar on 3/11/2016.
  */
//scalastyle:off
object Dashboard {
  val messageLoader: js.Object = "#messageLoader"
  val searchContainer: js.Object = "#searchContainer"
  val dashboardContainerMain: js.Object = "#dashboardContainerMain"
  val FeedTimeOut = 1500


  case class Props(proxy: ModelProxy[Pot[MessagesRootModel]])

  case class State(postMessage: MessagePost, ShowFullPostView: Boolean = false, isMessagePosted: Boolean = false, preventFullPostView: Boolean = true)

  class Backend(t: BackendScope[Props, State]) {
    //scalastyle:off

    /*def mounted(props: Props): Callback = {

    }*/

    def updateContent(e: ReactEventI) = {
      val value = e.target.value
      t.modState(s => s.copy(postMessage = s.postMessage.copy(postContent = s.postMessage.postContent.copy(text = value))))
    }

    def closeFullViewModalPopUp(): Callback = {
      $(dashboardContainerMain).addClass("SynereoCommanStylesCSS_Style-overflowYScroll")
      t.modState(s => s.copy(ShowFullPostView = false))
    }

    def openFullViewModalPopUP(e: ReactEvent): Callback = {
      $(dashboardContainerMain).removeClass("SynereoCommanStylesCSS_Style-overflowYScroll")
      t.modState(s => s.copy(ShowFullPostView = true, preventFullPostView = true))
    }

    def preventFullViewModalPopUP(e: ReactEvent): Callback = {
      val targetLi = e.target
      setTimeout(500) {
        $(targetLi).find(".glance-view-button").addClass(".hide")
      }
      t.modState(s => s.copy(ShowFullPostView = false))
    }

    def handleScroll(e: ReactEvent): Callback = {
      Callback.empty
    }

    def handleMouseEnterEvent(e: ReactEvent): Callback = {
      val targetLi = e.target
      val collapsiblePost: js.Object = $(targetLi).find(".collapse")
      setTimeout(FeedTimeOut) {
        if (!$(collapsiblePost).hasClass("in")) {
          $(collapsiblePost).addClass("in")
        }
      }
      Callback.empty
    }

    //scalastyle:off
    def render(s: State, p: Props) = {
      <.div(^.id := "dashboardContainerMain", ^.className := "container-fluid", DashboardCSS.Style.dashboardContainerMain)(
//        <.div(^.className := "row")(
//          //Left Sidebar
//          <.div(^.id := "searchContainer", ^.className := "col-md-2 sidebar sidebar-left sidebar-animate sidebar-lg-show ",
//            ^.onMouseEnter --> Callback {
//              $(searchContainer).removeClass("sidebar-left sidebar-animate sidebar-lg-show")
//            },
//            ^.onMouseLeave --> Callback {
//              $(searchContainer).addClass("sidebar-left sidebar-animate sidebar-lg-show")
//            }
//          )(
//            //            Footer(Footer.Props(c, r.page))
//            Sidebar(Sidebar.Props())
//          )
//        ),
        //        ),
        <.div(^.className := "container-fluid", DashboardCSS.Style.homeFeedMainContainer)(
          <.div(^.className := "row")(
            <.div(^.className := "col-lg-12 col-md-12 col-sm-12 col-xs-12")(
              <.div(^.className := "row")(
                <.div(^.className := "col-sm-12 col-md-12 col-lg-12")(
                  <.div(^.className := "text-center")(<.span(^.id := "messageLoader", ^.color.white, ^.className := "hidden", Icon.spinnerIconPulse)),
                  <.div(
                    p.proxy().render(
                      messagesRootModel =>
                        HomeFeedList(messagesRootModel.messagesModelList)
                    ),
                    p.proxy().renderFailed(ex => <.div(
                      //                      <.span(^.id := "loginLoader", SynereoCommanStylesCSS.Style.loading, ^.className := "", Icon.spinnerIconPulse),
                      <.div(SynereoCommanStylesCSS.Style.renderFailedMessage)("We are encountering problems with serving the request!")
                    )
                    ),
                    p.proxy().renderPending(ex => <.div(
                      <.div(^.id := "loginLoader", SynereoCommanStylesCSS.Style.messagesLoadingWaitCursor, ^.className := "", Icon.spinnerIconPulse)
                    )
                    ),
                    <.div(
                      if (p.proxy().isEmpty){
                        <.div(^.id := "loginLoader", SynereoCommanStylesCSS.Style.messagesLoadingWaitCursor, ^.className := "", Icon.spinnerIconPulse)
                      } else {
                        <.span()
                      }
                    )
                  ),
                  <.ul(^.id := "homeFeedMediaList", ^.className := "media-list cards-list-home-feed", DashboardCSS.Style.homeFeedContainer, ^.onScroll ==> handleScroll)(
                    for (i <- 1 to 50) yield {
                      if (i % 2 != 0) {
                        <.li(^.id := s"home-feed-card-$i", ^.className := "media", DashboardCSS.Style.CardHolderLiElement, ^.onMouseEnter ==> handleMouseEnterEvent /*, ^.onMouseLeave ==> handleMouseLeaveEvent*/)(
                          <.div(^.className := "card-shadow", DashboardCSS.Style.userPost)(
                            <.div(^.className := "", ^.onClick ==> openFullViewModalPopUP)(
                              <.div(^.className := "col-md-1")(
                                <.img(^.className := "media-object", ^.src := "./assets/synereo-images/default_avatar.jpg", ^.alt := "user avatar", DashboardCSS.Style.homeFeedUserAvatar)
                              ),
                              <.div(^.className := "col-md-11", SynereoCommanStylesCSS.Style.paddingLeftZero)(
                                <.div(DashboardCSS.Style.userNameDescription)(
                                  <.span("James Gosling"),
                                  <.span(MIcon.chevronRight),
                                  <.span(SynereoCommanStylesCSS.Style.synereoBlueText)("Ux love,party at new york"), <.br(),
                                  <.span("just now")
                                ),
                                <.button(^.className := "btn btn-default pull-right", DashboardCSS.Style.homeFeedCardBtn)(MIcon.moreVert)
                              )
                            ),
                            <.div(^.className := "row")(
                              <.div(^.className := "col-md-12")(
                                <.div(DashboardCSS.Style.cardDescriptionContainerDiv)(
                                  <.h3("The Beautiful Iceland", DashboardCSS.Style.cardHeading),
                                  <.div(DashboardCSS.Style.cardText, ^.onClick ==> openFullViewModalPopUP)("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do " +
                                    "eiusmod\ntempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,\nquis nostrud exercitation ullamco laboris nisi ut aliquip "),
                                  <.div(^.id := s"collapse-post-$i", ^.className := "collapse", DashboardCSS.Style.cardText)(
                                    <.div(^.className := "col-md-12", SynereoCommanStylesCSS.Style.paddingLeftZero, ^.onClick ==> openFullViewModalPopUP)(
                                      "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,"
                                    ),
                                    <.div(^.className := "col-md-12 text-uppercase", SynereoCommanStylesCSS.Style.paddingLeftZero)(
                                      <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Iceland"),
                                      <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("SXSW"),
                                      <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Travel"),
                                      <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Landscape"),
                                      <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Lorem")
                                    )
                                  ),
                                  <.button(SynereoCommanStylesCSS.Style.synereoBlueText, DashboardCSS.Style.homeFeedCardBtn,
                                    "data-toggle".reactAttr := "collapse", "data-target".reactAttr := s"#collapse-post-$i", ^.className := "glance-view-button", ^.onClick ==> preventFullViewModalPopUP)(
                                    (MIcon.moreHoriz)
                                  )
                                )
                              )
                            )
                          )
                        )
                      } else {
                        <.li(^.id := s"home-feed-card-$i", ^.className := "media", DashboardCSS.Style.CardHolderLiElement, ^.onMouseEnter ==> handleMouseEnterEvent /*, ^.onMouseLeave ==> handleMouseLeaveEvent*/)(
                          <.div(^.className := "card-shadow", DashboardCSS.Style.userPost)(
                            <.div(^.className := "", ^.onClick ==> openFullViewModalPopUP)(
                              <.div(^.className := "col-md-1")(
                                <.img(^.className := "media-object", ^.src := "./assets/synereo-images/default_avatar.jpg", ^.alt := "user avatar", DashboardCSS.Style.homeFeedUserAvatar)
                              ),
                              <.div(^.className := "col-md-11", SynereoCommanStylesCSS.Style.paddingLeftZero)(
                                <.div(DashboardCSS.Style.userNameDescription)(
                                  <.span("James Gosling"),
                                  <.span(MIcon.chevronRight),
                                  <.span(SynereoCommanStylesCSS.Style.synereoBlueText)("Ux love,party at new york"), <.br(),
                                  <.span("just now")
                                ),
                                <.button(^.className := "btn btn-default pull-right", DashboardCSS.Style.homeFeedCardBtn)(MIcon.moreVert)
                              )
                            ),
                            <.div(^.className := "")(
                              <.div(^.className := "row")(
                                <.div(^.className := "col-md-12")(
                                  <.img(^.src := "./assets/synereo-images/blogpostimg.png", ^.className := "img-responsive", DashboardCSS.Style.cardImage),
                                  <.div(DashboardCSS.Style.cardDescriptionContainerDiv)(
                                    <.h3("The Beautiful Iceland", DashboardCSS.Style.cardHeading),
                                    <.div(DashboardCSS.Style.cardText, ^.onClick ==> openFullViewModalPopUP)("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do " +
                                      "eiusmod\ntempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,\nquis nostrud exercitation ullamco laboris nisi ut aliquip "),
                                    <.div(^.id := s"collapse-post-$i", ^.className := "collapse", DashboardCSS.Style.cardText)(
                                      <.div(^.className := "col-md-12", SynereoCommanStylesCSS.Style.paddingLeftZero, ^.onClick ==> openFullViewModalPopUP)(
                                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,"
                                      ),
                                      <.div(^.className := "col-md-12 text-uppercase", SynereoCommanStylesCSS.Style.paddingLeftZero)(
                                        <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Iceland"),
                                        <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("SXSW"),
                                        <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Travel"),
                                        <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Landscape"),
                                        <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Lorem")
                                      )
                                    ),
                                    <.button(SynereoCommanStylesCSS.Style.synereoBlueText, DashboardCSS.Style.homeFeedCardBtn,
                                      "data-toggle".reactAttr := "collapse", "data-target".reactAttr := s"#collapse-post-$i", ^.className := "glance-view-button", ^.onClick ==> preventFullViewModalPopUP)(
                                      (MIcon.moreHoriz)
                                    )
                                  )
                                )
                              )
                            )
                          )
                        )
                      }
                    }
                  )
                )
              )
            )
          )
        ),
        <.div(
          if (s.ShowFullPostView && s.preventFullPostView) FullPostViewModal(FullPostViewModal.Props(closeFullViewModalPopUp))
          else Seq.empty[ReactElement]
        )
      )
    }
  }

  val component = ReactComponentB[Props]("Dashboard")
    .initialState_P(p => State(new MessagePost("", "", "", "", Nil, new MessagePostContent("", ""))))
    .renderBackend[Backend]
    //    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[Pot[MessagesRootModel]]) = component(Props(proxy))
}

object HomeFeedList {

  case class Props(messages: Seq[MessagePost])

  private val MessagesList = ReactComponentB[Props]("ProjectList")
    .render_P(p => {
      def renderMessages(message: MessagePost) = {

        val userId = SYNEREOCircuit.zoom(_.user.sessionUri).value.split("/")(2)
        var selfConnectionId = message.connections(0).source.split("/")(2)


        var toReceiver = "unknown"
        var fromSender = "unknown"
        if (userId == selfConnectionId) {
          fromSender = "me"
          // get other party ID, if there is one
          if (message.connections.size > 1) {
            if (message.connections(1).source.split("/")(2) == userId) {
              toReceiver = message.connections(1).target.split("/")(2)
            } else {
              toReceiver = message.connections(1).source.split("/")(2)
            }
            // ToDo: look up name of Receiver and use friendly name
          } else {
            toReceiver = "self"
          }
        } else {
          fromSender = "me"
          // ToDo: Look up name of Sender and use friendly name
          toReceiver = "me"
        }

        <.li(^.id := "home-feed-card", ^.className := "media", DashboardCSS.Style.CardHolderLiElement /*, ^.onMouseEnter ==> handleMouseEnterEvent , ^.onMouseLeave ==> handleMouseLeaveEvent*/)(
          <.div(^.className := "card-shadow", DashboardCSS.Style.userPost)(
            <.div(^.className := "")(
              <.div(^.className := "col-md-1")(
                <.img(^.className := "media-object", ^.src := "./assets/synereo-images/default_avatar.jpg", ^.alt := "user avatar", DashboardCSS.Style.homeFeedUserAvatar)
              ),
              <.div(^.className := "col-md-11", SynereoCommanStylesCSS.Style.paddingLeftZero)(
                <.div(DashboardCSS.Style.userNameDescription)(
                  <.span(fromSender),
                  <.span(MIcon.chevronRight),
                  <.span(SynereoCommanStylesCSS.Style.synereoBlueText)("Ux love,party at new york"), <.br(),
                  <.div("data-toggle".reactAttr := "tooltip", "title".reactAttr := message.created, "data-placement".reactAttr := "right")(Moment(message.created).format("LLL").toLocaleString)
                ),
                <.button(^.className := "btn btn-default pull-right", DashboardCSS.Style.homeFeedCardBtn)(MIcon.moreVert)
              )
            ),
            <.div(^.className := "")(
              <.div(^.className := "row")(
                <.div(^.className := "col-md-12")(
                  //                  <.img(^.src := "./assets/synereo-images/blogpostimg.png", ^.className := "img-responsive", DashboardCSS.Style.cardImage),
                  <.div(DashboardCSS.Style.cardDescriptionContainerDiv)(

                    <.h3(message.postContent.subject, DashboardCSS.Style.cardHeading),
                    <.div(DashboardCSS.Style.cardText)(
                      //                      <.h3(message.postContent.subject),
                      <.div()(
                        <.div(^.className := "col-md-8 col-sm-8 col-xs-12", PostFullViewCSS.Style.marginLeft15PX)(
                          message.postContent.text
                        ),
                        <.div(^.className := "col-md-4 col-sm-4 col-xs-12")(

                          if (message.postContent.imgSrc != "") {
                            <.img(^.src := message.postContent.imgSrc, ^.height := "100.px", ^.width := "100.px", DashboardCSS.Style.imgBorder)
                          } else {
                            <.div("")
                          }

                        )
                      ),
                      <.br(),
                      <.button(SynereoCommanStylesCSS.Style.synereoBlueText, DashboardCSS.Style.homeFeedCardBtn)(MIcon.moreHoriz)
                    )
                  )
                )
              )
            )
          )
        )
      }
      <.ul(^.id := "homeFeedMediaList", ^.className := "media-list cards-list-home-feed", DashboardCSS.Style.homeFeedContainer)(
        //        p.messages.map(message => message.created.toLocaleString),
        p.messages map renderMessages
        /*for (i <- 1 to 50) yield {
                  if (i % 2 != 0) {
                    <.li(^.id := s"home-feed-card-$i", ^.className := "media", DashboardCSS.Style.CardHolderLiElement /*, ^.onMouseEnter ==> handleMouseEnterEvent , ^.onMouseLeave ==> handleMouseLeaveEvent*/)(
                      <.div(^.className := "card-shadow", DashboardCSS.Style.userPost)(
                        <.div(^.className := "")(
                          <.div(^.className := "col-md-1")(
                            <.img(^.className := "media-object", ^.src := "./assets/synereo-images/default_avatar.jpg", ^.alt := "user avatar", DashboardCSS.Style.homeFeedUserAvatar)
                          ),
                          <.div(^.className := "col-md-11", SynereoCommanStylesCSS.Style.paddingLeftZero)(
                            <.div(DashboardCSS.Style.userNameDescription)(
                              <.span("James Gosling"),
                              <.span(MIcon.chevronRight),
                              <.span(SynereoCommanStylesCSS.Style.synereoBlueText)("Ux love,party at new york"), <.br(),
                              <.span("just now")
                            ),
                            <.button(^.className := "btn btn-default pull-right", DashboardCSS.Style.homeFeedCardBtn)(MIcon.moreVert)
                          )
                        ),
                        <.div(^.className := "row")(
                          <.div(^.className := "col-md-12")(
                            <.div(DashboardCSS.Style.cardDescriptionContainerDiv)(
                              <.h3("The Beautiful Iceland", DashboardCSS.Style.cardHeading),
                              <.div(DashboardCSS.Style.cardText)("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do " +
                                "eiusmod\ntempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,\nquis nostrud exercitation ullamco laboris nisi ut aliquip "),
                              <.button(SynereoCommanStylesCSS.Style.synereoBlueText, DashboardCSS.Style.homeFeedCardBtn,
                                "data-toggle".reactAttr := "collapse", "data-target".reactAttr := s"#collapse-post-$i", ^.className := "glance-view-button")(
                                (MIcon.moreHoriz)
                              ),
                              <.div(^.id := s"collapse-post-$i", ^.className := "collapse", DashboardCSS.Style.cardText /*, ^.onClick ==> openFullViewModalPopUP*/)(
                                <.div(^.className := "col-md-12", SynereoCommanStylesCSS.Style.paddingLeftZero)(
                                  "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,"
                                ),
                                <.div(^.className := "col-md-12 text-uppercase", SynereoCommanStylesCSS.Style.paddingLeftZero)(
                                  <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Iceland"),
                                  <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("SXSW"),
                                  <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Travel"),
                                  <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Landscape"),
                                  <.button(^.`type` := "button", ^.className := "btn btn-primary text-uppercase", DashboardCSS.Style.cardPostTagBtn)("Lorem")
                                )
                              )
                            )
                          )
                        )
                      )
                    )
                  }
                  else {
                    <.span()
                  }
                }*/
      )

    })
    .build

  def apply(messages: Seq[MessagePost]) =
    MessagesList(Props(messages))

}