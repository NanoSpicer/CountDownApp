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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}


val ripplePadButtonRadius = 72.dp
fun Modifier.padButtonSized() = this.size(72.dp)

// Start building your app here!
@Composable
fun MyApp() {

    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Keypad()
            Spacer(Modifier.size(16.dp))
        }
    }
}


@Composable
fun Keypad(viewModel: NumericKeypadViewModel = viewModel()) = Column(Modifier.wrapContentHeight()) {
    val input = viewModel.input.collectAsState(initial = Triple("00h", "00m", "00s"))
    val (hours, mins, secs) = input.value
    val alignCenter = Arrangement.Center
    val wFull = Modifier.fillMaxWidth()

    Row(modifier =wFull, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
        Box(modifier = Modifier.weight(1f)) {}
        Row(modifier =Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
            Text(text = hours, textAlign = TextAlign.Center)
            Text(text = mins, textAlign = TextAlign.Center)
            Text(text = secs, textAlign = TextAlign.Center)
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            IconPadButton(resId = R.drawable.ic_delete, onClick = viewModel::delete)
        }

    }
    Spacer(modifier = Modifier.size(24.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Box(
            Modifier
                .fillMaxWidth(0.6f)
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
            IconPadButton(R.drawable.ic_check, onClick = viewModel::startCountDown)
        }
    }
}


@Composable fun IconPadButton(@DrawableRes resId: Int, modifier: Modifier = Modifier,  onClick: () -> Unit)  {
    val icon = painterResource(id = resId)
    Box(
        modifier =
        modifier
            .padButtonSized()
            .clip(CircleShape)
            .clickable(role = Role.Button) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Image(painter = icon, contentDescription = "Check Icon", colorFilter = ColorFilter.tint(MaterialTheme.colors.primaryVariant))
    }
}

@Composable fun PadButton(
    number: Int,
    viewModel: NumericKeypadViewModel,
    modifier: Modifier = Modifier,
) {
    TextButton(
        modifier = modifier.padButtonSized(),
        shape = CircleShape,
        onClick = { viewModel.typeNumber(number) },
    ) {
        Text(text = "$number")
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