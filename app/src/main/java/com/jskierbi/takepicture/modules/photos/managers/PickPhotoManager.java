package com.jskierbi.takepicture.modules.photos.managers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import com.jskierbi.takepicture.base.BaseFragment;
import com.jskierbi.takepicture.base.EventBus;
import com.jskierbi.takepicture.d2.ActivityComponent;
import com.jskierbi.takepicture.d2.ForActivity;
import com.jskierbi.takepicture.modules.photos.model.PhotoEvent;
import rx.Observable;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Add this as headless fragment, preferably via AcitivityModule.
 * <p/>
 * Created on 09/08/2015.
 */
public class PickPhotoManager extends BaseFragment {

  public static final String TAG = PickPhotoManager.class.getSimpleName();

  public static final  String KEY_TEMP_IMAGE_URI      = "KEY_TEMP_IMAGE_URI";
  private static final int    REQUEST_CODE_PICK_IMAGE = 0x2101;
  private static final int    REQUEST_CODE_TAKE_IMAGE = 0x2102;

  @Inject
  @ForActivity
  Context context;

  private Uri tempImageUri;
  private List<Object> eventList = new ArrayList<>();

  @Override
  protected void inject(ActivityComponent component) {
    component.inject(this);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      tempImageUri = savedInstanceState.getParcelable(KEY_TEMP_IMAGE_URI);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(KEY_TEMP_IMAGE_URI, tempImageUri);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    if (resultCode != Activity.RESULT_OK) return;

    switch (requestCode) {
      case REQUEST_CODE_PICK_IMAGE: {
        Uri uri = intent.getData();
        Uri imageFileUri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
            getImageFileUriApi19(uri) :
            getImageFileUri(uri);
        eventList.add(new PhotoEvent(Observable.just(imageFileUri)));
        break;
      }
      case REQUEST_CODE_TAKE_IMAGE: {
        eventList.add(new PhotoEvent(Observable.just(tempImageUri)));
        break;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    for (Object event : eventList) {
      EventBus.ui().post(event);
    }
    eventList.clear();
  }

  public void pickPhoto() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
  }

  public void takePhoto() {
    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    tempImageUri = randomCacheFile();
    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
    startActivityForResult(intent, REQUEST_CODE_TAKE_IMAGE);
  }

  public Uri randomCacheFile() {
    File file = new File(context.getExternalCacheDir().getAbsolutePath()
        + File.separator
        + UUID.randomUUID().toString());
    file.deleteOnExit();
    return Uri.fromFile(file);
  }

  private Uri getImageFileUri(Uri uri) {
    // Treat "file" as plain path.
    if ("file".equals(uri.getScheme())) {
      return uri;
    }

    String imageFilePath;
    Cursor cursor = context.getContentResolver().query(
        uri,
        new String[]{android.provider.MediaStore.Images.ImageColumns.DATA},
        null,
        null,
        null);
    cursor.moveToFirst();
    imageFilePath = cursor.getString(0);
    cursor.close();
    return Uri.fromFile(new File(imageFilePath));
  }

  /**
   * Convert URI to file name.
   * This is not safe method, but with conjuncion with startPickPhotoApi19(), can be used as a temporary workaround.
   */
  @TargetApi(19)
  private Uri getImageFileUriApi19(Uri uri) {
    try {
      // Will return "image:x*"
      String wholeID = DocumentsContract.getDocumentId(uri);

      // Split at colon, use second item in the array
      String id = wholeID.replace("%3A", ":").split(":")[1];

      String[] column = {MediaStore.Images.Media.DATA};

      // where id is equal to
      String sel = MediaStore.Images.Media._ID + "=?";

      Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);

      String filePath = "";

      int columnIndex = cursor.getColumnIndex(column[0]);

      if (cursor.moveToFirst()) {
        filePath = cursor.getString(columnIndex);
      }

      cursor.close();

      return Uri.fromFile(new File(filePath));
    } catch (Throwable e) {
      Log.e(TAG, "getImageFileUriApi19", e);
      // Just in case, because some devices returns old style Uri even in
      // API 4.4
      return getImageFileUri(uri);
    }
  }
}
