package com.googlecode.tesseract.android.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

public class Fields_fill extends AppCompatActivity {
    EditText typetxt, countrytxt, nametxt, surnametxt, passportnotxt, dobtxt, gendertxt;
    RelativeLayout relativeLayout;
    ImageView imageView, pass_image1;


    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/Passport_image/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields_fill);

        typetxt = (EditText) findViewById(R.id.passporttype);
        countrytxt = (EditText) findViewById(R.id.country);
        nametxt = (EditText) findViewById(R.id.name);
        surnametxt = (EditText) findViewById(R.id.surname);
        passportnotxt = (EditText) findViewById(R.id.passportno);
        dobtxt = (EditText) findViewById(R.id.dateofbirth);
        gendertxt = (EditText) findViewById(R.id.gender);

        relativeLayout = (RelativeLayout) findViewById(R.id.relative);

        imageView = (ImageView) findViewById(R.id.pass_image);
        pass_image1 = (ImageView) findViewById(R.id.pass_image1);
//
//     imageView.setImageURI(Uri.parse(DATA_PATH + File.separator + "image.jpg"));

//        File imgFile = new  File(DATA_PATH + File.separator + "image.jpg");
        File imgFile = new File(DATA_PATH, "image.jpg");
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }

        File imgFile1 = new File(DATA_PATH, "image1.jpg");
        if (imgFile1.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
            pass_image1.setImageBitmap(myBitmap);

        }

        Intent intent = getIntent();
        String ocrtxt = intent.getStringExtra("Text");


        //String ocrtxt = "mmJww5x7f7V1f2mwwaggfwvEEgg1WWW77WWgfjsw<3mmaWREPUBLICormuftJPF3573irxugm3311M77VVWVQNDa71imzL995215r77511CWHAHVLAw7gmijK2r71jywfH82agra9aaGURGAONHARYANArDELHI37111V115tg2706201a255212P<INDCHAWLA<<AIT<<<<<<<<<<<<<<<<<<<<<<<<<<L99521SS<SIN08809025M2406260<<<<<<<<<<<<<<<6VoJIIAVIIHI";

        Log.e("ocrtxt", ocrtxt);
        try {
            int index = ocrtxt.indexOf("P<");


            String str = ocrtxt.substring(index);
            char[] ch = str.toCharArray();
            System.out.println("Character Array");
            for (int i = 0; i < ch.length; i++) {
                System.out.println(i + " " + ch[i]);
            }


            System.out.println("Back to string");
            String back = String.copyValueOf(ch);
            System.out.println(back);

            String[] strarr = new String[10];
            int j = 0;

            for (int i = 0; i < ch.length; i++) {
                String test = "";
                if (ch[i] == '<') {
                    continue;
                } else {
                    int k = i;

                    while (ch[k] != '<') {
                        String s = "";
                        System.out.println(k + "--" + ch.length);
                        if (k < ch.length - 1) {

                            strarr[j] += String.valueOf(ch[k]);
                            strarr[j] = strarr[j].replace("null", "");
//					if(k==ch.length-1);
//					else
                            if (k < ch.length - 1)
                                k++;

                        }
                        if (k == ch.length - 1) {
                            //System.out.println(k+"--"+ch.length);
                            test = "yes";
                            break;
                        }

                    }

                    i = k - 1;
                    j++;
                }
                if (test == "yes")
                    break;
            }
            System.out.println("String array");
            String country = null, surname = null, type = null, name = null, passportno = null, date = null, month = null, year = null, dob = null, sex = null;
            for (int i = 0; i < strarr.length; i++) {
                if (strarr[i] != null)
                    System.out.println((strarr[i]));
                try {
                    if (i == 0) {
                        type = strarr[i].substring(0, strarr[i].length());
                    }
                } catch (Exception e) {
                    type = "";
                }
                try {
                    if (i == 1) {
                        country = strarr[i].substring(0, 3);
                        surname = strarr[i].substring(3, strarr[i].length());
                    }
                } catch (Exception e) {
                    country = "";
                    surname = "";
                }
                try {
                    if (i == 2) {
                        name = strarr[i].substring(0, strarr[i].length());
                    }
                } catch (Exception e) {
                    name = "";
                }
                try {
                    if (i == 3) {
                        passportno = strarr[i].substring(0, strarr[i].length());
                    }
                } catch (Exception e) {
                    passportno = "";
                }
                try {
                    if (i == 4) {
                        year = strarr[i].substring(4, 6);
                        month = strarr[i].substring(6, 8);
                        date = strarr[i].substring(8, 10);
                        dob = date + "/" + month + "/" + year;
                        sex = strarr[i].substring(11, 12);

                    }
                } catch (Exception e) {
                    year = "";
                    month = "";
                    date = "";
                    dob = date + "/" + month + "/" + year;
                    sex = "";
                }
            }

            typetxt.setText(type);
            countrytxt.setText(country);
            nametxt.setText(name);
            surnametxt.setText(surname);
            passportnotxt.setText(passportno);
            dobtxt.setText(dob);
            gendertxt.setText(sex);


//            if(index != -1) {
//
//
////            char[] chars = ocrtxt.toCharArray();
////            String alltext = String.valueOf(chars,index,ocrtxt.length());
////            Log.e("Data",alltext);
////            }
//
//               // String.valueOf(chars,index,ocrtxt.length());
//            String type = ocrtxt.substring(index, index + 1);
//            int index2 = ocrtxt.indexOf("<", index + 2);
//            if (index2 == -1) {
//                System.out.println("Second Index not Found");
//            }
//            String country = ocrtxt.substring(index + 2, index + 5);
//            String surname = ocrtxt.substring(index + 5, index2);
//            int index3 = ocrtxt.indexOf("<", index2 + 2);
//            if (index3 == -1) {
//                System.out.println("Third Index not Found");
//            }
//            String name = ocrtxt.substring(index2 + 2, index3);
//            int index4 = ocrtxt.indexOf("<", index3 + 1);
//            if (index4 == -1) {
//                System.out.println("Fourth Index not Found");
//            } else {
//                while (ocrtxt.charAt(index4) == '<' || ocrtxt.charAt(index4) == '^') {
//                    index4++;
//                }
//            }
//            int index5 = ocrtxt.indexOf("<", index4);
//            if (index5 == -1) {
//                System.out.println("Fourth Index not Found");
//            }
//            String passrtno = ocrtxt.substring(index4, index5);
//            int index6 = ocrtxt.indexOf("<", index5 + 1);
//            String country2 = ocrtxt.substring(index5 + 2, index5 + 5);
//            String dob = ocrtxt.substring(index5 + 5, index5 + 11);
//            String year = dob.substring(0, 2);
//            String mnth = dob.substring(2, 4);
//            String date = dob.substring(4, 6);
//            String dob1 = date.concat(mnth).concat(year);
//            String gender = ocrtxt.substring(index5 + 12, index5 + 13);
//            String doexpiry = ocrtxt.substring(index5 + 13, index5 + 19);
//            String date1 = doexpiry.substring(0, 2);
//            System.out.println(type);
//            typetxt.setText(type);
//            System.out.println(country);
//            countrytxt.setText(country);
//            System.out.println(name);
//            nametxt.setText(name);
//            System.out.println(surname);
//            surnametxt.setText(surname);
//            System.out.println(passrtno);
//            passportnotxt.setText(passrtno);
//            System.out.println(country2);
//            System.out.println(dob1);
//            dobtxt.setText(dob1);
//            System.out.println(gender);
//            gendertxt.setText(gender);
//            System.out.println(doexpiry);
//
//            }
            Toast.makeText(getApplicationContext(), index + "  index value", Toast.LENGTH_LONG).show();

        } catch (StringIndexOutOfBoundsException e) {

            Toast.makeText(getApplicationContext(), "Image Not Clear", Toast.LENGTH_LONG).show();
//            relativeLayout.removeAllViews();
//            TextView textView = new TextView(Fields_fill.this);
//            textView.setText("You have not taken a clear picture");
//            relativeLayout.addView(textView);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Image Not Clear", Toast.LENGTH_LONG).show();
        }


    }


}
