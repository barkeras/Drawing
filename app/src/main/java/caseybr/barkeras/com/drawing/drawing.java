package caseybr.barkeras.com.drawing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Andrew on 10/16/2015.
 */
public class drawing extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_canvas);
        Bundle canvasInfo = getIntent().getExtras();
        ImageView canvasImage = (ImageView)findViewById(R.id.canvasImage);
        int userRC = canvasInfo.getInt("requestCode");
        Bitmap canvasBM;

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
}
