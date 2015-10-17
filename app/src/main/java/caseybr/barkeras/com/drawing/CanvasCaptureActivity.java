package caseybr.barkeras.com.drawing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.io.File;

public class CanvasCaptureActivity extends Activity {

    final int[] selectedColorRGB = new int[4];
    public static final int BLANK_CANVAS_REQUEST = 20;
    public static final int IMAGE_GALLERY_REQUEST = 21;
    public static final int DRAWING_ACTIVITY_REQUEST = 21;
    int defaultR = 255;
    int defaultB = 255;
    int defaultG = 255;
    Uri fileUri;
    Color backGroundBoxColor = new Color();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_capture);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_canvas_capture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void blankCanvasClicked(View view) {
// get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View promptView = layoutInflater.inflate(R.layout.blank_canvas_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CanvasCaptureActivity.this);

        // set prompts.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);

        final EditText widthInput = (EditText) promptView.findViewById(R.id.promptWidthInput);
        final EditText heightInput = (EditText) promptView.findViewById(R.id.promptHeightInput);
        final View backgroundColorView = (View)promptView.findViewById(R.id.promptBackgroundColorBox);

        selectedColorRGB[0] = defaultR;
        selectedColorRGB[1] = defaultG;
        selectedColorRGB[2] = defaultB;


        selectedColorRGB[3] = backGroundBoxColor.rgb(defaultR,defaultG,defaultB);
        backgroundColorView.setBackgroundColor(selectedColorRGB[3]);


        backgroundColorView.setOnClickListener(new View.OnClickListener() {
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

                final ColorPicker cp = new ColorPicker(CanvasCaptureActivity.this,defaultR,defaultG,defaultB);

                /* Show color picker dialog */
                cp.show();

    /* On Click listener for the dialog, when the user select the color */
                Button okColor = (Button)cp.findViewById(R.id.okColorButton);
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

                        selectedColorRGB[3] = backGroundBoxColor.rgb(selectedColorRGB[0],selectedColorRGB[1],selectedColorRGB[2]);
                        //Toast.makeText(CanvasCaptureActivity.this, String.valueOf(selectedColorRGB), Toast.LENGTH_LONG).show();
                        backgroundColorView.setBackgroundColor(selectedColorRGB[3]);
                    }
                });

            }
        });

        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("New Canvas")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        Intent canvasIntent = new Intent(CanvasCaptureActivity.this,drawing.class);
                        String widthPx = widthInput.getText().toString();
                        String heightPx = heightInput.getText().toString();
                        canvasIntent.putExtra("redValue", selectedColorRGB[0]);
                        canvasIntent.putExtra("greenValue", selectedColorRGB[1]);
                        canvasIntent.putExtra("blueValue", selectedColorRGB[2]);
                        canvasIntent.putExtra("width", widthPx);
                        canvasIntent.putExtra("height", heightPx);
                        canvasIntent.putExtra("requestCode",BLANK_CANVAS_REQUEST);
                        startActivity(canvasIntent);

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,	int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
    }

    public void existingImageClicked(View view) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // tells use where to get the data
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        //URI representation of the selected image
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type to get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");

        // starts the intent implicitly to open image gallery
        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode ==RESULT_OK){

            if(requestCode == IMAGE_GALLERY_REQUEST){
                Intent drawingIntent = new Intent(CanvasCaptureActivity.this, drawing.class);
                fileUri = data.getData();
                drawingIntent.putExtra("fileUri",fileUri.toString());
                drawingIntent.putExtra("requestCode",IMAGE_GALLERY_REQUEST);
                startActivityForResult(drawingIntent,DRAWING_ACTIVITY_REQUEST);

            }
            if(requestCode == DRAWING_ACTIVITY_REQUEST){



            }
        }
    }


}
