@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.fareshare

import android.graphics.Paint.Style
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis.Companion.Style
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fareshare.components.InputField
import com.example.fareshare.ui.theme.FareShareTheme
import com.example.fareshare.utils.calculateTipAmt
import com.example.fareshare.utils.calculateTotalPerPerson
import com.example.fareshare.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MyApp {
//                    TopHeader()
                    MainContent()
                }

            }
        }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    FareShareTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = Color(0xFFacdde7)) {
            content()
        }
    }
}




@Composable
fun TopHeader(totalBill: Double = 0.0) {
    val total = "%.2f".format(totalBill)
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 15.dp)
                .height(150.dp)
                .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
            color = Color(0xFFadb9e3)
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Total amount per person",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                )

                Text(
                    text = "Rs. $total",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                )
            }
        }
    }
}


@Composable
fun MainContent() {
    AccessBill() { billAmt ->
        Log.d("AMT", billAmt)
    }

}

@Preview
@Composable
fun AccessBill(modifier: Modifier = Modifier,
               onValChange: (String) -> Unit = {}){

    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val numPerson = remember {
        mutableStateOf(1)
    }
    val tipAmt = remember {
        mutableStateOf(0.0)
    }
    val totalBill = remember {
        mutableStateOf(0.0)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val sliderPosition = remember {
        mutableStateOf(0f)
    }
    val tipCent = (sliderPosition.value * 100).toInt()

    Surface(modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth()
        .fillMaxHeight(),
        shape = RoundedCornerShape(CornerSize(4.dp)),
        color = Color(0xFFacdde7),
        border = BorderStroke(1.dp, color = Color.LightGray),
    ){
        Column(modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
            TopHeader(totalBill.value)
            InputField(valueState = totalBillState,
                labelId = "Enter Amount",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if(!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()

                }
            )

            if(validState){
                Row(modifier = Modifier
                    .padding(all= 10.dp),
                    horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = "Split",
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.align(
                            alignment = CenterVertically
                        )
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(imageVector = Icons.Default.KeyboardArrowLeft, onClick = {
                            numPerson.value--
                            if (numPerson.value < 1) {
                                numPerson.value = 1
                            }
                            tipAmt.value = calculateTipAmt(totalBillState.value.toDouble(), tipCent)
                            totalBill.value = calculateTotalPerPerson(totalBillState.value.toDouble(), numPerson.value, tipCent)
                        })

                        Text(
                            text = numPerson.value.toString(), modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(all = 9.dp),
                            style = TextStyle(fontSize = 18.sp)
                        )

                        RoundIconButton(imageVector = Icons.Default.KeyboardArrowRight,
                            onClick = {
                                numPerson.value++
                                tipAmt.value = calculateTipAmt(totalBillState.value.toDouble(), tipCent)
                                totalBill.value = calculateTotalPerPerson(totalBillState.value.toDouble(), numPerson.value, tipCent)

                            })
                    }
                }


                Row(modifier = Modifier
                    .padding(all= 10.dp),
                    horizontalArrangement = Arrangement.Start){
                    Text(text = "Tip Share",
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.align(
                            alignment = CenterVertically
                        ))
                    Spacer(modifier = Modifier.width(120.dp))
                    Text(text = "Rs. ${tipAmt.value}", style = TextStyle(fontSize = 15.sp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier
                    .padding(all = 5.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(text = "$tipCent %", style = TextStyle(fontSize = 15.sp))
                    Slider(value = sliderPosition.value, onValueChange = {
                        sliderPosition.value = it

                        tipAmt.value = calculateTipAmt(totalBillState.value.toDouble(), tipCent)
                        totalBill.value = calculateTotalPerPerson(totalBillState.value.toDouble(), numPerson.value, tipCent)

                    }, modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 5)
                }
            }
            else{
                Box {

                }
            }
        }
    }
}



