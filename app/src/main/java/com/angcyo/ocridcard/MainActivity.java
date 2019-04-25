package com.angcyo.ocridcard;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.angcyo.camera.preview.CameraPreviewView;
import com.angcyo.camera.preview.IDCardPreview;
import com.angcyo.tesstwo.Tesstwo;

public class MainActivity extends AppCompatActivity {
    Tesstwo tesstwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //initRecyclerView();

        tesstwo = new Tesstwo();
        tesstwo.setOnResultCallback(new Tesstwo.OnResultCallback() {
            @Override
            public void onResult(final String idCardNo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, idCardNo, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

//        ((IDCardPreview) findViewById(R.id.id_card_preview)).setOnIDCardPictureCallback(new IDCardPreview.OnIDCardPictureCallback() {
//            @Override
//            public boolean onIDCardPicture(@NonNull final Bitmap idCardBitmap) {
//                //Log.i("test", Ocr.recognize(MainActivity.this, idCardBitmap).toString());
//
//                final Bitmap localre = tesstwo.localre(idCardBitmap);
//
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        ((ImageView) findViewById(R.id.image_view)).setImageBitmap(localre);
////                    }
////                });
//                return false;
//            }
//        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tesstwo.release();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                if (i == 0) {
                    return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_camera_preview, viewGroup, false));
                }
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_text_view, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

            }

            @Override
            public int getItemViewType(int position) {
                if (position == 0) {
                    return 0;
                }
                return 1;
            }

            @Override
            public int getItemCount() {
                return 10;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ((CameraPreviewView) findViewById(R.id.id_card_preview)).onRequestPermissionsResult(requestCode, grantResults);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
