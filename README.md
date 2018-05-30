# GridImageView
Android custom view for displaying set of an arbitrary images as a grid.

Image loading based on the third party library [Picasso](http://square.github.io/picasso/)

<img src="https://github.com/amagda/GridImageView/blob/master/screenshots/1img.png" width="270" height="480">
<img src="https://github.com/amagda/GridImageView/blob/master/screenshots/2img.png" width="270" height="480">

<img src="https://github.com/amagda/GridImageView/blob/master/screenshots/3img.png" width="270" height="480">
<img src="https://github.com/amagda/GridImageView/blob/master/screenshots/4img.png" width="270" height="480">

## Usage
### XML
```xml
<com.amagda.android.sample.GridImageView
            android:layout_width="200dp"
            android:layout_height="200dp"
            app:padding_btw_images="2dp"
            app:shape_mode="circle" />
```
##### Properties
You may use the following properties in your XML to customize your GridImageView.
* `app:padding_btw_images` (dimension) -> default 0dp
* `app:shape_mode` (rectangle or circle) -> default rectangle

### Java
```java
GridImageView gridImageView = (GridImageView) findViewById(R.id.grid_image_view);
gridImageView.setPaddingBetweenImages(getResources().getDimensionPixelOffset(R.dimen.padding_btw_images));
gridImageView.setShapeMode(GridImageView.CIRCLE_SHAPE_MODE);
gridImageView.setImagePaths(
    "http://x.annihil.us/u/prod/marvel/i/mg/9/80/537ba5b368b7d.jpg",
    "http://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg",
    "http://x.annihil.us/u/prod/marvel/i/mg/6/60/538cd3628a05e.jpg",
    "http://x.annihil.us/u/prod/marvel/i/mg/7/10/537bc71e9286f.jpg");
```
