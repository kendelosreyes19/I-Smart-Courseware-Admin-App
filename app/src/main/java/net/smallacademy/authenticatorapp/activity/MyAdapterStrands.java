package net.smallacademy.authenticatorapp.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smallacademy.authenticatorapp.R;

import java.util.ArrayList;

public class MyAdapterStrands extends RecyclerView.Adapter<MyViewHolderStrands> {

    Strands strands;
    ArrayList<DownModelStrands> downModelStrands;

    public MyAdapterStrands(Strands strands, ArrayList<DownModelStrands> downModelStrands) {
        this.strands = strands;
        this.downModelStrands = downModelStrands;
    }

    @NonNull
    @Override
    public MyViewHolderStrands onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(strands.getBaseContext());
        View view = layoutInflater.inflate(R.layout.elementsstrands, null, false);

        return new MyViewHolderStrands(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderStrands myViewHolderStrands, final int i) {

        myViewHolderStrands.mName.setText(downModelStrands.get(i).getName());
        myViewHolderStrands.mLink.setText(downModelStrands.get(i).getLink());
        myViewHolderStrands.mDate.setText(downModelStrands.get(i).getDate());
    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);
    }


    @Override
    public int getItemCount() {
        return downModelStrands.size();
    }
}
