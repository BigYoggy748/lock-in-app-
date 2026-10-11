package com.lockin.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lockin.dashboard.ui.theme.LockIn

@Composable
fun ProfileDialog(
    currentName: String,
    onNameSaved: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var nameInput by remember { mutableStateOf(currentName) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(LockIn.Surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(LockIn.Card)
                    .border(2.dp, LockIn.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nameInput.trim().take(1).uppercase().ifEmpty { "L" },
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = LockIn.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Account Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LockIn.TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "🔒 LOCKED IN PRO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LockIn.PriorityLow,
                    letterSpacing = 1.sp
                )
            }

            // Name edit field
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Your Name", color = LockIn.TextSecondary, fontSize = 12.sp) },
                placeholder = { Text("e.g. Alex", color = LockIn.TextSecondary) },
                textStyle = TextStyle(color = LockIn.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LockIn.White,
                    unfocusedBorderColor = LockIn.Border,
                    cursorColor = LockIn.White,
                    focusedLabelColor = LockIn.White,
                    unfocusedLabelColor = LockIn.TextSecondary
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Save and Cancel buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = LockIn.TextSecondary)
                }

                Button(
                    onClick = {
                        val finalName = nameInput.trim().ifEmpty { "CHAMP" }
                        onNameSaved(finalName)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LockIn.White,
                        contentColor = LockIn.Background
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Name", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
