package com.droidboyz.mynotes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


//@SuppressWarnings("ResultOfMethodCallIgnored")
public class MainActivity extends Activity implements AdapterView.OnFocusChangeListener{


    EditText myTitle;
    EditText myText;
    ListView myListView;
    ArrayList<String> titles;
    TextView textView;
    ArrayList<String> selection;
    int layoutID;
    ActionMode currentActionMode;
    ClipboardManager myClipboard;
    ClipData myClip;
    EditText et;
    String pasteData;
    String Path;
    String[] fileList;
    final int REQUEST_CODE = 1;
    Boolean allowed = false;
    TextView sampleTitle;
    TextView sampleText;
    TextView changeFontColor;
    TextView changeBackgroundColor;
    float myTitleSize = 20;
    float myTextSize = 14;
    String tempSave;
    int fontColor = Color.BLACK;
    int backColor = Color.WHITE;
    int titleTf = Typeface.BOLD;
    int textTf = Typeface.NORMAL;
    int which = 0;
    File path;
    Button deleteSelected;
    Button back;
    String titleTempSave = "";
    String textTempSave = ";";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermissions();
        layoutID = R.layout.activity_main;
        myTitle = findViewById(R.id.myTitle);
        myText = (EditText) findViewById(R.id.myText);
        sampleTitle =  findViewById(R.id.sampleTitle);
        sampleText =  findViewById(R.id.sampleText);
        textView =  findViewById(R.id.textView);
        changeFontColor =  findViewById(R.id.changeFontColor);
        changeBackgroundColor =  findViewById(R.id.changeBackgroundColor);
        deleteSelected = (Button) findViewById(R.id.deleteSelected);
        back = (Button) findViewById(R.id.back);
        titles = new ArrayList<>();
        selection = new ArrayList<>();
        currentActionMode = null;
        tempSave = "";
        myClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        myTitle.setOnFocusChangeListener(this);
        myText.setOnFocusChangeListener(this);
        et = myText;
        pasteData = "";
        Path = getExternalFilesDir(null).getPath() + "/myNotes/files/";
        //Log.d("*********************",Path);
        path = new File(Path);
        if (!path.exists())
            path.mkdirs();
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        float check1 = preferences.getFloat("myTitleSize", 20);
        int check2 = preferences.getInt("fontColor", Color.BLACK);
        int check3 = preferences.getInt("backColor", Color.WHITE);
        int check4 = preferences.getInt("titleTf", Typeface.BOLD);
        int check5 = preferences.getInt("textTf", Typeface.NORMAL);
        if (check1 != 20) {
            this.myTitleSize = preferences.getFloat("myTitleSize", 20);
            this.myTextSize = preferences.getFloat("myTextSize", 14);
        }
        if (check2 != Color.BLACK) {
            this.fontColor = preferences.getInt("fontColor", Color.BLACK);
        }
        if (check3 != Color.WHITE) {
            this.backColor = preferences.getInt("backColor", Color.WHITE);
        }
        if (check4 != Typeface.BOLD) {
            this.titleTf = preferences.getInt("titleTf", Typeface.BOLD);
        }
        if (check5 != Typeface.NORMAL) {
            this.textTf = preferences.getInt("textTf", Typeface.NORMAL);
        }
        setTextSize();
        changeMainColor();
        changeMainFontStyle();
    }

    public void saveMyTextMATTER() {
        myTitle = (EditText) findViewById(R.id.myTitle);
        myText = (EditText) findViewById(R.id.myText);
        if (!myTitle.getText().toString().trim().isEmpty()) {
            Log.d(getPackageName(), "method saveMyText start");
            //titles.add(count, myTitle.getText().toString());
            String fileName = myTitle.getText().toString();
            String fileMatter = myText.getText().toString();
            try {
                File myFileTitleDir = new File(Path);
                if (!myFileTitleDir.exists())
                    myFileTitleDir.mkdirs();
                File myFileTitle = new File(myFileTitleDir, fileName + ".txt");
                FileOutputStream titleFileOut = new FileOutputStream(myFileTitle);
                OutputStreamWriter titleFileOutWriter = new OutputStreamWriter(titleFileOut);
                titleFileOutWriter.write(fileName);
                titleFileOutWriter.flush();
                titleFileOutWriter.close();
                titleFileOut.flush();
                titleFileOut.close();

                File myFileMatterDir = new File(Path);
                if (!myFileMatterDir.exists())
                    myFileMatterDir.mkdirs();
                File myFileMatter = new File(myFileMatterDir, fileName + "matter.txt");
                FileOutputStream textFileOut = new FileOutputStream(myFileMatter);
                OutputStreamWriter textFileOutWriter = new OutputStreamWriter(textFileOut);
                textFileOutWriter.write(fileMatter);
                textFileOutWriter.flush();
                textFileOutWriter.close();
                textFileOut.flush();
                textFileOut.close();
                Toast.makeText(this, "SAVED!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else Toast.makeText(this, "Enter title first", Toast.LENGTH_SHORT).show();
    }

    public void saveMyText(View view) {
        saveMyTextMATTER();
    }

    public void readTitle(String fileName) {
        Log.d(getClass().getPackage().getName(), "reading title start");
        //int a;
        String Title;
        String titleFile = Path + fileName + ".txt";
        try {
            FileInputStream titleFileIn = new FileInputStream(titleFile);
            BufferedReader titleFileInReader = new BufferedReader(new InputStreamReader(titleFileIn));
            /*while ((a = titleFileInReader.read()) != -1) {
                Title=Title+Character.toString((char)a);
                //this.Title = Title;
            }*/
            Title = titleFileInReader.readLine();
            titleFileInReader.close();
            titleFileIn.close();
            EditText myTitle = (EditText) findViewById(R.id.myTitle);
            myTitle.setText(Title, TextView.BufferType.EDITABLE);
            Toast.makeText(MainActivity.this, myTitle.getText(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(getPackageName(), "reading title end");

    }

    public void readText(String fileName) {
        Log.d(getClass().getPackage().getName(), "reading text start");
        String aRow;
        String Text = "";
        String textFile = Path + fileName + "matter.txt";
        try {
            FileInputStream textFileIn = new FileInputStream(textFile);
            BufferedReader textFileInReader = new BufferedReader(new InputStreamReader(textFileIn));
            while ((aRow = textFileInReader.readLine()) != null) {
                Text += aRow + "\n";
            }
            textFileInReader.close();
            textFileIn.close();
            Text = Text.substring(0, Text.length() - 1);
            EditText myText = (EditText) findViewById(R.id.myText);
            myText.setText(Text, TextView.BufferType.EDITABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(getClass().getPackage().getName(), "reading text end");

    }

    public void keyboard() {
        if (myTitle.hasFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(myTitle, InputMethodManager.SHOW_IMPLICIT);
            this.et = myTitle;
        } else if (myText.hasFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(myText, InputMethodManager.SHOW_IMPLICIT);
            this.et = myText;
        }
    }

    public void openMyTextMatter(int check) {
        if(check!=0) {
            titleTempSave = ((EditText) findViewById(R.id.myTitle)).getText().toString();
            textTempSave = ((EditText) findViewById(R.id.myText)).getText().toString();
        }
        setContentView(R.layout.activity_list_view);
        registerForContextMenu(findViewById(R.id.myListView));
        which = 6;
        //MainActivity.this.tempSave = myListView.getItemAtPosition(i).toString();
        layoutID = R.layout.activity_list_view;
        titles.clear();
        fileList = new File(Path).list();
        myListView = (ListView) findViewById(R.id.myListView);
        for (int i = 0; i < fileList.length; i++) {
            String aFileList = fileList[i];
            if (aFileList.length() > 10 && aFileList.substring((aFileList.length()) - 10, aFileList.length()).equals("matter.txt")) {
                Log.d("########", aFileList.substring((aFileList.length()) - 10, aFileList.length()));
                continue;
            }
            titles.add(aFileList.substring(0, aFileList.length() - 4));
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_list, R.id.textView, titles);
        myListView.setAdapter(arrayAdapter);
        myListView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setContentView(R.layout.activity_main);
                myTitle = (EditText) findViewById(R.id.myTitle);
                myText = (EditText) findViewById(R.id.myText);
                layoutID = R.layout.activity_main;
                String fileName = myListView.getItemAtPosition(i).toString();
                setTextSize();
                changeMainColor();
                changeMainFontStyle();
                readTitle(fileName);
                readText(fileName);
                myTitle.setOnFocusChangeListener(MainActivity.this);
                myText.setOnFocusChangeListener(MainActivity.this);
                myTitle.requestFocus();
                myText.requestFocus();
                //Log.d(getPackageName(),getFilesDir().getAbsolutePath());
            }
        });
        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                which = 6;
                MainActivity.this.tempSave = myListView.getItemAtPosition(i).toString();
                return false;
            }
        });
    }

    public void openMyText(View view) {
        openMyTextMatter(1);
    }

    public void deleteMyText(View view) {
        myTitle = (EditText) findViewById(R.id.myTitle);
        myText = (EditText) findViewById(R.id.myText);
        fileList = new File(Path).list();
        if (fileList.length > 0 && !myTitle.getText().toString().equals("")) {
            for (int i = 0; i <= fileList.length - 1; i++) {
                if ((myTitle.getText().toString() + ".txt").equals(fileList[i])) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(this);
                    ad.setIcon(R.drawable.notify);
                    ad.setTitle("Deleting can't be reversed!");
                    ad.setMessage("Are you sure to Delete this Note?");
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new File(Path + "/" + myTitle.getText().toString() + ".txt").delete();
                            new File(Path + "/" + myTitle.getText().toString() + "matter.txt").delete();
                            Toast.makeText(MainActivity.this, "DELETED!", Toast.LENGTH_SHORT).show();
                            myTitle.setText("");
                            myText.setText("");
                        }
                    });

                    ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            myTitle.setOnFocusChangeListener(MainActivity.this);
                            myText.setOnFocusChangeListener(MainActivity.this);
                            myTitle.requestFocus();
                            myText.requestFocus();
                        }
                    });
                    ad.show();
                    break;
                } else if (i == fileList.length - 1) {
                    Toast.makeText(this, "File doesn't Exist OR already Deleted.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (fileList.length == 0) {
            Toast.makeText(this, "First SAVE then DELETE.", Toast.LENGTH_SHORT).show();
        } else if (myTitle.getText().toString().equals("")) {
            Toast.makeText(this, "Enter File name first.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutID == R.layout.activity_main) {
            myTitle = (EditText) findViewById(R.id.myTitle);
            final AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setIcon(R.drawable.notify);
            ad.setTitle("Sure to Exit!");
            ad.setMessage("Are you sure you want to close the app?");
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            if (!myTitle.getText().toString().trim().equals("")) {
                ad.setNeutralButton("SAVE & EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveMyTextMATTER();
                        finish();
                    }
                });
            }

            ad.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    myTitle.setOnFocusChangeListener(MainActivity.this);
                    myText.setOnFocusChangeListener(MainActivity.this);
                    myTitle.requestFocus();
                    myText.requestFocus();
                }
            });
            ad.show();
        } else if (layoutID == R.layout.activity_list_view) {
            setContentView(R.layout.activity_main);
            layoutID = R.layout.activity_main;
            setTextSize();
            changeMainColor();
            changeMainFontStyle();
            myTitle.setText(titleTempSave);
            myText.setText(textTempSave);
            myTitle.setOnFocusChangeListener(MainActivity.this);
            myText.setOnFocusChangeListener(MainActivity.this);
            myTitle.requestFocus();
            myText.requestFocus();
        } else if (layoutID == R.layout.activity_settings) {
            setContentView(R.layout.activity_main);
            layoutID = R.layout.activity_main;
            setTextSize();
            changeMainColor();
            changeMainFontStyle();
            myTitle.setText(titleTempSave);
            myText.setText(textTempSave);
            myTitle.setOnFocusChangeListener(MainActivity.this);
            myText.setOnFocusChangeListener(MainActivity.this);
            myTitle.requestFocus();
            myText.requestFocus();
        }
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        if (currentActionMode == null) {
            currentActionMode = mode;
            Menu menu = mode.getMenu();
            menu.clear();
            mode.getMenuInflater().inflate(R.menu.mymenu, menu);
            mode.setTitle("");
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(true);
            menu.getItem(2).setEnabled(true);
            menu.getItem(3).setEnabled(true);

            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(true);

            menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.getItem(2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.getItem(3).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            if ((!et.hasSelection()) || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menu.findItem(R.id.SELECT_ALL).setEnabled(false);
                menu.findItem(R.id.SELECT_ALL).setVisible(false);
            } else if (et.getSelectionStart() == 0 && et.getSelectionEnd() == et.getText().toString().length()) {
                menu.findItem(R.id.SELECT_ALL).setEnabled(false);
                menu.findItem(R.id.SELECT_ALL).setVisible(false);
            } else {
                menu.findItem(R.id.SELECT_ALL).setEnabled(true);
                menu.findItem(R.id.SELECT_ALL).setVisible(true);
            }
            if (!myClipboard.hasPrimaryClip() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menu.findItem(R.id.PASTE).setEnabled(false);
                menu.findItem(R.id.PASTE).setVisible(false);
                Toast.makeText(MainActivity.this, "Clipboard Empty", Toast.LENGTH_SHORT).show();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menu.findItem(R.id.PASTE).setEnabled(true);
                menu.findItem(R.id.PASTE).setVisible(true);
            } else {
                menu.findItem(R.id.PASTE).setEnabled(false);
                menu.findItem(R.id.PASTE).setVisible(false);
            }
        }
        super.onActionModeStarted(mode);
    }

    public void onActionItemClicked(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.COPY:
                if (et.hasSelection()) {
                    int start = et.getSelectionStart();
                    int end = et.getSelectionEnd();
                    Log.d(getPackageName(), "start " + Integer.toString(start) + "end " + Integer.toString(end));
                    if (!(et.getText().toString().substring(start, end).trim()).equals("")) {
                        if (start == end) {
                            Toast.makeText(MainActivity.this, "Select text first.", Toast.LENGTH_SHORT).show();
                        } else {
                            myClip = ClipData.newPlainText("myText", et.getText().toString().substring(start, end));
                            Log.d(getPackageName(), et.getText().toString().substring(start, end));
                            myClipboard.setPrimaryClip(myClip);
                            Toast.makeText(MainActivity.this, "TEXT COPIED!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Blank(s) selected!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.CUT:
                if (et.hasSelection()) {
                    int start = et.getSelectionStart();
                    int end = et.getSelectionEnd();
                    if (!(et.getText().toString().substring(start, end).trim()).equals("")) {
                        if (start == end) {
                            Toast.makeText(MainActivity.this, "Select text first.", Toast.LENGTH_SHORT).show();
                        } else {
                            myClip = ClipData.newPlainText("myText", et.getText().toString().substring(start, end));
                            myClipboard.setPrimaryClip(myClip);
                            Toast.makeText(MainActivity.this, "TEXT CUT!", Toast.LENGTH_SHORT).show();
                            et.setText((et.getText().toString().substring(0, start)).concat(et.getText().toString().substring(end, et.getText().toString().length())));
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Blank(s) selected!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.SELECT_ALL:
                if (!et.getText().toString().isEmpty()) {
                    et.setSelection(0, et.getText().toString().length());
                } else {
                    Toast.makeText(MainActivity.this, "No text to select", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.PASTE:
                ClipData.Item item = myClipboard.getPrimaryClip().getItemAt(0);
                pasteData = item.getText().toString();
                if (et.getSelectionStart() == et.getSelectionEnd()) {
                    et.getText().insert(et.getSelectionEnd(), pasteData);
                    Toast.makeText(MainActivity.this, "Pasted!", Toast.LENGTH_SHORT).show();
                } else {
                    String start;
                    String end;
                    if (et.getSelectionStart() != 0 && et.getSelectionEnd() != et.getText().toString().length()) {
                        start = et.getText().toString().substring(0, et.getSelectionStart());
                        end = et.getText().toString().substring(et.getSelectionEnd(), et.getText().toString().length());
                        et.setText(start.trim() + " " + pasteData.trim() + " " + end.trim());
                    } else if (et.getSelectionStart() == 0 && et.getSelectionEnd() != et.getText().toString().length()) {
                        end = et.getText().toString().substring(et.getSelectionEnd(), et.getText().toString().length());
                        et.setText(pasteData.trim() + " " + end.trim());
                    } else if (et.getSelectionStart() != 0 && et.getSelectionEnd() == et.getText().toString().length()) {
                        start = et.getText().toString().substring(0, et.getSelectionStart());
                        et.setText(start.trim() + " " + pasteData.trim());
                    } else {
                        et.setText(pasteData);
                    }
                }
            default:
                Log.d(getPackageName(), "default case.");
        }
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        currentActionMode = null;
        super.onActionModeFinished(mode);
    }


    @Override
    public void onFocusChange(View view, boolean b) {
        keyboard();
    }

    public void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Permission Grant Necessary!!")
                            .setMessage("WRITE_EXTERNAL_STORAGE permission needs to be granted to save and open your files.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    allowed = true;
                } else {
                    allowed = false;
                    finish();
                }
            }
        }
    }

    public float pxtoSp(float px) {
        float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public void openSettings(View view) {
        titleTempSave = ((EditText) findViewById(R.id.myTitle)).getText().toString();
        textTempSave = ((EditText) findViewById(R.id.myText)).getText().toString();
        setContentView(R.layout.activity_settings);
        layoutID = R.layout.activity_settings;
        sampleTitle =  findViewById(R.id.sampleTitle);
        sampleText =  findViewById(R.id.sampleText);
        sampleTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.myTitleSize);
        sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.myTextSize);
        changeSampleColorMatter();
        changeSampleFontStyleMatter();
        registerForContextMenu(findViewById(R.id.changeFontColor));
        registerForContextMenu(findViewById(R.id.changeBackgroundColor));
        registerForContextMenu(findViewById(R.id.changeTitleStyle));
        registerForContextMenu(findViewById(R.id.changeTextStyle));
        registerForContextMenu(findViewById(R.id.share));
    }

    public void increaseTextSize(View view) {
        sampleTitle =  findViewById(R.id.sampleTitle);
        sampleText =  findViewById(R.id.sampleText);
        float titleSize = pxtoSp(sampleTitle.getTextSize());
        float textSize = pxtoSp(sampleText.getTextSize());
        int titleSize2 = (int) titleSize;
        titleSize2 = titleSize2 + 2;
        titleSize = titleSize2;
        int textSize2 = (int) textSize;
        textSize2 = textSize2 + 2;
        textSize = textSize2;
        if (pxtoSp(sampleTitle.getTextSize()) <= 24) {
            this.myTitleSize = titleSize;
            Log.d(getPackageName(), Double.toString(titleSize));
            sampleTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.myTitleSize);
        }
        if (pxtoSp(sampleText.getTextSize()) <= 18) {
            this.myTextSize = textSize;
            Log.d(getPackageName(), Double.toString(textSize));
            sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.myTextSize);
        }
    }

    public void decreaseTextSize(View view) {
        sampleTitle =  findViewById(R.id.sampleTitle);
        sampleText =  findViewById(R.id.sampleText);
        float titleSize = pxtoSp(sampleTitle.getTextSize());
        float textSize = pxtoSp(sampleText.getTextSize());
        int titleSize2 = (int) titleSize;
        titleSize2 = titleSize2 - 2;
        titleSize = titleSize2;
        int textSize2 = (int) textSize;
        textSize2 = textSize2 - 2;
        textSize = textSize2;
        if (pxtoSp(sampleTitle.getTextSize()) >= 16) {
            this.myTitleSize = titleSize;
            sampleTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.myTitleSize);
        }
        if (pxtoSp(sampleText.getTextSize()) >= 10) {
            this.myTextSize = textSize;
            sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.myTextSize);
        }
    }

    public void defaultTextSize(View view) {
        sampleTitle =  findViewById(R.id.sampleTitle);
        sampleText =  findViewById(R.id.sampleText);
        this.myTitleSize = 20F;
        this.myTextSize = 14F;
        sampleTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    }

    public void setTextSize() {
        myTitle = (EditText) findViewById(R.id.myTitle);
        myText = (EditText) findViewById(R.id.myText);
        myTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.myTitleSize);
        myText.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.myTextSize);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("myTitleSize", this.myTitleSize);
        editor.putFloat("myTextSize", this.myTextSize);
        editor.putInt("fontColor", this.fontColor);
        editor.putInt("backColor", this.backColor);
        editor.putInt("titleTf", this.titleTf);
        editor.putInt("textTf", this.textTf);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        float check1 = preferences.getFloat("myTitleSize", 20);
        int check2 = preferences.getInt("fontColor", Color.BLACK);
        int check3 = preferences.getInt("backColor", Color.WHITE);
        int check4 = preferences.getInt("titleTf", Typeface.BOLD);
        int check5 = preferences.getInt("textTf", Typeface.NORMAL);
        if (check1 != 20) {
            this.myTitleSize = preferences.getFloat("myTitleSize", 20);
            this.myTextSize = preferences.getFloat("myTextSize", 14);
        }
        if (check2 != Color.BLACK) {
            this.fontColor = preferences.getInt("fontColor", Color.BLACK);
        }
        if (check3 != Color.WHITE) {
            this.backColor = preferences.getInt("backColor", Color.WHITE);
        }
        if (check4 != Typeface.BOLD) {
            this.titleTf = preferences.getInt("titleTf", Typeface.BOLD);
        }
        if (check5 != Typeface.NORMAL) {
            this.textTf = preferences.getInt("textTf", Typeface.NORMAL);
        }
    }

    public void changeFontColor(View view) {
        which = 1;
        MainActivity.this.openContextMenu(view);
    }

    public void changeBackgroundColor(View view) {
        which = 2;
        registerForContextMenu(view);
        MainActivity.this.openContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        if (which == 1) {
            inflater.inflate(R.menu.font_color, menu);
        } else if (which == 2) {
            inflater.inflate(R.menu.background_color, menu);
        } else if (which == 3 || which == 4) {
            inflater.inflate(R.menu.font_style, menu);
        } else if (which == 5) {
            inflater.inflate(R.menu.share_menu, menu);
        } else if (which == 6) {
            inflater.inflate(R.menu.share_file, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (which == 1) {
            switch (item.getItemId()) {
                case R.id.defaultC:
                case R.id.blackC:
                    changeSampleColor(Color.BLACK);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.grayC:
                    changeSampleColor(Color.GRAY);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.dkgrayC:
                    changeSampleColor(Color.DKGRAY);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.ltgrayC:
                    changeSampleColor(Color.LTGRAY);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.redC:
                    changeSampleColor(Color.RED);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.blueC:
                    changeSampleColor(Color.BLUE);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.greenC:
                    changeSampleColor(Color.GREEN);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.yellowC:
                    changeSampleColor(Color.YELLOW);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.cyanC:
                    changeSampleColor(Color.CYAN);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.whiteC:
                    changeSampleColor(Color.WHITE);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.magentaC:
                    changeSampleColor(Color.MAGENTA);
                    MainActivity.this.closeContextMenu();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        } else if (which == 2) {
            switch (item.getItemId()) {
                case R.id.defaultCc:
                case R.id.whiteCc:
                    changeSampleColor(Color.WHITE);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.blackCc:
                    changeSampleColor(Color.BLACK);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.grayCc:
                    changeSampleColor(Color.GRAY);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.dkgrayCc:
                    changeSampleColor(Color.DKGRAY);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.ltgrayCc:
                    changeSampleColor(Color.LTGRAY);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.redCc:
                    changeSampleColor(Color.RED);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.blueCc:
                    changeSampleColor(Color.BLUE);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.greenCc:
                    changeSampleColor(Color.GREEN);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.yellowCc:
                    changeSampleColor(Color.YELLOW);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.cyanCc:
                    changeSampleColor(Color.CYAN);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.magentaCc:
                    changeSampleColor(Color.MAGENTA);
                    MainActivity.this.closeContextMenu();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        } else if (which == 3 || which == 4) {
            switch (item.getItemId()) {
                case R.id.defaultStyle:
                    if (which == 3) {
                        changeSampleFontStyle(Typeface.BOLD);
                    } else if (which == 4) {
                        changeSampleFontStyle(Typeface.NORMAL);
                    }
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.normal:
                    changeSampleFontStyle(Typeface.NORMAL);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.bold:
                    changeSampleFontStyle(Typeface.BOLD);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.italic:
                    changeSampleFontStyle(Typeface.ITALIC);
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.bold_italic:
                    changeSampleFontStyle(Typeface.BOLD_ITALIC);
                    MainActivity.this.closeContextMenu();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        } else if (which == 5) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            switch (item.getItemId()) {
                case R.id.apkShare:
                    intent.setType("application/vnd.android.package-archive");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(MainActivity.this.getApplicationInfo().publicSourceDir)));
                    MainActivity.this.startActivity(Intent.createChooser(intent, "Share Apk using..."));
                    MainActivity.this.closeContextMenu();
                    return true;
                case R.id.linkShare:
                    Toast.makeText(MainActivity.this, "LINK NOT AVAILABLE!", Toast.LENGTH_SHORT).show();
                    MainActivity.this.closeContextMenu();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            switch (item.getItemId()) {
                case R.id.shareNote:
                    String Title = "PROBLEM IN LOADING TITLE!!";
                    String titleFile = Path + tempSave + ".txt";
                    try {
                        FileInputStream titleFileIn = new FileInputStream(titleFile);
                        BufferedReader titleFileInReader = new BufferedReader(new InputStreamReader(titleFileIn));
                        Title = titleFileInReader.readLine();
                        titleFileInReader.close();
                        titleFileIn.close();
                        Toast.makeText(MainActivity.this, myTitle.getText(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String aRow;
                    String Text = "";
                    String textFile = Path + tempSave + "matter.txt";
                    try {
                        FileInputStream textFileIn = new FileInputStream(textFile);
                        BufferedReader textFileInReader = new BufferedReader(new InputStreamReader(textFileIn));
                        while ((aRow = textFileInReader.readLine()) != null) {
                            Text += aRow + "\n";
                            //this.Title = Title;
                        }
                        textFileInReader.close();
                        textFileIn.close();
                        Text = Text.substring(0, Text.length() - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String shareFile = "TITLE:   " + Title + "\n\n" + "NOTE:\n" + Text;
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, shareFile);
                    MainActivity.this.startActivity(Intent.createChooser(intent, "Share Note using..."));
                    MainActivity.this.closeContextMenu();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }

    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        which = 0;
        super.onContextMenuClosed(menu);
    }

    public void changeSampleColor(int color) {
        sampleTitle =  findViewById(R.id.sampleTitle);
        sampleText =  findViewById(R.id.sampleText);
        if (which == 1) {
            this.fontColor = color;
        } else if (which == 2) {
            this.backColor = color;
        }
        changeSampleColorMatter();
    }

    public void changeMainColor() {
        myTitle = (EditText) findViewById(R.id.myTitle);
        myText = (EditText) findViewById(R.id.myText);
        myTitle.setTextColor(this.fontColor);
        myText.setTextColor(this.fontColor);
        myTitle.setBackgroundColor(this.backColor);
        myText.setBackgroundColor(this.backColor);
    }

    public void changeSampleColorMatter() {
        sampleTitle =  findViewById(R.id.sampleTitle);
        sampleText =  findViewById(R.id.sampleText);
        sampleTitle.setTextColor(this.fontColor);
        sampleText.setTextColor(this.fontColor);
        sampleTitle.setBackgroundColor(this.backColor);
        sampleText.setBackgroundColor(this.backColor);
    }

    public void changeSampleFontStyle(int tf) {
        if (which == 3) {
            this.titleTf = tf;
        } else if (which == 4) {
            this.textTf = tf;
        }
        changeSampleFontStyleMatter();
    }

    public void changeSampleFontStyleMatter() {
        sampleTitle =  findViewById(R.id.sampleTitle);
        sampleText =  findViewById(R.id.sampleText);
        sampleTitle.setTypeface(null, this.titleTf);
        sampleText.setTypeface(null, this.textTf);
    }

    public void changeMainFontStyle() {
        myTitle = (EditText) findViewById(R.id.myTitle);
        myText = (EditText) findViewById(R.id.myText);
        myTitle.setTypeface(null, this.titleTf);
        myText.setTypeface(null, this.textTf);
    }

    public void changeTitleStyle(View view) {
        which = 3;
        MainActivity.this.openContextMenu(view);
    }

    public void changeTextStyle(View view) {
        which = 4;
        MainActivity.this.openContextMenu(view);
    }

    public void shareApp(View view) {
        which = 5;
        MainActivity.this.openContextMenu(view);
    }
    /*    public void changeFont(View view) {
        which = 6;
        MainActivity.this.openContextMenu(view);
    }
    public void changeMainFont(){
        myTitle=(EditText)findViewById(R.id.myTitle);
        myText=(EditText)findViewById(R.id.myText);
        myTitle.setTypeface(this.font);
        myText.setTypeface(this.font);
    }
    public void changeSampleFontMatter(){
        sampleTitle= findViewById(R.id.sampleTitle);
        sampleText= findViewById(R.id.sampleText);
        sampleTitle.setTypeface(this.font);
        sampleText.setTypeface(this.font);
    }
    public void changeSampleFont(Typeface font){
            this.font = font;
        changeSampleFontStyleMatter();
    }*/

    public void deleteSelected(View view) {
        if (myListView.getChildCount() != 0) {
            myListView.setOnItemClickListener(null);/////**OK**
            Button deleteSelected = (Button) findViewById(R.id.deleteSelected);/////**OK**
            Button back = (Button) findViewById(R.id.back);/////**OK**
            if (deleteSelected.getText().toString().equals(getResources().getString(R.string.deleteSelected))) {/////**OK**
                deleteSelected.setText(R.string.delete);/////**OK**
                back.setText(android.R.string.cancel);/////**OK**
                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {/////**OK**
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (selection.contains(myListView.getItemAtPosition(i).toString())) {
                            view.setBackgroundColor(Color.TRANSPARENT);/////**OK**
                            selection.remove(myListView.getItemAtPosition(i).toString());
                        } else {
                            view.setBackgroundColor(Color.RED);
                            selection.add(myListView.getItemAtPosition(i).toString());
                        }
                    }
                });
            } else if (deleteSelected.getText().toString().equals(getResources().getString(R.string.delete)) && !selection.isEmpty()) {
                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                ad.setIcon(R.drawable.notify);
                ad.setTitle("Deleting can't be reversed!");
                ad.setMessage("Are you sure to Delete these Notes?");
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int j = 0; j <= selection.size() - 1; j++) {/////**OK**
                            new File(Path + "/" + selection.get(j) + ".txt").delete();
                            new File(Path + "/" + selection.get(j) + "matter.txt").delete();
                        }
                        selection.clear();
                        openMyTextMatter(0);
                        Toast.makeText(getApplicationContext(), "DELETED!", Toast.LENGTH_SHORT).show();
                    }
                });

                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {/////**OK**
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {/////**OK**
                                if (selection.contains(myListView.getItemAtPosition(i).toString())) {/////**OK**
                                    view.setBackgroundColor(Color.TRANSPARENT);/////**OK**
                                    selection.remove(myListView.getItemAtPosition(i).toString());/////**OK**
                                } else {
                                    view.setBackgroundColor(Color.RED);/////**OK**
                                    selection.add(myListView.getItemAtPosition(i).toString());/////**OK**
                                }
                            }
                        });
                    }
                });
                ad.show();
            } else if (selection.isEmpty() && deleteSelected.getText().toString().equals(getResources().getString(R.string.delete))) {/////**OK**
                Toast.makeText(this, "Please select a File first.", Toast.LENGTH_SHORT).show();/////**OK**
                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {/////**OK**
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {/////**OK**
                        if (selection.contains(myListView.getItemAtPosition(i).toString())) {/////**OK**
                            view.setBackgroundColor(Color.TRANSPARENT);/////**OK**
                            selection.remove(myListView.getItemAtPosition(i).toString());/////**OK**
                        } else {
                            view.setBackgroundColor(Color.RED);/////**OK**
                            selection.add(myListView.getItemAtPosition(i).toString());/////**OK**
                        }
                    }
                });
            }
        }else {
            Toast.makeText(this, "No file available to Select & Del.", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "First Save a file.", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View view) {
        Button back = (Button) findViewById(R.id.back);
        if (back.getText().toString().equals(getResources().getString(android.R.string.cancel))) {
            back.setText(R.string.Back);
            selection.clear();
        }
        setContentView(R.layout.activity_main);
        layoutID = R.layout.activity_main;
        setTextSize();
        changeMainColor();
        changeMainFontStyle();
        myTitle.setText(titleTempSave);
        myText.setText(textTempSave);
        myTitle.setOnFocusChangeListener(MainActivity.this);
        myText.setOnFocusChangeListener(MainActivity.this);
        myTitle.requestFocus();
        myText.requestFocus();
    }
}