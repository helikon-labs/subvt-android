package io.helikon.subvt.ui.screen.validator.details.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.filament.Engine
import com.google.android.filament.LightManager
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.Scene
import io.github.sceneview.SceneView
import io.github.sceneview.managers.color
import io.github.sceneview.math.Position
import io.github.sceneview.node.LightNode
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

private fun getLightNode(
    engine: Engine,
    position: Float3,
    intensity: Float = 1.0f,
) = LightNode(
    engine = engine,
    type = LightManager.Type.POINT,
    apply = {
        color(SceneView.DEFAULT_MAIN_LIGHT_COLOR)
        intensity(SceneView.DEFAULT_MAIN_LIGHT_COLOR_INTENSITY * 0.10f * intensity)
        falloff(0.5f)
        position(position.x, position.y, position.z)
        castShadows(false)
    },
)

@Composable
fun IdenticonView(
    modifier: Modifier = Modifier,
    accountId: AccountId,
    rotation: Float3,
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val environment =
        rememberEnvironment(engine) {
            environmentLoader.createHDREnvironment(
                assetFileLocation = "environment/cube_environment_1k.hdr",
            )!!
        }.apply {
            this.indirectLight?.intensity = 50_000f
        }

    val cameraNode =
        rememberCameraNode(engine).apply {
            position = Position(z = 3.0f)
        }
    val modelNode =
        rememberNode {
            ModelNode(
                modelInstance = modelLoader.createModelInstance("model/cube_no_light.glb"),
                scaleToUnits = 1.0f,
            )
        }

    Scene(
        modifier = modifier,
        engine = engine,
        modelLoader = modelLoader,
        isOpaque = false,
        cameraNode = cameraNode,
        mainLightNode = null,
        childNodes =
            listOf(
                modelNode,
                // center
                getLightNode(
                    engine,
                    Float3(0.0f, 0.0f, 0.25f),
                ),
                // left
                getLightNode(
                    engine,
                    Float3(-0.4f, 0.0f, 0.3f),
                ),
                // top-left
                getLightNode(
                    engine,
                    Float3(-0.25f, 0.25f, 0.25f),
                ),
                // top
                getLightNode(
                    engine,
                    Float3(0.0f, 0.4f, 0.25f),
                    1.5f,
                ),
                // top-right
                getLightNode(
                    engine,
                    Float3(0.25f, 0.25f, 0.25f),
                ),
                // right
                getLightNode(
                    engine,
                    Float3(0.4f, 0.0f, 0.25f),
                ),
                // bottom-right
                getLightNode(
                    engine,
                    Float3(0.25f, -0.25f, 0.25f),
                ),
                // bottom
                getLightNode(
                    engine,
                    Float3(0.0f, -0.4f, 0.25f),
                    1.5f,
                ),
                // bottom-left
                getLightNode(
                    engine,
                    Float3(-0.25f, -0.25f, 0.25f),
                ),
            ),
        environment = environment,
        // cameraManipulator = null,
        onViewCreated = {
            this.scene.skybox = null
            this.view.blendMode = com.google.android.filament.View.BlendMode.TRANSLUCENT
            this.uiHelper.isOpaque = false

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
                material.setParameter("metallicFactor", 0.0f)
                material.setParameter("roughnessFactor", 1.0f)
            }
        },
        onFrame = {
            cameraNode.setProjection(fovInDegrees = 25.0)
            modelNode.rotation = rotation
        },
    )
}
