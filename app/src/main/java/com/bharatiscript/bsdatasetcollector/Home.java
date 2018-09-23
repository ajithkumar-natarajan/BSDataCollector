package com.bharatiscript.bsdatasetcollector;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    private boolean fabExpanded = false;
//    private FloatingActionButton fabSettings;
//    private LinearLayout layoutFabAdd;
//    private LinearLayout layoutFabMail;
    private static final String LOG_TAG = Home.class.getSimpleName();
    private String userName="";
    private String character = "mid_a";
    private int ID = R.id.mid_a;
    final String PREFS_NAME = "PrefsFile";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SharedPreferences userNamePref = getSharedPreferences(PREFS_NAME, 0);

        if (userNamePref.getBoolean("my_first_time", true)) {

            //the app is being launched for first time, do something
            // get username_prompt.xml view
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.prompt_username, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

//            TextView title = (TextView) promptsView.findViewById(R.id.title);
//
//            title.setText("Hello!");

            // set username_prompt.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.username_input);

            File userNameFile = new File(getBaseContext().getExternalFilesDir(null), "Username.txt");
            if (!userNameFile.exists()) {
                try {
                    userNameFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Adds a line to the trace file
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(userNameFile, true /*append*/));
            } catch (IOException e) {
                e.printStackTrace();
            }

            final BufferedWriter finalWriter = writer;
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // get user input and set it to result
                                    // edit text
                                    Home.this.userName = userInput.getText().toString();
                                    try {
                                        finalWriter.write(Home.this.userName);
                                        finalWriter.close();
                                        setUserName(userName);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(getBaseContext(), "Welcome " + Home.this.userName + " :)",
                                            Toast.LENGTH_LONG).show();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });



            // set dialog message


            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            // record the fact that the app has been started at least once
            userNamePref.edit().putBoolean("my_first_time", false).commit();
        }
        else
            userName = getUserName();



//        fabSettings = (FloatingActionButton) this.findViewById(R.ID.fabSetting);
//
//        layoutFabAdd = (LinearLayout) this.findViewById(R.ID.layoutFabAdd);
//        layoutFabMail = (LinearLayout) this.findViewById(R.ID.layoutFabMail);
        //layoutFabSettings = (LinearLayout) this.findViewById(R.ID.layoutFabSettings);

        //When main Fab (Settings) is clicked, it expands if not expanded already.
        //Collapses if main FAB was open already.
        //This gives FAB (Settings) open/close behavior
//        fabSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (fabExpanded == true){
//                    closeSubMenusFab();
//                } else {
//                    openSubMenusFab();
//                }
//            }
//        });
//
//        //Only main FAB is visible in the beginning
//        closeSubMenusFab();

        //    FloatingActionButton fab = (FloatingActionButton) findViewById(R.ID.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//        }
//    });

        Button mailButton = (Button) findViewById(R.id.mail_button);
        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Toast.makeText(getBaseContext(), "Zipping data to mail",
                            Toast.LENGTH_LONG).show();

                File root = getExternalFilesDir(null);

                if(root.getPath().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please add data!",
                                Toast.LENGTH_LONG).show();
                    return;
                }
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setType("vnd.android.cursor.item/email");


                    zipFileAtPath(root.getPath(),zipToPreviousFolder(root.getPath())+"DatasetArchive_"+userName+".zip");
                    String pathToMyAttachedFile = "DatasetArchive_" + userName + ".zip";
                    File file = new File(zipToPreviousFolder(root.getPath()), pathToMyAttachedFile);

                    if (!file.exists() || !file.canRead()) {
                        Toast.makeText(getApplicationContext(), "Zipping failed",
                                Toast.LENGTH_LONG).show();
                        return ;
                    }

                    emailIntent.setData(Uri.parse("mailto:" + "natarajan.ajithkumar@gmail.com" + "?subject=" + "PFA BS dataset archive"));
                    Uri uri = Uri.fromFile(file);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
            }
        });

        Button aboutButton = (Button) findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, ProfilePage.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddPage);
            fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] curentChar = character.split("_",2);
                Toast.makeText(getBaseContext(), "Please draw character: '" + curentChar[1] + "'",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Home.this, WriteActivity.class);
                intent.putExtra("character",character);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

//    private void setIconInMenu(Menu menu, int menuItemID, int labelID, int iconID){
//        MenuItem item = menu.findItem(menuItemID);
////        SpannableStringBuilder builder = new SpannableStringBuilder("  " + getResources().getString(labelID));
//        SpannableStringBuilder builder = new SpannableStringBuilder("           " +"");
//        builder.setSpan(new ImageSpan(this, iconID), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////        item.setTitle(builder);
//        item.setTitle(builder);
//    }

//    //closes FAB submenus
//    private void closeSubMenusFab(){
//        layoutFabAdd.setVisibility(View.INVISIBLE);
//        layoutFabMail.setVisibility(View.INVISIBLE);
//        fabSettings.setImageResource(R.drawable.ic_expand_less_black_24dp);
//        fabExpanded = false;
//    }
//
//    //Opens FAB submenus
//    private void openSubMenusFab(){
//        layoutFabAdd.setVisibility(View.VISIBLE);
//        layoutFabMail.setVisibility(View.VISIBLE);
//        //Change settings icon to 'X' icon
//        fabSettings.setImageResource(R.drawable.ic_close_black_24dp);
//        fabExpanded = true;
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//
//        setIconInMenu(menu, R.id.mail_dataset, R.string.mail_dataset, R.mipmap.email_96x96_text);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.mail_dataset) {
//            Toast.makeText(getBaseContext(), "Zipping data to mail",
//                    Toast.LENGTH_LONG).show();
//
//            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//            emailIntent.setType("vnd.android.cursor.item/email");
////            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"natarajan.ajithkumar@gmail.com"});
////            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PFA BS dataset archive");
////            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
////            File root = Environment.getExternalStorageDirectory();
//            File root = this.getExternalFilesDir(null);
//            zipFileAtPath(root.getPath(),zipToPreviousFolder(root.getPath())+"DatasetArchive_"+userName+".zip");
//            String pathToMyAttachedFile = "DatasetArchive_" + userName + ".zip";
////            String pathToMyAttachedFile = "TestFile.txt";
//            File file = new File(zipToPreviousFolder(root.getPath()), pathToMyAttachedFile);
//
//            if (!file.exists() || !file.canRead()) {
//                Toast.makeText(getApplicationContext(), "Zipping failed",
//                        Toast.LENGTH_LONG).show();
//                return false;
//            }
////            Uri data = Uri.parse("mailto:natarajan.ajithkumar@gmail.com?subject=" + "PFA BS dataset archive");
//            emailIntent.setData(Uri.parse("mailto:" + "natarajan.ajithkumar@gmail.com" + "?subject=" + "PFA BS dataset archive"));
//            Uri uri = Uri.fromFile(file);
////            emailIntent.setData(data);
//            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
//            startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        ID = item.getItemId();

        if (ID == R.id.mid_a) {
            character = "mid_a";
        } else if (ID == R.id.mid_am) {
            character = "mid_am";
        } else if (ID == R.id.mid_aha) {
            character = "mid_aha";
        } else if (ID == R.id.mid_ka) {
            character = "mid_ka";
        } else if (ID == R.id.mid_nga) {
            character = "mid_nga";
        } else if (ID == R.id.mid_cha) {
            character = "mid_cha";
        } else if (ID == R.id.mid_nya) {
            character = "mid_nya";
        } else if (ID == R.id.mid_ta) {
            character = "mid_ta";
        } else if (ID == R.id.mid_nae) {
            character = "mid_nae";
        } else if (ID == R.id.mid_tha) {
            character = "mid_tha";
        } else if (ID == R.id.mid_na) {
            character = "mid_na";
        } else if (ID == R.id.mid_pa) {
            character = "mid_pa";
        } else if (ID == R.id.mid_ma) {
            character = "mid_ma";
        } else if (ID == R.id.mid_ya) {
            character = "mid_ya";
        } else if (ID == R.id.mid_ra) {
            character = "mid_ra";
        } else if (ID == R.id.mid_la) {
            character = "mid_la";
        } else if (ID == R.id.mid_va) {
            character = "mid_va";
        } else if (ID == R.id.mid_sa) {
            character = "mid_sa";
        } else if (ID == R.id.mid_ha) {
            character = "mid_ha";
        } else if (ID == R.id.up_aa) {
            character = "up_aa mathra";
        } else if (ID == R.id.up_i) {
            character = "up_i mathra";
        } else if (ID == R.id.up_ii) {
            character = "up_ii mathra";
        } else if (ID == R.id.up_u) {
            character = "up_u mathra";
        } else if (ID == R.id.up_uu) {
            character = "up_uu mathra";
        } else if (ID == R.id.up_ru) {
            character = "up_ru mathra";
        } else if (ID == R.id.up_ae) {
            character = "up_ae mathra";
        } else if (ID == R.id.up_ai) {
            character = "up_ai mathra";
        } else if (ID == R.id.up_o) {
            character = "up_o mathra";
        } else if (ID == R.id.up_ou) {
            character = "up_ou mathra";
        } else if (ID == R.id.low_asp) {
            character = "low_aspiration symbol";
        } else if (ID == R.id.low_voi) {
            character = "low_voicing symbol";
        } else if (ID == R.id.low_aspvoi) {
            character = "low_aspiration & voicing combined symbol";
        } else if (ID == R.id.low_sin) {
            character = "low_single line";
        } else if (ID == R.id.low_dou) {
            character = "low_double line";
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String getCharacter() {
        return character;
    }

    public int getID(){
        return ID;
    }



    /*
     *
     * Zips a file at a location and places the resulting zip file at the toLocation
     * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
     */

    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
     *
     * Zips a subfolder
     *
     */

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

    public String zipToPreviousFolder(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String previousFolder = "";
        int directory = 0;
        while (directory < segments.length-1)
        {
            previousFolder = previousFolder + segments[directory] + "/";
            directory++;
        }
        return previousFolder;
    }

    private void setUserName(String userName) {
        String fileUserName ="userName.bin";
        FileOutputStream fos;
        try {
            fos = openFileOutput(fileUserName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeBytes(userName);
            oos.flush();
            oos.close();
            fos.close();
            Toast.makeText(getBaseContext(), "Username is "+userName,
                    Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(getBaseContext(), "File doesn't exist!",
                    Toast.LENGTH_LONG).show();
        } catch (IOException e){
            Toast.makeText(getBaseContext(), "IO exception!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private String getUserName() {
        try {
            FileInputStream fis = openFileInput("userName.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);
            String userName = ois.readLine();
            Toast.makeText(getBaseContext(), "Username is "+userName,
                    Toast.LENGTH_SHORT).show();
            return userName;
        } catch (FileNotFoundException e) {
            Toast.makeText(getBaseContext(), "File doesn't exist!",
                    Toast.LENGTH_LONG).show();
        } catch (IOException e){
            Toast.makeText(getBaseContext(), "IO exception!",
                    Toast.LENGTH_LONG).show();
        }
        return "";
    }
}
