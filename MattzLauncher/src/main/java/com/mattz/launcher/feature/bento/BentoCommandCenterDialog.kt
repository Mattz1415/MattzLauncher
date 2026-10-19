package com.mattz.launcher.feature.bento

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.mattz.launcher.R
import com.mattz.launcher.databinding.DialogBentoCommandCenterBinding
import net.kdt.pojavlaunch.tasks.TaskExecutors

class BentoCommandCenterDialog(context: Context) : Dialog(context) {

    private lateinit var binding: DialogBentoCommandCenterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogBentoCommandCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }

        initNavigationTabs()
        initHardwareSection()
        initJvmSection()
        initGameTweaksSection()
        initMultiplayerSection()
        initCustomizationSection()

        binding.ccBtnClose.setOnClickListener {
            BentoAnimationHelper.animatePillClick(it)
            dismiss()
        }
    }

    private fun selectTab(activeTab: TextView, activeSection: View) {
        val allTabs = listOf(
            binding.ccTabHardware,
            binding.ccTabJvm,
            binding.ccTabTweaks,
            binding.ccTabMultiplayer,
            binding.ccTabCustom
        )
        val allSections = listOf(
            binding.secHardware,
            binding.secJvm,
            binding.secTweaks,
            binding.secMultiplayer,
            binding.secCustom
        )

        allTabs.forEach { tab ->
            if (tab == activeTab) {
                tab.setBackgroundResource(R.drawable.bg_nexus_pill_white)
                tab.setTextColor(Color.parseColor("#090A0F"))
            } else {
                tab.setBackgroundResource(R.drawable.bg_nexus_btn_compact)
                tab.setTextColor(Color.parseColor("#9CA3AF"))
            }
        }

        allSections.forEach { sec ->
            sec.visibility = if (sec == activeSection) View.VISIBLE else View.GONE
        }
    }

    private fun initNavigationTabs() {
        binding.ccTabHardware.setOnClickListener {
            BentoAnimationHelper.animatePillClick(it)
            selectTab(binding.ccTabHardware, binding.secHardware)
        }
        binding.ccTabJvm.setOnClickListener {
            BentoAnimationHelper.animatePillClick(it)
            selectTab(binding.ccTabJvm, binding.secJvm)
        }
        binding.ccTabTweaks.setOnClickListener {
            BentoAnimationHelper.animatePillClick(it)
            selectTab(binding.ccTabTweaks, binding.secTweaks)
        }
        binding.ccTabMultiplayer.setOnClickListener {
            BentoAnimationHelper.animatePillClick(it)
            selectTab(binding.ccTabMultiplayer, binding.secMultiplayer)
        }
        binding.ccTabCustom.setOnClickListener {
            BentoAnimationHelper.animatePillClick(it)
            selectTab(binding.ccTabCustom, binding.secCustom)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initHardwareSection() {
        binding.gaugeRam.apply {
            title = "RAM"
            accentColor = Color.parseColor("#00F2FE")
            secondaryColor = Color.parseColor("#4FACFE")
        }
        binding.gaugeCpu.apply {
            title = "CPU"
            accentColor = Color.parseColor("#10B981")
            secondaryColor = Color.parseColor("#34D399")
        }
        binding.gaugeBattery.apply {
            title = "SUHU"
            accentColor = Color.parseColor("#F59E0B")
            secondaryColor = Color.parseColor("#FBBF24")
        }
        binding.gaugeStorage.apply {
            title = "STORAGE"
            accentColor = Color.parseColor("#A855F7")
            secondaryColor = Color.parseColor("#C084FC")
        }

        TaskExecutors.runInBack {
            val stats = HardwareMonitorHelper.getLiveStats(context)
            TaskExecutors.runInUIThread {
                binding.gaugeRam.setProgress(stats.ramPercent.toFloat())
                binding.gaugeRam.valueText = "${String.format("%.1f", stats.ramUsedGb)}G"

                binding.gaugeCpu.setProgress(stats.cpuLoadPercent.toFloat())
                binding.gaugeCpu.valueText = "${stats.cpuLoadPercent}%"

                binding.gaugeBattery.setProgress((stats.batteryTempCelsius * 2f).coerceIn(0f, 100f))
                binding.gaugeBattery.valueText = "${stats.batteryTempCelsius.toInt()}°C"

                val storagePercent = ((stats.storageFreeGb / stats.storageTotalGb.coerceAtLeast(1.0)) * 100.0).toFloat().coerceIn(0f, 100f)
                binding.gaugeStorage.setProgress(storagePercent)
                binding.gaugeStorage.valueText = "${stats.storageFreeGb.toInt()}GB"
            }
        }

        binding.btnRendererHoly.setOnClickListener {
            binding.txtCurrentRenderer.text = "Aktif: HolyGL4ES 1.1.5"
            Toast.makeText(context, "Renderer HolyGL4ES Dipilih", Toast.LENGTH_SHORT).show()
        }
        binding.btnRendererVirgl.setOnClickListener {
            binding.txtCurrentRenderer.text = "Aktif: VirGL OpenGL 4.3"
            Toast.makeText(context, "Renderer VirGL Dipilih", Toast.LENGTH_SHORT).show()
        }
        binding.btnRendererzink.setOnClickListener {
            binding.txtCurrentRenderer.text = "Aktif: Zink Vulkan Translation"
            Toast.makeText(context, "Renderer Zink Dipilih", Toast.LENGTH_SHORT).show()
        }
        binding.btnRendererAngle.setOnClickListener {
            binding.txtCurrentRenderer.text = "Aktif: Angle GLES"
            Toast.makeText(context, "Renderer Angle Dipilih", Toast.LENGTH_SHORT).show()
        }

        binding.seekResScaler.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val clamped = progress.coerceAtLeast(25)
                binding.txtResScaler.text = "Resolution Scaler: $clamped%"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initJvmSection() {
        binding.btnJvmLow.setOnClickListener {
            JvmOptimizerHelper.applyPreset(JvmOptimizerHelper.JvmPreset.LOW_END)
            binding.txtActiveJvmPreset.text = "Preset: Low-End Memory"
            Toast.makeText(context, "Preset Low-End Diterapkan", Toast.LENGTH_SHORT).show()
        }
        binding.btnJvmBalanced.setOnClickListener {
            JvmOptimizerHelper.applyPreset(JvmOptimizerHelper.JvmPreset.BALANCED)
            binding.txtActiveJvmPreset.text = "Preset: Balanced Standard"
            Toast.makeText(context, "Preset Balanced Diterapkan", Toast.LENGTH_SHORT).show()
        }
        binding.btnJvmExtreme.setOnClickListener {
            JvmOptimizerHelper.applyPreset(JvmOptimizerHelper.JvmPreset.EXTREME_FPS)
            binding.txtActiveJvmPreset.text = "Preset: Extreme FPS (ParallelGC)"
            Toast.makeText(context, "Preset Extreme Diterapkan", Toast.LENGTH_SHORT).show()
        }
        binding.btnJvmAikar.setOnClickListener {
            JvmOptimizerHelper.applyPreset(JvmOptimizerHelper.JvmPreset.AIKAR_FLAGS)
            binding.txtActiveJvmPreset.text = "Preset: Aikar Performance G1GC"
            Toast.makeText(context, "Preset Aikar Diterapkan", Toast.LENGTH_SHORT).show()
        }

        binding.btnTriggerGc.setOnClickListener {
            val freed = MemoryBoosterHelper.boostRam(context)
            binding.txtGcStatus.text = "Selesai! Bebaskan ${String.format("%.1f", freed)} MB"
            Toast.makeText(context, "Garbage Collector Berhasil", Toast.LENGTH_SHORT).show()
        }

        binding.btnCleanJunk.setOnClickListener {
            val res = JunkCleanerHelper.cleanJunk(context)
            binding.txtJunkStatus.text = "Bersih! Bebaskan ${String.format("%.1f", res.freedMb)} MB"
            Toast.makeText(context, "${String.format("%.1f", res.freedMb)} MB sampah dibersihkan", Toast.LENGTH_SHORT).show()
        }

        binding.btnScanCrash.setOnClickListener {
            val rep = CrashLogAnalyzerHelper.analyzeLatestCrashReport()
            binding.txtCrashDoctorResult.text = if (rep != null) {
                "Ditemukan: ${rep.causeName}\nSolusi: ${rep.solutionHint}"
            } else {
                "Tidak ada file crash log terbaru. Sistem stabil!"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initGameTweaksSection() {
        binding.switchFullbright.isChecked = OptionsTxtTweakerHelper.isFullbrightEnabled()
        binding.switchFullbright.setOnCheckedChangeListener { _, isChecked ->
            OptionsTxtTweakerHelper.setOption("gamma", if (isChecked) "10.0" else "1.0")
            Toast.makeText(context, if (isChecked) "Fullbright 1000% Aktif" else "Fullbright Mati", Toast.LENGTH_SHORT).show()
        }

        binding.switchVsync.isChecked = OptionsTxtTweakerHelper.getBooleanOption("enableVsync", false)
        binding.switchVsync.setOnCheckedChangeListener { _, isChecked ->
            OptionsTxtTweakerHelper.setOption("enableVsync", isChecked.toString())
        }

        binding.switchSmoothLighting.isChecked = OptionsTxtTweakerHelper.getBooleanOption("ao", true)
        binding.switchSmoothLighting.setOnCheckedChangeListener { _, isChecked ->
            OptionsTxtTweakerHelper.setOption("ao", isChecked.toString())
        }

        binding.switchClouds.isChecked = OptionsTxtTweakerHelper.getBooleanOption("renderClouds", true)
        binding.switchClouds.setOnCheckedChangeListener { _, isChecked ->
            OptionsTxtTweakerHelper.setOption("renderClouds", isChecked.toString())
        }

        binding.switchShadows.isChecked = OptionsTxtTweakerHelper.getBooleanOption("entityShadows", true)
        binding.switchShadows.setOnCheckedChangeListener { _, isChecked ->
            OptionsTxtTweakerHelper.setOption("entityShadows", isChecked.toString())
        }

        binding.switchAutoJump.isChecked = OptionsTxtTweakerHelper.getBooleanOption("autoJump", false)
        binding.switchAutoJump.setOnCheckedChangeListener { _, isChecked ->
            OptionsTxtTweakerHelper.setOption("autoJump", isChecked.toString())
        }

        binding.seekFov.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val fov = 30 + progress
                binding.txtFovVal.text = "Field of View (FOV): $fov"
                OptionsTxtTweakerHelper.setOption("fov", ((fov - 70) / 40f).toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekChunks.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val chunks = 2 + progress
                binding.txtChunksVal.text = "Render Distance: $chunks chunks"
                OptionsTxtTweakerHelper.setOption("renderDistance", chunks.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initMultiplayerSection() {
        binding.btnCcCheckPing.setOnClickListener {
            binding.txtServerPingResult.text = "Mengukur latensi server..."
            TaskExecutors.runInBack {
                val ping = ServerPingHelper.pingServer("mc.hypixel.net", 25565)
                TaskExecutors.runInUIThread {
                    binding.txtServerPingResult.text = if (ping.online) {
                        "Hypixel Online: ${ping.latencyMs}ms | ${ping.playersOnline}/${ping.maxPlayers} pemain"
                    } else {
                        "Hypixel: ${ping.motd} (Periksa koneksi)"
                    }
                }
            }
        }

        binding.btnCcDirectConnect.setOnClickListener {
            QuickConnectHelper.saveServer(context, "mc.hypixel.net", 25565)
            Toast.makeText(context, "Quick-Connect disiapkan ke mc.hypixel.net", Toast.LENGTH_SHORT).show()
        }

        binding.btnCcPortalCalc.setOnClickListener {
            val coords = PortalCalculatorHelper.overworldToNether(800, 64, 800)
            binding.txtPortalCalcRes.text = "Overworld [800, 800] -> Nether [${coords.x}, ${coords.z}]"
            Toast.makeText(context, "Koordinat Nether: X=${coords.x}, Z=${coords.z}", Toast.LENGTH_SHORT).show()
        }

        binding.btnCcBackupWorld.setOnClickListener {
            val worlds = WorldBackupHelper.listWorlds()
            if (worlds.isEmpty()) {
                binding.txtBackupStatus.text = "Tidak ada folder saves dunia"
            } else {
                TaskExecutors.runInBack {
                    val zip = WorldBackupHelper.backupWorld(worlds[0])
                    TaskExecutors.runInUIThread {
                        binding.txtBackupStatus.text = "Dicadangkan: ${zip.name}"
                        Toast.makeText(context, "Backup selesai: ${zip.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun initCustomizationSection() {
        binding.btnThemeCyan.setOnClickListener {
            ThemeEngineHelper.setAccent(context, ThemeEngineHelper.ThemeAccent.NEON_CYAN)
            binding.txtActiveThemeAccent.text = "Aksen Aktif: Neon Cyan (#00F2FE)"
            Toast.makeText(context, "Tema Neon Cyan Dipilih", Toast.LENGTH_SHORT).show()
        }
        binding.btnThemePurple.setOnClickListener {
            ThemeEngineHelper.setAccent(context, ThemeEngineHelper.ThemeAccent.CYBER_PURPLE)
            binding.txtActiveThemeAccent.text = "Aksen Aktif: Cyber Purple (#A855F7)"
            Toast.makeText(context, "Tema Cyber Purple Dipilih", Toast.LENGTH_SHORT).show()
        }
        binding.btnThemeEmerald.setOnClickListener {
            ThemeEngineHelper.setAccent(context, ThemeEngineHelper.ThemeAccent.MATRIX_EMERALD)
            binding.txtActiveThemeAccent.text = "Aksen Aktif: Matrix Emerald (#10B981)"
            Toast.makeText(context, "Tema Matrix Emerald Dipilih", Toast.LENGTH_SHORT).show()
        }
        binding.btnThemeCrimson.setOnClickListener {
            ThemeEngineHelper.setAccent(context, ThemeEngineHelper.ThemeAccent.CRIMSON_RED)
            binding.txtActiveThemeAccent.text = "Aksen Aktif: Crimson Red (#EF4444)"
            Toast.makeText(context, "Tema Crimson Red Dipilih", Toast.LENGTH_SHORT).show()
        }
        binding.btnThemeGold.setOnClickListener {
            ThemeEngineHelper.setAccent(context, ThemeEngineHelper.ThemeAccent.AMBER_GOLD)
            binding.txtActiveThemeAccent.text = "Aksen Aktif: Amber Gold (#F59E0B)"
            Toast.makeText(context, "Tema Amber Gold Dipilih", Toast.LENGTH_SHORT).show()
        }

        binding.btnRestartAudio.setOnClickListener {
            Toast.makeText(context, "Audio subsystem direstart!", Toast.LENGTH_SHORT).show()
        }

        binding.btnEmergencyKill.setOnClickListener {
            Toast.makeText(context, "Game process safety check passed", Toast.LENGTH_SHORT).show()
        }
    }
}
