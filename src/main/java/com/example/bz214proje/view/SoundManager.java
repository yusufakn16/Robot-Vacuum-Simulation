package com.example.bz214proje.view;

import javafx.scene.media.AudioClip;
import java.util.Objects;

// ========================================================
// SES YÖNETİMİ (SOUND MANAGER)
// ========================================================
// Oyun içerisindeki ses efektlerinin (AudioClip) yüklenmesi, 
// seviyelerinin ayarlanması ve tetiklenmesi işlemlerini gerçekleştirir.
public class SoundManager {

    // private AudioClip clickSound; // DİKKAT: Klasörde click.mp3 yok diye şimdilik kapattım.
    private AudioClip bumpSound;
    private AudioClip cleanSound;
    private AudioClip carpetSound;

    private AudioClip sesBasla;
    private AudioClip sesDuraklat;
    private AudioClip sesIstasyon;

    private AudioClip sesToz;
    private AudioClip sesSivi;
    private AudioClip sesLeke;

    public SoundManager() {
        // --------------------------------------------------------
        // 1. SES DOSYALARININ YÜKLENMESİ
        // --------------------------------------------------------
        // Dosya bulunamaması durumunda sistemin çökmesini engellemek adına 
        // her AudioClip nesnesi kendi try-catch bloğu içerisinde yüklenmiştir.

        try { bumpSound = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bz214proje/sounds/bump.mp3")).toExternalForm()); } catch (Exception e) { System.err.println("HATA: bump.mp3 bulunamadı."); }
        try { cleanSound = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bz214proje/sounds/clean.mp3")).toExternalForm()); } catch (Exception e) { System.err.println("HATA: clean.mp3 bulunamadı."); }

        try { carpetSound = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bz214proje/sounds/carpet.mp3")).toExternalForm()); } catch (Exception e) { System.err.println("HATA: carpet.mp3 bulunamadı."); }

        try { sesBasla = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bz214proje/sounds/basla.mp3")).toExternalForm()); } catch (Exception e) { System.err.println("HATA: basla.mp3 bulunamadı."); }
        try { sesDuraklat = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bz214proje/sounds/duraklat.mp3")).toExternalForm()); } catch (Exception e) { System.err.println("HATA: duraklat.mp3 bulunamadı."); }
        try { sesIstasyon = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bz214proje/sounds/istasyon.mp3")).toExternalForm()); } catch (Exception e) { System.err.println("HATA: istasyon.mp3 bulunamadı."); }

        try { sesToz = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bz214proje/sounds/toz.mp3")).toExternalForm()); } catch (Exception e) { System.err.println("HATA: toz.mp3 bulunamadı."); }
        try { sesSivi = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bz214proje/sounds/sivi.mp3")).toExternalForm()); } catch (Exception e) { System.err.println("HATA: sivi.mp3 bulunamadı."); }
        try { sesLeke = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bz214proje/sounds/leke.mp3")).toExternalForm()); } catch (Exception e) { System.err.println("HATA: leke.mp3 bulunamadı."); }

        // --------------------------------------------------------
        // 2. SES SEVİYELERİ VE DÖNGÜ (LOOP) AYARLARI
        // --------------------------------------------------------
        if (sesBasla != null) sesBasla.setVolume(1.0);
        if (sesDuraklat != null) sesDuraklat.setVolume(1.0);
        if (sesIstasyon != null) sesIstasyon.setVolume(1.0);
        if (bumpSound != null) bumpSound.setVolume(0.8);

        if (sesToz != null) sesToz.setVolume(0.9);
        if (sesSivi != null) sesSivi.setVolume(0.9);
        if (sesLeke != null) sesLeke.setVolume(0.9);

        if (cleanSound != null) {
            cleanSound.setVolume(0.5);
            cleanSound.setCycleCount(AudioClip.INDEFINITE);
        }
        if (carpetSound != null) {
            carpetSound.setVolume(0.4);
            carpetSound.setCycleCount(AudioClip.INDEFINITE);
        }
    }

    // --------------------------------------------------------
    // 3. SES ÇALMA VE KONTROL METOTLARI
    // --------------------------------------------------------
    public void playBump() { if (bumpSound != null) bumpSound.play(); }

    public void playClean(boolean onCarpet) {
        if (onCarpet) {
            if (cleanSound != null && cleanSound.isPlaying()) cleanSound.stop();
            if (carpetSound != null && !carpetSound.isPlaying()) carpetSound.play();
        } else {
            if (carpetSound != null && carpetSound.isPlaying()) carpetSound.stop();
            if (cleanSound != null && !cleanSound.isPlaying()) cleanSound.play();
        }
    }

    public void stopClean() {
        if (cleanSound != null && cleanSound.isPlaying()) cleanSound.stop();
        if (carpetSound != null && carpetSound.isPlaying()) carpetSound.stop();
    }

    public void playSesBasla() { if (sesBasla != null) sesBasla.play(); }
    public void playSesDuraklat() { if (sesDuraklat != null) sesDuraklat.play(); }
    public void playSesIstasyon() { if (sesIstasyon != null) sesIstasyon.play(); }

    public void playDirtSound(String dirtType) {
        stopClean(); // Önce genel motor sesi durdurulur

        if (dirtType.equalsIgnoreCase("toz") && sesToz != null) {
            sesToz.play();
        } else if ((dirtType.equalsIgnoreCase("sıvı") || dirtType.equalsIgnoreCase("sivi")) && sesSivi != null) {
            sesSivi.play();
        } else if (dirtType.equalsIgnoreCase("leke") && sesLeke != null) {
            sesLeke.play();
        }
    }

    public void setCleanSoundQuiet() {
        if (cleanSound != null && cleanSound.isPlaying()) {
            cleanSound.stop();
            cleanSound.setVolume(0.3);
            cleanSound.play();
        }
        if (carpetSound != null && carpetSound.isPlaying()) {
            carpetSound.stop();
            carpetSound.setVolume(0.35);
            carpetSound.play();
        }
    }

    public void setCleanSoundNormal() {
        if (cleanSound != null && cleanSound.isPlaying()) {
            cleanSound.stop();
            cleanSound.setVolume(0.5);
            cleanSound.play();
        }
        if (carpetSound != null && carpetSound.isPlaying()) {
            carpetSound.stop();
            carpetSound.setVolume(0.4);
            carpetSound.play();
        }
    }
}