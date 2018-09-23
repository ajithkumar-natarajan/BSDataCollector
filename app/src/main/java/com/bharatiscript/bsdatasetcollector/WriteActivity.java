package com.bharatiscript.bsdatasetcollector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class WriteActivity extends AppCompatActivity {

    private PaintView paintView;
    private HashMap datasetCounter;
    final String USERDATA = "characterMap";
    final String PREFS_NAME = "MapFile";

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_paint_main);
//        paintView = (PaintView) findViewById(R.id.paintView);
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        paintView.init(metrics);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

        SharedPreferences charMap = getSharedPreferences(PREFS_NAME, 0);

        if (charMap.getBoolean("my_first_time", true)) {

            //the app is being launched for first time, do something
            datasetCounter = new HashMap<String, Integer>();
            saveMap(datasetCounter);

            // record the fact that the app has been started at least once
            charMap.edit().putBoolean("my_first_time", false).commit();

        }
        else {
            datasetCounter = loadMap();
        }

        FloatingActionButton saveFab = (FloatingActionButton) findViewById(R.id.fabSaveData);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getIntent();
                String characterName = intent.getStringExtra("character");


                if (datasetCounter.containsKey(characterName)) {
                    int counter = (int) datasetCounter.get(characterName);
//                } else
//                    datasetCounter.put(characterName, new Integer(0));
//
//                Snackbar.make(view, "Saved", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                    counter++;
                    String filePath = characterName + "/" + counter + ".txt";

                    if(paintView.saveTouchPoints(getBaseContext(), filePath)){
                        Snackbar.make(view, "Saved", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        datasetCounter.put(characterName,(Integer)counter);
                        saveMap(datasetCounter);
                    }
                    else
                        counter--;

//                    File writeFile = new File(getBaseContext().getExternalFilesDir(null), characterName + "/" + counter + ".txt");
//                    try {
//                        // Creates a file in the primary external storage space of the
//                        // current application.
//                        // If the file does not exists, it is created.
//                        writeFile = new File(getBaseContext().getExternalFilesDir(null), characterName + "/" + counter + ".txt");
//                        if (!writeFile.exists())
//                            writeFile.createNewFile();
//                        // Adds a line to the trace file
//                        BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile, true /*append*/));
//                        writer.write("This is a test file.");
//                        writer.close();
//                        // Refresh the data so it can seen when the device is plugged in a
//                        // computer. You may have to unplug and replug the device to see the
//                        // latest changes. This is not necessary if the user should not modify
//                        // the files.
//                        MediaScannerConnection.scanFile(getBaseContext(),
//                                new String[]{writeFile.toString()},
//                                null,
//                                null);
//                    } catch (IOException e) {
//                        Log.e("ReadWriteFile", "Unable to write to the TestFile.txt file.");
//                    }
//                    Log.v("ReadWriteFile", "Write to TestFile.txt file.");
//                    String path = writeFile.getPath();
//                    path = path.substring(path.length()-15);
//
//                    Toast.makeText(getBaseContext(), path,
//                            Toast.LENGTH_LONG).show();
                }

                else {
//                    datasetCounter.put(characterName, new Integer(1));
                    new File(getBaseContext().getExternalFilesDir(null), characterName).mkdirs();
                    int counter = 1;
                    String filePath = characterName + "/" + counter + ".txt";
                    if(paintView.saveTouchPoints(getBaseContext(), filePath)){
                        Snackbar.make(view, "Saved", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        datasetCounter.put(characterName,(Integer)counter);
                        saveMap(datasetCounter);
                    }
                }
            }
        });
    }


    private void setIconInMenu(Menu menu, int menuItemID, int labelID, int iconID){
        MenuItem item = menu.findItem(menuItemID);
        SpannableStringBuilder builder = new SpannableStringBuilder("       " +"");
        builder.setSpan(new ImageSpan(this, iconID), 1, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(builder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.paint_screen_menu, menu);

        setIconInMenu(menu, R.id.clear, R.string.action_reset, R.mipmap.clear_96x384_text);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.clear:
                paintView.clear();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*method to save HashMap to phone memory*/

    private void saveMap(HashMap<String, Integer> characterMap) {
        String mapFileName ="characterMap.bin";
        FileOutputStream fos;
        try {
            fos = openFileOutput(mapFileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for(HashMap.Entry<String, Integer> entrySet : characterMap.entrySet()){
//                oos.writeBytes(entrySet.getKey());
//                Integer integerValue = entrySet.getValue();
//                oos.writeObject(integerValue);
                oos.writeBytes(toString(entrySet));
                oos.write('\n');
            }
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*method to retrieve stored HashMap from phone memory*/

    private HashMap<String, Integer> loadMap() {
        try {
            FileInputStream fis = openFileInput("characterMap.bin");
            InputStreamReader isr = new InputStreamReader(fis);
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashMap<String, Integer> storedMap = new HashMap<>();
            while(ois.available()>0){
                String eachLine = ois.readLine();
                String[] keyVal = eachLine.split("@");
                storedMap.put(keyVal[0], Integer.valueOf(keyVal[1]));
            }
            return storedMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String toString(Map.Entry<String, Integer> entrySet){
        return (entrySet.getKey() + "@" + entrySet.getValue());
    }

}
