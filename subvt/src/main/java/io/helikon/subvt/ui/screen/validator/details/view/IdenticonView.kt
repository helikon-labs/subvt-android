package io.helikon.subvt.ui.screen.validator.details.view

import android.graphics.PixelFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.ui.util.getIdenticonColors

private val sphereOrdering =
    arrayOf(0, 5, 2, 3, 11, 14, 15, 12, 13, 8, 7, 18, 10, 17, 9, 1, 16, 4, 6)

@Composable
fun IdenticonView(
    modifier: Modifier = Modifier,
    accountId: AccountId,
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val environment =
        rememberEnvironment(engine) {
            environmentLoader.createHDREnvironment(
                assetFileLocation = "environment/cube_environment_1k.hdr",
                // createSkybox = false,
            )!!
        }

    val cameraNode =
        rememberCameraNode(engine).apply {
            position = Position(z = 3.0f)
        }
    val modelNode =
        rememberNode {
            ModelNode(
                modelInstance = modelLoader.createModelInstance("model/cube.glb"),
                scaleToUnits = 1.0f,
            )
        }

    Scene(
        modifier =
            modifier
                // alpha is zero to make the background transparent
                .alpha(0.0f),
        engine = engine,
        modelLoader = modelLoader,
        isOpaque = false,
        cameraNode = cameraNode,
        // cameraManipulator = null,
        onViewCreated = {
            this.setZOrderOnTop(true)
            // this.setZOrderMediaOverlay(true)
            this.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            this.holder.setFormat(PixelFormat.TRANSLUCENT)

            this.uiHelper.isOpaque = true
            this.view.blendMode = com.google.android.filament.View.BlendMode.TRANSLUCENT
            this.scene.skybox = null

            this.renderer.clearOptions =
                this.renderer.clearOptions.apply {
                    clear = true
                }

            val identiconColors = getIdenticonColors(accountId)

            for (i in 0..<19) {
                val color = identiconColors[i]
                val sphereIndex = sphereOrdering[i]
                val material =
                    modelNode.renderableNodes.find {
                        it.name == "Sphere.%03d".format(sphereIndex + 1)
                    }!!.materialInstance
                material.setParameter(
                    "baseColorFactor",
                    color.red,
                    color.green,
                    color.blue,
                    0.0f,
                )
                material.setParameter("metallicFactor", 0.1f)
                material.setParameter("roughnessFactor", 1.0f)
            }
        },
        childNodes = listOf(modelNode),
        environment = environment,
        onFrame = {
            cameraNode.setProjection(fovInDegrees = 25.0)
        },
    )
}
