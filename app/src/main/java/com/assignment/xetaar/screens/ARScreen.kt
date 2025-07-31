package com.assignment.xetaar.screens

import android.Manifest
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
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

    ARScene(
        modifier = modifier,
        childNodes = childNodes,
        engine = engine,
        modelLoader = modelLoader,
        planeRenderer = true,
        onTouchEvent = { motionEvent, hitResult ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                hitResult?.let {
                    if (childNodes.any { it is AnchorNode }) {
                        childNodes.removeIf { it is AnchorNode }
                    }
                    childNodes.add(
                        AnchorNode(
                            engine = engine,
                            anchor =
                                (it as com.google.ar.core.HitResult)
                                    .createAnchor()
                        ).apply {
                            addChildNode(
                                ModelNode(
                                    modelInstance =
                                        modelLoader.createModelInstance(
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
                    fontFamily = spaceGrotesk,
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
