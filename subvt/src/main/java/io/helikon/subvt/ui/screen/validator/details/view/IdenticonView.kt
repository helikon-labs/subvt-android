package io.helikon.subvt.ui.screen.validator.details.view

import android.graphics.PixelFormat
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.helikon.subvt.ui.style.Color

@Composable
fun IdenticonView(modifier: Modifier = Modifier) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val cameraNode =
        rememberCameraNode(engine) {
            position = Position(z = 2.75f)
        }
    val centerNode = rememberNode(engine).addChildNode(cameraNode)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val environment =
        rememberEnvironment(engine) {
            environmentLoader.createHDREnvironment(
                assetFileLocation = "environment/cube_environment.hdr",
                // indirectLightSpecularFilter = false,
                // createSkybox = false,
            )!!
        }
    val modelNode =
        rememberNode {
            ModelNode(
                modelInstance = modelLoader.createModelInstance("model/cube_16.glb"),
                scaleToUnits = 1.0f,
            )
        }

    LaunchedEffect(Unit) {
        cameraNode.setProjection(fovInDegrees = 25.0)

        // modelNode.renderableNodes[1].materialInstances[0].material.setDefaultParameter("baseColorIndex", android.graphics.Color.RED)
        modelNode.renderableNodes[0].materialInstance.setParameter(
            "baseColorFactor",
            1.0f,
            1.0f,
            0.0f,
            0.0f,
        )
        // modelNode.renderableNodes[0].materialInstances[0].setParameter("metallicFactor", 0.5f)
        modelNode.renderableNodes[0].materialInstance.setParameter("roughnessFactor", 0.85f)
    }

    Scene(
        modifier = modifier.background(color = Color.transparent()),
        engine = engine,
        modelLoader = modelLoader,
        cameraNode = cameraNode,
        environmentLoader = environmentLoader,
        onViewCreated = {
            this.setZOrderOnTop(true)
            this.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            this.holder.setFormat(PixelFormat.TRANSPARENT)
            this.uiHelper.isOpaque = false
            this.view.blendMode = com.google.android.filament.View.BlendMode.TRANSLUCENT
            this.scene.skybox = null

            val options = this.renderer.clearOptions
            options.clear = true
            this.renderer.clearOptions = options

            /*
            this.view.dynamicResolutionOptions =
                View.DynamicResolutionOptions().apply {
                    enabled = true
                    quality = View.QualityLevel.LOW
                }
             */
        },
        childNodes = listOf(centerNode, modelNode),
        environment = environment,
        onFrame = {
            // cameraNode.setProjection(fovInDegrees = 25.0)
        },
    )
}
