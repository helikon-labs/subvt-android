package io.helikon.subvt.ui.screen.validator.details.view

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
                assetFileLocation = "environment/cube_environment.hdr",
                // indirectLightSpecularFilter = false,
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

    LaunchedEffect(Unit) {
        cameraNode.setProjection(fovInDegrees = 25.0)

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
    }

    Scene(
        modifier = modifier,
        engine = engine,
        modelLoader = modelLoader,
        cameraNode = cameraNode,
        // cameraManipulator = null,
        onViewCreated = {
            this.setZOrderOnTop(true)

            /*
            this.holder.setFormat(PixelFormat.TRANSPARENT)
            this.uiHelper.isOpaque = false
            this.view.blendMode = com.google.android.filament.View.BlendMode.TRANSLUCENT

            this.scene.skybox = null

            val options = this.renderer.clearOptions
            options.clear = true
            this.renderer.clearOptions = options

            this.view.dynamicResolutionOptions =
                View.DynamicResolutionOptions().apply {
                    enabled = true
                    quality = View.QualityLevel.LOW
                }
             */
        },
        childNodes = listOf(modelNode),
        environment = environment,
        /*
        onFrame = {
            // cameraNode.setProjection(fovInDegrees = 25.0)
        },

         */
    )
}
