package watted.imep;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
/**
 * Created by ANDROID on 22/11/2017.
 */


//adapter: Adapts the ArrayList<Palace> to the UI RecyclerView
public class PalacAdapter extends RecyclerView.Adapter<PalacAdapter.PlacesViewHolder> {

    //Fields:
    private ArrayList<Place> places;
    private Context context;
    //inflater -> takes an xml as a parameter and Creates a fully fledged android View from it.
    private LayoutInflater inflater;

    //Constructor:
    public PalacAdapter(ArrayList<Place> places, Context context) {

        this.places = places;
        this.context = context;

        this.inflater = LayoutInflater.from(context);
    }

    //creates a view holder:
    @Override
    public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //take an xml and convert it to a view object.

        View v = inflater.inflate(R.layout.palace_item, parent, false);

        //create a view holder and return it the caller of the method.
        return new PlacesViewHolder(v);
    }

    //take a palace and bind it to the view.
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    @Override
    public void onBindViewHolder(final PlacesViewHolder holder, final int position) {
        //position: index of the current palace:
        final Place palace = places.get(position);


        holder.CentereName.setText(palace.getname());
        holder.PalaceName.setText(palace.getVicinity());
        holder.distance.setText(df2.format(palace.getDistance())+"Km");
        String url = palace.getIcone();

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Context context = v.getContext();

                try {
                     String url = "https://waze.com/ul?ll=" + places.get(position).getLat().toString() + "," + places.get(position).getLng().toString();
                    System.out.println("https://waze.com/ul?ll=" + places.get(position).getLat().toString() + "," + places.get(position).getLng().toString());

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // If Waze is not installed, open it in Google Play:
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                    context.startActivity(intent);
                }

            }
        });

        Picasso.with(context).
                load(url).
                placeholder(R.drawable.ic_placeholder).
                error(R.drawable.ic_error).
                into(holder.ivPoster);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    //one reusable view:
    //static inner class
    //JOB title: findView by id...
    public static class PlacesViewHolder extends RecyclerView.ViewHolder {
        //no encapsulation for efficiency:
        TextView CentereName, PalaceName,distance;
        ImageView ivPoster;
        View v;

        //constructor:
        public PlacesViewHolder(View v) {
            super(v);
            this.v = v;
            ivPoster = v.findViewById(R.id.ivPoster);
            CentereName = v.findViewById(R.id.CentereName);
            PalaceName = v.findViewById(R.id.PalaceName);
            distance = v.findViewById(R.id.dis);
        }
    }
}


