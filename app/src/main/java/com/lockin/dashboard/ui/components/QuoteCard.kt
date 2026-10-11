package com.lockin.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.dashboard.ui.theme.LockIn

data class MindsetQuote(val quote: String, val author: String)

val LOCKIN_QUOTES = listOf(
    MindsetQuote("You have power over your mind - not outside events. Realize this, and you will find strength.", "Marcus Aurelius"),
    MindsetQuote("Focus is about saying no to a hundred other good ideas.", "Steve Jobs"),
    MindsetQuote("Great things come from hard work and perseverance. No excuses.", "Kobe Bryant"),
    MindsetQuote("Don't stop when you're tired. Stop when you're done.", "David Goggins"),
    MindsetQuote("We suffer more often in imagination than in reality.", "Seneca"),
    MindsetQuote("Do what you can, with what you have, where you are.", "Theodore Roosevelt"),
    MindsetQuote("Discipline equals freedom.", "Jocko Willink"),
    MindsetQuote("The secret of getting ahead is getting started.", "Mark Twain"),
    MindsetQuote("Simplicity is the ultimate sophistication.", "Leonardo da Vinci")
)

@Composable
fun QuoteCard(
    modifier: Modifier = Modifier
) {
    var quoteIndex by remember { mutableIntStateOf(0) }
    val current = LOCKIN_QUOTES[quoteIndex]

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LockIn.Card)
            .border(1.dp, LockIn.Border, RoundedCornerShape(16.dp))
            .clickable {
                quoteIndex = (quoteIndex + 1) % LOCKIN_QUOTES.size
            }
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚡ DAILY MANTRA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LockIn.FocusBlue,
                letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Tap to switch",
                    fontSize = 11.sp,
                    color = LockIn.TextSecondary
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    tint = LockIn.TextSecondary,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        Text(
            text = "“${current.quote}”",
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            color = LockIn.TextPrimary,
            lineHeight = 20.sp
        )

        Text(
            text = "— ${current.author}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = LockIn.TextSecondary,
            modifier = Modifier.align(Alignment.End)
        )
    }
}
