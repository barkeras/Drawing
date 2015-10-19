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

        //uses the fontawesome font to set the button texts to icons
        Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        brushBtn.setTypeface(fontFamily);
        shareBtn.setTypeface(fontFamily);
        saveBtn.setTypeface(fontFamily);
        eraseBtn.setTypeface(fontFamily);
        brushBtn.setText("\uf1fc");
        shareBtn.setText("\uf1e0");
        saveBtn.setText("\uf0c7");
        eraseBtn.setText("\uf12d");

        //sets the brush icon color to the default color of white
        selectedColorRGB[3] = brushBoxColor.argb(255,defaultR,defaultG,defaultB);
        brushBtn.setTextColor(selectedColorRGB[3]);


        //if the user chose to create a new canvas
        if(userRC == 20){

            //gets the width, height, and color values passed in from the CanvasCaptureActivity
            int canvasWidth = Integer.parseInt(canvasInfo.getString("width"));
            int canvasHeight = Integer.parseInt(canvasInfo.getString("height"));
            int[] canvasColor =  new int[4];
            canvasColor[0] = canvasInfo.getInt("redValue");
            canvasColor[1] = canvasInfo.getInt("greenValue");
            canvasColor[2] = canvasInfo.getInt("blueValue");

            //sets canvasColor to the final int of the RGB color user picked when creating canvas
            Color canvasBmColor = new Color();
            canvasColor[3] = canvasBmColor.rgb(canvasColor[0],canvasColor[1],canvasColor[2]);

            //creates the Bitmap of the width and height the user selected when creating canvas in CanvasCaptureActivity
            canvasBM = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);

            //sets the canvas to the color the user choose in CanvasCaptureActivity and then sets bitmap as imageView
            canvasBM.eraseColor(canvasColor[3]);
            canvasImage.setImageBitmap(canvasBM);

        }
        //runs if user chose an existing image following code runs
        if(userRC == 21){

            //gets the uri of the image the user selected from the gallery
            Uri imageUri = Uri.parse(canvasInfo.getString("fileUri"));

            InputStream imgStream = null;

            //tries to open the image the user picked and set it as a Bitmap and then set the imageView to display the image
            try {
                imgStream = getContentResolver().openInputStream(imageUri);
                canvasBM = BitmapFactory.decodeStream(imgStream);
                canvasImage.setImageBitmap(canvasBM);

            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }

        //runs when the brush button is clicked
        brushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //notify the user the brush is now active
                Toast.makeText(drawing.this, "Brush activated", Toast.LENGTH_LONG).show();

                //sets the brush button background to half opaque white
                // set erase button text to white and background to half opaque black
                int colorWhite = getResources().getColor(R.color.white);
                int colorClear = getResources().getColor(R.color.clear);
                int colorWhiteBack = getResources().getColor(R.color.white_half_opacity);
                eraseBtn.setTextColor(colorWhite);
                eraseBtn.setBackgroundColor(colorClear);
                brushBtn.setBackgroundColor(colorWhiteBack);

            }
        });

        //when brush button is held down
        brushBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //executes the brushBtnHeld method
                brushBtnHeld();

                return false;
            }
        });

        //runs when erase button is clicked
        eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //notify user the erase is now active
                Toast.makeText(drawing.this,"Eraser Activated",Toast.LENGTH_LONG).show();

                //sets erase button background to half white with black icon color
                //sets the brush button backgroud back to half black
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

        //gets the brush_prompt.xml for the dialog layout
        View promptView = layoutInflater.inflate(R.layout.brush_prompt, null);

        //build a new alertDialog to display in drawing activity
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(drawing.this);

        // set brush_prompt.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);

        final EditText brushSize = (EditText) promptView.findViewById(R.id.promptBrushPixels);
        final SeekBar brushSeekBar = (SeekBar) promptView.findViewById(R.id.brushSeek);
        final View brushColorView = (View)promptView.findViewById(R.id.promptBrushColorBox);
        final SeekBar opacitySeek = (SeekBar)promptView.findViewById(R.id.opacitySeekBar);
        final TextView opacityTextView = (TextView)promptView.findViewById(R.id.opacityValueTV);
        final Button brushBtn = (Button)findViewById(R.id.brushBtn);

        //if first time prompt is opened then set the default color values
        if(firstPropmpt == false){

            selectedColorRGB[0] = defaultR;
            selectedColorRGB[1] = defaultG;
            selectedColorRGB[2] = defaultB;

        }
        else{


        }


        //gets the final int value of the RGB (default or user selected) and saves into array
        selectedColorRGB[3] = brushBoxColor.rgb(selectedColorRGB[0],selectedColorRGB[1],selectedColorRGB[2]);
        brushColorView.setBackgroundColor(selectedColorRGB[3]);

        //sets the brush size editText to the value of the seekBar
        brushSize.setText(String.valueOf(brushSeekBar.getProgress()));


        //this listens for interaction with the seek bar that adjusts the brush size
        brushSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //while the user is changing the bar set the new value to the editText
                brushSize.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //gets the int value from the opacity seekbar
        opacitySeekValue = opacitySeek.getProgress();

        //sets the opacity percentage view to the percent value
        opacityTextView.setText(opacityPercent(opacitySeekValue));

        //listens for interaction with the opacity seekbar
        opacitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //while the user is adjusting the opacity seekbar
                //sets the opacity percentage view to the percent value
                opacitySeekValue = progress;
                opacityTextView.setText(opacityPercent(opacitySeekValue));

                //sets the final RGB color to the RGB color with the opacity (alpha) value and save into array
                //set color box background color to reflect the color with the opacity
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

                //creates new color picker
                final ColorPicker cp;

                //if this is first time color picker opens then set the RGB values to the defaults to be shown when color picker opens
                if(firstPropmpt == false){
                    cp = new ColorPicker(drawing.this, defaultR, defaultG, defaultB);
                }
                //if not the first time color picker opens, set the RGB color values to the last color user selected
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

                        //closes the color picker
                        cp.dismiss();

                        //sets the brush color box background color to the color the user selected
                        selectedColorRGB[3] = brushBoxColor.rgb(selectedColorRGB[0], selectedColorRGB[1], selectedColorRGB[2]);
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

                        //sets the brush icon color to the color user selected and hides the keyboard
                        brushBtn.setTextColor(selectedColorRGB[3]);
                        hideKeyboard();

                        //run the code that emulates brush button being clicked (not held)
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

        //set firstPrompt to true so after the first run all instances of past user selections will be displayed, not default values
        firstPropmpt = true;

    }

    //runs when save icon is clicked
    public void saveImageClicked(View view) {
        //run the saveImage method
        saveImage();

    }

    //runs when the share icon is clicked
    public void shareImageClicked(View view) {
        //run the saveImage method
        saveImage();

        //create a new intent using Action_Send
        Intent share = new Intent(Intent.ACTION_SEND);

        //set the type of share to only images and pass the file
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + imageFile));

        //start the Activity to allow user to choose what they want to share photo with
        startActivity(Intent.createChooser(share, "Share Photo"));
    }

    //accepts the opacity value (0-255) as parameter
    public String opacityPercent(int opacitySeekInput){
        //save parameter as float so decimals can be performed
        float opacityFloat = opacitySeekInput;

        //get percent value and display as whole number
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

        // get the image from the imageview to save as bitmap
        ImageView canvas = (ImageView)findViewById(R.id.canvasImage);
        canvas.setDrawingCacheEnabled(true);
        Bitmap canvasBM = canvas.getDrawingCache();

        //create the new file with following filePath
        String root = Environment.getExternalStorageDirectory().toString();
        imageFile = new File(root + "/Pictures" + createImageName());

        //try to compress the image in the stream and notify the user the image is saved
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
