package client.modules

import client.rootmodel.ProjectsRootModel
import client.css._
import shared.models.ProjectsPost
import client.components.Bootstrap._
import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import client.components._
import client.css.{DashBoardCSS, HeaderCSS}
import client.logger._
import client.modals._
import client.services.LGCircuit
import japgolly.scalajs.react
import org.querki.jquery._
import org.widok.moment.Moment
import diode.AnyAction._

import scala.scalajs.js
import scalacss.ScalaCssReact._
import scala.language.existentials

object ProjectResults {

  case class Props(proxy: ModelProxy[Pot[ProjectsRootModel]])

  case class State(showErrorModal: Boolean = false)

  val getServerError = LGCircuit.zoom(_.appRootModel).value

  class Backend(t: BackendScope[Props, State]) {
    def mounted(props: Props): react.Callback = Callback {
      //      log.debug("project view mounted")
      /*if (props.proxy().isEmpty) {
        ContentModelHandler.subsForContentAndBeginSessionPing(AppModule.PROJECTS_VIEW)
      }*/

    }

    def serverError(showErrorModal: Boolean = false): Callback = {
      if (showErrorModal)
        t.modState(s => s.copy(showErrorModal = false))
      else
        t.modState(s => s.copy(showErrorModal = true))
    }

    def render(P: Props, S: State) = {

      <.div(^.id := "rsltScrollContainer", DashBoardCSS.Style.rsltContainer)(
        <.div(DashBoardCSS.Style.gigActionsContainer, ^.className := "row")(
          <.div(^.className := "col-md-6 col-sm-6 col-xs-12")(
            <.input(^.`type` := "checkbox", DashBoardCSS.Style.rsltCheckboxStyle),
            <.div(^.display := "inline-block")(
              <.div(DashBoardCSS.Style.displayInlineText, ^.className := "dropdown")(
                <.button(DashBoardCSS.Style.gigMatchButton, ^.className := "btn dropdown-toggle", "data-toggle".reactAttr := "dropdown")("Select Bulk Action ")(
                  <.span(^.className := "caret", DashBoardCSS.Style.rsltCaretStyle)
                ),
                <.ul(^.className := "dropdown-menu")(
                  <.li()(<.a()("Hide")),
                  <.li()(<.a()("Favorite")),
                  <.li()(<.a()("Unhide")),
                  <.li()(<.a()("Unfavorite"))
                )
              ),
              <.div(PresetsCSS.Style.modalBtn)(
                NewProject(NewProject.Props("", Seq(HeaderCSS.Style.rsltContainerIconBtn), Icon.clipboard, "Create New Job"))
              ),
              <.div(DashBoardCSS.Style.displayInlineText, DashBoardCSS.Style.rsltCountHolderDiv, DashBoardCSS.Style.marginResults)("2,352 Results")
            )
          ),
          <.div(^.className := "col-md-6 col-sm-6 col-xs-12")(
            <.div(^.display := "inline-block")(
              <.div(DashBoardCSS.Style.displayInlineText, ^.className := "dropdown")(
                <.button(DashBoardCSS.Style.gigMatchButton, ^.className := "btn dropdown-toggle", "data-toggle".reactAttr := "dropdown")("By Date ")(
                  <.span(^.className := "caret", DashBoardCSS.Style.rsltCaretStyle)
                ),
                <.ul(^.className := "dropdown-menu")(
                  <.li()(<.a()("By Date")),
                  <.li()(<.a()("By Experience")),
                  <.li()(<.a()("By Reputation")),
                  <.li()(<.a()("By Rate")),
                  <.li()(<.a()("By Projects Completed"))
                )
              ),
              <.div(DashBoardCSS.Style.displayInlineText, ^.className := "dropdown")(
                <.button(DashBoardCSS.Style.gigMatchButton, DashBoardCSS.Style.padding0px, ^.className := "btn dropdown-toggle", "data-toggle".reactAttr := "dropdown")("Newest ")(
                  <.span(Icon.longArrowDown)
                )
              )
            ),
            <.div(^.className := "pull-right")(
              <.button(DashBoardCSS.Style.btn, "data-toggle".reactAttr := "tooltip", "title".reactAttr := "View Summary")(<.span(^.className := "icon-List1")),
              <.button(DashBoardCSS.Style.btn, "data-toggle".reactAttr := "tooltip", "title".reactAttr := "View Brief")(<.span(^.className := "icon-List2")),
              <.button(DashBoardCSS.Style.btn, "data-toggle".reactAttr := "tooltip", "title".reactAttr := "View Full Posts")(<.span(^.className := "icon-List3"))
            )
          )
        ), //col-12
        <.div(^.id := "resultsContainer")(
          P.proxy().render(jobPostsRootModel =>
            ProjectsList(jobPostsRootModel.projectsModelList)),
          P.proxy().renderFailed(ex => <.div()(
            //<.span(Icon.warning), " Error loading")
            if (!getServerError.isServerError) {
              ServerErrorModal(ServerErrorModal.Props(serverError, "Server offline"))
            }
            else
              <.div()
          )),
          if (P.proxy().isEmpty) {
            <.div()(
              <.img(^.src := "./assets/images/processing.gif", DashBoardCSS.Style.imgc)
            )
          } else {
            <.div()
          }
        ) //gigConversation
      )

    }


  }

  // create the React component for Dashboard
  val component = ReactComponentB[Props]("Projects")
    .initialState_P(p => State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[Pot[ProjectsRootModel]]) = component(Props(proxy))
}

object ProjectsList {

  case class Props(projects: Seq[ProjectsPost])

  case class Backend(t: BackendScope[Props, _]) {
    def mounted(props: Props): Callback = Callback {
      val msgTime: js.Object = ".msgTime"
      $(msgTime).tooltip(PopoverOptions.html(true))
    }

    def render(p: Props) = {
      def renderJobPosts(project: ProjectsPost) = {
        //  <.li(^.className := "media profile-description", DashBoardCSS.Style.rsltpaddingTop10p)(
        <.li(^.className := "media", DashBoardCSS.Style.profileDescription, DashBoardCSS.Style.rsltpaddingTop10p)(
          <.input(^.`type` := "checkbox", DashBoardCSS.Style.rsltCheckboxStyle),
          <.span(^.className := "checkbox-lbl"),
          <.div(DashBoardCSS.Style.profileNameHolder)(
            project.postContent.name,
            <.div(DashBoardCSS.Style.displayInlineText)("  Posted by: @LivelyGig  "),
            <.div(DashBoardCSS.Style.displayInlineText)("  Posted: "),
            <.div(DashBoardCSS.Style.displayInlineText, ^.className := "msgTime", "data-toggle".reactAttr := "tooltip", ^.title := project.created, "data-placement".reactAttr := "right")(Moment(project.created).toLocaleString)
          ),
          <.div(^.className := "media-body", ^.paddingLeft := "28px")(
            "Job Type: " + project.postContent.contractType,
            <.div(project.postContent.description),
            <.div(/*^.className := "col-md-4 col-sm-4",*/)(
              <.br(),
              "Skills: Java, Financial Apps, cryptography",
              <.br(),
              "Recommended By: @Tom"
              // project.pageOfPosts.skills.toString()
            ),
            <.div()(
              <.button(^.tpe := "button", ^.className := "btn profile-action-buttons pull-right", HeaderCSS.Style.rsltContainerIconBtn, ^.title := "Hide", Icon.remove),
              <.button(^.tpe := "button", ^.className := "btn profile-action-buttons pull-right", HeaderCSS.Style.rsltContainerIconBtn, ^.title := "Favorite", Icon.star),
              RecommendationJobs(RecommendationJobs.Props("", Seq(HeaderCSS.Style.rsltContainerIconBtn), Icon.thumbsOUp, "Recommend Job")),
              <.button(^.tpe := "button", ^.className := "btn profile-action-buttons pull-right", HeaderCSS.Style.rsltContainerIconBtn, ^.title := "Find Matching Talent", Icon.users),
              WorkContractModal(WorkContractModal.Props("", Seq(HeaderCSS.Style.rsltContainerIconBtn), Icon.pencilSquareO, "Apply")),
              NewMessage(NewMessage.Props("", Seq(HeaderCSS.Style.rsltContainerIconBtn), Icon.envelope, "Message"))
            )
          ) //media-body
        ) //li
      }
      <.div(DashBoardCSS.Style.rsltSectionContainer)(
        <.ul(^.className := "media-list")(p.projects map renderJobPosts)
      )
    }
  }


  val ProjectsList = ReactComponentB[Props]("ProjectList")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(jobPosts: Seq[ProjectsPost]) = ProjectsList(Props(jobPosts))
}