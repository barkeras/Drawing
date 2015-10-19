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


    //Runs when user clicks the new canvas button
    public void blankCanvasClicked(View view) {

        //gets the blank_canvas prompt_xml to be displayed in alert dialog
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.blank_canvas_prompt, null);

        //creates the alertDialog and tells it to launch in the CanvasCaptureActivity context
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CanvasCaptureActivity.this);

        // set blank_canvas_prompt.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);

        final EditText widthInput = (EditText) promptView.findViewById(R.id.promptWidthInput);
        final EditText heightInput = (EditText) promptView.findViewById(R.id.promptHeightInput);
        final View backgroundColorView = (View)promptView.findViewById(R.id.promptBackgroundColorBox);


        //sets each portion of the selectedColorArray to the according default RGB value so when alert displays the box that
        // shows the color is default to white
        selectedColorRGB[0] = defaultR;
        selectedColorRGB[1] = defaultG;
        selectedColorRGB[2] = defaultB;

        //gets the integer color value of the rgb colors and saves into array element and sets the view color to reflect the color
        selectedColorRGB[3] = backGroundBoxColor.rgb(defaultR,defaultG,defaultB);
        backgroundColorView.setBackgroundColor(selectedColorRGB[3]);


        //when the color view is clicked this runs
        backgroundColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //uses color picker library to create the color picker pop up with the default RGB values to show white
                final ColorPicker cp = new ColorPicker(CanvasCaptureActivity.this,defaultR,defaultG,defaultB);

                /* Show color picker dialog */
                cp.show();

    /* On Click listener for the dialog, when the user select the color */
                Button okColor = (Button)cp.findViewById(R.id.okColorButton);
                okColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //gets the int value of R,G,and B the user selects and saves into array
                        selectedColorRGB[0] = cp.getRed();
                        selectedColorRGB[1] = cp.getGreen();
                        selectedColorRGB[2] = cp.getBlue();

                        //closes the color picker pop up
                        cp.dismiss();

                        //saves the exact color integer from RGB values into array and sets view background color to reflect users decision
                        selectedColorRGB[3] = backGroundBoxColor.rgb(selectedColorRGB[0],selectedColorRGB[1],selectedColorRGB[2]);
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

                        // creates new intent to go from CanvasCaptureActivity to the drawing activity
                        Intent canvasIntent = new Intent(CanvasCaptureActivity.this,drawing.class);

                        //gets the width and height input from the editTexts for how big user wants to make new canvas
                        String widthPx = widthInput.getText().toString();
                        String heightPx = heightInput.getText().toString();

                        //pass the color values, canvas height and width, and the request code to next activity and start drawing activity
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

        // creates the new canvas dialog
        AlertDialog alertD = alertDialogBuilder.create();
        //shows the new canvas dialog
        alertD.show();
    }


    //runs when existing image button is clicked
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

        //code within will run as long as user does not cancel their action
        if(resultCode == RESULT_OK){

            //code will run if the user chooses existing image button to launch the gallery intent
            if(requestCode == IMAGE_GALLERY_REQUEST){
                //creates new intent to go from CanvasCaptureActivity to the drawing activity
                Intent drawingIntent = new Intent(CanvasCaptureActivity.this, drawing.class);

                //saves the image the user selects as a uri
                fileUri = data.getData();

                //pass the uri and request code to the next activity and start next activity
                drawingIntent.putExtra("fileUri",fileUri.toString());
                drawingIntent.putExtra("requestCode",IMAGE_GALLERY_REQUEST);
                startActivityForResult(drawingIntent,DRAWING_ACTIVITY_REQUEST);

            }
            //this runs and should pull the last image the user worked on into imageView under previous canvas on the launcher screen
            if(requestCode == DRAWING_ACTIVITY_REQUEST){



            }
        }
    }


}
