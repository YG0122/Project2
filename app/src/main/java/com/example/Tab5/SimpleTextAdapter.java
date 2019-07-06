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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;

public class SimpleTextAdapter extends RecyclerView.Adapter<SimpleTextAdapter.ViewHolder> {
    private ViewGroup Parent;
    private JSONArray mData = null ;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView icon ;
        ImageButton call;
        ImageButton message;
        TextView name;
        TextView number;
        private ArrayList<Dictionary> mList;
        private Context mContext;
        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
//            textView1 = itemView.findViewById(R.id.text1) ;
            icon = itemView.findViewById(R.id.icon) ;
            name=itemView.findViewById(R.id.name);
            number=itemView.findViewById(R.id.number);
            call=itemView.findViewById(R.id.call);
            message=itemView.findViewById(R.id.message);

            itemView.setOnCreateContextMenuListener(this);
        }
        public void onCreateContextMenu(ContextMenu menu,View v, ContextMenu.ContextMenuInfo menuInfo){
            MenuItem Edit=menu.add(Menu.NONE, 1001, 1, "Edit");
            MenuItem Delete=menu.add(Menu.NONE, 1002, 2, "Delete");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:  //  편집 항목을 선택시
                        try {
                            Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse("content://contacts/people/" + String.valueOf(mData.getJSONObject(getAdapterPosition()).get("ID"))));
                            itemView.getContext().startActivity(intent);
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                        break;

                    case 1002: //삭제

//                       Jremove(getAdapterPosition());
                        try{
//                            Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("content://contacts/people/"+String.valueOf(mData.getJSONObject(getAdapterPosition()).get("ID"))));
//                            itemView.getContext().startActivity(intent);
                            itemView.getContext().getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.RawContacts.CONTACT_ID + "=" + String.valueOf(mData.getJSONObject(getAdapterPosition()).get("ID")), null);
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                        Jremove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), mData.length());


                        break;

                }
                return true;
            }
        };
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    SimpleTextAdapter(JSONArray list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public SimpleTextAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Parent=parent;
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recyclerview_item, parent, false) ;
        SimpleTextAdapter.ViewHolder vh = new SimpleTextAdapter.ViewHolder(view) ;

        return vh ;
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

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(SimpleTextAdapter.ViewHolder holder, int position)  {
        try {
            JSONObject item = mData.getJSONObject(position);
            Bitmap photo=(Bitmap)item.get("Photo");
            if(photo!=null){
                holder.icon.setImageBitmap((Bitmap)item.get("Photo"));
            }
            else{

                holder.icon.setImageDrawable(Parent.getContext().getResources().getDrawable(R.drawable.profileicon));
            }
            holder.name.setText((String)item.get("Name"));
            holder.number.setText((String)item.get("Phone Number"));
//            String name = (String) mData.getJSONObject(position).get("Name");
        } catch(JSONException e){
            Log.e("MYAPP", "unexpected JSON exception");
            e.getStackTrace();
        }

        //연락처에 전화를 걸고 싶을 때
        final String pn=holder.number.getText().toString();
        holder.call.setOnClickListener(new View.OnClickListener() {
            String tel="tel:"+pn;
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));

            }
        });
//연락처에 문자보내고 싶을 때
        holder.message.setOnClickListener(new View.OnClickListener() {
            String sms="sms:"+pn;
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(sms)));

            }
        });

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {

        return mData.length();
    }

}
