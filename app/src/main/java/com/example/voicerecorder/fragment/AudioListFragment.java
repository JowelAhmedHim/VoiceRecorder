package com.example.voicerecorder.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.voicerecorder.R;
import com.example.voicerecorder.adapter.AudioListAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


public class AudioListFragment extends Fragment implements AudioListAdapter.onItemClick {

     private LinearLayout bottomSheet_layout;
     private BottomSheetBehavior bottomSheetBehavior;
     private RecyclerView recyclerView;

     private File[] allFile;
     private File fileToPlay;

     private MediaPlayer mediaPlayer =null;
     private boolean isPlaying=false;

     private SeekBar seekBar;
     private Handler handler;
     private Runnable updateSeekBer;

     //Ui element

    private TextView playerFilename;
    private TextView playerTitle;
    private TextView playerSubtitle;


    private ImageButton play;



    public AudioListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView=view.findViewById(R.id.recyclerview);
        playerFilename=view.findViewById(R.id.fileName);


        play=view.findViewById(R.id.play);
        seekBar=view.findViewById(R.id.seekBar);


        String getPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory= new File(getPath);
        allFile=directory.listFiles();

        AudioListAdapter audioListAdapter = new AudioListAdapter(allFile,this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(audioListAdapter);




        bottomSheet_layout=view.findViewById(R.id.bottom_sheet_layout);
        bottomSheetBehavior= BottomSheetBehavior.from(bottomSheet_layout);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState==BottomSheetBehavior.STATE_HIDDEN)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying)
                {
                    pauseAudio();
                }
                else
                {
                    if(fileToPlay!=null) {
                        playAudio();
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(fileToPlay!=null)
                {
                    int progress= seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                }
                playAudio();

            }
        });
    }

    @Override
    public void onClickListener(File file, int position) {

        if(isPlaying)
        {
             stopPlaying();

        }else {
            fileToPlay=file;
            startPlaying(fileToPlay);
        }

    }

    private void pauseAudio()
    {
        mediaPlayer.pause();
        play.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));
        isPlaying=false;
        handler.removeCallbacks(updateSeekBer);
    }

    private void playAudio(){
        mediaPlayer.start();
        play.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_circle_outline_24, null));

        updateHandler();
        seekBar.postDelayed(updateSeekBer,0);
        isPlaying=true;
    }

    private void startPlaying(File fileToPlay) {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        play.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_circle_outline_24, null));

        playerFilename.setText(fileToPlay.getName());

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });

        seekBar.setMax(mediaPlayer.getDuration());

        updateHandler();

        isPlaying=true;
    }

    private void updateHandler() {
        handler = new Handler();
        updateSeekBer = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBar.postDelayed(this,300);
            }
        };
        handler.postDelayed(updateSeekBer,0);
    }

    private void stopPlaying() {
        play.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));
        isPlaying=false;
        mediaPlayer.stop();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isPlaying) {
            mediaPlayer.stop();
        }
    }
}