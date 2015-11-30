package nfctutorials.tutorial04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by masterUNG on 11/30/15 AD.
 */
public class MyAdapter extends BaseAdapter{

    //Explicit
    private Context objContext;
    private String[] deviceStrings;

    public MyAdapter(Context objContext, String[] deviceStrings) {
        this.objContext = objContext;
        this.deviceStrings = deviceStrings;
    }   // Constructor

    @Override
    public int getCount() {
        return deviceStrings.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater objLayoutInflater = (LayoutInflater) objContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View objView1 = objLayoutInflater.inflate(R.layout.my_listview, viewGroup, false);

        //Setup Title Device
        TextView deviceTextView = (TextView) objView1.findViewById(R.id.txtListDevice);
        deviceTextView.setText(deviceStrings[i]);

        return objView1;
    }
}   // Main Class
