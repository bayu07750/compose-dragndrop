@file:OptIn(ExperimentalFoundationApi::class)

package com.bayu07750.dragndrop

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.customview.widget.ExploreByTouchHelper
import com.bayu07750.dragndrop.ui.theme.DragNDropTheme
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

val Explode: List<Party>
    get() = buildList {
        repeat(5) {
            add(
                Party(
                    speed = 0f,
                    maxSpeed = 30f,
                    damping = 0.9f,
                    spread = 360,
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                    position = Position.Relative(0.5, 0.3)
                )
            )
        }
    }


@Composable
fun Numbers(
    modifier: Modifier = Modifier,
) {
    var items by remember {
        mutableStateOf(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9).shuffled())
    }

    var selectedItemDragged by remember { mutableStateOf<Int?>(null) }
    val isSuccess by remember {
        derivedStateOf {
            items == listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        }
    }


    var konfettiCount by remember { mutableIntStateOf(1) }
    val parties by remember {
        mutableStateOf(Explode)
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth(),
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 44.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = items, key = { it }) { item ->
                val defaultBorderColor = MaterialTheme.colorScheme.primary
                var borderColor by remember { mutableStateOf(defaultBorderColor) }
                var shouldShakeAnimation by remember { mutableStateOf(false) }
                val infiniteTransition = rememberInfiniteTransition(label = "Shake Animation")
                val zOffset by infiniteTransition.animateFloat(
                    initialValue = -3f,
                    targetValue = 3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 300,
                        ),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = ""
                )

                Surface(
                    tonalElevation = 2.dp,
                    border = BorderStroke(
                        width = 2.dp,
                        color = borderColor,
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .animateItem()
                        .let {
                            if (shouldShakeAnimation && selectedItemDragged != item) {
                                it.graphicsLayer {
                                    rotationZ = zOffset
                                }
                            } else it
                        }
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event
                                    .mimeTypes()
                                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {

                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        return event
                                            .toAndroidDragEvent()
                                            .clipData
                                            ?.getItemAt(0)
                                            ?.text
                                            ?.toString()
                                            .orEmpty()
                                            .toIntOrNull()
                                            ?.let { droppedData ->
                                                val iDroppedData = items.indexOf(droppedData)
                                                val iPrevData = items.indexOf(item)
                                                items = items
                                                    .toMutableList()
                                                    .apply {
                                                        this[iPrevData] = droppedData
                                                        this[iDroppedData] = item
                                                    }
                                                    .toList()
                                                true
                                            } ?: false
                                    }

                                    override fun onEnded(event: DragAndDropEvent) {
                                        super.onEnded(event)
                                        borderColor = defaultBorderColor
                                        shouldShakeAnimation = false
                                        selectedItemDragged = null
                                    }

                                    override fun onEntered(event: DragAndDropEvent) {
                                        super.onEntered(event)
                                        borderColor = Color.Green
                                    }

                                    override fun onExited(event: DragAndDropEvent) {
                                        super.onExited(event)
                                        borderColor = defaultBorderColor
                                    }

                                    override fun onStarted(event: DragAndDropEvent) {
                                        super.onStarted(event)
                                        shouldShakeAnimation = true
                                    }
                                }
                            }
                        )
                        .dragAndDropSource {
                            detectTapGestures(
                                onLongPress = {
                                    startTransfer(
                                        transferData = DragAndDropTransferData(
                                            clipData = ClipData.newPlainText("$item", item.toString())
                                        )
                                    )
                                    selectedItemDragged = item
                                }
                            )
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(item.toString(), style = MaterialTheme.typography.headlineLarge)
                    }
                }
            }
        }

        AnimatedVisibility(
            isSuccess
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        items = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9).shuffled()
                        konfettiCount = 1
                    }
                ) {
                    Text("Play again")
                }

                OutlinedButton(
                    onClick = {
                       konfettiCount++
                    }
                ) {
                    Text("Celebrate")
                }
            }
        }
    }

    if (isSuccess) {
        repeat(konfettiCount) {
            key(it) {
                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = parties,
                    updateListener = object : OnParticleSystemUpdateListener {
                        override fun onParticleSystemEnded(
                            system: PartySystem,
                            activeSystems: Int,
                        ) {
                            if (activeSystems == 0) {
                                /* no-op */
                            }
                        }
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewResolveNumbers() {
    DragNDropTheme {
        Numbers()
    }
}