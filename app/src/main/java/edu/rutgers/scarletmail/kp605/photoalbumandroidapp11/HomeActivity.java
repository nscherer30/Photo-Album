package edu.rutgers.scarletmail.kp605.photoalbumandroidapp11;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.rutgers.scarletmail.kp605.photoalbumandroidapp11.application.PhotoActivity;
import edu.rutgers.scarletmail.kp605.photoalbumandroidapp11.model.Album;
import edu.rutgers.scarletmail.kp605.photoalbumandroidapp11.model.Photo;
import edu.rutgers.scarletmail.kp605.photoalbumandroidapp11.model.User;

import edu.rutgers.scarletmail.kp605.photoalbumandroidapp11.adapters.*;

import static edu.rutgers.scarletmail.kp605.photoalbumandroidapp11.R.id.deleteButton;


public class HomeActivity extends AppCompatActivity {

    Button createAlbumButton;
    ListView albumListView;
    View empty;
    String path;

    public static User user;
    ArrayList<Album> albums;

    AlbumAdapter albumAdapter;

    private View.OnClickListener createDialogListener;
    private AdapterView.OnItemClickListener itemClickListener;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        initLayoutWidgets();
        setWidgetActions();

        if(albums != null) {
            for(Album album : albums) {
                if(album.getName().contains("Search__")) {
                    user.getAlbums().remove(album);
                    albums = user.getAlbums();
                }
            }
        }

        path = getApplicationContext().getFilesDir().getPath().toString() +  File.separator + "userData.dat";
        user = User.read(path);
        if(user != null) {
            albums = user.getAlbums();
        } else {
            user = new User("me");
            albums = user.getAlbums();
        }

        albumAdapter = new AlbumAdapter(getApplicationContext(), albums);
        albumListView.setAdapter(albumAdapter);

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        if(albums == null){
            View empty = findViewById(R.id.empty);
            ListView list = (ListView) findViewById(R.id.list);
            list.setEmptyView(empty);
        }
    }

    private void initLayoutWidgets(){

        createAlbumButton = (Button) findViewById(R.id.createAlbumButton);

        albumListView = (ListView) findViewById(R.id.list);

        empty = findViewById(R.id.empty);

        createDialogListener = new View.OnClickListener() {
            public void onClick(View v) {

                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.create_album_dialog);
                dialog.setTitle("Add Album");

                final EditText value = (EditText) dialog.findViewById(R.id.albumName);

                Button addButton = (Button) dialog.findViewById(R.id.add);
                Button cancelButton = (Button) dialog.findViewById(R.id.cancel);

                // if button is clicked, close the custom dialog
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(user.getAlbum(value.getText().toString()) == null) {
                            List<Photo> emptyList = new ArrayList<Photo>();
                            Album newAlbum = new Album(value.getText().toString(), emptyList);
                            user.addAlbum(newAlbum);
                            albums = user.getAlbums();
                            albumAdapter.notifyDataSetChanged();
                            User.write(user, path);
                            dialog.dismiss();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("Album exists with this name");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }

                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                dialog.show();
            }
        };

        itemClickListener = new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.item_click_dialog);
                dialog.setTitle("Open, Edit or Delete Album");

                final EditText value = (EditText) dialog.findViewById(R.id.albumEditText);

                Button openButton = (Button) dialog.findViewById(R.id.openAlbumButton);
                Button saveButton = (Button) dialog.findViewById(R.id.saveEditButton);
                Button deleteButton = (Button) dialog.findViewById(R.id.deleteAlbumButton);
                Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                final String albumName = ((TextView) view).getText().toString();
                final Album album = user.getAlbum(albumName);
                value.setText( albumName );

                // if button is clicked, close the custom dialog
                openButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(HomeActivity.this, PhotoActivity.class);
                        intent.putExtra("album", albumName);
                        startActivity(intent);

                        dialog.dismiss();

                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(user.getAlbum(value.getText().toString()) == null) {
                            String newAlbumName = value.getText().toString();
                            user.getAlbum(album.getName()).setName(newAlbumName);
                            albums = user.getAlbums();
                            albumAdapter.notifyDataSetChanged();
                            User.write(user, path);
                            dialog.dismiss();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("Album exists with this name");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }

                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        user.removeAlbum(album);
                        albums = user.getAlbums();
                        albumAdapter.notifyDataSetChanged();

                        User.write(user, path);

                        dialog.dismiss();

                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                dialog.show();
            }
        };



    }

    private void setWidgetActions(){

        createAlbumButton.setOnClickListener(createDialogListener);

        albumListView.setOnItemClickListener(itemClickListener);

    }
}
