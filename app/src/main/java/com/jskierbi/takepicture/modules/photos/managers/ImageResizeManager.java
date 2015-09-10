package com.jskierbi.takepicture.modules.photos.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.jskierbi.takepicture.d2.ApplicationLevel;
import com.jskierbi.takepicture.d2.ForApplication;
import com.squareup.picasso.Picasso;
import rx.Observable;
import rx.Subscriber;

import javax.inject.Inject;
import java.io.FileOutputStream;

/**
 * Created on 09/09/2015.
 */
@ApplicationLevel
public class ImageResizeManager {

  @Inject
  @ForApplication
  Context context;

  @Inject
  ImageResizeManager() {
  }

  public Observable<Uri> resizePhoto(final Uri sourceFileUri,
                                     final Uri targetFileUri,
                                     final int maxSize) {
    return Observable.create(new Observable.OnSubscribe<Uri>() {
      @Override
      public void call(Subscriber<? super Uri> subscriber) {
        try {
          BitmapFactory.Options bmOptions = new BitmapFactory.Options();
          bmOptions.inJustDecodeBounds = true;
          BitmapFactory.decodeFile(sourceFileUri.getPath(), bmOptions);

          int scaleFactor = Math.min(bmOptions.outWidth / maxSize, bmOptions.outHeight / maxSize);
          bmOptions.inJustDecodeBounds = false;
          bmOptions.inSampleSize = scaleFactor;

          Bitmap bitmap = BitmapFactory.decodeFile(sourceFileUri.getPath(), bmOptions);

          FileOutputStream out = null;
          try {
            out = new FileOutputStream(targetFileUri.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
          } finally {
            if (out != null) out.close();
          }
          // Invalidate picasso - just in case image was already used in application :)
          // This is crucial when overwriting already existing file that was previously loaded by picasso
          Picasso.with(context).invalidate(targetFileUri);
          subscriber.onNext(targetFileUri);
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    });
  }
}
