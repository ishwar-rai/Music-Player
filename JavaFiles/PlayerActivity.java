package com.example.beathub;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    View topView;
    ImageView play,next,previous,topImage, thumbnail;
    TextView topSongName, startTime, endTime, topArtistname;
    SeekBar seekBar;

    String song;
    String artistName;
    public static  MediaPlayer mediaPlayer;
    public int position;
    ArrayList<File> mySongs;
    Thread updateBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        seekBar=findViewById(R.id.seekBar);
        play=findViewById(R.id.play);
        next=findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        topImage=findViewById(R.id.topImgView);
        topSongName=findViewById(R.id.topTextView);
        topArtistname = findViewById(R.id.topArtistname);
        startTime=findViewById(R.id.startTime);
        endTime=findViewById(R.id.endTime);
        topView = findViewById(R.id.topView);

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();

        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("Songs");
        String songName = intent.getStringExtra("SongName");
        String album = intent.getStringExtra("Album");
        artistName = intent.getStringExtra("Artist");
        position = bundle.getInt("Position", 0);

        Uri uri = Uri.parse(mySongs.get(position).toString());
        song = mySongs.get(position).getName();
        topSongName.setText(song);
        topArtistname.setText(artistName);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        updateBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentDuration = 0;
                //endTime.setText(totalDuration);

                while (currentDuration<mediaPlayer.getDuration()){
                    try {
                        Thread.sleep(500);
                        currentDuration = mediaPlayer.getCurrentPosition();
                        if (seekBar != null) {
                            seekBar.setProgress(currentDuration);
                            seekBar.setMax(mediaPlayer.getDuration());
                        }
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        updateBar.start();

        seekBar.getProgressDrawable().setColorFilter(
                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(Color.parseColor("#7C5A5F"), android.graphics.PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                } else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != mySongs.size()){
                    position+=1;
                }
                else position = 0;
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                song = mySongs.get(position).getName();
                topSongName.setText(song);
                topArtistname.setText(MediaStore.Audio.Media.ARTIST);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);


                String totalTime = Time(mediaPlayer.getDuration());
                endTime.setText(totalTime);

            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next.performClick();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?mySongs.size()-1:(position-1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                song = mySongs.get(position).getName();
                topSongName.setText(song);
                topArtistname.setText(MediaStore.Audio.Media.ARTIST);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);


                String totalTime = Time(mediaPlayer.getDuration());
                endTime.setText(totalTime);
            }
        });

        String totalTime = Time(mediaPlayer.getDuration());
        endTime.setText(totalTime);

        Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    String currentTime = Time(mediaPlayer.getCurrentPosition());
                    startTime.setText(currentTime);
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    public String Time(int duration)
    {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time+=min+":";
        if(sec<10){
            time+="0";
        }
        time+=sec;

        return time;
    }
}