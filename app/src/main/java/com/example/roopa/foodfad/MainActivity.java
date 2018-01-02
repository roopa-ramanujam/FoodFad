package com.example.roopa.foodfad;

import android.Manifest;
import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.ClarifaiRequest;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static TextView mTextView;
    private static Button mCameraButton;
    private static Button mButton;
    private static Button mGraphsButton;
    private static ImageView mImageView;
    private static ImageView mImageViewJPG;
    private int numberOfEntries;
    private String tagMostLikely = "";
    private ArrayList<Integer> itemsCals = new ArrayList<>();
    private ArrayList<Integer> itemsCarbs = new ArrayList<>();
    //    private LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    //private GraphView graph;

    private TextView textView8;
    private TextView textViewCals;
    private int itemCalories = 0;

    private int dailyCalories = 0;
    private int dailyCaloriesGoal = 2000;

    public AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVars();
        setListeners();
//        String APIKEY = "e14859a3ccae40a095a45a2fde6c8d57";
//        final ClarifaiClient client = new ClarifaiBuilder(APIKEY).buildSync();
//
//
//        Model<Concept> generalModel = client.getDefaultModels().generalModel();
//
//        PredictRequest<Concept> request = generalModel.predict().withInputs(
//                ClarifaiInput.forImage("http://juliandance.org/wp-content/uploads/2016/01/RedApple.jpg")
//        );
//        //ClarifaiRequest.OnFailure onFailure = null;
//        request.executeAsync(onSuccess);

        verifyStoragePermissions(MainActivity.this);


//        onFailure = new ClarifaiRequest.OnFailure() {
//            @Override
//            public void onClarifaiResponseUnsuccessful(int errorCode) {
//                mTextView.setText("failed");
//            }
//        };

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        numberOfEntries = prefs.getInt("number_entries", 1);

        db = AppDatabase.getAppDatabase(getApplicationContext());
        //testdb();



    }

    private void testdb() {

        NutritionInfo newItem = new NutritionInfo();

        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Or whatever format fits best your needs.
        String dateStr = sdf.format(date);
//
//        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        newItem.setTimestamp(dateStr);
//        newItem.setId(3);
        newItem.setCalories(100);
        newItem.setCarbohydrates(10);
        newItem.setFat(2);
        newItem.setProtein(3);
        newItem.setFoodgroup("");
        db.nutritionInfoDao().insertNewEntry(newItem);
        int a = 6;
    }


    private void sendClarifaiRequest() {
        String APIKEY = "e14859a3ccae40a095a45a2fde6c8d57";
        final ClarifaiClient client = new ClarifaiBuilder(APIKEY).buildSync();


        Model<Concept> generalModel = client.getDefaultModels().foodModel();

        PredictRequest<Concept> request = generalModel.predict().withInputs(
                ClarifaiInput.forImage("http://juliandance.org/wp-content/uploads/2016/01/RedApple.jpg")
        );
        //ClarifaiRequest.OnFailure onFailure = null;
        request.executeAsync(onSuccess);
    }
    private void initVars() {
        //mTextView = (TextView) findViewById(R.id.hello_text_view);
        mCameraButton = (Button) findViewById(R.id.camera_button);
        mButton = (Button) findViewById(R.id.button);
        mGraphsButton = (Button) findViewById(R.id.seeGraphsButton);
        //mImageView = (ImageView) findViewById(R.id.photo);
//        graph = (GraphView) findViewById(R.id.graph);
//        graph.getViewport().setMinX(1);
//        graph.getViewport().setMaxX(50);
//        graph.getViewport().setMinY(0);
//        graph.getViewport().setMaxY(500);
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt("number_entries", 1); // value to store
//        editor.commit();
        //mydatabase = openOrCreateDatabase("nutrition tracker",MODE_PRIVATE,null);

        numberOfEntries = 0;

        //graph.setVisibility(View.GONE);
        //graph.setVisibility(View.INVISIBLE);
//        textView8 = (TextView) findViewById(R.id.textView13);
//        textViewCals = (TextView) findViewById(R.id.textView15);
    }

    private void handleNewActivity() {
        Intent myIntent  = new Intent(this, LongTermVisuals.class);
        startActivity(myIntent);
    }



    ClarifaiRequest.OnSuccess<List<ClarifaiOutput<Concept>>> onSuccess = new ClarifaiRequest.OnSuccess<List<ClarifaiOutput<Concept>>>() {
        @Override
        public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {
            String response = clarifaiOutputs.toString();
            clarifaiOutputs.get(0);
            ArrayList<String> tags = new ArrayList<String>();

            double maxProb = 0.0;
            for (Concept concept : clarifaiOutputs.get(0).data()) {
                if (concept.value() > maxProb) {
                    maxProb = concept.value();
                    tagMostLikely = concept.name();
                }
            }
            if (tagMostLikely != "") {
//                OkHttpClient client = new OkHttpClient();
//                String usdaAPI = "R9oOFUARnDJIfMT0g9f5gzhYAtnXQDzkRlzy1sYV";
//                String url = "https://api.nal.usda.gov/ndb/search/?format=json&q=" + tagMostLikely + "&sort=n&max=25&offset=0&api_key=" + usdaAPI;
//                Request request = new Request.Builder()
//                        .url(url)
//                        .build();
//                Response resp;
//                try {
//                    resp = client.newCall(request).execute();
//                    String Response = resp.body().string();
//                    int a = 5;
//                    JSONObject jsonObject = new JSONObject(Response);
//
//                    String[] arr = (String[])jsonObject.get("item");
//                    int b = 6;
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                OkHttpClient client = new OkHttpClient();
                JSONObject obj = new JSONObject();
                try {
                    obj.put("query", tagMostLikely);
                    obj.put("num_servings", 1);
                    obj.put("use_branded_foods", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MediaType MEDIA_TYPE = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(MEDIA_TYPE, obj.toString());

                final Request request = new Request.Builder()
                        .url("https://trackapi.nutritionix.com/v2/natural/nutrients")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-app-id", "8d7950fe")
                        .addHeader("x-app-key", "4585c51df5f6052af23a24c87f6b0139")
                        .addHeader("x-remote-user-id", "roopa.r")
                        .build();
                Response resp;
                try {
                    resp = client.newCall(request).execute();
                    String Response = resp.body().string();
                    int a = 5;
                    JSONObject jsonObject = new JSONObject(Response);
                    JSONObject nutritionInfo = (JSONObject) jsonObject.getJSONArray("foods").get(0);
                    int calories = nutritionInfo.getInt("nf_calories");
                    itemCalories = calories;
                    dailyCalories += itemCalories;
                    int carbs = nutritionInfo.getInt("nf_total_carbohydrate");
                    int fat = nutritionInfo.getInt("nf_total_fat");
                    int protein = nutritionInfo.getInt("nf_protein");
                    String foodgroup = "";

//                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putInt("number_entries",numberOfEntries++);
//                    editor.apply();
                    itemsCals.add(calories);
                    itemsCarbs.add(carbs);

                    // INSERT INTO DB
                    NutritionInfo newItem = new NutritionInfo();

                    Date date = new Date();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Or whatever format fits best your needs.
                    String dateStr = sdf.format(date);
//
//        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

                    newItem.setTimestamp(dateStr);
//        newItem.setId(3);
                    newItem.setCalories(calories);
                    newItem.setCarbohydrates(carbs);
                    newItem.setFat(fat);
                    newItem.setProtein(protein);
                    newItem.setFoodgroup(foodgroup);
                    db.nutritionInfoDao().insertNewEntry(newItem);

                    //graphStuff();
                    numberOfEntries++;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // Instantiate the RequestQueue.

    private void setListeners() {
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItems();
            }
        });

        mGraphsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNewActivity();
            }
        });


    }

    private void updateItems() {
//        textView8.setText(tagMostLikely);
//        String itemCaloriesString = "" + itemCalories;
//        textViewCals.setText(itemCaloriesString);
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);

            String extStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
            OutputStream outStream = null;
            //File file = new File(extStorageDirectory, "temp.jpg");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File file = new File(imageFileName);
            if (file.exists()) {
                file.delete();
                Log.e("file exist", "" + file + ",Bitmap= ");
            }
            file = new File(extStorageDirectory, imageFileName);
            try {
                // make a new bitmap from your file
                outStream = new FileOutputStream(file);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Uri savedImageURI = Uri.parse(file.getAbsolutePath());

            String APIKEY = "e14859a3ccae40a095a45a2fde6c8d57";
            final ClarifaiClient client = new ClarifaiBuilder(APIKEY).buildSync();


            Model<Concept> generalModel = client.getDefaultModels().foodModel();

            PredictRequest<Concept> request = generalModel.predict().withInputs(
                    ClarifaiInput.forImage(new File(file.getAbsolutePath()))
            );
            //ClarifaiRequest.OnFailure onFailure = null;
            request.executeAsync(onSuccess);
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}
