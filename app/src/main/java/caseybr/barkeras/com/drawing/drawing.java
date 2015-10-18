package caseybr.barkeras.com.drawing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andrew on 10/16/2015.
 */
public class drawing extends Activity {

    int defaultR = 255;
    int defaultB = 255;
    int defaultG = 255;
    int[] selectedColorRGB = new int[4];
    Color brushBoxColor = new Color();
    int opacitySeekValue;
    File imageFile;
    Boolean firstPropmpt = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_canvas);
        Bundle canvasInfo = getIntent().getExtras();
        int userRC = canvasInfo.getInt("requestCode");
        Bitmap canvasBM;
        final Button brushBtn = (Button)findViewById(R.id.brushBtn);
        Button shareBtn = (Button)findViewById(R.id.shareImageBtn);
        Button saveBtn = (Button)findViewById(R.id.saveImageBtn);
        final Button eraseBtn = (Button)findViewById(R.id.eraseImageBtn);
        ImageView canvasImage = (ImageView)findViewById(R.id.canvasImage);


        Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        brushBtn.setTypeface(fontFamily);
        shareBtn.setTypeface(fontFamily);
        saveBtn.setTypeface(fontFamily);
        eraseBtn.setTypeface(fontFamily);
        brushBtn.setText("\uf1fc");
        shareBtn.setText("\uf1e0");
        saveBtn.setText("\uf0c7");
        eraseBtn.setText("\uf12d");


        selectedColorRGB[3] = brushBoxColor.argb(255,defaultR,defaultG,defaultB);
        brushBtn.setTextColor(selectedColorRGB[3]);



        if(userRC == 20){

            int canvasWidth = Integer.parseInt(canvasInfo.getString("width"));
            int canvasHeight = Integer.parseInt(canvasInfo.getString("height"));
            int[] canvasColor =  new int[4];
            canvasColor[0] = canvasInfo.getInt("redValue");
            canvasColor[1] = canvasInfo.getInt("greenValue");
            canvasColor[2] = canvasInfo.getInt("blueValue");

            Color canvasBmColor = new Color();
            canvasColor[3] = canvasBmColor.rgb(canvasColor[0],canvasColor[1],canvasColor[2]);

            canvasBM = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
            canvasBM.eraseColor(canvasColor[3]);
            canvasImage.setImageBitmap(canvasBM);

        }
        if(userRC == 21){

            Uri imageUri = Uri.parse(canvasInfo.getString("fileUri"));

            InputStream imgStream = null;
            try {
                imgStream = getContentResolver().openInputStream(imageUri);
                canvasBM = BitmapFactory.decodeStream(imgStream);
                canvasImage.setImageBitmap(canvasBM);

            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }


        brushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(drawing.this, "Brush activated", Toast.LENGTH_LONG).show();


                int colorWhite = getResources().getColor(R.color.white);
                int colorClear = getResources().getColor(R.color.clear);
                int colorWhiteBack = getResources().getColor(R.color.white_half_opacity);
                eraseBtn.setTextColor(colorWhite);
                eraseBtn.setBackgroundColor(colorClear);
                brushBtn.setBackgroundColor(colorWhiteBack);

            }
        });

        brushBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                brushBtnHeld();

                return false;
            }
        });


        eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(drawing.this,"Eraser Activated",Toast.LENGTH_LONG).show();

                int colorBlack = getResources().getColor(R.color.black);
                int colorWhite = getResources().getColor(R.color.white);
                int colorWhiteBack = getResources().getColor(R.color.white_half_opacity);
                int colorBlackBack = getResources().getColor(R.color.black_half_opacity);
                eraseBtn.setBackgroundColor(colorWhiteBack);
                eraseBtn.setTextColor(colorBlack);
                brushBtn.setBackgroundColor(colorBlackBack);


            }
        });


    }



    public void brushBtnHeld() {



        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View promptView = layoutInflater.inflate(R.layout.brush_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(drawing.this);

        // set prompts.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);

        final EditText brushSize = (EditText) promptView.findViewById(R.id.promptBrushPixels);
        final SeekBar brushSeekBar = (SeekBar) promptView.findViewById(R.id.brushSeek);
        final View brushColorView = (View)promptView.findViewById(R.id.promptBrushColorBox);
        final SeekBar opacitySeek = (SeekBar)promptView.findViewById(R.id.opacitySeekBar);
        final TextView opacityTextView = (TextView)promptView.findViewById(R.id.opacityValueTV);
        final Button brushBtn = (Button)findViewById(R.id.brushBtn);


        if(firstPropmpt == false){

            selectedColorRGB[0] = defaultR;
            selectedColorRGB[1] = defaultG;
            selectedColorRGB[2] = defaultB;

        }
        else{


        }



        selectedColorRGB[3] = brushBoxColor.rgb(selectedColorRGB[0],selectedColorRGB[1],selectedColorRGB[2]);
        brushColorView.setBackgroundColor(selectedColorRGB[3]);

        brushSize.setText(String.valueOf(brushSeekBar.getProgress()));

        brushSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushSize.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        opacitySeekValue = opacitySeek.getProgress();
        opacityTextView.setText(opacityPercent(opacitySeekValue));

        opacitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                opacitySeekValue = progress;
                opacityTextView.setText(opacityPercent(opacitySeekValue));
                selectedColorRGB[3] = brushBoxColor.argb(opacitySeekValue, selectedColorRGB[0], selectedColorRGB[1], selectedColorRGB[2]);
                brushColorView.setBackgroundColor(selectedColorRGB[3]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        brushColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(getApplicationContext(), "Background box clicked!", Toast.LENGTH_LONG).show();
//                LayoutInflater colorInflater = LayoutInflater.from(getApplicationContext());
//                View colorPrompt =colorInflater.inflate(R.layout.color_picker_layout, null);
//                AlertDialog.Builder alertColorPickDialog = new AlertDialog.Builder(CanvasCaptureActivity.this);
//
//                alertColorPickDialog.setView(colorPrompt);
//
//                AlertDialog alertColorPick = alertColorPickDialog.create();
//
//                alertColorPick.show();
//                hideKeyboard();

                final ColorPicker cp;

                if(firstPropmpt == false){
                    cp = new ColorPicker(drawing.this, defaultR, defaultG, defaultB);
                }
                else{
                    cp = new ColorPicker(drawing.this, selectedColorRGB[0], selectedColorRGB[1], selectedColorRGB[2]);
                }


                /* Show color picker dialog */
                cp.show();





    /* On Click listener for the dialog, when the user select the color */
                Button okColor = (Button) cp.findViewById(R.id.okColorButton);
                okColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                /* You can get single channel (value 0-255) */
                        selectedColorRGB[0] = cp.getRed();
                        selectedColorRGB[1] = cp.getGreen();
                        selectedColorRGB[2] = cp.getBlue();

                /* Or the android RGB Color (see the android Color class reference) */
                        //selectedColorRGB[0] = cp.getColor();

                        cp.dismiss();

                        selectedColorRGB[3] = brushBoxColor.rgb(selectedColorRGB[0], selectedColorRGB[1], selectedColorRGB[2]);
                        //Toast.makeText(CanvasCaptureActivity.this, String.valueOf(selectedColorRGB), Toast.LENGTH_LONG).show();
                        brushColorView.setBackgroundColor(selectedColorRGB[3]);
                        //brushBtn.setTextColor(selectedColorRGB[3]);
                    }
                });

            }
        });

        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Brush Properties")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        brushBtn.setTextColor(selectedColorRGB[3]);
                        hideKeyboard();
                        brushBtn.callOnClick();


                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();

        firstPropmpt = true;

    }

    public void saveImageClicked(View view) {
        saveImage();

    }

    public void shareImageClicked(View view) {
        saveImage();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + imageFile));

        startActivity(Intent.createChooser(share, "Share Photo"));
    }


    public String opacityPercent(int opacitySeekInput){
        float opacityFloat = opacitySeekInput;
        float percentInt = (opacityFloat/255)*100;
        String percentValue = String.format("%3.0f",percentInt);
        percentValue = percentValue + "%";

        return percentValue;
    }

    private String createImageName() {
        //file name is the (name of this app + the date + time) the image was taken
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = sdf.format(new Date());
        return "/Drawing" + timeStamp + ".jpg";
    }

    public void saveImage(){
        ImageView canvas = (ImageView)findViewById(R.id.canvasImage);
        canvas.setDrawingCacheEnabled(true);
        Bitmap canvasBM = canvas.getDrawingCache();

        String root = Environment.getExternalStorageDirectory().toString();
        imageFile = new File(root + "/Pictures" + createImageName());

        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            canvasBM.compress(Bitmap.CompressFormat.JPEG,90,out);
            out.flush();
            out.close();
            Toast.makeText(drawing.this, "Saved photo to: " + imageFile.toString(), Toast.LENGTH_SHORT).show();
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }

    }

    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
