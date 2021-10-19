//packages need to import
package com.example.beathub;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public ListView listView;
    public String[] items;
    ImageView songImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //super.onCreate();
        songImage = findViewById(R.id.songImg);
        listView = findViewById(R.id.listView);

        //Dexter for asking user to allow to read mp3 files from external storage or memory card (Permission)
        //First you have to add dependency (implementation 'com.karumi:dexter:6.2.3') in Bundle.gradle(Module) and click sync now
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        //Fetching files from external storage in ArrayList if permission granted
                        ArrayList<File> songs = fetchSongs(Environment.getExternalStorageDirectory());
                        items = new String[songs.size()];

                        //replacing the .mp3 extension with space i,e " "
                        for (int i=0; i<songs.size(); i++){
                            items[i] = songs.get(i).getName().replace(".mp3", " ");
                        }
                        MyAdaptor adaptor = new MyAdaptor();
                        listView.setAdapter(adaptor);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String songName = (String) listView.getItemAtPosition(i);
                                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                                        .putExtra("Songs", songs)
                                        .putExtra("SongName", songName)
                                        .putExtra("Position", i)
                                        .putExtra("Album", MediaStore.Audio.Media.ALBUM)
                                        .putExtra("Artist", MediaStore.Audio.Media.ARTIST));
                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }


    public ArrayList<File> fetchSongs (File files){
        ArrayList<File> songs = new ArrayList<>();
        File[] fileList = files.listFiles();
        if (fileList!=null){
            for (File file : fileList){
                if (!file.isHidden() && file.isDirectory()){
                    songs.addAll(fetchSongs(file));
                } else{
                    if (file.getName().endsWith(".mp3") && !file.getName().startsWith(".")){
                        songs.add(file);
                    }
                }
            }
        }
        return songs;
    }

    class MyAdaptor extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.showlist, null);
            TextView songText = myView.findViewById(R.id.songname);
            TextView artistSongText = myView.findViewById(R.id.topArtistname);
            //artistSongText.setText(Arrays.stream(items));
            songText.setSelected(true);
            songText.setText(items[i]);
            return myView;
    }
}
