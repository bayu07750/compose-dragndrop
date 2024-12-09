@file:OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)

package com.bayu07750.dragndrop

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.bayu07750.dragndrop.ui.theme.DragNDropTheme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DragNDropTheme(darkTheme = true) {
                //AndroidCodelabs()
                Scaffold {
                    Game(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun AndroidCodelabs() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 44.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        DragImage(
            url = "https://images.unsplash.com/photo-1480497490787-505ec076689f?q=80&w=2500&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        )
        DropTargetImage(
            url = "https://images.unsplash.com/photo-1502085671122-2d218cd434e6?q=80&w=4226&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        )
    }
}

@Composable
fun DragImage(
    url: String,
    modifier: Modifier = Modifier,
) {
    GlideImage(
        model = url,
        contentDescription = "Dragged Image",
        modifier = modifier
            .dragAndDropSource {
                detectTapGestures(
                    onLongPress = {
                        startTransfer(
                            DragAndDropTransferData(
                                clipData = ClipData.newPlainText("image uri", url),
                            )
                        )
                    }
                )
            }
    )
}

@Composable
fun DropTargetImage(
    url: String,
    modifier: Modifier = Modifier,
) {
    val urlState = remember { mutableStateOf(url) }
    var tintColor by remember {
        mutableStateOf(Color(0xffE5E4E2))
    }

    GlideImage(
        model = urlState.value,
        contentDescription = "Dropped Image",
        colorFilter = ColorFilter.tint(color = tintColor, blendMode = BlendMode.Modulate),
        modifier = modifier
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                target = remember { object : DragAndDropTarget {
                    override fun onChanged(event: DragAndDropEvent) {
                        super.onChanged(event)
                        Log.d("jetpack_compose", "onChanged...")
                    }

                    override fun onDrop(event: DragAndDropEvent): Boolean {
                        Log.d("jetpack_compose", "onDrop...")
                        val draggedData = event.toAndroidDragEvent().clipData?.getItemAt(0)?.text
                        return draggedData?.let {
                            urlState.value = it.toString()
                            true
                        } ?: false
                    }

                    override fun onStarted(event: DragAndDropEvent) {
                        Log.d("jetpack_compose", "onStarted...")
                        super.onStarted(event)
                    }

                    override fun onEnded(event: DragAndDropEvent) {
                        Log.d("jetpack_compose", "onEnded...")
                        super.onEnded(event)
                        tintColor = Color(0xffE5E4E2)
                    }

                    override fun onEntered(event: DragAndDropEvent) {
                        Log.d("jetpack_compose", "onEntered...")
                        super.onEntered(event)
                        tintColor = Color(0xff00ff00)
                    }

                    override fun onExited(event: DragAndDropEvent) {
                        Log.d("jetpack_compose", "onExited...")
                        super.onExited(event)
                        tintColor = Color(0xffE5E4E2)
                    }

                    override fun onMoved(event: DragAndDropEvent) {
                        Log.d("jetpack_compose", "onMoved...")
                        super.onMoved(event)
                    }
                } }
            )
    )
}