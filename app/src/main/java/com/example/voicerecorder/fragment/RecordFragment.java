package com.example.voicerecorder.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.voicerecorder.R;

import java.io.IOException;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController navController;
    private ImageButton listbtn,recordbtn,pauseBtn,stopBtn;
    private boolean isRecording=false;

    private MediaRecorder mediaRecorder;
    private Chronometer timer;


    private static final int REQUEST_PERMISSION=1;




    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        listbtn=view.findViewById(R.id.list_btn);
        recordbtn=view.findViewById(R.id.record_btn);
        stopBtn=view.findViewById(R.id.stop_btn);
        pauseBtn=view.findViewById(R.id.pause_btn);
        timer=view.findViewById(R.id.chronometer2);
        listbtn.setOnClickListener(this);
        recordbtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.list_btn:

                if(isRecording)
                {
                   AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                          alert .setTitle("Audio still recordin");
                          alert .setMessage("Are you sure you want to stop");
                          alert .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                               }
                           });
                          alert.setNegativeButton("no",null);
                          alert.create();
                          alert.show();

                }else {
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                }
                break;

            case R.id.record_btn:

                if(isRecording)
                {
                    stopRecording();
                    recordbtn.setVisibility(View.VISIBLE);
                    stopBtn.setVisibility(View.GONE);
                    pauseBtn.setVisibility(View.GONE);
                    isRecording=false;
                }
                else {

                    if(checkPermission()) {
                        startRecording();
                        stopBtn.setVisibility(View.VISIBLE);
                        recordbtn.setVisibility(View.GONE);
                        pauseBtn.setVisibility(View.VISIBLE);
                        isRecording = true;
                    }
                }
                break;
            case R.id.stop_btn:

                if(isRecording)
                {
                    stopRecording();
                    stopBtn.setVisibility(View.GONE);
                    pauseBtn.setVisibility(View.GONE);
                    recordbtn.setVisibility(View.VISIBLE);
                    isRecording=false;
                }
                else {

                    if(checkPermission()) {
                        startRecording();

                        recordbtn.setVisibility(View.GONE);
                        pauseBtn.setVisibility(View.VISIBLE);
                        stopBtn.setVisibility(View.VISIBLE);
                        isRecording = true;
                    }
                }
                break;

            case R.id.pause_btn:
        }
    }

    private boolean checkPermission()
    {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_PERMISSION);
            return false;
        }
    }

    private void stopRecording() {
        timer.stop();
        timer.setText("00:00");
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startRecording() {

        timer.setBase(SystemClock.elapsedRealtime());

        timer.start();

        String getFilePath = getActivity().getExternalFilesDir("/").getAbsolutePath();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.getDefault());
        Date date = new Date();
        String recordFile = "Recording_" +simpleDateFormat.format(date)+".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(getFilePath+"/"+recordFile);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();

    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording) {
            stopRecording();
        }
    }
}