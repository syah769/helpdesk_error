package com.example.syahril.yourtaskapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.syahril.yourtaskapp.Model.Data;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Data> {

    List<Data> notelist;
    Context mcontext;
    int mres;

    public CustomAdapter(Context context, int resource, List<Data> objects ) {

        super(context, resource, objects);
        this.notelist=objects;
        this.mcontext=context;
        this.mres=resource;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(mres,parent,false);
        }

        final Data g=notelist.get(position);

        TextView tv1 =(TextView) convertView.findViewById(R.id.title);
        TextView tv2 =(TextView) convertView.findViewById(R.id.note);

        CheckBox cbox = (CheckBox) convertView.findViewById(R.id.check_box_status);
        final AlertDialog.Builder builder = new AlertDialog.Builder((HomeActivity)mcontext);
        tv1.setText(g.getTitle());
        tv2.setText(g.getNote());

        cbox.setChecked(g.getStatus().equals("0")?false:true);
        cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                {
                    if (isChecked){
                        g.getStatus("1");
                        ((HomeActivity) mcontext).updateStatus(g,position);
                    }
                    else
                    {
                        builder.setTitle(R.string.pendinemsg);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                g.setStatus("0");
                                ((HomeActivity) mcontext).updateStatus(g,position);
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        });

                        builder.create().show();
                    }
                }
            }
        });
    }
}
