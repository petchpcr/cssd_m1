package com.poseintelligence.cssdm1.core.audio;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.poseintelligence.cssdm1.R;

import java.io.IOException;

public class iAudio extends AppCompatActivity{
    public MediaPlayer mediaPlayer;
    Activity context;

    // -----------------------------------------------------------------------
    // Sound
    // -----------------------------------------------------------------------
    public SoundPool soundPool;
    private static final int MAX_STREAMS = 5;
    private static final int streamType = AudioManager.STREAM_MUSIC;
    private int soundIdok;
    private int soundIdno;
    private int soundIdpayinfull;
    private int soundIdone;
    private int soundIdtwo;
    private int soundIdthree;
    private int soundIdfour;
    private int soundIdfive;
    private int soundIdsix;
    private int soundIdseven;
    private int soundIdeight;
    private int soundIdnine;
    private int soundIdten;
    private int soundIdtwenty;
    private int soundIdhundred;
    private int soundIded;
    private int soundIdnoitemfound;
    private int soundIdexpiring;
    private int soundIdexpire;
    private int soundIdfifo;
    private int soundIdrepeatscan;
    private int soundIdclosedoc;
    private int soundIdday;
    private int soundIdwithin;
    private int soundIdtoday;

    public iAudio(Activity context){
        this.context = context;
        // For Android SDK >= 21
        if (Build.VERSION.SDK_INT >= 21) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            this.soundPool = builder.build();
        }
        // for Android SDK < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        // When Sound Pool load complete.
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                //loaded = true;
            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        soundIdok = soundPool.load(context, R.raw.okay, 1);
        soundIdno = soundPool.load(context, R.raw.no, 1);
        soundIdpayinfull = soundPool.load(context, R.raw.pay_in_full, 1);
        soundIdone = soundPool.load(context, R.raw.one, 1);
        soundIdtwo = soundPool.load(context, R.raw.two, 1);
        soundIdthree = soundPool.load(context, R.raw.three, 1);
        soundIdfour = soundPool.load(context, R.raw.four, 1);
        soundIdfive = soundPool.load(context, R.raw.five, 1);
        soundIdsix = soundPool.load(context, R.raw.six, 1);
        soundIdseven = soundPool.load(context, R.raw.seven, 1);
        soundIdeight = soundPool.load(context, R.raw.eight, 1);
        soundIdnine = soundPool.load(context, R.raw.nine, 1);
        soundIdten = soundPool.load(context, R.raw.ten, 1);
        soundIdtwenty = soundPool.load(context, R.raw.twenty, 1);
        soundIdhundred = soundPool.load(context, R.raw.hundred, 1);
        soundIded = soundPool.load(context, R.raw.ed, 1);
        soundIdnoitemfound = soundPool.load(context, R.raw.no_item_found, 1);
        soundIdexpiring = soundPool.load(context, R.raw.expiring, 1);
        soundIdfifo = soundPool.load(context, R.raw.fifo, 1);
        soundIdrepeatscan = soundPool.load(context, R.raw.repeat_scan, 1);
        soundIdclosedoc = soundPool.load(context, R.raw.closedoc, 1);
        soundIdday = soundPool.load(context, R.raw.day, 1);
        soundIdwithin = soundPool.load(context, R.raw.within, 1);
        soundIdtoday = soundPool.load(context, R.raw.today, 1);
        soundIdexpire = soundPool.load(context, R.raw.expire, 1);
    }

    public void getAudio(String Number) {
        if(Number.equals("no_item_found") || Number.equals("pay_in_full") || Number.equals("okay") ||
                Number.equals("no") || Number.equals("expiring") || Number.equals("fifo") ||
                Number.equals("repeat_scan") || Number.equals("closedoc") || Number.equals("day") ||
                Number.equals("within") || Number.equals("today") || Number.equals("expire")) {
            playAudio(Number);
        }else{
            if (Number.length() == 1) {
                playAudio(Number);
            } else if (Number.length() == 2) {
                if (Number.substring(0, 1).equals("1")) {
                    playAudio("10");
                } else if (Number.substring(0, 1).equals("2")) {
                    playAudio("20");
                } else {
                    playAudio(Number.substring(0, 1));
                    playAudio("10");
                }

                if (Number.substring(1, 2).equals("0")) {
                    System.out.println("xxx : ========");
                } else if (Number.substring(1, 2).equals("1")) {
                    playAudio("ed");
                } else {
                    playAudio(Number.substring(1, 2));
                }
            } else if (Number.length() == 3) {
                playAudio(Number.substring(0, 1));
                playAudio("100");

                if (Number.substring(1, 2).equals("0")) {
                    System.out.println("xxx : ========");
                } else if (Number.substring(1, 2).equals("1")) {
                    playAudio("10");
                } else if (Number.substring(1, 2).equals("2")) {
                    playAudio("20");
                } else {
                    playAudio(Number.substring(1, 2));
                    playAudio("10");
                }

                if (Number.substring(2, 3).equals("0")) {
                    System.out.println("xxx : ========");
                } else if (Number.substring(2, 3).equals("1")) {
                    if (Number.substring(1, 2).equals("0")) {
                        playAudio("1");
                    } else {
                        playAudio("ed");
                    }
                } else {
                    playAudio(Number.substring(2, 3));
                }
            }
        }
    }

    private int[] getId(String Number) {
        int[] gId = new int[2];
        switch( Number ){
            case "1": gId[0] = soundIdone;gId[1] = 500;break;
            case "2": gId[0] = soundIdtwo;gId[1] = 500;break;
            case "3": gId[0] = soundIdthree;gId[1] = 500;break;
            case "4": gId[0] = soundIdfour;gId[1] = 500;break;
            case "5": gId[0] = soundIdfive;gId[1] = 500;break;
            case "6": gId[0] = soundIdsix;gId[1] = 500;break;
            case "7": gId[0] = soundIdseven;gId[1] = 500;break;
            case "8": gId[0] = soundIdeight;gId[1] = 500;break;
            case "9": gId[0] = soundIdnine;gId[1] = 500;break;
            case "10": gId[0] = soundIdten;gId[1] = 500;break;
            case "20": gId[0] = soundIdtwenty;gId[1] = 500;break;
            case "100": gId[0] = soundIdhundred;gId[1] = 500;break;
            case "ed": gId[0] = soundIded;gId[1] = 500;break;
            case "no_item_found": gId[0] = soundIdnoitemfound;gId[1] = 2000;break;
            case "pay_in_full": gId[0] = soundIdpayinfull;gId[1] = 2000;break;
            case "okay": gId[0] = soundIdok;gId[1] = 1000;break;
            case "no": gId[0] = soundIdno;gId[1] = 500;break;
            case "expiring": gId[0] = soundIdexpiring;gId[1] = 1000;break;
            case "fifo": gId[0] = soundIdfifo;gId[1] = 1000;break;
            case "repeat_scan": gId[0] = soundIdrepeatscan;gId[1] = 2000;break;
            case "closedoc": gId[0] = soundIdclosedoc;gId[1] = 2000;break;
            case "day": gId[0] = soundIdday;gId[1] = 500;break;
            case "within": gId[0] = soundIdwithin;gId[1] = 1000;break;
            case "today": gId[0] = soundIdtoday;gId[1] = 1000;break;
            case "expire": gId[0] = soundIdexpire;gId[1] = 1000;break;
        }
        return gId;
    }

    private void playAudio(String Number) {
        int[] gId = getId(Number);
        int streamId = soundPool.play(gId[0], 1, 1, 1, 0, 1f);
        try {
            Thread.sleep(gId[1]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        soundPool.stop(streamId);
    }

}
