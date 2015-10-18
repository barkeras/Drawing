package caseybr.barkeras.com.drawing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.io.FileNotFoundException;
import java.io.InputStream;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_canvas);
        Bundle canvasInfo = getIntent().getExtras();
        ImageView canvasImage = (ImageView)findViewById(R.id.canvasImage);
        int userRC = canvasInfo.getInt("requestCode");
        Bitmap canvasBM;
        Button brushBtn = (Button)findViewById(R.id.brushBtn);
        Button shareBtn = (Button)findViewById(R.id.shareImageBtn);
        Button saveBtn = (Button)findViewById(R.id.saveImageBtn);

        Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        brushBtn.setTypeface(fontFamily);
        shareBtn.setTypeface(fontFamily);
        saveBtn.setTypeface(fontFamily);
        brushBtn.setText("\uf1fc");
        shareBtn.setText("\uf1e0");
        saveBtn.setText("\uf0c7");



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



    }

    public void brushBtnClicked(View view) {

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

        selectedColorRGB[0] = defaultR;
        selectedColorRGB[1] = defaultG;
        selectedColorRGB[2] = defaultB;


        selectedColorRGB[3] = brushBoxColor.rgb(defaultR,defaultG,defaultB);
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
                selectedColorRGB[3] = brushBoxColor.argb(opacitySeekValue,selectedColorRGB[0],selectedColorRGB[1],selectedColorRGB[2]);
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

                final ColorPicker cp = new ColorPicker(drawing.this, defaultR, defaultG, defaultB);

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


    }

    public void saveImageClicked(View view) {
    }

    public void shareImageClicked(View view) {
    }


    public String opacityPercent(int opacitySeekInput){
        float opacityFloat = opacitySeekInput;
        float percentInt = (float)(opacityFloat/255)*100;
        String percentValue = String.format("%3.0f",percentInt);
        percentValue = percentValue + "%";

        return percentValue;
    }

}
