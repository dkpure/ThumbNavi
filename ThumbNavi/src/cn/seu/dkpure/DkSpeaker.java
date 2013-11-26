package cn.seu.dkpure;

import android.content.Context;
import android.os.RemoteException;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;

public class DkSpeaker {
	private static final String TAG = "DkSpeaker";
	private SpeechSynthesizer mTts = null;
	
	DkSpeaker(Context context) {
		mTts = new SpeechSynthesizer(context, new InitListener() {
			@Override
			public void onInit(ISpeechModule arg0, int code) {
	        	if (code != ErrorCode.SUCCESS) {
	        		DkDebuger.e(TAG, "failed to init SpeechSynthesizer! Error code: " + code);
	        	}
			}
	    });
		
		if (mTts != null) {
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME,	"xiaoyan");
			mTts.setParameter(SpeechSynthesizer.SPEED, "50");
			mTts.setParameter(SpeechSynthesizer.PITCH, "50");
			mTts.setParameter(SpeechSynthesizer.VOLUME, "100");
			mTts.setParameter(SpeechConstant.PARAMS, "tts_audio_path=/sdcard/tts.pcm");
		}
	}
	
	public void SpeakTextOut(String txt) {
		if (mTts != null) {
			mTts.startSpeaking(txt, mTtsListener);
		}
	}
	
	private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
        @Override
        public void onBufferProgress(int progress) throws RemoteException {
        	 DkDebuger.d(TAG, "onBufferProgress :" + progress);
        }

        @Override
        public void onCompleted(int code) throws RemoteException {
        	DkDebuger.d(TAG, "onCompleted code =" + code);
        }

        @Override
        public void onSpeakBegin() throws RemoteException {
        	DkDebuger.d(TAG, "onSpeakBegin");
        }

        @Override
        public void onSpeakPaused() throws RemoteException {
        	DkDebuger.d(TAG, "onSpeakPaused.");
        }

        @Override
        public void onSpeakProgress(int progress) throws RemoteException {
//        	DkDebuger.d(TAG, "onSpeakProgress :" + progress);
        }

        @Override
        public void onSpeakResumed() throws RemoteException {
        	DkDebuger.d(TAG, "onSpeakResumed.");
        }
    };
}
