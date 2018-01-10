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
import android.widget.NumberPicker;
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
    private static File currentPicFile;
    private static TextView mTextView;
    private static Button mCameraButton;
    private static Button mButton;
    private static Button mGraphsButton;
    private static Button mNutritionReport;
    private static ImageView mImageView;
    private static ImageView mImageViewJPG;
    private static NumberPicker np;
    private int numberOfEntries;
    private String tagMostLikely = "";
    private ArrayList<Integer> itemsCals = new ArrayList<>();
    private ArrayList<Integer> itemsCarbs = new ArrayList<>();
    //    private LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    //private GraphView graph;

    private TextView Item;
    private TextView Calories;
    private TextView Carbs;
    private TextView Protein;
    private TextView Fat;
    private TextView Sodium;
    private TextView Sugar;
    private String itemName = "";
    private int itemCalories = 0;
    private int itemCarbs = 0;
    private int itemProtein = 0;
    private int itemFat = 0;
    private int itemSodium = 0;
    private int itemSugar = 0;

    private String itemCaloriesString = "";
    private String itemCarbsString = "";
    private String itemProteinString = "";
    private String itemFatString = "";
    private String itemSodiumString = "";
    private String itemSugarString = "";

    private int dailyCalories = 0;
    private int dailyCaloriesGoal = 2000;

    private int servingSize = 1;

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

//    private void testdb() {
//
//        NutritionInfo newItem = new NutritionInfo();
//
//        Date date = new Date();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Or whatever format fits best your needs.
//        String dateStr = sdf.format(date);
////
////        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
//
//        newItem.setTimestamp(dateStr);
////        newItem.setId(3);
//        newItem.setCalories(100);
//        newItem.setCarbohydrates(10);
//        newItem.setFat(2);
//        newItem.setProtein(3);
//        newItem.setFoodgroup("");
//        db.nutritionInfoDao().insertNewEntry(newItem);
//        int a = 6;
//    }


    private void sendClarifaiRequest(File file) {
        String APIKEY = "e14859a3ccae40a095a45a2fde6c8d57";
        final ClarifaiClient client = new ClarifaiBuilder(APIKEY).buildSync();


        Model<Concept> generalModel = client.getDefaultModels().foodModel();

        PredictRequest<Concept> request = generalModel.predict().withInputs(
                ClarifaiInput.forImage(new File(file.getAbsolutePath()))
        );            //ClarifaiRequest.OnFailure onFailure = null;

        request.executeAsync(onSuccess);
    }
    private void initVars() {
        //mTextView = (TextView) findViewById(R.id.hello_text_view);
        mCameraButton = (Button) findViewById(R.id.camera_button);
        mButton = (Button) findViewById(R.id.button);
        mGraphsButton = (Button) findViewById(R.id.seeGraphsButton);
        mNutritionReport = (Button) findViewById(R.id.NutritionReport);
        mImageView = (ImageView) findViewById(R.id.photo);
        np = (NumberPicker) findViewById(R.id.np);
        np.setMinValue(1);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(10);
        np.setWrapSelectorWheel(true);
        numberOfEntries = 0;

        Item = (TextView) findViewById(R.id.Item);
        Calories = (TextView) findViewById(R.id.Calories);
        Carbs = (TextView) findViewById(R.id.Carbs);
        Protein = (TextView) findViewById(R.id.Protein);
        Fat = (TextView) findViewById(R.id.Fat);
        Sodium = (TextView) findViewById(R.id.Sodium);
        Sugar = (TextView) findViewById(R.id.Sugar);
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
                    int carbs = nutritionInfo.getInt("nf_total_carbohydrate");
                    int fat = nutritionInfo.getInt("nf_total_fat");
                    int protein = nutritionInfo.getInt("nf_protein");
                    int sugar = nutritionInfo.getInt("nf_sugars");
                    int sodium = nutritionInfo.getInt("nf_sodium");
                    itemCalories = calories * servingSize;
                    itemCarbs = carbs * servingSize;
                    itemFat = fat * servingSize;
                    itemProtein = protein * servingSize;
                    itemSodium = sodium;
                    itemSugar = sugar;

                    itemCaloriesString = itemCalories + "";
                    itemCarbsString = itemCarbs + " g";
                    itemFatString = itemFat + " g";
                    itemProteinString = itemProtein + " g";
                    itemSodiumString = itemSodium + " mg";
                    itemSugarString = itemSugar + " g";

                    String foodgroup = "";
                    dailyCalories += itemCalories;
                    itemsCals.add(itemCalories);
                    itemsCarbs.add(itemCarbs);

                    // INSERT INTO DB
                    NutritionInfo newItem = new NutritionInfo();

                    Date date = new Date();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Or whatever format fits best your needs.
                    String dateStr = sdf.format(date);

                    newItem.setTimestamp(dateStr);
                    newItem.setCalories(itemCalories);
                    newItem.setCarbohydrates(itemCarbs);
                    newItem.setFat(itemFat);
                    newItem.setProtein(itemProtein);
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

            //displayNutritionInfo();
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
                analyze();
            }
        });

        mNutritionReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item.setText(tagMostLikely);
                Calories.setText(itemCaloriesString);
                Carbs.setText(itemCarbsString);
                Fat.setText(itemFatString);
                Protein.setText(itemProteinString);
                Sodium.setText(itemSodiumString);
                Sugar.setText(itemSugarString);
            }
        });
        mGraphsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNewActivity();
            }
        });

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                servingSize = newVal;
            }
        });


    }

    private void analyze() {
        sendClarifaiRequest(currentPicFile);
    }

    private void displayNutritionInfo() {
        //sendClarifaiRequest(currentPicFile);
        Item.setText(tagMostLikely);
        Calories.setText(itemCalories + " g");
        Carbs.setText(itemCarbs + " g");
        Fat.setText(itemFat + " g");
        Protein.setText(itemProtein + " g");
        Sodium.setText(itemSodium + " mg");
        Sugar.setText(itemSugar + " g");
        //FoodGroup.setText(itemFoodGroup);
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
            mImageView.setImageBitmap(imageBitmap);

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
            currentPicFile = file;
            //sendClarifaiRequest(currentPicFile);
//            String APIKEY = "e14859a3ccae40a095a45a2fde6c8d57";
//            final ClarifaiClient client = new ClarifaiBuilder(APIKEY).buildSync();
//
//
//            Model<Concept> generalModel = client.getDefaultModels().foodModel();
//
//            PredictRequest<Concept> request = generalModel.predict().withInputs(
//                    ClarifaiInput.forImage(new File(file.getAbsolutePath()))
//            );            //ClarifaiRequest.OnFailure onFailure = null;
//
//            request.executeAsync(onSuccess);
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
