package client.modals

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import client.components.Bootstrap._
import client.components._
import client.css.{FooterCSS, DashBoardCSS}
import client.models.{UserModel}
import scala.util.{Failure, Success}
import scalacss.ScalaCssReact._
import scala.language.reflectiveCalls
import org.querki.jquery._
//case class UserModel (email: String = "", password: String = "",ConfirmPassword:String="", name: String = "", lastName: String = "", createBTCWallet: Boolean = true,
//                      isLoggedIn: Boolean = false, imgSrc: String = "")
object NewAgentForm {
  var addNewAgentState : Boolean = false
  var userModelUpdate = new UserModel ("","","","","",false,false,"")
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(submitHandler: (UserModel, Boolean, Boolean) => Callback)

  case class State(userModel: UserModel, addNewAgent: Boolean = false, showTermsOfServicesForm: Boolean = false)

  case class Backend(t: BackendScope[Props, State]) {
    def hideModal = Callback {
      // instruct Bootstrap to hide the modal
      addNewAgentState = false
      userModelUpdate = new UserModel ("","","","","",false,false,"")
      $(t.getDOMNode()).modal("hide")
    }

    def hidecomponent = {
      // instruct Bootstrap to hide the modal
      $(t.getDOMNode()).modal("hide")
    }

    def mounted(props: Props): Callback = Callback {

    }

    def updateName(e: ReactEventI) = {
      val value = e.target.value
      t.modState(s => s.copy(userModel = s.userModel.copy(name = value)))
    }

    def updateLastName(e: ReactEventI) = {
      val value = e.target.value
      t.modState(s => s.copy(userModel = s.userModel.copy(lastName = value)))
    }

    def updateConfirmPassword(e: ReactEventI) = {
      val value = e.target.value
      t.modState(s => s.copy(userModel = s.userModel.copy(ConfirmPassword = value)))
    }

    def updateEmail(e: ReactEventI) = {
      val value = e.target.value
      t.modState(s => s.copy(userModel = s.userModel.copy(email = value)))
    }

    def updatePassword(e: ReactEventI) = {
      val value = e.target.value
      t.modState(s => s.copy(userModel = s.userModel.copy(password = value)))
    }

    def toggleBTCWallet(e: ReactEventI) = {
      t.modState(s => s.copy(userModel = s.userModel.copy(createBTCWallet = !s.userModel.createBTCWallet)))
    }

    def showTermsOfServices(e: ReactEventI) = {
      addNewAgentState = true
      t.modState(s => s.copy(showTermsOfServicesForm = true))
    }

    def submitForm(e: ReactEventI) = {
      e.preventDefault()
      t.modState(s => s.copy(addNewAgent = true))
    }

    def formClosed(state: State, props: Props): Callback = {
      // call parent handler with the new item and whether form was OK or cancelled
//      println(state.addNewAgent)
      userModelUpdate = state.userModel
      props.submitHandler(state.userModel, state.addNewAgent, state.showTermsOfServicesForm)
    }

    def render(s: State, p: Props) = {
      val headerText = "Sign up with LivelyGig credentials"
      Modal(Modal.Props(
        // header contains a cancel button (X)
        header = hide => <.span(<.button(^.tpe := "button", bss.close, ^.onClick --> hideModal, Icon.close), <.div(DashBoardCSS.Style.modalHeaderText)(headerText)),
        // this is called after the modal has been hidden (animation is completed)
        closed = () => formClosed(s, p)),
        <.form(^.onSubmit ==> submitForm)(
          <.div(^.className := "row")(
            <.div(^.className := "col-md-6 col-sm-6 col-xs-6")(
              <.div(^.className := "row")(
                <.div(^.className := "col-md-12 col-sm-12 col-xs-12", DashBoardCSS.Style.slctInputWidthLabel)(
                  <.label(^.`for` := "First name *", "First name *")
                ),
                <.div(DashBoardCSS.Style.scltInputModalLeftContainerMargin)(
                  <.input(^.tpe := "text", bss.formControl, DashBoardCSS.Style.inputModalMargin, ^.id := "First name", ^.value := s.userModel.name,
                    ^.onChange ==> updateName, ^.required := true)
                )
              ),
              <.div(^.className := "row")(
                <.div(^.className := "col-md-12 col-sm-12 col-xs-12", DashBoardCSS.Style.slctInputWidthLabel)(
                  <.label(^.`for` := "Last name *", "Last name *")
                ),
                <.div(DashBoardCSS.Style.scltInputModalLeftContainerMargin)(
                  <.input(^.tpe := "text", bss.formControl, DashBoardCSS.Style.inputModalMargin, ^.id := "Last name",^.value := s.userModel.lastName,
                    ^.onChange ==> updateLastName)
                )
              ),
              <.div(^.className := "row")(
                <.div(^.className := "col-md-12 col-sm-12 col-xs-12", DashBoardCSS.Style.slctInputWidthLabel)(
                  <.label(^.`for` := "Email *", "Email *")
                ),
                <.div(DashBoardCSS.Style.scltInputModalLeftContainerMargin)(
                  <.input(^.tpe := "email", bss.formControl, DashBoardCSS.Style.inputModalMargin, ^.id := "Email", ^.value := s.userModel.email,
                    ^.onChange ==> updateEmail, ^.required := true)
                )
              ),
              <.div(^.className := "row")(
                <.div(^.className := "col-md-12 col-sm-12 col-xs-12", DashBoardCSS.Style.slctInputWidthLabel)(
                  <.label(^.`for` := "Password *", "Password *")
                ),
                <.div(DashBoardCSS.Style.scltInputModalLeftContainerMargin)(
                  <.input(^.tpe := "password", bss.formControl, DashBoardCSS.Style.inputModalMargin, ^.id := "Password", ^.value := s.userModel.password,
                    ^.onChange ==> updatePassword, ^.required := true)
                )
              ),
              <.div(^.className := "row")(
                <.div(^.className := "col-md-12 col-sm-12 col-xs-12", DashBoardCSS.Style.slctInputWidthLabel)(
                  <.label(^.`for` := "Confirm Password *", "Confirm Password *")
                ),
                <.div(DashBoardCSS.Style.scltInputModalLeftContainerMargin)(
                  <.input(^.tpe := "password", bss.formControl, DashBoardCSS.Style.inputModalMargin, ^.id := "Confirm Password", ^.value := s.userModel.ConfirmPassword,
                    ^.onChange ==> updateConfirmPassword,^.required := true)
                )
              )
            ), //col-md-8
            <.div(^.className := "col-md-6 col-sm-6 col-xs-6")(
              <.div(^.className := "row")(
                <.div(^.className := "col-md-12 col-sm-12 col-xs-12", DashBoardCSS.Style.slctInputWidthLabel)(
                  <.label(^.`for` := "Account type *", "Account roles")
                ),
                <.div(DashBoardCSS.Style.scltInputModalLeftContainerMargin)(
                  <.div()(
                    <.label()(<.input(^.`type` := "checkbox"), " Freelancer"), <.br(),
                    <.label()(<.input(^.`type` := "checkbox"), " Client"), <.br(),
                    <.label()(<.input(^.`type` := "checkbox"), " Moderator"), <.br()
                  ),
                  <.div()(
                  "These roles have distinct alias profiles and reputation. You can change your role options later. Obviously, one cannot moderate their own work contracts."
                  )
                )
              )
            )
          ), //main row
          <.div(^.className := "row", DashBoardCSS.Style.MarginLeftchkproduct)(
            <.div(DashBoardCSS.Style.marginTop10px)(
              <.div()(<.input(^.`type` := "checkbox"), " * I understand and agree to the LivelyGig",
                <.button(^.tpe := "button", ^.className := "btn btn-default", FooterCSS.Style.legalModalBtn, "Terms of Service ", ^.onClick ==> showTermsOfServices))
            ),
            <.div()(
              <.input(^.`type` := "checkbox"), " Send me occasional email updates from LivelyGig"
            )
            /*,
            <.div(DashBoardCSS.Style.marginTop10px)(
              <.div()(
                <.input(^.tpe := "checkbox") ," Send me notifications related to projects"
              )
            )
            */
          ),
          <.div()(
            <.div(DashBoardCSS.Style.modalHeaderPadding, DashBoardCSS.Style.footTextAlign, DashBoardCSS.Style.marginTop10px)("You will receive a via email a code confirming creation of your new account shortly after completing this form"),
            <.div(DashBoardCSS.Style.modalHeaderPadding, ^.className := "text-right")(
              <.button(^.tpe := "submit", ^.className := "btn", DashBoardCSS.Style.marginLeftCloseBtn, ^.onClick --> hideModal, "Submit"),
              <.button(^.tpe := "button", ^.className := "btn", DashBoardCSS.Style.marginLeftCloseBtn, ^.onClick --> hideModal, "Cancel")
            )
          ),
          <.div(bss.modal.footer, DashBoardCSS.Style.marginTop10px, DashBoardCSS.Style.marginLeftRight)()
        )
      )
    }
  }
  //case class UserModel (email: String = "", password: String = "",ConfirmPassword:String="", name: String = "", lastName: String = "", createBTCWallet: Boolean = true,
  //                      isLoggedIn: Boolean = false, imgSrc: String = "")
  private val component = ReactComponentB[Props]("NewAgentForm")
    .initialState_P(p =>
      if(addNewAgentState)
      State(new UserModel(userModelUpdate.email ,userModelUpdate.password,userModelUpdate.ConfirmPassword,userModelUpdate.name,userModelUpdate.lastName, false))
    else
      State(new UserModel ("","","","","",false,false,""))
    )
    .renderBackend[Backend]
    .componentDidUpdate(scope => Callback {
      if (scope.currentState.addNewAgent || scope.currentState.showTermsOfServicesForm) {
        scope.$.backend.hidecomponent
      }
    })
    .build

  def apply(props: Props) = component(props)
}