package soptqs.paste.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import soptqs.paste.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by S0ptq on 2018/3/11.
 */

public class SaveImageToRaw {
    private Context context;
    private Bitmap bitmap;

    public SaveImageToRaw(Context context) {
        this.context = context;
    }

    public void savaImage(NotificationManager notificationManager, String filename, Bitmap bitmap) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(new NotificationChannel(context.getResources().getString(R.string.channel_images_processing),
                        context.getResources().getString(R.string.channel_images_processing), NotificationManager.IMPORTANCE_HIGH));
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                    context.getResources().getString(R.string.channel_images_processing))
                    .setContentTitle(context.getResources().getString(R.string.image_processing))
                    .setContentText(context.getResources().getString(R.string.image_processing_desc))
                    .setSmallIcon(R.mipmap.ic_smallicon)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setColor(context.getResources().getColor(R.color.colorAPPDark))
                    .setOngoing(true);
            notificationManager.notify(1005, builder.build());

            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            notificationManager.cancel(1005);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getImage(String filename) {
        try {
            FileInputStream fis = context.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
