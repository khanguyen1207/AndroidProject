package com.example.khanguyen.gasconsumption;

/**
 * Created by khanguyen on 01/05/16.
 */
/**
 * Created by khanguyen on 27/04/16.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by zozo on 13.2.2016.
 */
public class CustomAdapter extends ArrayAdapter<String> {
    String[] date={};
    String[] distance={};
    String[] volume={};
    String[] result={};
    Context c;
    LayoutInflater inflater;

    public CustomAdapter(Context context, String[] date, String[] distance, String[] volume,String[] result){
        super(context,R.layout.custom_adapter,date);
        this.c= context;
        this.date=date;
        this.distance=distance;
        this.volume=volume;
        this.result=result;
    }


    public class ViewHolder{
        TextView date;
        TextView distance;
        TextView volume;
        TextView result;

    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.custom_adapter, null);

        }
        final ViewHolder holder = new ViewHolder();
        holder.date=(TextView)convertView.findViewById(R.id.date);
        holder.distance=(TextView)convertView.findViewById(R.id.distance);
        holder.volume=(TextView)convertView.findViewById(R.id.volume);
        holder.result=(TextView)convertView.findViewById(R.id.result);
        holder.date.setText(date[position]);
        holder.distance.setText(distance[position]);
        holder.volume.setText(volume[position]);
        holder.result.setText(result[position]);
        return convertView;
    }
}