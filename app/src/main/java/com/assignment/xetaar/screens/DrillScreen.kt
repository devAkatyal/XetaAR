package com.assignment.xetaar.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.assignment.xetaar.R

val spaceGrotesk =
    FontFamily(
        Font(R.font.space_grotesk_light, FontWeight.Light),
        Font(R.font.space_grotesk_regular, FontWeight.Normal),
        Font(R.font.space_grotesk_medium, FontWeight.Medium),
        Font(R.font.space_grotesk_semibold, FontWeight.SemiBold),
        Font(R.font.space_grotesk_bold, FontWeight.Bold)
    )

@Composable
fun DrillScreen(onStartDrillClick: () -> Unit) {
    val drills = listOf("DeWalt DCD996", "Makita XFD131", "Bosch GSB 18V-50")
    var selectedDrill by remember { mutableStateOf(drills[0]) }

    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF5F5F5))) {
        val (
            topBar,
            dropdown,
            drillImage,
            drillTitle,
            drillDescription,
            tipsTitle,
            tipsDescription,
            startDrillButton) =
            createRefs()

        val startGuideline = createGuidelineFromStart(16.dp)
        val endGuideline = createGuidelineFromEnd(16.dp)

        TopBar(
            modifier =
                Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        DrillDown(
            modifier =
                Modifier.constrainAs(dropdown) {
                    top.linkTo(topBar.bottom, margin = 16.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    width = Dimension.fillToConstraints
                },
            drills = drills,
            selectedDrill = selectedDrill,
            onDrillSelected = { selectedDrill = it }
        )

        DrillImage(
            modifier =
                Modifier.constrainAs(drillImage) {
                    top.linkTo(dropdown.bottom, margin = 16.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    width = Dimension.fillToConstraints
                },
            imageRes =
                when (selectedDrill) {
                    drills[0] -> R.drawable.drill1
                    drills[1] -> R.drawable.drill2
                    else -> R.drawable.drill3
                }
        )

        DrillTitle(
            modifier =
                Modifier.constrainAs(drillTitle) {
                    top.linkTo(drillImage.bottom, margin = 16.dp)
                    start.linkTo(startGuideline)
                },
            title = selectedDrill
        )

        DrillDescription(
            modifier =
                Modifier.constrainAs(drillDescription) {
                    top.linkTo(drillTitle.bottom, margin = 8.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    width = Dimension.fillToConstraints
                }
        )

        TipsTitle(
            modifier =
                Modifier.constrainAs(tipsTitle) {
                    top.linkTo(drillDescription.bottom, margin = 16.dp)
                    start.linkTo(startGuideline)
                }
        )

        TipsDescription(
            modifier =
                Modifier.constrainAs(tipsDescription) {
                    top.linkTo(tipsTitle.bottom, margin = 8.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    width = Dimension.fillToConstraints
                }
        )

        StartDrillButton(
            modifier =
                Modifier.constrainAs(startDrillButton) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    width = Dimension.fillToConstraints
                },
            onClick = onStartDrillClick
        )
    }
}

@Composable
fun TopBar(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp)
    ) {
        val (title) = createRefs()

        Text(
            text = "Drills",
            style =
                TextStyle(
                    fontFamily = spaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
            modifier =
                Modifier.constrainAs(title) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}

@Composable
fun DrillDown(
    modifier: Modifier = Modifier,
    drills: List<String>,
    selectedDrill: String,
    onDrillSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp)
        ) {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (text, icon) = createRefs()
                Text(
                    text = selectedDrill,
                    modifier =
                        Modifier.constrainAs(text) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier =
                        Modifier.constrainAs(icon) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            drills.forEach { drill ->
                DropdownMenuItem(
                    text = { Text(text = drill) },
                    onClick = {
                        onDrillSelected(drill)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DrillImage(modifier: Modifier = Modifier, imageRes: Int) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Drill Image",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
    )
}

@Composable
fun DrillTitle(modifier: Modifier = Modifier, title: String) {
    Text(
        text = title,
        style =
            TextStyle(
                fontFamily = spaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
        modifier = modifier
    )
}

@Composable
fun DrillDescription(modifier: Modifier = Modifier) {
    Text(
        text =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        style =
            TextStyle(
                fontFamily = spaceGrotesk,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.Gray
            ),
        modifier = modifier
    )
}

@Composable
fun TipsTitle(modifier: Modifier = Modifier) {
    Text(
        text = "Tips",
        style =
            TextStyle(
                fontFamily = spaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
        modifier = modifier
    )
}

@Composable
fun TipsDescription(modifier: Modifier = Modifier) {
    Text(
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        style =
            TextStyle(
                fontFamily = spaceGrotesk,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.Gray
            ),
        modifier = modifier
    )
}

@Composable
fun StartDrillButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "Start Drill",
            style =
                TextStyle(
                    fontFamily = spaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
        )
    }
}
