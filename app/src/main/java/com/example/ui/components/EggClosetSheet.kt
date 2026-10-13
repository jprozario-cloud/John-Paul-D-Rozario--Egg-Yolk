package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.YolkAmber
import com.example.ui.theme.YolkGreenSuccess
import com.example.ui.theme.YolkOrange
import com.example.ui.theme.YolkWhite
import com.example.ui.theme.YolkYellowPrimary
import com.example.viewmodel.EggAccessory

@Composable
fun EggClosetScreen(
  yolksEarned: Int,
  equippedAccessory: EggAccessory,
  unlockedAccessories: Set<String>,
  onEquipAccessory: (EggAccessory) -> Unit,
  tasksCompletedCount: Int,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))
      // Stats Header Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("egg_stats_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Yolk's Golden Nest",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = YolkOrange
              )
              Text(
                text = "Earn 10 Yolks each time you complete a task!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = YolkAmber
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "⭐", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "$yolksEarned Yolks",
                  fontWeight = FontWeight.ExtraBold,
                  color = Color(0xFF451A03),
                  fontSize = 14.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            StatPill(emoji = "🎯", label = "Completed", value = "$tasksCompletedCount")
            StatPill(emoji = "🔥", label = "Streak", value = "${if (tasksCompletedCount > 0) 3 else 0} Days")
            StatPill(emoji = "🐣", label = "Friendship", value = "100% Sunny")
          }
        }
      }
    }

    item {
      Text(
        text = "Egg Headwear & Hats",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    items(EggAccessory.values().size) { index ->
      val accessory = EggAccessory.values()[index]
      val isEquipped = equippedAccessory == accessory
      val isUnlocked = unlockedAccessories.contains(accessory.id)
      val canAfford = yolksEarned >= accessory.cost

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("accessory_card_${accessory.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
          if (isEquipped) 2.dp else 1.dp,
          if (isEquipped) YolkYellowPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              modifier = Modifier.size(46.dp),
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = accessory.emoji, fontSize = 22.sp)
              }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = accessory.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )

              Text(
                text = if (accessory.cost == 0) "Free Starter" else "${accessory.cost} Yolks",
                style = MaterialTheme.typography.bodySmall,
                color = if (isUnlocked) YolkGreenSuccess else YolkOrange,
                fontWeight = FontWeight.Medium
              )
            }
          }

          if (isEquipped) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = YolkGreenSuccess.copy(alpha = 0.15f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = YolkGreenSuccess,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Equipped",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = YolkGreenSuccess
                )
              }
            }
          } else if (isUnlocked) {
            OutlinedButton(
              onClick = { onEquipAccessory(accessory) },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("equip_button_${accessory.id}")
            ) {
              Text("Wear", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          } else {
            Button(
              onClick = { onEquipAccessory(accessory) },
              enabled = canAfford,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = YolkYellowPrimary),
              modifier = Modifier.testTag("unlock_button_${accessory.id}")
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Unlock", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = YolkWhite)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

@Composable
private fun StatPill(emoji: String, label: String, value: String) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surface,
    shadowElevation = 1.dp
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = emoji, fontSize = 16.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
