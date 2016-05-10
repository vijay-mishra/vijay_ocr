package com.googlecode.tesseract.android.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ShutterCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class MainActivity extends Activity implements SurfaceHolder.Callback {

    SurfaceView surfaceView;
    Camera camera;
    SurfaceHolder surfaceHolder;
    static Bitmap bitmap;
    Boolean preview = false;
    final int TAKE_PICTURE = 0;
    private String resultUrl = "result.txt";
    public static final String lang = "eng";
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/Passport_image/";
    int capture_chek = 0;
    LinearLayout linearLayout;
    ImageView camera_button;
    Bitmap bmOut;
    Bitmap resizemap;
    int height=0,width=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);


        String[] paths = new String[]{DATA_PATH, DATA_PATH + "tessdata/"};

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v("Tesseract", "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v("Tesseract", "Created directory " + path + " on sdcard");
                }
            }

        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v("Tesseract", "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e("Tesseract", "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }


        linearLayout = (LinearLayout) findViewById(R.id.linear);

        linearLayout.post(new Runnable() {
            public void run() {
                height = linearLayout.getMeasuredHeight();
                width = linearLayout.getMeasuredWidth();
                linearLayout.addView(new TransparentView(getApplicationContext(),height,width));


                Log.e("Height", height + "\n" + width);

            }
        });


        surfaceView = (SurfaceView) findViewById(R.id.surfacecamera);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


    }

//    private static Uri getOutputMediaFileUri(){
//
//        return Uri.fromFile(getOutputMediaFile());
//    }
//
//    /** Create a File for saving an image or video */
//    private static File getOutputMediaFile(){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//        File folder = new File(Environment.getExternalStorageDirectory() +
//                File.separator + "Passport Image");
//
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! folder.exists()){
//            if (! folder.mkdirs()){
//                return null;
//            }
//        }
//
//
//
//
//
//        // Create a media file name
//
//
//
//        File mediaFile = new File(folder.getPath() + File.separator + "image.jpg" );
//        FileOutputStream out = null;
//        try {
//            out = new FileOutputStream(mediaFile);
//            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
//            int h = bitmap.getHeight();
//            int w = bitmap.getWidth();
//            Bitmap grayscale = Bitmap.createBitmap(h, w, Bitmap.Config.ARGB_8888);
//            Canvas c = new Canvas(grayscale);
//            Paint paint = new Paint();
//            ColorMatrix cm = new ColorMatrix();
//            cm.setSaturation(0);
//            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
//            paint.setColorFilter(f);
//            c.drawBitmap(bitmap, 0, 0, paint);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return mediaFile;
//    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (preview) {
            camera.stopFaceDetection();
            camera.stopPreview();
            preview = false;
        }

        if (camera != null) {
          //  try {

                //camera.startPreview();

                preview = true;
                // Parameters
                Parameters parameters = camera.getParameters();
                parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        parameters.set("jpeg-quality", 100);
//        parameters.setPictureFormat(PixelFormat.OPAQUE);
                // parameters.setPictureSize(2048, 1232);
               // parameters.setPreviewSize(width, height);

            Camera.Size bestSize = null;

            List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
            bestSize = sizeList.get(0);

            for(int i = 1; i < sizeList.size(); i++){
                if((sizeList.get(i).width * sizeList.get(i).height) >
                        (bestSize.width * bestSize.height)){
                    bestSize = sizeList.get(i);
                }
            }
            parameters.setPictureSize(bestSize.width, bestSize.height);
            //parameters.setPreviewSize(bestSize.width, bestSize.height);


                camera.setParameters(parameters);


                camera.startPreview();
                camera.startFaceDetection();
                camera.setFaceDetectionListener(faceDetectionListener);





//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        }


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera_button = (ImageView) findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(capture_chek==0) {
                    camera.cancelAutoFocus();
                    camera.takePicture(shutterCallback, null, pictureCallback);
                    capture_chek=1;
                }
            }
        });

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            preview = false;
        }

    }



    FaceDetectionListener faceDetectionListener = new FaceDetectionListener() {

        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera arg1) {
            if (camera != null)
                camera.autoFocus(autoFocusCallback);

        }
    };
    ShutterCallback shutterCallback = new ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };
    AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
        int count = 1;

        @Override
        public void onAutoFocus(boolean success, final Camera arg1) {
            final Handler handler = new Handler();

            if (capture_chek == 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (arg1 != null) {
                            camera_button.setClickable(false);
                            arg1.takePicture(shutterCallback, null, pictureCallback);
                        }
                    }
                }, 5000); //<-5 second delay
            }
            capture_chek = 1;


        }
    };

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

//            if (camera != null) {
//                camera.stopPreview();
//                camera.release();
//                preview = false;
//            }
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            ImageView img =(ImageView)findViewById(R.id.img);
//            img.setImageBitmap(bitmap);

//            String imgString = Base64.encodeToString(data,
//                    Base64.NO_WRAP);
//            Log.e("bytes",imgString+"");
            //Toast.makeText(getApplicationContext(), "data --"+data.length, Toast.LENGTH_LONG).show();
            if (data.length != 0) {
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 1;
               // bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);


                File mediaFile = new File(DATA_PATH, "image.jpg");

                if (mediaFile.exists()) mediaFile.delete();
                //FileOutputStream out = null;
                try {
//
                    FileOutputStream fos = new FileOutputStream(mediaFile);
                    fos.write(data);
                    fos.close();
//                    out = new FileOutputStream(mediaFile);
//
//
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bitmap is your Bitmap instance
//                    out.flush();
//                    out.close();

                    new TesseractProcessing().execute();


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    try {
//                        if (out != null) {
//                            out.close();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }


            }else{
               // Toast.makeText(getApplicationContext(), "data 0", Toast.LENGTH_LONG).show();

            }
        }
    };


    /**
     * Background Async Task to process file
     */
    class TesseractProcessing extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Tesseract Engine Processing");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {


            File mediaFile = new File(DATA_PATH, "image.jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());
            Bitmap bm=null;

           // bm=grayScaleImage(myBitmap);

            bm=grayScaleImage(myBitmap);
            File imgFile2 = new  File(DATA_PATH,"image1.jpg");
            if (imgFile2.exists()) imgFile2.delete();
            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(imgFile2);
                bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
              //  myBitmap.recycle();
            } catch (Exception e) {}



            TessBaseAPI baseApi = new TessBaseAPI();
            baseApi.setDebug(true);
            baseApi.init(DATA_PATH, lang);

            baseApi.setImage(bm);

            String recognizedText = baseApi.getUTF8Text();

            baseApi.end();


            return recognizedText;

        }

            /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String recognizedText) {
            // dismiss the dialog after the file was downloaded
            progressDialog.dismiss();
            Log.v("Tesseract", "OCRED TEXT: " + recognizedText);


            if (lang.equalsIgnoreCase("eng")) {
                recognizedText = recognizedText.replaceAll("[^<a-zA-Z0-9]+", "");
            }

            recognizedText = recognizedText.trim();

            if (recognizedText.length() != 0) {

                Intent intent = new Intent(MainActivity.this, Fields_fill.class);
                intent.putExtra("Text", recognizedText);
                intent.putExtra("height",height);
                intent.putExtra("width",width);
                startActivity(intent);
                finish();



            }



        }


    }

    public static Bitmap createBlackAndWhite(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);

                // use 128 as threshold, above -> white, below -> black
                if (gray > 128)
                    gray = 255;
                else
                    gray = 0;
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }
        return bmOut;
    }

    public static Bitmap grayScaleImage(Bitmap src) {
        // constant factors
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;

        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

//        bmOut=bmOut.copy(Bitmap.Config.RGB_565, true);

//         pixel information
        int A, R, G, B;
        int pixel;

        // get image size
        int width = src.getWidth();
        int height = src.getHeight();

        // scan through every single pixel
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
//        bmOut = bmOut.copy(Bitmap.Config.ALPHA_8,true);
        return bmOut;
    }

    public static Bitmap createGrayscale(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }
        return bmOut;
    }


    public static Bitmap GrayscaleToBin(Bitmap bm2)
    {
        Bitmap bm;
        bm=bm2.copy(Bitmap.Config.RGB_565, true);
        final   int width = bm.getWidth();
        final  int height = bm.getHeight();

        int pixel1,pixel2,pixel3,pixel4,A,R;
        int[]  pixels;
        pixels = new int[width*height];
        bm.getPixels(pixels,0,width,0,0,width,height);
        int size=width*height;
        int s=width/8;
        int s2=s>>1;
        double t=0.15;
        double it=1.0-t;
        int []integral= new int[size];
        int []threshold=new int[size];
        int i,j,diff,x1,y1,x2,y2,ind1,ind2,ind3;
        int sum=0;
        int ind=0;
        while(ind<size)
        {
            sum+=pixels[ind] & 0xFF;
            integral[ind]=sum;
            ind+=width;
        }
        x1=0;
        for(i=1;i<width;++i)
        {
            sum=0;
            ind=i;
            ind3=ind-s2;
            if(i>s)
            {
                x1=i-s;
            }
            diff=i-x1;
            for(j=0;j<height;++j)
            {
                sum+=pixels[ind] & 0xFF;
                integral[ind]=integral[(int)(ind-1)]+sum;
                ind+=width;
                if(i<s2)continue;
                if(j<s2)continue;
                y1=(j<s ? 0 : j-s);
                ind1=y1*width;
                ind2=j*width;

                if (((pixels[ind3]&0xFF)*(diff * (j - y1))) < ((integral[(int)(ind2 + i)] - integral[(int)(ind1 + i)] - integral[(int)(ind2 + x1)] + integral[(int)(ind1 + x1)])*it)) {
                    threshold[ind3] = 0x00;
                } else {
                    threshold[ind3] = 0xFFFFFF;
                }
                ind3 += width;
            }
        }

        y1 = 0;
        for( j = 0; j < height; ++j )
        {
            i = 0;
            y2 =height- 1;
            if( j <height- s2 )
            {
                i = width - s2;
                y2 = j + s2;
            }

            ind = j * width + i;
            if( j > s2 ) y1 = j - s2;
            ind1 = y1 * width;
            ind2 = y2 * width;
            diff = y2 - y1;
            for( ; i < width; ++i, ++ind )
            {

                x1 = ( i < s2 ? 0 : i - s2);
                x2 = i + s2;

                // check the border
                if (x2 >= width) x2 = width - 1;

                if (((pixels[ind]&0xFF)*((x2 - x1) * diff)) < ((integral[(int)(ind2 + x2)] - integral[(int)(ind1 + x2)] - integral[(int)(ind2 + x1)] + integral[(int)(ind1 + x1)])*it)) {
                    threshold[ind] = 0x00;
                } else {
                    threshold[ind] = 0xFFFFFF;
                }
            }
        }
   /*-------------------------------
    * --------------------------------------------*/
        bm.setPixels(threshold,0,width,0,0,width,height);

        return bm;
    }
}