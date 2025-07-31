package com.assignment.xetaar.screens

import android.Manifest
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.ar.core.Config
import com.google.ar.core.Frame
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ARScreen(onClose: () -> Unit) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        ARContent(onClose = onClose)
    } else {
        LaunchedEffect(Unit) { cameraPermissionState.launchPermissionRequest() }
    }
}

@Composable
fun ARContent(onClose: () -> Unit) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (arView, topBar) = createRefs()

        ARView(
            modifier =
                Modifier.constrainAs(arView) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        TopBar(
            modifier =
                Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            onClose = onClose
        )
    }
}

@Composable
fun ARView(modifier: Modifier = Modifier) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val childNodes = remember { mutableStateListOf<Node>() }
    val hasPlacedObject by remember { derivedStateOf { childNodes.any { it is AnchorNode } } }
    var frame by remember { mutableStateOf<Frame?>(null) }

    ConstraintLayout(modifier = modifier) {
        val (arScene, instructionText) = createRefs()

        ARScene(
            modifier =
                Modifier.constrainAs(arScene) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            childNodes = childNodes,
            engine = engine,
            modelLoader = modelLoader,
            planeRenderer = true,
            onSessionUpdated = { session, sessionFrame -> frame = sessionFrame },
            sessionConfiguration = { session, config ->
                Log.d("AR", "AR session configured – plane mode=${config.planeFindingMode}")
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    config.depthMode = Config.DepthMode.AUTOMATIC
                }
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            },
            onTouchEvent = { motionEvent, _ ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    frame?.hitTest(motionEvent)
                        ?.firstOrNull {
                            val plane = it.trackable as? com.google.ar.core.Plane
                            plane?.isPoseInPolygon(it.hitPose) == true
                        }
                        ?.let { hitResult ->
                            if (childNodes.any { it is AnchorNode }) {
                                childNodes.removeIf { it is AnchorNode }
                            }
                            childNodes.add(
                                AnchorNode(
                                    engine = engine,
                                    anchor = hitResult.createAnchor()
                                )
                                    .apply {
                                        addChildNode(
                                            ModelNode(
                                                modelInstance =
                                                    modelLoader
                                                        .createModelInstance(
                                                            assetFileLocation =
                                                                "models/cube.glb"
                                                        ),
                                                scaleToUnits = 0.5f
                                            )
                                        )
                                    }
                            )
                        }
                }
                true
            }
        )

        if (!hasPlacedObject) {
            Text(
                text = "Move your phone to find a surface, then tap to place the cube.",
                style =
                    TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    ),
                modifier =
                    Modifier
                        .constrainAs(instructionText) {
                            bottom.linkTo(parent.bottom, margin = 32.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun TopBar(modifier: Modifier = Modifier, onClose: () -> Unit) {
    ConstraintLayout(
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp)
    ) {
        val (closeButton, title) = createRefs()

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.White,
            modifier =
                Modifier
                    .constrainAs(closeButton) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clickable { onClose() }
        )

        Text(
            text = "AR View",
            style =
                TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                ),
            modifier =
                Modifier.constrainAs(title) {
                    start.linkTo(closeButton.end, margin = 16.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}
