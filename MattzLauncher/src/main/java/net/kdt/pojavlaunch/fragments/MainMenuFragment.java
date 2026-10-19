package net.kdt.pojavlaunch.fragments;

import static com.mattz.launcher.event.single.RefreshVersionsEvent.MODE.END;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mattz.anim.AnimPlayer;
import com.mattz.anim.animations.Animations;
import com.mattz.launcher.R;
import com.mattz.launcher.databinding.FragmentLauncherBinding;
import com.mattz.launcher.event.single.AccountUpdateEvent;
import com.mattz.launcher.event.single.LaunchGameEvent;
import com.mattz.launcher.event.single.RefreshVersionsEvent;
import com.mattz.launcher.feature.bento.*;
import com.mattz.launcher.feature.version.Version;
import com.mattz.launcher.feature.version.VersionInfo;
import com.mattz.launcher.feature.version.VersionsManager;
import com.mattz.launcher.feature.version.utils.VersionIconUtils;
import com.mattz.launcher.task.TaskExecutors;
import com.mattz.launcher.ui.fragment.AboutFragment;
import com.mattz.launcher.ui.fragment.ControlButtonFragment;
import com.mattz.launcher.ui.fragment.FilesFragment;
import com.mattz.launcher.ui.fragment.FragmentWithAnim;
import com.mattz.launcher.ui.fragment.InstallGameFragment;
import com.mattz.launcher.ui.fragment.ModsFragment;
import com.mattz.launcher.ui.fragment.SettingsFragment;
import com.mattz.launcher.ui.fragment.VersionManagerFragment;
import com.mattz.launcher.ui.fragment.VersionsListFragment;
import com.mattz.launcher.ui.subassembly.account.AccountViewWrapper;
import com.mattz.launcher.utils.ZHTools;
import com.mattz.launcher.utils.anim.ViewAnimUtils;
import com.mattz.launcher.utils.path.PathManager;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

public class MainMenuFragment extends FragmentWithAnim {
    public static final String TAG = "MainMenuFragment";
    private FragmentLauncherBinding binding;
    private AccountViewWrapper accountViewWrapper;

    public MainMenuFragment() {
        super(R.layout.fragment_launcher);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLauncherBinding.inflate(getLayoutInflater());
        accountViewWrapper = new AccountViewWrapper(this, binding.viewAccount);
        accountViewWrapper.refreshAccountInfo();
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initTopBar();
        initNavigationDock();
        initBentoDashboard();
        initLaunchBar();
        refreshCurrentVersion();

        // Staggered entrance animation for Nexus Cards
        BentoAnimationHelper.INSTANCE.animateBentoStagger(
                binding.bentoGreetingCard,
                binding.cardDiscoveryHub,
                binding.cardOptimizer,
                binding.cardServerPing,
                binding.cardOptionsTweaker,
                binding.cardCleanerCrash,
                binding.cardPortalCalc,
                binding.cardToolsHub
        );
    }

    private void initTopBar() {
        binding.btnTopSearch.setOnClickListener(v -> {
            BentoAnimationHelper.INSTANCE.animatePillClick(v);
            ZHTools.swapFragmentWithAnim(this, VersionsListFragment.class, VersionsListFragment.TAG, null);
        });

        if (binding.btnTopSettings != null) {
            binding.btnTopSettings.setOnClickListener(v -> {
                BentoAnimationHelper.INSTANCE.animatePillClick(v);
                ZHTools.swapFragmentWithAnim(this, SettingsFragment.class, SettingsFragment.TAG, null);
            });
        }

        binding.segTabInstances.setOnClickListener(v -> binding.bentoScrollContainer.smoothScrollTo(0, 0));

        binding.segTabLibrary.setOnClickListener(v -> {
            BentoAnimationHelper.INSTANCE.animatePillClick(v);
            ZHTools.swapFragmentWithAnim(this, VersionsListFragment.class, VersionsListFragment.TAG, null);
        });

        binding.segTabCommunity.setOnClickListener(v -> {
            BentoAnimationHelper.INSTANCE.animatePillClick(v);
            ZHTools.swapFragmentWithAnim(this, AboutFragment.class, AboutFragment.TAG, null);
        });
    }

    private void initNavigationDock() {
        binding.tabDashboard.setOnClickListener(v -> {
            BentoAnimationHelper.INSTANCE.animatePillClick(v);
            binding.bentoScrollContainer.smoothScrollTo(0, 0);
        });

        binding.tabMods.setOnClickListener(v -> {
            BentoAnimationHelper.INSTANCE.animatePillClick(v);
            Bundle bundle = new Bundle();
            bundle.putString(ModsFragment.BUNDLE_ROOT_PATH, PathManager.DIR_GAME_HOME + "/mods");
            ZHTools.swapFragmentWithAnim(this, ModsFragment.class, ModsFragment.TAG, bundle);
        });

        binding.tabOptimizer.setOnClickListener(v -> {
            BentoAnimationHelper.INSTANCE.animatePillClick(v);
            new BentoCommandCenterDialog(requireContext()).show();
        });

        binding.tabTools.setOnClickListener(v -> {
            BentoAnimationHelper.INSTANCE.animatePillClick(v);
            new BentoCommandCenterDialog(requireContext()).show();
        });

        binding.tabProfiles.setOnClickListener(v -> {
            BentoAnimationHelper.INSTANCE.animatePillClick(v);
            Bundle bundle = new Bundle();
            bundle.putString(FilesFragment.BUNDLE_LOCK_PATH, PathManager.DIR_GAME_HOME);
            bundle.putString(FilesFragment.BUNDLE_LIST_PATH, PathManager.DIR_GAME_HOME);
            ZHTools.swapFragmentWithAnim(this, FilesFragment.class, FilesFragment.TAG, bundle);
        });

        if (binding.tabSettings != null) {
            binding.tabSettings.setOnClickListener(v -> {
                BentoAnimationHelper.INSTANCE.animatePillClick(v);
                ZHTools.swapFragmentWithAnim(this, SettingsFragment.class, SettingsFragment.TAG, null);
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void initBentoDashboard() {
        // 1. Dynamic Greeting & Stats
        binding.dynamicGreetingText.setText("SIAP DIMAINKAN");
        updateHeroStats();

        // 2. Hardware Live Monitor (RAM & Battery)
        updateHardwareStats();

        // 3. JVM Optimizer Button
        binding.btnOptimizeJvm.setOnClickListener(v -> new BentoCommandCenterDialog(requireContext()).show());

        // 4. Boost RAM Button (Amber pill)
        binding.btnBoostRam.setOnClickListener(v -> {
            ViewAnimUtils.setViewAnim(binding.btnBoostRam, Animations.Pulse);
            float freedMb = MemoryBoosterHelper.INSTANCE.boostMemory(requireContext());
            Toast.makeText(requireContext(), "RAM Dioptimasi: " + String.format("%.1f", freedMb) + " MB dibebaskan", Toast.LENGTH_SHORT).show();
            updateHardwareStats();
            updateHeroStats();
        });

        // Filter & Quick Tweaks button -> Bento Command Center
        binding.btnFilterTweaks.setOnClickListener(v -> new BentoCommandCenterDialog(requireContext()).show());

        // 5. Server Ping Button
        binding.btnPingServer.setOnClickListener(v -> {
            binding.serverPingResult.setText("Memeriksa latency...");
            TaskExecutors.runInBack(() -> {
                ServerPingHelper.PingResult res = ServerPingHelper.INSTANCE.pingServer("mc.hypixel.net", 25565, 3000);
                TaskExecutors.runInUIThread(() -> binding.serverPingResult.setText(res.getHost() + " • " + res.getMessage()));
            });
        });

        // 6. Direct Connect Button
        binding.btnDirectConnect.setOnClickListener(v -> showDirectConnectDialog());

        // 7. Fullbright Tweaker Button
        updateFullbrightButtonLabel();
        binding.btnToggleFullbright.setOnClickListener(v -> {
            boolean nowOn = OptionsTxtTweakerHelper.INSTANCE.toggleFullbright();
            updateFullbrightButtonLabel();
            Toast.makeText(requireContext(), nowOn ? "Fullbright 1000% Aktif" : "Gamma Normal (100%)", Toast.LENGTH_SHORT).show();
        });

        // 8. Cleaner Button
        binding.btnCleanJunk.setOnClickListener(v -> {
            JunkCleanerHelper.CleanResult result = JunkCleanerHelper.INSTANCE.cleanJunk(requireContext());
            binding.cleanerStatus.setText("Bersih! Membebaskan " + String.format("%.1f", result.getFreedMb()) + " MB");
            Toast.makeText(requireContext(), String.format("%.1f", result.getFreedMb()) + " MB cache dibersihkan", Toast.LENGTH_SHORT).show();
        });

        // 9. Crash Analyzer Button
        binding.btnAnalyzeCrash.setOnClickListener(v -> {
            CrashLogAnalyzerHelper.CrashDiagnosis diagnosis = CrashLogAnalyzerHelper.INSTANCE.analyzeLatestCrash();
            new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
                    .setTitle(diagnosis.getSummary())
                    .setMessage("Berkas: " + diagnosis.getReportFile() + "\n\nPenyebab:\n" + diagnosis.getSuspectedCause() + "\n\nSolusi:\n" + diagnosis.getRecommendedSolution())
                    .setPositiveButton("Tutup", null)
                    .show();
        });

        // 10. Portal Calculator Button
        binding.btnOpenPortalCalc.setOnClickListener(v -> showPortalCalculatorDialog());

        // 11. Re-anchored essential utilities
        binding.customControlButton.setOnClickListener(v -> ZHTools.swapFragmentWithAnim(this, ControlButtonFragment.class, ControlButtonFragment.TAG, null));
        binding.openMainDirButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(FilesFragment.BUNDLE_LIST_PATH, PathManager.DIR_GAME_HOME);
            bundle.putString(FilesFragment.BUNDLE_LOCK_PATH, PathManager.DIR_GAME_HOME);
            ZHTools.swapFragmentWithAnim(this, FilesFragment.class, FilesFragment.TAG, bundle);
        });
        binding.installJarButton.setOnClickListener(v -> runInstallerWithConfirmation(false));
        binding.installJarButton.setOnLongClickListener(v -> {
            runInstallerWithConfirmation(true);
            return true;
        });
    }

    private void showQuickTweaksDialog() {
        new BentoCommandCenterDialog(requireContext()).show();
    }

    private void updateHeroStats() {
        kotlin.Pair<String, Integer> stats = PlaytimeTrackerHelper.INSTANCE.getStatsFormatted(requireContext());
        binding.playtimeSessionBadge.setText("Total Bermain: " + stats.getFirst() + " • " + stats.getSecond() + " Sesi");
    }

    @SuppressLint("SetTextI18n")
    private void updateHardwareStats() {
        TaskExecutors.runInBack(() -> {
            HardwareMonitorHelper.HardwareStats stats = HardwareMonitorHelper.INSTANCE.getLiveStats(requireContext());
            TaskExecutors.runInUIThread(() -> {
                if (binding.gaugeRamDash != null) {
                    binding.gaugeRamDash.setTitle("RAM");
                    binding.gaugeRamDash.setAccentColor(0xFF00F2FE);
                    binding.gaugeRamDash.setSecondaryColor(0xFF4FACFE);
                    binding.gaugeRamDash.setProgress((float) stats.getRamPercent(), true);
                    binding.gaugeRamDash.setValueText(String.format("%.1fG", stats.getRamUsedGb()));
                }
                if (binding.gaugeCpuDash != null) {
                    binding.gaugeCpuDash.setTitle("CPU");
                    binding.gaugeCpuDash.setAccentColor(0xFF10B981);
                    binding.gaugeCpuDash.setSecondaryColor(0xFF34D399);
                    float cpuPct = (float) Math.min(100.0, Math.max(10.0, stats.getCpuCores() * 12.5));
                    binding.gaugeCpuDash.setProgress(cpuPct, true);
                    binding.gaugeCpuDash.setValueText(stats.getCpuCores() + " Core");
                }
                if (binding.gaugeBatteryDash != null) {
                    binding.gaugeBatteryDash.setTitle("SUHU");
                    binding.gaugeBatteryDash.setAccentColor(0xFFF59E0B);
                    binding.gaugeBatteryDash.setSecondaryColor(0xFFFBBF24);
                    binding.gaugeBatteryDash.setProgress(Math.min(100f, (float) stats.getBatteryTempCelsius() * 2f), true);
                    binding.gaugeBatteryDash.setValueText(((int) stats.getBatteryTempCelsius()) + "°C");
                }
                if (binding.gaugeStorageDash != null) {
                    binding.gaugeStorageDash.setTitle("STORAGE");
                    binding.gaugeStorageDash.setAccentColor(0xFFA855F7);
                    binding.gaugeStorageDash.setSecondaryColor(0xFFC084FC);
                    float storagePct = (float) Math.min(100.0, (stats.getStorageFreeGb() / Math.max(1.0, stats.getStorageTotalGb())) * 100.0);
                    binding.gaugeStorageDash.setProgress(storagePct, true);
                    binding.gaugeStorageDash.setValueText(((int) stats.getStorageFreeGb()) + "GB");
                }
                if (binding.hwRamChip != null) {
                    binding.hwRamChip.setText("RAM: " + String.format("%.1f", stats.getRamUsedGb()) + "/" + String.format("%.1f", stats.getRamTotalGb()) + " GB (" + stats.getRamPercent() + "%)");
                }
                if (binding.hwBatteryChip != null) {
                    binding.hwBatteryChip.setText("Suhu: " + String.format("%.1f", stats.getBatteryTempCelsius()) + "°C (" + stats.getBatteryPercent() + "%)");
                }
            });
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateFullbrightButtonLabel() {
        boolean isFull = OptionsTxtTweakerHelper.INSTANCE.isFullbrightEnabled();
        binding.btnToggleFullbright.setText(isFull ? "Fullbright (Aktif)" : "Aktifkan Fullbright");
    }

    private void initLaunchBar() {
        // Arrow navigation for cycling versions
        binding.btnPrevVersion.setOnClickListener(v -> cycleVersion(-1));
        binding.btnNextVersion.setOnClickListener(v -> cycleVersion(1));

        binding.versionName.setOnClickListener(v -> {
            if (!isTaskRunning()) {
                ZHTools.swapFragmentWithAnim(this, VersionsListFragment.class, VersionsListFragment.TAG, null);
            } else {
                ViewAnimUtils.setViewAnim(binding.versionName, Animations.Shake);
                Toast.makeText(requireContext(), R.string.version_manager_task_in_progress, Toast.LENGTH_SHORT).show();
            }
        });

        if (binding.version != null) {
            binding.version.setOnClickListener(v -> {
                if (!isTaskRunning()) {
                    ZHTools.swapFragmentWithAnim(this, VersionsListFragment.class, VersionsListFragment.TAG, null);
                }
            });
        }

        binding.managerProfileButton.setOnClickListener(v -> {
            if (!isTaskRunning()) {
                BentoAnimationHelper.INSTANCE.animatePillClick(binding.managerProfileButton);
                ZHTools.swapFragmentWithAnim(this, InstallGameFragment.class, InstallGameFragment.TAG, null);
            } else {
                ViewAnimUtils.setViewAnim(binding.managerProfileButton, Animations.Shake);
                Toast.makeText(requireContext(), R.string.version_manager_task_in_progress, Toast.LENGTH_SHORT).show();
            }
        });

        binding.playButton.setOnClickListener(v -> {
            BentoAnimationHelper.INSTANCE.animateSpringPress(binding.playButton, () -> {
                MemoryBoosterHelper.INSTANCE.boostMemory(requireContext());
                PlaytimeTrackerHelper.INSTANCE.recordSessionLaunch(requireContext());
                EventBus.getDefault().post(new LaunchGameEvent());
                return kotlin.Unit.INSTANCE;
            });
        });

        binding.versionName.setSelected(true);
        binding.versionInfo.setSelected(true);
    }

    private void cycleVersion(int direction) {
        List<Version> list = VersionsManager.INSTANCE.getVersions();
        if (list == null || list.isEmpty()) {
            Toast.makeText(requireContext(), "Belum ada versi Minecraft terpasang", Toast.LENGTH_SHORT).show();
            return;
        }
        Version current = VersionsManager.INSTANCE.getCurrentVersion();
        int currentIndex = -1;
        if (current != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getVersionName().equals(current.getVersionName())) {
                    currentIndex = i;
                    break;
                }
            }
        }
        int newIndex;
        if (currentIndex == -1) {
            newIndex = 0;
        } else {
            newIndex = (currentIndex + direction + list.size()) % list.size();
        }
        Version target = list.get(newIndex);
        VersionsManager.INSTANCE.saveCurrentVersion(target.getVersionName());
        refreshCurrentVersion();
    }

    private void showJvmPresetDialog() {
        JvmOptimizerHelper.JvmPreset[] presets = JvmOptimizerHelper.JvmPreset.values();
        String[] names = new String[presets.length];
        for (int i = 0; i < presets.length; i++) {
            names[i] = presets[i].getDisplayName() + "\n" + presets[i].getDescription();
        }

        new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
                .setTitle("Pilih Preset JVM Arguments")
                .setItems(names, (dialog, which) -> {
                    JvmOptimizerHelper.INSTANCE.applyPreset(presets[which]);
                    binding.optCurrentPreset.setText("Preset: " + presets[which].getDisplayName());
                    Toast.makeText(requireContext(), "Preset JVM " + presets[which].getDisplayName() + " Diterapkan!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showDirectConnectDialog() {
        final EditText input = new EditText(requireContext());
        input.setHint("contoh: mc.hypixel.net:25565");
        input.setTextColor(0xFFFFFFFF);

        new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
                .setTitle("Direct Server Quick-Connect")
                .setMessage("Masukkan IP dan Port server tujuan:")
                .setView(input)
                .setPositiveButton("Simpan & Siapkan", (dialog, which) -> {
                    String val = input.getText().toString().trim();
                    if (!val.isEmpty()) {
                        QuickConnectHelper.INSTANCE.saveServer(requireContext(), val, 25565);
                        Toast.makeText(requireContext(), "Server tersimpan: " + val, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showPortalCalculatorDialog() {
        final EditText input = new EditText(requireContext());
        input.setHint("Masukkan koordinat X Overworld");
        input.setTextColor(0xFFFFFFFF);

        new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
                .setTitle("Nether Portal Calculator")
                .setMessage("Masukkan koordinat X di Overworld untuk menghitung posisi portal di Nether:")
                .setView(input)
                .setPositiveButton("Hitung", (dialog, which) -> {
                    int x = 0;
                    try {
                        x = Integer.parseInt(input.getText().toString().trim());
                    } catch (Exception ignored) {}
                    PortalCalculatorHelper.Coords res = PortalCalculatorHelper.INSTANCE.overworldToNether(x, 64, x);
                    binding.portalResultPreview.setText("Overworld [" + x + ", " + x + "] -> Nether [" + res.getX() + ", " + res.getZ() + "]");
                    Toast.makeText(requireContext(), "Nether Coords: X=" + res.getX() + ", Z=" + res.getZ(), Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Tutup", null)
                .show();
    }

    private void showToolsDialog() {
        new BentoCommandCenterDialog(requireContext()).show();
    }

    private void refreshCurrentVersion() {
        Version version = VersionsManager.INSTANCE.getCurrentVersion();

        int versionInfoVisibility;
        if (version != null) {
            binding.versionName.setText(version.getVersionName());
            VersionInfo versionInfo = version.getVersionInfo();
            if (versionInfo != null && versionInfo.getInfoString() != null) {
                binding.versionInfo.setText(versionInfo.getInfoString());
                versionInfoVisibility = View.VISIBLE;
            } else {
                binding.versionInfo.setText("Minecraft • Siap");
                versionInfoVisibility = View.VISIBLE;
            }

            if (binding.versionIcon != null) {
                new VersionIconUtils(version).start(binding.versionIcon);
            }
            binding.managerProfileButton.setVisibility(View.VISIBLE);
        } else {
            binding.versionName.setText(R.string.version_no_versions);
            binding.managerProfileButton.setVisibility(View.GONE);
            versionInfoVisibility = View.GONE;
        }
        binding.versionInfo.setVisibility(versionInfoVisibility);
        updateHeroStats();
    }

    @Subscribe()
    public void event(RefreshVersionsEvent event) {
        if (event.getMode() == END) {
            TaskExecutors.runInUIThread(this::refreshCurrentVersion);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(AccountUpdateEvent event) {
        if (accountViewWrapper != null) accountViewWrapper.refreshAccountInfo();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void runInstallerWithConfirmation(boolean isCustomArgs) {
        if (ProgressKeeper.getTaskCount() == 0)
            Tools.installMod(requireActivity(), isCustomArgs);
        else
            Toast.makeText(requireContext(), R.string.tasks_ongoing, Toast.LENGTH_LONG).show();
    }

    @Override
    public void slideIn(AnimPlayer animPlayer) {
        animPlayer.apply(new AnimPlayer.Entry(binding.nexusTopBar, Animations.FadeInDown))
                .apply(new AnimPlayer.Entry(binding.bentoScrollContainer, Animations.FadeInDown))
                .apply(new AnimPlayer.Entry(binding.floatingNavigationIsland, Animations.SlideInUp));
    }

    @Override
    public void slideOut(AnimPlayer animPlayer) {
        animPlayer.apply(new AnimPlayer.Entry(binding.nexusTopBar, Animations.FadeOutUp))
                .apply(new AnimPlayer.Entry(binding.bentoScrollContainer, Animations.FadeOutUp))
                .apply(new AnimPlayer.Entry(binding.floatingNavigationIsland, Animations.SlideOutDown));
    }
}
