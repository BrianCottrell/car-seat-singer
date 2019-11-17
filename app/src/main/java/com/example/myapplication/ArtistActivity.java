package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ArtistActivity extends AppCompatActivity {

    String artist;
    ProgressDialog pd;
    TextView textView;
    ImageView imageView;
    EditText editText;
    Button button1;
    Button button2;
    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        score = findViewById(R.id.score);

        artist = "Katy%20Perry";

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            score.setText(intent.getExtras().getString("score"));
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArtistActivity.this, EventActivity.class);
                intent.putExtra("artist", artist);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    artist = URLEncoder.encode(editText.getText().toString(), "UTF-8");
                    new JsonTask().execute("https://rest.bandsintown.com/artists/" + artist + "?app_id=capitol201939ad4ebef3caf1ac2914b0eb8203c030");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        new JsonTask().execute("https://rest.bandsintown.com/artists/Katy%20Perry?app_id=capitol201939ad4ebef3caf1ac2914b0eb8203c030");
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(ArtistActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }

            JSONObject parsedResponse = null;

            try {
                parsedResponse = parseResponse(result);

                String name = parsedResponse.get("name").toString();
                String events = parsedResponse.get("upcoming_event_count").toString();
                textView.setText(name);
                button1.setText(name + " has " + events + " upcoming events");
                DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(imageView);
                downloadTask.execute(parsedResponse.get("image_url").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadImageWithURLTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageWithURLTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String pathToFile = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(pathToFile).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private JSONObject parseResponse(String response) throws JSONException {
        JSONObject responseObject = null;
        try {
            responseObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            throw e;
        }
        return responseObject;
    }
}
