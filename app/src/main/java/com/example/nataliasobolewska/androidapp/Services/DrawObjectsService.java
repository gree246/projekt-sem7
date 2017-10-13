package com.example.nataliasobolewska.androidapp.Services;

import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import com.example.nataliasobolewska.androidapp.Atributtes.Position;
import com.example.nataliasobolewska.androidapp.Objects.Rectangle;

/**
 * Created by Marcin on 10.10.2017.
 */

public class DrawObjectsService {

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

    public void drawOnImageView(ImageView image, Rectangle rectangle){
        Bitmap myBitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        Paint myPaint = createPaint(rectangle.getColor());
        Position position = rectangle.getPosition();

        //Create a new image bitmap and attach a brand new canvas to it
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);

        //Draw the image bitmap into the cavas
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        //Draw everything else you want into the canvas, in this example a rectangle with rounded edges
        tempCanvas.drawRoundRect(new RectF(position.getDownPoint().getX(),position.getDownPoint().getY(),position.getUpPoint().getX(),position.getUpPoint().getY()), 2, 2, myPaint);

        //Attach the canvas to the ImageView
        image.setImageDrawable(new BitmapDrawable(tempBitmap));
    }
}
