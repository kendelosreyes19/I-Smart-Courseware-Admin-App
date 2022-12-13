package net.smallacademy.authenticatorapp.activity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smallacademy.authenticatorapp.R;

public class MyViewHolderStrands extends RecyclerView.ViewHolder {

    TextView mName;
    TextView mLink;
    TextView mDate;
    Button mDownload;

    public MyViewHolderStrands(@NonNull View itemView) {
        super(itemView);

        mName=itemView.findViewById(R.id.nameStrand);
        mLink=itemView.findViewById(R.id.linkStrand);
        mDate=itemView.findViewById(R.id.dateStrand);
    }
}
