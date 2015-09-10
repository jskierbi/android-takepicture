package com.jskierbi.takepicture;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jskierbi.takepicture.base.BaseActivity;
import com.jskierbi.takepicture.d2.ActivityComponent;
import com.jskierbi.takepicture.modules.photos.managers.ImageResizeManager;
import com.jskierbi.takepicture.modules.photos.managers.PickPhotoManager;
import com.jskierbi.takepicture.modules.photos.model.PhotoEvent;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

import javax.inject.Inject;
import java.io.File;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public class MainActivity extends BaseActivity {

  @Bind(R.id.img_photo)
  ImageView imgPhoto;

  @Inject
  PickPhotoManager   pickPhotoManager;
  @Inject
  ImageResizeManager imageResizeImage;

  private Subscription loadPhotoSubscription = Subscriptions.empty();

  @Override
  protected void inject(ActivityComponent component) {
    component.inject(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    Picasso.with(this)
        .load(getPhotoFileUri())
        .placeholder(R.drawable.photo_placeholder)
        .fit().centerCrop()
        .into(imgPhoto);
  }

  @Override
  protected void onDestroy() {
    loadPhotoSubscription.unsubscribe();
    super.onDestroy();
  }

  @OnClick(R.id.btn_photo)
  void photoClick() {
    pickPhotoManager.takePhoto();
  }

  @OnClick(R.id.btn_gallery)
  void galleryClick() {
    pickPhotoManager.pickPhoto();
  }

  @Subscribe
  public void subscribe(PhotoEvent event) {
    loadPhotoSubscription.unsubscribe();
    loadPhotoSubscription = event.getImageUriObservable()
        .flatMap(new Func1<Uri, Observable<Uri>>() {
          @Override
          public Observable<Uri> call(Uri uri) {
            return imageResizeImage.resizePhoto(uri, getPhotoFileUri(), 500);
          }
        })
        .subscribeOn(io())
        .observeOn(mainThread())
        .subscribe(new Subscriber<Uri>() {
          @Override
          public void onCompleted() {
          }

          @Override
          public void onError(Throwable e) {
            Toast.makeText(MainActivity.this, "Photo loading failed", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onNext(Uri uri) {
            Picasso.with(MainActivity.this)
                .load(uri)
                .placeholder(R.drawable.photo_placeholder)
                .fit().centerCrop()
                .into(imgPhoto);
          }
        });
  }

  private Uri getPhotoFileUri() {
    return Uri.fromFile(new File(
        getDir("photos", Context.MODE_PRIVATE).getAbsolutePath()
            + File.separator
            + "photo.jpg"));
  }
}
