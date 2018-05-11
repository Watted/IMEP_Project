package watted.imep;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<FriendlyMessage> {
    ImageView photoImageView;
    TextView messageTextView;
    LinearLayout LinearLayout;
    TextView authorTextView;
    FriendlyMessage message;
    public MessageAdapter(Context context, int resource, List<FriendlyMessage> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }
        LinearLayout=  (LinearLayout) convertView.findViewById(R.id.linearLayout);

        photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);

        authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);

         message = getItem(position);
        if(authorTextView.getText().toString()==message.getName()) {
            Toast.makeText(getContext(),this.authorTextView.getText().toString(),Toast.LENGTH_LONG).show();
            //messageTextView.setBackgroundColor(0xFF9FED75);
            messageTextView.setBackgroundResource(R.drawable.textred);
             LinearLayout.setGravity(Gravity.LEFT);


        }
        else if(authorTextView.getText().toString()!=message.getName()){


            messageTextView.setBackgroundResource(R.drawable.textblue);
            LinearLayout.setGravity(Gravity.RIGHT);

            //authorTextView.setText(message.getName());

        }
        boolean isPhoto = message.getPhotoUrl() != null;
        if (isPhoto) {
            messageTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(photoImageView);
        } else {

            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            messageTextView.setText("   "+message.getText()+" ");

        }


        authorTextView.setText(message.getName());

        return convertView;
    }

}
