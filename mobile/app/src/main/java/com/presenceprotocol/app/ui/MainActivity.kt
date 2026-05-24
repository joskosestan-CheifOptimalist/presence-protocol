// Backup of broken file

package com.presenceprotocol.app.ui

import androidx.compose.animation.AnimatedVisibility
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.presenceprotocol.app.ui.theme.PresenceTheme
import com.presenceprotocol.app.ui.theme.Olive
import com.presenceprotocol.app.ui.theme.OlivePale
import com.presenceprotocol.app.ui.theme.GoldBright
import com.presenceprotocol.app.ui.theme.GoldLight
import com.presenceprotocol.app.ui.theme.Cream
import com.presenceprotocol.app.ui.theme.Dark
import com.presenceprotocol.app.ui.theme.Mid
import com.presenceprotocol.app.ui.theme.Gray
import com.presenceprotocol.app.ui.theme.Gold
import com.presenceprotocol.app.ui.theme.GoldPale
import com.presenceprotocol.app.ui.theme.LayerMobile
import com.presenceprotocol.app.ui.theme.LayerEncounter
import com.presenceprotocol.app.ui.theme.LayerRelay
import com.presenceprotocol.app.ui.theme.LayerMidnight
import com.presenceprotocol.app.ui.theme.LayerCardano
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon

class MainActivity : ComponentActivity() {

    private val dashboardViewModel: DashboardViewModel by lazy { DashboardViewModelClient.default() }
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.e("PP_BLE", "BOOT: PresenceProtocol started")
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result.values.all { it }
            if (granted) {
                PresenceMiningService.start(this@MainActivity)
            } else {
                Toast.makeText(this, "Presence Protocol requires Bluetooth permissions", Toast.LENGTH_LONG).show()
            }
        }
        if (hasBlePermissions()) {
            PresenceMiningService.start(this)
        } else {
            permissionLauncher.launch(requiredPermissions())
        }
        setContent { PresenceApp(dashboardViewModel) }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Device Field continues in PresenceMiningService — do not stop here
    }

    private fun hasBlePermissions(): Boolean =
        requiredPermissions().all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }

    private fun requiredPermissions(): Array<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
}

@Composable
private fun PresenceApp(viewModel: DashboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    PresenceTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BrandHeader(
                    onLongPress = { viewModel.showDeveloperPanel(true) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                PresencePulseHero(uiState)
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryToggle(isMining = uiState.isMining) { viewModel.toggleMining() }
                VerifiedCard(uiState)
                Spacer(modifier = Modifier.height(12.dp))
                YieldCard(uiState)

                Spacer(modifier = Modifier.height(12.dp))

                CollapsibleProtocolLayers(
                    uiState = uiState,
                    onOpenDeveloperPanel = { viewModel.showDeveloperPanel(true) }
                )
            }
            if (uiState.showDeveloperPanel) {
                DeveloperPanel(uiState = uiState, dismiss = { viewModel.showDeveloperPanel(false) })
            }
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun TopBar(title: String, subtitle: String, pill: String, onLongPress: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .pointerInput(Unit) { detectTapGestures(onLongPress = { onLongPress() }) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = subtitle, fontSize = 12.sp, color = GoldLight)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(OlivePale.copy(alpha = 0.18f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
        }
    }
}

@Composable
private fun BrandHeader(onLongPress: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clickable { onLongPress() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "◈",
            fontSize = 18.sp,
            color = Olive,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "PRESENCE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Dark,
                letterSpacing = 1.sp
            )
            Text(
                text = "PROTOCOL",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Olive,
                letterSpacing = 1.sp
            )
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(OlivePale.copy(alpha = 0.75f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "◌", fontSize = 18.sp, color = Olive)
        }
    }
}

@Composable
private fun PresencePulseHero(uiState: DashboardUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp),
        colors = CardDefaults.cardColors(containerColor = OlivePale),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetricTile(
                icon = Icons.Rounded.Today,
                label = "Today",
                value = "+${String.format("%.1f", uiState.todayYield)} ${uiState.tokenSymbol}",
                modifier = Modifier.weight(1f)
            )

            DividerMark()

            MetricTile(
                icon = Icons.Rounded.Layers,
                label = "Total",
                value = "${String.format("%.1f", uiState.totalBalance)} ${uiState.tokenSymbol}",
                modifier = Modifier.weight(1f)
            )

            DividerMark()

            MetricTile(
                icon = Icons.Rounded.Redeem,
                label = "Last reward",
                value = "+${String.format("%.1f", uiState.lastReward)} ${uiState.tokenSymbol}",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricTile(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(OlivePale.copy(alpha = 0.65f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Olive,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 12.sp, color = Gray.copy(alpha = 0.78f))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Dark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DividerMark() {
    Box(
        modifier = Modifier
            .height(42.dp)
            .width(1.dp)
            .background(Gray.copy(alpha = 0.35f))
    )
}

@Composable
private fun PrimaryToggle(isMining: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = GoldBright),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Dark.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Sensors,
                    contentDescription = null,
                    tint = Dark,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isMining) "Stop Field" else "Enter Field",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )
                Text(
                    text = if (isMining) "Field is ACTIVE" else "Field is IDLE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Dark.copy(alpha = 0.68f)
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(OlivePale.copy(alpha = 0.9f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "›", fontSize = 22.sp, color = Dark)
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
private fun VerifiedCard(uiState: DashboardUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(138.dp),
        colors = CardDefaults.cardColors(containerColor = OlivePale),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Device Field",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "● Rolling",
                    fontSize = 13.sp,
                    color = Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Seen (10m) ${uiState.peersSeenLast10Minutes}",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Dark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Nearby ${uiState.peersNearby}",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Dark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Based on signed BLE discovery",
                fontSize = 11.sp,
                color = Gray
            )
        }
    }
}

@Composable
private fun YieldCard(uiState: DashboardUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp),
        colors = CardDefaults.cardColors(containerColor = OlivePale),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Local Receipt Balance", fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text(text = "Local only", fontSize = 12.sp, color = Gray)
            }
            Text(text = "${String.format("%.1f", uiState.totalBalance)} ${uiState.tokenSymbol}", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MiningCountersCard(uiState: DashboardUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp),
        colors = CardDefaults.cardColors(containerColor = OlivePale),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Field Counters", fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text(text = "Protocol Epoch ${uiState.epoch}", fontSize = 12.sp, color = Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = uiState.encountersThisEpoch.toString(), fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "This Epoch", fontSize = 12.sp, color = Gray)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = uiState.totalEncounters.toString(), fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Total", fontSize = 12.sp, color = Gray)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = uiState.pendingEncounters.toString(), fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Pending", fontSize = 12.sp, color = Gray)
                }
            }
        }
    }
}



private fun formatReceiptAge(timestampMs: Long): String {
    val deltaSec = ((System.currentTimeMillis() - timestampMs) / 1000).coerceAtLeast(0)
    return when {
        deltaSec < 15 -> "just now"
        deltaSec < 60 -> "${deltaSec}s ago"
        deltaSec < 3600 -> "${deltaSec / 60}m ago"
        else -> "${deltaSec / 3600}h ago"
    }
}

@Composable
private fun CollapsibleProtocolLayers(
    uiState: DashboardUiState,
    onOpenDeveloperPanel: () -> Unit
) {
    var expandedLayer by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LayerAccordion(
            title = "Field Counters",
            subtitle = "${uiState.verifiedToday} verified · ${uiState.peersNearby} nearby",
            expanded = expandedLayer == "counters",
            onClick = {
                expandedLayer = if (expandedLayer == "counters") null else "counters"
            }
        ) {
            MiningCountersCard(uiState)
        }
        LayerAccordion(
            title = "Encounter Receipts",
            subtitle = if (uiState.recentReceipts.isEmpty())
                "No local receipts yet"
            else
                "Latest ${uiState.recentReceipts.first().peerLabel}",
            expanded = expandedLayer == "receipts",
            onClick = {
                expandedLayer = if (expandedLayer == "receipts") null else "receipts"
            }
        ) {
            RecentReceiptsCard(uiState)

            CpopIssuanceTelemetryCard(uiState)
        }

        LayerAccordion(
            title = "Settlement Readiness",
            subtitle = "Wallet offline · settlement preview",
            expanded = expandedLayer == "settlement",
            onClick = {
                expandedLayer = if (expandedLayer == "settlement") null else "settlement"
            }
        ) {
            SettlementLayerCard(uiState)
        }
        LayerAccordion(
            title = "Details & Logs",
            subtitle = "Developer protocol state",
            expanded = false,
            onClick = onOpenDeveloperPanel
        ) {}
    }
}

@Composable
private fun LayerAccordion(
    title: String,
    subtitle: String,
    expanded: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Cream.copy(alpha = 0.92f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Dark
                    )

                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Gray
                    )
                }
                Text(
                    text = if (expanded) "−" else "+",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gold
                )
            }

            AnimatedVisibility(
                visible = expanded
            ) {
                Column(
                    modifier = Modifier.padding(top = 14.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun CpopIssuanceTelemetryCard(uiState: DashboardUiState) {
    val avgReward = if (uiState.totalEncounters > 0) {
        uiState.totalBalance / uiState.totalEncounters
    } else {
        0.0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GoldPale),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "CPOP Issuance",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Dark
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IssuanceMetric(
                    label = "Issued",
                    value = String.format("%.2f", uiState.totalBalance),
                    modifier = Modifier.weight(1f)
                )
                IssuanceMetric(
                    label = "Receipts",
                    value = uiState.totalEncounters.toString(),
                    modifier = Modifier.weight(1f)
                )
                IssuanceMetric(
                    label = "Avg",
                    value = String.format("%.2f", avgReward),
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Local protocol accounting only — not on-chain minting.",
                fontSize = 11.sp,
                color = Gray,
                fontWeight = FontWeight.Medium
            )

            if (uiState.anchorHash.isNotBlank()) {
                Text(
                    text = "Anchor ${uiState.anchorHash.take(18)}…",
                    fontSize = 11.sp,
                    color = Mid
                )
            }
        }
    }
}

@Composable
private fun IssuanceMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Cream.copy(alpha = 0.72f), RoundedCornerShape(18.dp))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Dark
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Gray
        )
    }
}

@Composable
private fun RecentReceiptsCard(uiState: DashboardUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = OlivePale),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Encounter Receipts",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Dark
            )

            if (uiState.recentReceipts.isEmpty()) {
                Text(
                    text = "No local encounter receipts yet",
                    fontSize = 12.sp,
                    color = Gray
                )
            } else {
                val latest = uiState.recentReceipts.first()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Latest local receipt",
                            fontSize = 11.sp,
                            color = Gray
                        )

                        Text(
                            text = latest.peerLabel,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Dark
                        )

                        Text(
                            text = formatReceiptAge(latest.timestampMs),
                            fontSize = 11.sp,
                            color = Gray
                        )
                    }

                    Text(
                        text = "+${String.format("%.2f", latest.reward)} ${uiState.tokenSymbol}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReceiptStatusChip("CBOR", Olive)
                    ReceiptStatusChip("SIGNED", Gold)
                    ReceiptStatusChip("LOCAL", Mid)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Receipt Anchor",
                    fontSize = 10.sp,
                    color = Gray
                )

                Text(
                    text = uiState.anchorHash.take(18).ifBlank { "pending..." },
                    fontSize = 11.sp,
                    color = Dark,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Stored locally from signed BLE encounter events",
                    fontSize = 11.sp,
                    color = Gray
                )
            }
        }
    }
}


@Composable
private fun ReceiptStatusChip(
    label: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun SettlementLayerCard(uiState: DashboardUiState) {
    var showWalletPreview by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GoldPale),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Settlement Readiness",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Dark
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LayerChip("Receipt Layer", LayerEncounter)
                LayerChip("Wallet Offline", LayerMidnight)
                LayerChip("Preview", LayerCardano)
            }

            Text(
                text = "Encounter receipts are stored locally. Wallet confirmation and settlement remain future protocol layers.",
                fontSize = 12.sp,
                color = Mid
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Local Receipt Balance",
                        fontSize = 11.sp,
                        color = Gray
                    )

                    Text(
                        text = String.format("%.1f %s", uiState.totalBalance, uiState.tokenSymbol),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gold
                    )
                }

                Text(
                    text = "Settlement Offline",
                    fontSize = 11.sp,
                    color = Mid
                )
            }

            OutlinedButton(
                onClick = { showWalletPreview = true },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Olive.copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Olive
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "View Settlement Status",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showWalletPreview) {
        AlertDialog(
            onDismissRequest = { showWalletPreview = false },
            title = {
                Text(
                    text = "Settlement Readiness",
                    color = Olive,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Encounter receipts are currently stored locally.",
                        fontSize = 14.sp,
                        color = Dark
                    )

                    Text(
                        text = "Settlement Status: Preview",
                        fontSize = 13.sp,
                        color = Olive,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Wallet Layer: Offline",
                        fontSize = 13.sp,
                        color = Mid
                    )

                    Text(
                        text = "Claiming: Inactive",
                        fontSize = 13.sp,
                        color = Mid
                    )

                    Text(
                        text = "No wallet claim is active in this MVP.",
                        fontSize = 13.sp,
                        color = Mid
                    )

                    Button(
                        onClick = { },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Olive.copy(alpha = 0.45f),
                            disabledContentColor = GoldPale.copy(alpha = 0.75f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Settlement Offline",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWalletPreview = false }) {
                    Text("Close", color = Olive, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = GoldPale,
            shape = RoundedCornerShape(28.dp)
        )
    }
}

@Composable
private fun LayerChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DeveloperPanel(uiState: DashboardUiState, dismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f))
            .clickable { dismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.72f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {


                DebugRow("Mining", if (uiState.isMining) "ON" else "OFF")
                DebugRow("Debug State", uiState.debugState)
                DebugRow("Status", uiState.statusText)
                DebugRow("Network", uiState.networkHealth)
                DebugRow("Last Peer Seen", uiState.lastPeerSeenId)
                DebugRow("Peers Nearby", uiState.peersNearby.toString())
                DebugRow("Peers Seen (10m)", uiState.peersSeenLast10Minutes.toString())
                DebugRow("Pending", uiState.pendingEncounters.toString())
                DebugRow("Verified Today", uiState.verifiedToday.toString())
                DebugRow("Heartbeat", uiState.heartbeatTick.toString())
                DebugRow("Epoch", uiState.epoch.toString())


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    uiState.devLog.forEach { entry ->
                        Text(text = entry, fontSize = 12.sp)
                    }
                    if (uiState.devLog.isEmpty()) {
                        Text(text = "No events yet", fontSize = 12.sp, color = Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun DebugRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = Gray)
        Text(text = value, fontSize = 12.sp, color = Dark, fontWeight = FontWeight.Medium)
    }
}

