package com.example.nataliasobolewska.androidapp.Services;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.example.nataliasobolewska.androidapp.Atributtes.Position;
import com.example.nataliasobolewska.androidapp.Objects.ListOfAllObjects;
import com.example.nataliasobolewska.androidapp.Objects.OurObject;

/**
 * Created by Marcin on 10.10.2017.
 */

public class DrawObjectsService{

    Canvas canvas = new Canvas();
    Bitmap myBitmap;

    public DrawObjectsService(ImageView image) {
        this.myBitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
    }

    private Paint createPaint(int color){
        Paint myPaint = new Paint();
        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
        myPaint.setColor(color);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(12);

        return myPaint;
    }

    public void drawOnImageView(ImageView image, ListOfAllObjects listOfAllObjects){
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //Create a new image bitmap and attach a brand new canvas to it
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        canvas.setBitmap(tempBitmap);
        //Draw the image bitmap into the cavas
        canvas.drawBitmap(myBitmap, 0, 0, null);

        for(OurObject ourObject: listOfAllObjects.getListOfOurObjects()){
            drawRectangle(ourObject, image, tempBitmap);
        }
        //Stream.stream(listOfAllObjects.getListOfOurObjects()).forEach(r -> drawRectangle(r, image, tempBitmap));
    }

    private void drawRectangle(OurObject ourObject, ImageView image, Bitmap tempBitmap){
        Paint myPaint = createPaint(ourObject.getColor());
        Position position = ourObject.getPosition();

        //Draw everything else you want into the canvas, in this example a rectangle with rounded edges
        canvas.drawRoundRect(new RectF(position.getDownPoint().getX(),position.getDownPoint().getY(),position.getUpPoint().getX(),position.getUpPoint().getY()), 2, 2, myPaint);

        //Attach the canvas to the ImageView
        image.setImageDrawable(new BitmapDrawable(tempBitmap));
    }
}
