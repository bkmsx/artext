package com.fxbind.textphoto.text;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fxbind.textphoto.R;
import com.fxbind.textphoto.helper.Utils;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/18/2016.
 */

public class FontAdapter extends ArrayAdapter<String> {
    private int selectedPosition;
    private ArrayList<Boolean> mListFavorite;
    public boolean onlyFavorite;
    private Context mContext;

    public FontAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mListFavorite = getListFavorite();
        for (int i = mListFavorite.size(); i < objects.size(); i++) {
            mListFavorite.add(false);
        }
        selectedPosition = getSelectedFont();
    }

    public void saveListFavorite(){
        String stringFavorite = "";
        for (boolean favorite : mListFavorite) {
            stringFavorite += favorite + " ";
        }
        Utils.getSharedPref(mContext).edit()
                .putString(mContext.getString(R.string.list_favorite_font), stringFavorite).apply();

    }

    public void saveSeletedFont() {
        Utils.getSharedPref(mContext).edit()
                .putInt(mContext.getString(R.string.selected_font), selectedPosition).apply();
    }

    public int getSelectedFont() {
        return Utils.getSharedPref(mContext).getInt(mContext.getString(R.string.selected_font), 0);
    }

    public ArrayList<Boolean> getListFavorite(){
        String stringFavorite = Utils.getSharedPref(mContext)
                .getString(mContext.getString(R.string.list_favorite_font),"");
        String[] favorites = stringFavorite.split(" ");
        ArrayList<Boolean> listFavorite = new ArrayList<>();
        for (int i = 0; i < favorites.length; i++) {
            listFavorite.add(Boolean.parseBoolean(favorites[i]));
        }
        return listFavorite;
    }

    public void showOnlyFavorite() {
        onlyFavorite = true;
        notifyDataSetChanged();
    }

    public void showAllFont() {
        onlyFavorite = false;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_font_item, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 90);
        view.setLayoutParams(params);
        if (selectedPosition == position) {
            view.setBackgroundColor(Color.RED);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        TextView textView = (TextView) view.findViewById(R.id.text_view);
        ImageView imageview = (ImageView) view.findViewById(R.id.favorite);
        imageview.setOnClickListener(onFavoriteClick);
        imageview.setTag(position);
        if (mListFavorite.get(position)) {
            setFavorite(imageview, position);
        } else {
            unFavorite(imageview, position);
        }
        Typeface typeface = Typeface.createFromFile(getItem(position));
        textView.setTypeface(typeface);

        View emptyView = new View(getContext());

        if (onlyFavorite) {
            if (!mListFavorite.get(position)) {
                return emptyView;
            }
        }
        return view;
    }

    View.OnClickListener onFavoriteClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = Integer.parseInt(view.getTag().toString());
            if (mListFavorite.get(position)) {
                unFavorite(view, position);
            } else {
                setFavorite(view, position);
            }
            notifyDataSetChanged();
        }
    };

    private void setFavorite(View view, int position) {
        ((ImageView) view).setImageResource(R.drawable.ic_star_selected);
        mListFavorite.set(position, true);
    }
    private void unFavorite(View view, int position) {
        ((ImageView) view).setImageResource(R.drawable.ic_star_normal);
        mListFavorite.set(position, false);
    }
}
