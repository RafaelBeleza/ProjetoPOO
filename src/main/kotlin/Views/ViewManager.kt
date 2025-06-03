package Views

import javafx.scene.Parent
import javafx.scene.layout.BorderPane

object ViewManager {
    lateinit var rootLayout: BorderPane

    fun setView(view: Parent) {
        rootLayout.center = view
    }
}