package Views

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.stage.Stage
import javafx.util.Duration

class TopBar(private val stage: Stage) {
    val root = HBox().apply {
        padding = Insets(10.0)
        alignment = Pos.TOP_RIGHT
        spacing = 10.0
        style = "-fx-background-color: #2c2f33;"

        val minimizeBtn = createButton("-") {
            stage.isIconified = true
        }

        val closeBtn = createButton("×") {
            stage.close()
        }

        children.addAll(minimizeBtn, closeBtn)

        var offsetX = 0.0
        var offsetY = 0.0

        setOnMousePressed { e ->
            offsetX = e.sceneX
            offsetY = e.sceneY
        }

        setOnMouseDragged { e ->
            stage.x = e.screenX - offsetX
            stage.y = e.screenY - offsetY
        }
    }

    private fun createButton(text: String, onClick: () -> Unit): Button {
        return Button(text).apply {
            style = """
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-cursor: hand;
            -fx-background-radius: 50%;
            -fx-min-width: 30px;
            -fx-min-height: 30px;
            """.trimIndent()

            setOnMouseEntered {
                val hoverColor = if (text == "×") "#ff4d4f" else "#3a86ff"
                style = """
                -fx-background-color: $hoverColor;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                -fx-background-radius: 50%;
                -fx-min-width: 30px;
                -fx-min-height: 30px;
            """.trimIndent()
            }

            setOnMouseExited {
                style = """
                -fx-background-color: transparent;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                -fx-background-radius: 50%;
                -fx-min-width: 30px;
                -fx-min-height: 30px;
            """.trimIndent()
            }

            setOnMousePressed {
                scaleX = 0.9
                scaleY = 0.9
            }

            setOnMouseReleased {
                scaleX = 1.0
                scaleY = 1.0
            }

            setOnAction {
                if (text == "×") {
                    val fadeOut = Timeline(
                        KeyFrame(Duration.ZERO, KeyValue(stage.opacityProperty(), 1.0)),
                        KeyFrame(Duration.millis(120.0), KeyValue(stage.opacityProperty(), 0.0))
                    ).apply {
                        setOnFinished {
                            stage.hide()
                            stage.close()
                        }
                    }
                    fadeOut.play()
                } else if (text == "-") {
                    stage.isIconified = true
                } else {
                    onClick()
                }
            }

        }
    }
}