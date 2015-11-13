package com.thanhle.englishvocabulary.utils;

import android.os.Handler;
import android.util.Log;

import com.acapelagroup.android.tts.acattsandroid;
import com.acapelagroup.android.tts.acattsandroid.iTTSEventsCallback;

public class TTS implements iTTSEventsCallback
{
    private acattsandroid TTS = null;
    private static TTS instance = null;
    private String texttospeak;
    private Handler mHandler;

    public static TTS getInstance()
    {
        if (instance == null)
        {
            instance = new TTS();
        }
        return instance;
    }

    public void setHandler(Handler handler)
    {
        mHandler = handler;
    }

    public void init(String voicePath)
    {
        TTS = new acattsandroid(this);
        // A license is required and is linked to a voice pack.
        TTS.setLicense(
                0x444e4153,
                0x00306483,
                "\"6095 0 SAND #EVALUATION#Acapela Group Android SDK\"\nWCu3N!wM2nTDH9oID$B6cwmsz7GoYWHlcCg5rYDOXb55O!scsulDB8@gRJ7UyFeSszm#\nYmhJ7Gf8mL6Z@$iQJNU!QX3CuRWsziIXV2W%$D%zu6ESLiSD\nWCuZw6xz!PLAmumZDO8xmT##\n");
        String[] voiceDirPaths = {voicePath};
        String[] voicesList = TTS.getVoicesList(voiceDirPaths);
        if (voicesList.length > 0)
        {
            int r = TTS.load(voicesList[0]);
            Log.d("TTS", "Load TTS result: " + r);
        }
        else
        {
            Log.w("TTS", "No voices");
        }
    }

    public void speak(String text)
    {
        if (TTS == null)
        {
            return;
        }
        texttospeak += text;
        TTS.queueText(text);
    }

    @Override
    public void ttsevents(long type, long param1, long param2, long param3,
                          long param4)
    {
        if (type == acattsandroid.EVENT_WORD_POS && mHandler != null)
        {
            // Check word position is not out of range
            if (param1 >= texttospeak.length() || param2 == 0)
            {
                // in evalation (free), if param2 = 0 and param1 > text length,
                // stop speak
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TTS.stop();
                    }
                });
            }
        }
        else if (type == acattsandroid.EVENT_TEXT_END)
        {
            texttospeak = "";
        }
    }

}
