/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.theme.ColorHours
import com.example.androiddevchallenge.ui.theme.ColorMinutes
import com.example.androiddevchallenge.ui.theme.ColorSeconds
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme(darkTheme = true) {
                MyApp()
            }
        }
    }
}


val ripplePadButtonRadius = 72.dp
fun Modifier.padButtonSized() = this.size(82.dp)

// Start building your app here!
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MyApp() {
    val fullSize = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
    val keypadVM = viewModel<CountdownViewModel>()

    val isKeyboardVisible by keypadVM.keyboardIsVisible.collectAsState()
    val isCounterVisible = !isKeyboardVisible

    Surface(color = MaterialTheme.colors.background, modifier = fullSize) {

        val delay = AnimationConstants.DefaultDurationMillis*2
        val enterAnimation = fadeIn(animationSpec = tween(delayMillis = delay)) + expandIn(Alignment.Center, animationSpec = tween(delayMillis = delay))
        val outAnimation = fadeOut(animationSpec = tween(delayMillis = delay)) + shrinkOut(Alignment.Center, animationSpec = tween(delayMillis = delay))
        AnimatedVisibility(
            visible = isCounterVisible,
            enter = enterAnimation,
            exit = outAnimation
        ) {
        Box {

                CountDownView(keypadVM)
            }

        }

        
        Column(
            modifier = fullSize,
            verticalArrangement = Arrangement.Bottom
        ) {
            Keypad(keypadVM)
            Spacer(Modifier.size(16.dp))
        }


        Column(
            modifier = fullSize,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            val delay = AnimationConstants.DefaultDurationMillis*2
            val enterAnimation = fadeIn(animationSpec = tween(delayMillis = delay)) + expandIn(Alignment.Center, animationSpec = tween(delayMillis = delay))
            val outAnimation = fadeOut(animationSpec = tween(delayMillis = delay)) + shrinkOut(Alignment.Center, animationSpec = tween(delayMillis = delay))
            AnimatedVisibility(
                visible = isCounterVisible,
                enter = enterAnimation,
                exit = outAnimation
            ) {
                val modifs = Modifier
                    .padding(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary)
                IconButton(R.drawable.ic_stop, modifs, keypadVM::stopCountDown)
            }
        }



    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Keypad(viewModel: CountdownViewModel = viewModel()) = Column(Modifier.wrapContentHeight()) {
    // Can't use by when de-structuring
    val input = viewModel.input.collectAsState(initial = Triple("00h", "00m", "00s"))
    val (hours, mins, secs) = input.value
    val isVisible by viewModel.keyboardIsVisible.collectAsState()

    val tgtWidth = if(isVisible) 0.6f else 0f
    val lineWidth by animateFloatAsState(targetValue = tgtWidth)

    val alignCenter = Arrangement.Center
    val wFull = Modifier.fillMaxWidth()

    AnimatedVisibility(
        visible = isVisible,
        modifier = wFull,
        enter = fadeIn() + expandHorizontally(Alignment.CenterHorizontally),
        exit = fadeOut() + shrinkHorizontally(Alignment.CenterHorizontally)
    ) {
        Row(modifier =wFull, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
            Box(modifier = Modifier.weight(1f)) {}
            Row(modifier =Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
                Text(text = hours, textAlign = TextAlign.Center)
                Text(text = mins, textAlign = TextAlign.Center)
                Text(text = secs, textAlign = TextAlign.Center)
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                IconButton(resId = R.drawable.ic_delete, onClick = viewModel::delete)
            }

        }
    }

    Spacer(modifier = Modifier.size(8.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Box(
            Modifier
                .fillMaxWidth(lineWidth)
                .size(1.dp)
                .background(Color.LightGray))
    }
    Spacer(modifier = Modifier.size(8.dp))

    Column {
        val spaceSize = Modifier.size(4.dp)
        Row (modifier =wFull, horizontalArrangement = alignCenter){
            PadButton(1,viewModel)
            Spacer(modifier = spaceSize)
            PadButton(2,viewModel)
            Spacer(modifier = spaceSize)
            PadButton(3,viewModel)
        }
        Spacer(modifier = spaceSize)
        Row (modifier =wFull, horizontalArrangement = alignCenter){
            PadButton(4,viewModel)
            Spacer(modifier = spaceSize)
            PadButton(5,viewModel)
            Spacer(modifier = spaceSize)
            PadButton(6,viewModel)
        }
        Spacer(modifier = spaceSize)
        Row (modifier =wFull, horizontalArrangement = alignCenter){
            PadButton(7, viewModel)
            Spacer(modifier = spaceSize)
            PadButton(8, viewModel)
            Spacer(modifier = spaceSize)
            PadButton(9, viewModel)
        }
        Spacer(modifier = spaceSize)
        Row(modifier =wFull, horizontalArrangement = alignCenter) {
            Box(modifier = Modifier.padButtonSized())
            PadButton(0, viewModel)
            IconPadButton(R.drawable.ic_check, viewIndex = 10, onClick = viewModel::startCountDown)
        }
    }
}

@Composable
fun IconButton(@DrawableRes resId: Int, modifier: Modifier = Modifier,  onClick: () -> Unit) {
    val tint = if(resId == R.drawable.ic_stop) Color.White else MaterialTheme.colors.primary
    val icon = painterResource(id = resId)
    Box(
        modifier =
        modifier
            .padButtonSized()
            .clip(CircleShape)
            .clickable(role = Role.Button) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Image( modifier = Modifier.size(MaterialTheme.typography.button.fontSize.value.dp),painter = icon, contentDescription = "Check Icon", colorFilter = ColorFilter.tint(tint))
    }
}

@Composable fun IconPadButton(@DrawableRes resId: Int, viewIndex: Int, modifier: Modifier = Modifier,  onClick: () -> Unit)  {
    AnimatedKeyPadContent(viewIndex = viewIndex, viewModel = viewModel()) {
        IconButton(resId, modifier, onClick)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable fun PadButton(
    number: Int,
    viewModel: CountdownViewModel,
    modifier: Modifier = Modifier,
) {
    AnimatedKeyPadContent(viewIndex = number, viewModel = viewModel) {
        TextButton(
            modifier = modifier.padButtonSized(),
            shape = CircleShape,
            onClick = { viewModel.typeNumber(number) },
        ) {
            Text(text = "$number")
        }
    }
}


@Composable
@OptIn(ExperimentalAnimationApi::class)
fun AnimatedKeyPadContent(viewIndex: Int, viewModel: CountdownViewModel, content: @Composable () -> Unit) {
    val visibilityState by viewModel.keyboardIsVisible.collectAsState()
    val staggeringDelayIn = (30 * viewIndex)
    val staggeringDelayOut = (30 * (11-viewIndex)) // because we have 11 buttons
    val enterAnimation = fadeIn(animationSpec = tween(delayMillis = staggeringDelayIn)) + expandIn(animationSpec = tween(delayMillis = staggeringDelayIn))
    val outAnimation = fadeOut(animationSpec = tween(delayMillis = staggeringDelayOut)) + shrinkOut(animationSpec = tween(delayMillis = staggeringDelayOut))
    // visibilityState.value
    AnimatedVisibility(
        visible = visibilityState,
        enter = enterAnimation,
        exit = outAnimation,
        initiallyVisible = false
    ) {
        content()
    }
}


@Composable
fun CountDownView(vm: CountdownViewModel) = Box(
    Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.4f), contentAlignment = Alignment.Center
) {

    val counterText by vm.countingDownTextLabel.collectAsState()
    val progresses = vm.countDownProgresses.collectAsState()

    val (_progHours, _progMins, progSecs) = progresses.value

    val progHours by animateFloatAsState(targetValue = _progHours)
    val progMins by animateFloatAsState(targetValue = _progMins)
    //val progSecs by animateFloatAsState(targetValue = _progSecs,)

    Column(modifier = Modifier.fillMaxWidth(0.7f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(counterText, style = MaterialTheme.typography.h1)
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .padding(top = 4.dp)
        .fillMaxWidth(0.68f), horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(progress = progHours,
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(), strokeWidth = 12.dp, color = ColorHours)
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .padding(top = 19.dp)
        .fillMaxWidth(0.60f), horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(progress = progMins,
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(), strokeWidth = 8.dp, color = ColorMinutes)
    }

    Column( modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(0.7f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator(progress = progSecs,
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(), strokeWidth = 1.dp, color = ColorSeconds)
    }



}



/*
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
*/