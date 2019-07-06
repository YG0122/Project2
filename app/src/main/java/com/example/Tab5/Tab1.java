package com.example.Tab5;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class Tab1 extends Fragment {
    SimpleTextAdapter mAdapter;
    RecyclerView mRecyclerView=null;
    private ImageButton btn_plus;
    private ImageButton btn_minus;
    JSONArray jArray = new JSONArray();
    @Override
   public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        initialize();

        }


        public Bitmap resizingBitmap(Bitmap oBitmap) {
            if (oBitmap == null)
                return null;
            float width = oBitmap.getWidth();
            float height = oBitmap.getHeight();
            float resizing_size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,55, getResources().getDisplayMetrics());
            Bitmap rBitmap = null;
//        if (width > resizing_size) {
//            float mWidth = (float) (width / 100);
//            float fScale = (float) (resizing_size / mWidth);
//            width *= (fScale / 100);
//            height *= (fScale / 100);
//
//        } else if (height > resizing_size) {
//            float mHeight = (float) (height / 100);
//            float fScale = (float) (resizing_size / mHeight);
//            width *= (fScale / 100);
//            height *= (fScale / 100);
//        }
            float mWidth = (float) (width / 100);
            float fScale = (float) (resizing_size / mWidth);
            width *= (fScale / 100);
            height *= (fScale / 100);
//        float mHeight = (float) (height / 100);
//        float fScale = (float) (resizing_size / mHeight);
//        width *= (fScale / 100);
//        height *= (fScale / 100);

//        Log.d("rBitmap : " + width + ", " + height);
            rBitmap = Bitmap.createScaledBitmap(oBitmap, (int) width, (int) height, true);
            return rBitmap;

        }
    @Override
    public void onResume() {
        super.onResume();
        initialize();
    }
    public void initialize(){
        Cursor c = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);


        Bitmap photo;
        try {
            while (c.moveToNext()) {
                JSONObject sObject = new JSONObject();
                String contactID = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                long id=Long.parseLong(contactID);
                ContentResolver cr= getActivity().getContentResolver();
                Uri imageUrl = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
                InputStream io = ContactsContract.Contacts.openContactPhotoInputStream(cr, imageUrl);

                if(io!=null){
                    photo=resizingBitmap(BitmapFactory.decodeStream(io));

                }
                else{photo=resizingBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.profileicon));
                }
                sObject.put("ID", contactID);
                sObject.put("Name", contactName);
                sObject.put("Phone Number", phNumber);

                sObject.put("Photo", photo);
                jArray.put(sObject);


            }
        }catch(JSONException e){
            System.out.print("TT");
            Log.e("MYAPP", "Unexpected");
            System.out.print("EE");
        }
        c.close();
        mAdapter = new SimpleTextAdapter(jArray);
    }
    private void refresh(){
        FragmentTransaction transaction=getFragmentManager().beginTransaction();

        transaction.detach(this).attach(this).commit();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Bitmap CallIcon = BitmapFactory.decodeResource(getResources(), R.drawable.greenphone);
        final Bitmap MIcon = BitmapFactory.decodeResource(getResources(), R.drawable.micon);




        View rootView = inflater.inflate(R.layout.tab1, container, false);
        btn_plus=rootView.findViewById(R.id.plus);
        btn_minus=rootView.findViewById(R.id.minus);

        mRecyclerView = rootView.findViewById(R.id.recycler1);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper.SimpleCallback myCallback =  new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
                try {
                    final String pn = String.valueOf(jArray.getJSONObject(viewHolder.getAdapterPosition()).get("Phone Number"));
                    String tel = "tel:" + pn;
                    Intent intent=new Intent(getView().getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getView().getContext().startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                    getActivity().finish();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
                c.clipRect(0f, viewHolder.itemView.getTop(), dX, viewHolder.itemView.getBottom());
                c.drawColor(Color.GREEN);
                int textMargin=(int)getResources().getDimension(R.dimen.fab_margin);
//                CallIcon.setBounds(getView().getLeft(), getView().getTop(), getView().getLeft()+CallIcon.getIntrinsicWidth(), getView().getTop()+CallIcon.getIntrinsicHeight());
//                CallIcon.draw(c);
                c.drawBitmap(CallIcon, null, new Rect( 0, viewHolder.itemView.getTop(), 160, viewHolder.itemView.getBottom()), null);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        ItemTouchHelper.SimpleCallback myMessageback =  new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
                try {
                    final String pn = String.valueOf(jArray.getJSONObject(viewHolder.getAdapterPosition()).get("Phone Number"));
                    String sms = "sms:" + pn;
                    Intent intent=new Intent(getView().getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getView().getContext().startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(sms)));
                    getActivity().finish();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
                c.clipRect(viewHolder.itemView.getRight()+dX, viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());
                c.drawColor(Color.YELLOW);
                int textMargin=(int)getResources().getDimension(R.dimen.fab_margin);
//                MIcon.setBounds(textMargin, viewHolder.itemView.getTop()+textMargin, textMargin+MIcon.getIntrinsicWidth(), viewHolder.itemView.getTop()+MIcon.getIntrinsicHeight()+textMargin);
//                MIcon.draw(c);
                c.drawBitmap(MIcon, null, new Rect( viewHolder.itemView.getRight()-160, viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom()), null);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        new ItemTouchHelper(myCallback).attachToRecyclerView(mRecyclerView);
        new ItemTouchHelper(myMessageback).attachToRecyclerView(mRecyclerView);

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_INSERT, Uri.parse("content://contacts/people"));
                startActivity(intent);
            }
        });

        return rootView;
    }

}
