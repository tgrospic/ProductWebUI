package synereo.client.modalpopups

import japgolly.scalajs.react.extra.OnUnmount
import japgolly.scalajs.react.vdom.prefix_<^._
import synereo.client.components.Bootstrap.Button
import synereo.client.components.Bootstrap.CommonStyle
import synereo.client.components.Bootstrap.Modal
import synereo.client.components.GlobalStyles
import synereo.client.components.Icon._
import synereo.client.css._
import scalacss.Defaults._
import scalacss.ScalaCssReact._
import scala.language.reflectiveCalls
import shared.dtos.{EstablishConnection, IntroConnections}
import japgolly.scalajs.react
import japgolly.scalajs.react.{Callback, _}
import org.querki.jquery._
import shared.models.ConnectionsModel
import synereo.client.components.{ConnectionsSelectize, _}
import synereo.client.services.SYNEREOCircuit
import synereo.client.utils.{ConnectionsUtils, ContentUtils}
import synereo.client.components.Bootstrap._
import scala.scalajs.js

// scalastyle:off
object NewConnection {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(buttonName: String, addStyles: Seq[StyleA] = Seq(), addIcons: Icon, title: String = "Introduce Connections")

  case class State(showConnectionsForm: Boolean = false)

  abstract class RxObserver[BS <: BackendScope[_, _]](scope: BS) extends OnUnmount {
  }

  class Backend(t: BackendScope[Props, State]) extends RxObserver(t) {

    def mounted(props: Props): Callback = {
      t.modState(s => s.copy(showConnectionsForm = true))
    }

    def addConnectionForm(): Callback = {
      t.modState(s => s.copy(showConnectionsForm = true))
    }

    def addConnections(): Callback = {
      t.modState(s => s.copy(showConnectionsForm = false))
    }
  }

  val component = ReactComponentB[Props]("Connections")
    .initialState(State())
    .backend(new Backend(_))
    .renderPS(($, P, S) => {
      val B = $.backend
      <.div(/*ProjectCSS.Style.displayInitialbtn*/)(
        Button(Button.Props(B.addConnectionForm(), CommonStyle.default, P.addStyles, "", P.title, className = ""), P.title),
        if (S.showConnectionsForm) ConnectionsForm(ConnectionsForm.Props(B.addConnections, "Introduce Connections"))
        else
          Seq.empty[ReactElement]
      )
    })
    //  .componentDidMount(scope => scope.backend.mounted(scope.props))
    .configure(OnUnmount.install)
    .build

  def apply(props: Props) = component(props)
}

object ConnectionsForm {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(submitHandler: () => Callback, header: String)

  case class State(postConnection: Boolean = false, selectizeInputId: String = "pstNewCnxnSelParent",
                   introConnections: IntroConnections = IntroConnections(),
                   establishConnection: EstablishConnection = EstablishConnection(), introduceUsers: Boolean = true,
                   chkCnxnNewUser: Boolean = true, chkCnxnExstUser: Boolean = false, agentUid: String = "", userName: String = "")

  val connectionSelectize = SYNEREOCircuit.connect(_.connections)


  case class ConnectionsFormBackend(t: BackendScope[Props, State]) {
    def hide: Callback = Callback {
      jQuery(t.getDOMNode()).modal("hide")
    }

    def fromSelecize(): Callback = {
      val cnxns = ConnectionsSelectize.getConnectionNames(t.state.runNow().selectizeInputId)
      val msg = if (cnxns.length > 1) {
        s"Hi ${cnxns(0)} and ${cnxns(1)}, \n Here's an introduction for the two of you to connect. \n \n Best regards, \n ${t.state.runNow().userName}"
      } else {
        s"Hi <Recipient 1> and <Recipient 2>, \n Here's an introduction for the two of you to connect. \n \n Best regards, \n ${t.state.runNow().userName}"
      }
      t.modState(s => s.copy(introConnections = s.introConnections.copy(aMessage = msg)))
    }

    def mounted(props: ConnectionsForm.Props): Callback = {
      val usr = SYNEREOCircuit.zoom(_.user).value.name
      val msg = s"Hi <Recipient 1> and <Recipient 2>, \n Here's an introduction for the two of you to connect. \n \n Best regards, \n ${usr}"
      t.modState(s => s.copy(userName = usr, introConnections = s.introConnections.copy(aMessage = msg)))
    }

    def hideModal(): Unit = {
      jQuery(t.getDOMNode()).modal("hide")
    }

    def chkCnxnExstUser(e: ReactEventI): react.Callback = {
      val state = t.state.runNow()
      t.modState(s => s.copy(chkCnxnExstUser = true, introduceUsers = false, chkCnxnNewUser = false))
    }

    def chkCnxnNewUser(e: ReactEventI): react.Callback = {
      val state = t.state.runNow()
      t.modState(s => s.copy(chkCnxnNewUser = true, introduceUsers = false, chkCnxnExstUser = false))
    }

    def chkCnxnExstandOther(e: ReactEventI): react.Callback = {
      val state = t.state.runNow()
      t.modState(s => s.copy(introduceUsers = true, chkCnxnNewUser = true, chkCnxnExstUser = false))
    }

    def getCnxn(uri: String): Option[ConnectionsModel] = {
      //      val test = SYNEREOCircuit.zoom(_.connections.get.connectionsResponse)
      //      println(test)
      val connectionsModel = SYNEREOCircuit.zoom(_.connections.connectionsResponse).value
      if (!connectionsModel.isEmpty)
        connectionsModel.find(e => e.connection.target.contains(uri))
      else
        None
    }

    def introduceTwoUsers() = {
      $("#cnxnError".asInstanceOf[js.Object]).addClass("hidden")
      val state = t.state.runNow()
      val msg = state.introConnections.aMessage.replaceAll("/", "//")
      val uri = SYNEREOCircuit.zoom(_.sessionRootModel.sessionUri).value
      val connections = ConnectionsSelectize.getConnectionsFromSelectizeInput(state.selectizeInputId)
      if (connections.length == 2) {
        val content = state.introConnections.copy(aConnection = connections(0), bConnection = connections(1),
          sessionURI = uri, alias = "alias", aMessage = msg, bMessage = msg)
        //        SYNEREOCircuit.dispatch(PostNewConnection(content))
        ContentUtils.postNewConnection(content)
        t.modState(s => s.copy(postConnection = true))
      } else {
        $("#cnxnError".asInstanceOf[js.Object]).removeClass("hidden")
        t.modState(s => s.copy(postConnection = false))
      }
    }

    def establishConnection(): react.Callback = {
      val state = t.state.runNow()
      $("#agentFieldError".asInstanceOf[js.Object]).addClass("hidden")
      getCnxn(state.agentUid) match {
        case Some(cnxn) =>
          $("#agentFieldError".asInstanceOf[js.Object]).removeClass("hidden")
          t.modState(s => s.copy(postConnection = false))
        case None =>
          val uri = SYNEREOCircuit.zoom(_.sessionRootModel.sessionUri).value
          val content = state.establishConnection.copy(sessionURI = uri,
            aURI = ConnectionsUtils.getSelfConnnection().source,
            bURI = s"agent://${state.agentUid}", label = "869b2062-d97b-42dc-af5d-df28332cdda1")
          //          SYNEREOCircuit.dispatch(PostNewConnection(content))
          ContentUtils.postNewConnection(content)
          t.modState(s => s.copy(postConnection = true))
      }
    }

    def submitForm(e: ReactEventI): react.Callback = {
      e.preventDefault()
      val state = t.state.runNow()
      if (state.introduceUsers) {
        introduceTwoUsers
      }
      else {
        establishConnection
      }
    }

    def formClosed(state: ConnectionsForm.State, props: ConnectionsForm.Props): Callback = {
      // call parent handler with the new item and whether form was OK or cancelled
      //      println(state.postConnection)
      props.submitHandler()
    }

    def updateContent(e: ReactEventI): react.Callback = {
      val value = e.target.value
      t.modState(s => s.copy(introConnections = s.introConnections.copy(bMessage = value, aMessage = value)))
    }

    def updateAgentUid(e: ReactEventI): react.Callback = {
      val value = e.target.value
      t.modState(s => s.copy(agentUid = value))
    }

  }

  private val component = ReactComponentB[Props]("PostConnections")
    .initialState_P(p => State())
    .backend(new ConnectionsFormBackend(_))
    .renderPS((t, P, S) => {

      val headerText = P.header
      Modal(
        Modal.Props(
          // header contains a cancel button (X)
          header = hide => <.span(<.button(^.tpe := "button", bss.close, ^.onClick --> hide, Icon.close), <.div()(headerText)),
          // this is called after the modal has been hidden (animation is completed)
          closed = () => t.backend.formClosed(S, P)
        ),
        <.form(^.onSubmit ==> t.backend.submitForm)(
          <.div(^.className := "row")(
            <.div()(
              <.div()(<.input(^.`type` := "radio", ^.name := "userConnection", ^.checked := S.chkCnxnNewUser, ^.onChange ==> t.backend.chkCnxnExstandOther),
                " Introduce your existing connections to each other."), <.br(),
              <.div(^.marginLeft := "15px", (!S.introduceUsers == true) ?= ConnectionsCSS.Style.hidden)(
                <.div(<.h5("Connections:")),
                <.div(^.id := s"${S.selectizeInputId}")(
                  //  SYNEREOCircuit.connect(_.connections)(conProxy => ConnectionsSelectize(ConnectionsSelectize.Props(conProxy, s"${s.selectizeInputId}")))
                  ConnectionsSelectize(ConnectionsSelectize.Props(s"${S.selectizeInputId}", t.backend.fromSelecize, Option(2)))
                ),
                <.div(^.id := "cnxnError", ^.className := "hidden text-danger")
                ("Please provide Only 2 Connections... !!!"),
                <.div((!S.introduceUsers) ?= ConnectionsCSS.Style.hidden,
                  <.div(<.h5("Introduction:")),
                  <.div()(
                    <.textarea(^.rows := 6,
                      ^.value := S.introConnections.aMessage, ^.onChange ==> t.backend.updateContent, ^.className := "form-control")
                    //                  <.div(s.introConnections.aMessage)
                  )

                )
              ),
              //  <.div()(<.input(^.`type` := "radio", ^.name := "userConnection", ^.onChange ==> chkCnxnNewUser),
              // " Invite new user(s) to sign up and  connect with you."), <.br(),

              <.div()(<.input(^.`type` := "radio", ^.name := "userConnection", /*^.checked := S.chkCnxnExstUser,*/ ^.onChange ==> t.backend.chkCnxnExstUser),
                " Introduce yourself to existing user(s)."), <.br()

            ),

            //            else if (s.chkCnxnNewUser == true) {
            //              <.div()(
            //                <.input(^.`type` := "text", ^.className := "form-control", ^.placeholder := "Please Enter Email ID")
            //              )
            //            }
            //            else if (s.introduceTwoUsers == true) {
            if (S.chkCnxnExstUser == true) {
              <.div(^.marginLeft := "15px")(
                <.input(^.`type` := "text", ^.className := "form-control", ^.placeholder := "User ID, e.g. 2a6d5dcb40634e8dafa4ec0f562b8fda, 05d1ba8d0d7945359b717873b7e7f6bf",
                  ^.value := S.agentUid, ^.onChange ==> t.backend.updateAgentUid),
                <.div(^.id := "agentFieldError", ^.className := "hidden")
                ("User with this uid is already added as your connection")
              )
            }
            else
              <.div(),


            //            }
            <.div()(
              <.div(^.className := "text-right")(
                <.button(^.tpe := "submit", ^.className := "btn btn-default", DashboardCSS.Style.createConnectionBtn, /* ^.onClick --> hide*/ "Send"),
                <.button(^.tpe := "button", ^.className := "btn btn-default", NewMessageCSS.Style.newMessageCancelBtn, ^.onClick --> t.backend.hide, "Cancel")
              )
            )
          ),
          <.div(bss.modal.footer)
        ))

    })
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .componentDidUpdate(scope => Callback {
      if (scope.currentState.postConnection) {
        scope.$.backend.hideModal
      }
    })
    .build

  def apply(props: Props) = component(props)
}
