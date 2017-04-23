package androdevians.pilotplus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by sanjit on 23/4/17.
 */

public class PlaceOfInterestAdapter extends ArrayAdapter<PlaceOfInterest> {
    public PlaceOfInterestAdapter(@NonNull Context context, @NonNull List<PlaceOfInterest> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newView = convertView;
        if (newView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) (getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            newView = layoutInflater.inflate(R.layout.place_of_interest_item, null);
        }
        PlaceOfInterest placeOfInterest = getItem(position);
        TextView PlaceName = (TextView) newView.findViewById(R.id.place_name);
        PlaceName.setText(placeOfInterest.getName());

//        ImageView Icon = (ImageView) newView.findViewById(R.id.place_icon);
//        Glide.with(getContext()).load(placeOfInterest.getId()).into(Icon);

        return newView;
    }
}
