package com.example.notex;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.media.MediaScannerConnection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.notex.adapters.MusicNoteAdapter;
import com.example.notex.models.MusicNote;

import com.bumptech.glide.Glide;



public class TransActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;

    private TextView textViewXml;
    private ImageView imageView;
    private List<MusicNote> musicNotes = new ArrayList<>();
    private Bitmap musicBitmap;
    private static final String TAG = "TransActivity";

    private static final int REQUEST_WRITE_STORAGE = 112;

    private String xmlString;

    private void checkAndRequestPermissions() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Button buttonUpload = findViewById(R.id.button_upload);
        textViewXml = findViewById(R.id.textView_xml);
        imageView = findViewById(R.id.imageView);
        Button buttonSave = findViewById(R.id.button_save);
        Button buttonBackHome= findViewById(R.id.buttonBackToHome);


        buttonUpload.setOnClickListener(v -> openFilePicker());
        buttonSave.setOnClickListener(v -> saveImageToExternalStorage(xmlString));
        buttonBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        });

    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            uploadFile(fileUri);
        }
    }

    private void uploadFile(Uri fileUri) {
        String fileName = getFileName(fileUri);
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);

            // Use Retrofit to upload file
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://47.120.9.223:8000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);
            RequestBody requestBody = RequestBody.create(MediaType.parse("audio/*"), fileBytes);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", fileName, requestBody);

            Call<ResponseBody> call = apiService.uploadFile(filePart);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            byte[] imageBytes = response.body().bytes();
//                            textViewXml.setText(xmlString);
//                            parseXml(xmlString);
//                            generateMusicImage();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            if (bitmap == null) {
                                Log.e("TransActivity", "Bitmap is null after decoding");
                                Toast.makeText(TransActivity.this, "Failed to decode image", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            imageView.setImageBitmap(bitmap);
                            saveImage(bitmap);
                            imageView.setImageBitmap(bitmap);
                            saveImage(bitmap);
                        } catch (IOException e) {
                            Toast.makeText(TransActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
//                        Log.e(TAG, "Upload failed with response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Toast.makeText(TransActivity.this, "Upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveImage(Bitmap bitmap) {
        String savedImagePath = null;
        String imageFileName = "music_score.png";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MusicScores");

        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                FileOutputStream fOut = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.close();
                Toast.makeText(this, "Image saved to Downloads/MusicScores", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void loadImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);

        saveImageToExternalStorage(imageUrl);
    }

    private void saveImageToExternalStorage(String imageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            String fileName = "music_notation.png";
                            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                            if (!storageDir.exists()) {
                                storageDir.mkdirs();
                            }

                            File file = new File(storageDir, fileName);

                            FileOutputStream out = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();

                            MediaScannerConnection.scanFile(TransActivity.this, new String[]{file.getAbsolutePath()}, null, null);

                            Toast.makeText(TransActivity.this, "Image saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(TransActivity.this, "Error saving image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void parseXml(String xmlString) {
        musicNotes.clear();
        try {
            InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            MusicNote currentNote = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "START_TAG: " + tagName);
                        if ("note".equals(tagName)) {
                            currentNote = new MusicNote();
                        } else if (currentNote != null) {
                            if ("pitch".equals(tagName)) {
                                currentNote.setPitch(parsePitch(parser));
                            } else if ("duration".equals(tagName)) {
                                currentNote.setDuration(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "END_TAG: " + tagName);
                        if ("note".equals(tagName) && currentNote != null) {
                            musicNotes.add(currentNote);
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveMusicNotationImage(String notationData) {
        Bitmap bitmap = generateMusicNotationBitmap(notationData);

        // Save the bitmap to external storage
        try {
            String fileName = "music_notation.png";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            if (!storageDir.exists()) {
                storageDir.mkdirs(); // Create the directory if it doesn't exist
            }

            File file = new File(storageDir, fileName);

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            // Make the image available in the gallery
            MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);

            Toast.makeText(this, "Image saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            imageView.setImageBitmap(bitmap); // Display the image in ImageView
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap generateMusicNotationBitmap(String notationData) {
        // Create a blank bitmap with white background
        Bitmap bitmap = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        // Use a Paint object to draw the music notation
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setTypeface(Typeface.MONOSPACE);

        // For simplicity, just drawing the notation data as text
        // Replace this with actual drawing code for music notation
        canvas.drawText(notationData, 50, 100, paint);
        return bitmap;
    }

    private String parsePitch(XmlPullParser parser) throws Exception {
        StringBuilder pitchBuilder = new StringBuilder();
        int eventType = parser.next();
        while (eventType != XmlPullParser.END_TAG || !"pitch".equals(parser.getName())) {
            if (eventType == XmlPullParser.START_TAG) {
                Log.d(TAG, "PITCH START_TAG: " + parser.getName());
                pitchBuilder.append(parser.nextText()).append(" ");
            }
            eventType = parser.next();
        }
        Log.d(TAG, "Parsed pitch: " + pitchBuilder.toString().trim());
        return pitchBuilder.toString().trim();
    }

    private void generateMusicImage() {
        int width = 800;
        int height = 400;
        musicBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(musicBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setAntiAlias(true);

        int x = 20;
        int y = 50;

        for (MusicNote note : musicNotes) {
            canvas.drawText("Pitch: " + note.getPitch(), x, y, paint);
            canvas.drawText("Duration: " + note.getDuration(), x, y + 30, paint);
            y += 60;
        }

        imageView.setImageBitmap(musicBitmap);
    }

    private void saveImageToStorage() {
        checkAndRequestPermissions();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            try {
                File file = new File(getExternalFilesDir(null), "music_notes.png");
                FileOutputStream outputStream = new FileOutputStream(file);
                musicBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
                sendBroadcast(intent);

                Toast.makeText(this, "Image saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToStorage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
