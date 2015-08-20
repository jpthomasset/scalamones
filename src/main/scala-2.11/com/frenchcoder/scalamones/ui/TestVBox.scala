package com.frenchcoder.scalamones.ui

import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.layout.{StackPane, VBox}

import scalafxml.core.{NoDependencyResolver, FXMLLoader}

/**
 *
 */
class TestVBox extends VBox {
  @FXML private var contentPane: StackPane = null;

  val loader = new FXMLLoader(getClass.getResource("/scala-widgetpane.fxml"), NoDependencyResolver)
  loader.setRoot(this);
  loader.setController(this);
  loader.load();

  def getContent() = contentPane.getChildren
}
