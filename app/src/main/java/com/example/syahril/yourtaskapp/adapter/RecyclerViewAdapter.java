package com.example.syahril.yourtaskapp.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.syahril.yourtaskapp.HomeActivity;
import com.example.syahril.yourtaskapp.MainActivity;
import com.example.syahril.yourtaskapp.Model.Data;
import com.example.syahril.yourtaskapp.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by JUNED on 6/16/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Data> list;
    private ClickListener mClickListener = null;
    private CheckBoxListener checkBoxListener1=null;

    private Context context;
    private DatabaseReference database;

    public RecyclerViewAdapter(DatabaseReference db, Context ctx, List<Data> list2, ClickListener listener,CheckBoxListener checkBoxListener) {

        context = ctx;
        list = list2;
        mClickListener = listener;
        database = db;
        checkBoxListener1=checkBoxListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mNote;
        public TextView mStaff;
        public TextView mDate;
        public ImageView imgStatus;
        private CheckBox checkBox;
        public CardView root;

        public ViewHolder(View v) {

            super(v);
            root = v.findViewById(R.id.card_view);
            mTitle = v.findViewById(R.id.title);
            mNote = v.findViewById(R.id.note);
            mStaff = v.findViewById(R.id.staff);
            mDate = v.findViewById(R.id.date);
            imgStatus = v.findViewById(R.id.img_status);
            checkBox = v.findViewById(R.id.check_box);
        }
    }


    public void setImageStatus(ImageView imgStatus, int status) {

        if (status == 0) {

            ImageViewCompat.setImageTintList(imgStatus,
                    ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.colorAccent)));
            imgStatus.setImageResource(R.drawable.new_box);

        } else if (status == 1) {
            ImageViewCompat.setImageTintList(imgStatus,
                    ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.colorSuccess)));
            imgStatus.setImageResource(R.drawable.check_circle);
        } else {
            ImageViewCompat.setImageTintList(imgStatus,
                    ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.colorPending)));
            imgStatus.setImageResource(R.drawable.account_clock);
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view1 = LayoutInflater.from(context).inflate(R.layout.item_data, parent, false);

        final ViewHolder viewHolder1 = new ViewHolder(view1);
        viewHolder1.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder1.getAdapterPosition();
//                viewHolder1.tvPetOwner
                mClickListener.btnClick(list.get(position), position);
            }
        });

        return viewHolder1;
    }

    public void update(List<Data> list) {
        this.list = list;
    }

    @Override
    public void onBindViewHolder(final ViewHolder Vholder, final int position) {


        Data data = list.get(position);
//         String post_key = getRef(position).getKey();
        Vholder.mTitle.setText(data.getTitle());
        Vholder.mNote.setText(data.getNote());

        Vholder.mStaff.setText("Assigned to: " + data.getStaff());
        String date = convertDateBasedOnDay(data.getDate());
        if (!date.equalsIgnoreCase("TODAY")) {
            if (data.getStatus() == 0) {
                data.setStatus(2);
            }
        }
        setImageStatus(Vholder.imgStatus, data.getStatus());

        Vholder.mDate.setText(date);
        updateCheckBox(Vholder.checkBox, data);


    }

    public void updateCheckBox(CheckBox checkBox, final Data data) {
        if (data.getStatus() == 0 || data.getStatus() == 2) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    checkBoxListener1.clickCheckBox(data);
//                    data.setStatus(1);//success
//
//                    //fire listener
//                    database.child(data.getParentNode()).setValue(data, new DatabaseReference.CompletionListener() {
//                        public void onComplete(DatabaseError error, DatabaseReference ref) {
//                            System.out.println("Value was set. Error = " + error);
//                        }
//                    });
                }


            }
        });
    }

    public String convertDateBasedOnDay(String givenDateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        long timeInMilliseconds = 0;
        String convertedDate = "";
        try {
            Date mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();
            convertedDate = convertTime(timeInMilliseconds);
            System.out.println("cobvert " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }


    private String convertTime(long created) {
        Date date = null;
        Date curDate = null;
        Date currentTime = null;
        //check if today,display time,if yesterday display yesterday.if more than 2 days,display date
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        ///  String dateString = formatter.format(new Date(created * 1000L));
        String dateString = DateFormat.getDateTimeInstance().format(new Date(created));


        String currDate = formatter.format(new Date());

        //// currentTime = Calendar.getInstance().getTime();
        try {

            date = formatter.parse(dateString);
            curDate = formatter.parse(currDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (curDate.compareTo(date) > 0) {
            //// System.out.println("Date1 is after Date2");
            //calculate if 1 day diff,display yesterday,if more,display date
            int diff = showDiff(date, curDate);
            if (diff == 1) {
                dateString = "YESTERDAY";
            } else {
                SimpleDateFormat formatterDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                dateString = formatterDate.format(new Date(created));
            }


        } else if (curDate.compareTo(date) < 0) {
            //imposible
            ////System.out.println("Date1 is before Date2");
        } else if (curDate.compareTo(date) == 0) {
            //today
//            SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm",Locale.getDefault());
//            dateString=formatterTime.format(new Date(created));
            dateString = "TODAY";
            ////  System.out.println("Date1 is equal to Date2");
        } else {
            System.out.println("How to get here?");
        }


        return dateString;
    }

    public int showDiff(Date before, Date now) {
        Period age = new Period(new DateTime(before), new DateTime(now));

        int days = age.getDays();
        return days;
    }


    @Override
    public int getItemCount() {
        return list.size();

    }

}