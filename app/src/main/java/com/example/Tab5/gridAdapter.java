package com.example.Tab5;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.Tab5.Tab2;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class gridAdapter extends BaseAdapter {
    JSONArray mData = null;
    ImageView icon;
    int k;
    gridAdapter(JSONArray list) {
        mData = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView icon;
        TextView name;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
//            textView1 = itemView.findViewById(R.id.text1) ;
            icon = itemView.findViewById(R.id.icon) ;
            name=itemView.findViewById(R.id.name);

            itemView.setOnCreateContextMenuListener(this);
        }
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
            MenuItem Delete=menu.add(Menu.NONE, 1002, 2, "Delete");
            Delete.setOnMenuItemClickListener(onEditMenu);
        }
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case 1002: //삭제
                        Jremove(k);
                        Tab2.initialize(mData);
                        break;

                }
                return true;
            }
        };
    }


    public void Jremove(int index){
        int len=mData.length();
        JSONArray output=new JSONArray();
        for(int i=0;i<len;i++){
            if(i !=index){
                try{
                    output.put(mData.get(i));

                } catch(JSONException e){
                    throw new RuntimeException(e);
                }
            }
        }
        mData=output;
    }
    public final int getCount() {

        return mData.length();
    }

    public final Object getItem(int position) {
        Object ret = null;
        try {
            ret = mData.getJSONObject(position);
        } catch (JSONException e) {
            e.getStackTrace();
        }
        return ret;
    }

    public final long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        k=position;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridview, parent, false);
            convertView.setLayoutParams(new GridView.LayoutParams(500, 400));
        }
        holder = new ViewHolder(convertView);
        holder.icon = convertView.findViewById(R.id.imageView1);
        if (mData != null) {
            try {
                JSONObject item = mData.getJSONObject(position);
                Bitmap photo = (Bitmap) item.get("Photo");
                if (photo != null) {
                    holder.icon.setImageBitmap(photo);
                } else {

                }

            } catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception");
                e.getStackTrace();
            }

        }
        return convertView;

    }
}
