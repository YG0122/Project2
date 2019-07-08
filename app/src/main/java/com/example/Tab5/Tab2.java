package com.example.Tab5;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

public class Tab2 extends Fragment implements View.OnClickListener{
    private ImageView imgMain;
    private Button  btnCloud;
    private ImageButton btnCamera, btnAlbum, btnSend, btnSync;
    static GridView gridView;
    static gridAdapter adapter = null;
    private Bitmap bitmap;
    public byte[] byteArray;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;

    private Uri photoUri;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;
    static JSONArray jArray = new JSONArray();
    static JSONArray new_jArray = new JSONArray();
    String filepath;
    String a;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(getContext(), pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(getContext(), "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(getContext(),
                    "com.example.Tab5.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            refreshmedia(photoFile);
            filepath = photoFile.getAbsolutePath();
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "nostest_" + timeStamp + "_";
        File storageDir = new File(getExternalStorageDirectory() + "/NOSTest/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public static void initialize(JSONArray Data) {
        jArray = Data;
        adapter = new gridAdapter(jArray);
        gridView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera: {
                takePhoto();
                break;
            }
            case R.id.btn_album :{
                goToAlbum();
                break;
            }
            case R.id.btn_send :{
                send_server();
                Toast.makeText(getContext(), "Upload Success", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btn_sync: {
                sync_server();
                Toast.makeText(getContext(), "Synchronized", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btn_cloud: {
                show_cloud(v);
                Toast.makeText(getContext(), "Cloud", Toast.LENGTH_SHORT).show();
                break;

            }
        }
    }

    public void send_server() {
        if (jArray != null) {//누나꺼에서 images=jArray
            for (int i = 0; i < jArray.length(); i++){
                try {
                    JSONObject jsonObject = (JSONObject) jArray.get(i);
                    Bitmap bitmap = (Bitmap) jsonObject.get("Photo");
                    bitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, false); // 비트맵 사이즈 줄이기
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();  //bitmap크기 줄이고 bytearray로 만드는중
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                    a = getStringFromBitmap(bitmap);
                    new JSONPOST().execute("http://143.248.39.55:8080/api/gallerys");
                }
                catch(JSONException e){
                    //TODO exception handler code
                }
            }
        }
    }

    public void show_cloud(View view){
        btnCloud.setVisibility(view.VISIBLE);
        Intent i= new Intent(getActivity(), Godbgallery.class);
        startActivity(i);
    }

    public void sync_server(){
        JSONArray jArray = new JSONArray();
        int list_size= Godbgallery.listdata.size();
        try {
            for (int i = 0; i < list_size; i++) {
                JSONObject obj = new JSONObject();
                obj.put("bitmap", Godbgallery.listdata.get(i));
                obj.put("date", Godbgallery.arrayname[i]);
                jArray.put(obj);
            }
        }catch(JSONException e) {
            e.printStackTrace();
        }
        adapter = new gridAdapter(jArray);
        gridView.setAdapter(adapter);
        }


    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    private String getStringFromBitmap(Bitmap bitmap) {
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public class JSONPOST extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date time = new Date();
                String time1 = format1.format(time);
                jsonObject.accumulate("bitmap", a);  //a는 String
                jsonObject.accumulate("date", time1);
                HttpURLConnection con = null;   //http client 객체 생성
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();    //con : 연결 객체


                    // 연결 객체를 POST 방식으로 설정,
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);
                    con.connect();//연결 수행

                    //입력 스트림 생성
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));


                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            switch (requestCode) {
                case MULTIPLE_PERMISSIONS: {
                    if (grantResults.length > 0) {
                        for (int i = 0; i < permissions.length; i++) {
                            if (permissions[i].equals(this.permissions[0])) {
                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                    showNoPermissionToastAndFinish();
                                }
                            } else if (permissions[i].equals(this.permissions[1])) {
                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                    showNoPermissionToastAndFinish();

                                }
                            } else if (permissions[i].equals(this.permissions[2])) {
                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                    showNoPermissionToastAndFinish();

                                }
                            }
                        }
                    } else {
                        showNoPermissionToastAndFinish();
                    }
                    return;
                }
            }
        }

        private void showNoPermissionToastAndFinish() {
            Toast.makeText(getContext(), "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Bitmap bitmap = null;
            if (resultCode != getActivity().RESULT_OK) {
                Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (requestCode == PICK_FROM_ALBUM) {
                if (data == null) {
                    return;
                }
                photoUri = data.getData();
                try {
                    InputStream in = getActivity().getContentResolver().openInputStream(photoUri);
                    bitmap = BitmapFactory.decodeStream(in);
                    in.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == PICK_FROM_CAMERA) {
                MediaScannerConnection.scanFile(getActivity().getApplicationContext(), new String[]{photoUri.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.v("File scan", "file:" + path + "was scanned successfully");
                    }
                });
                try {
                    bitmap = BitmapFactory.decodeFile(filepath);
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage().toString());
                }

            }
            JSONObject sObject = new JSONObject();
            try {
                sObject.put("bitmap", bitmap);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date time = new Date();
                String time2 = format1.format(time);
                sObject.put("date", time2);
                sObject.put("Uri", photoUri.toString());
                jArray.put(sObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new gridAdapter(jArray);
            gridView.setAdapter(adapter);
        }

    public void refreshmedia(File file) {
        MediaScannerConnection.scanFile(getContext(), new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.v("File scan", "file:" + path + "was scanned successfully");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.tab2, container, false);
        btnCamera = layout.findViewById(R.id.btn_camera);
        btnAlbum = layout.findViewById(R.id.btn_album);
        btnSend = layout.findViewById(R.id.btn_send);
        btnCloud= layout.findViewById(R.id.btn_cloud);
        btnSync = layout.findViewById(R.id.btn_sync);
        gridView = layout.findViewById(R.id.gridView1);
        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnCloud.setOnClickListener(this);
        btnSync.setOnClickListener(this);
        return layout;
    }
}