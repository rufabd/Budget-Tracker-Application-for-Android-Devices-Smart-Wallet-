
package com.example.app_dev_money_tracking;

        import android.content.Context;
        import android.database.DataSetObserver;
        import android.graphics.Bitmap;
        import android.media.Image;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.Spinner;
        import android.widget.SpinnerAdapter;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.app_dev_money_tracking.Categories;
        import com.example.app_dev_money_tracking.R;

        import java.util.ArrayList;
        import java.util.List;

public class SpinnedAdapter extends BaseAdapter {

    private List<Categories> categoriesList;
    private Context context;
    private OnItemClickListener listener;

    public SpinnedAdapter(List<Categories> categoriesList, Context context) {
        this.categoriesList = categoriesList;
        this.context = context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return categoriesList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.categories_item, parent, false);
//            view = LayoutInflater.from(context).inflate(R.layout.categories_item, parent);
        }

        ImageView imageView = view.findViewById(R.id.categories_item_image);
        TextView name=view.findViewById(R.id.categories_item_name);
        imageView.setBackgroundResource(categoriesList.get(position).getCategoryImg());
        name.setText(categoriesList.get(position).getCategoryName());
        return view;
    }


    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        ImageView imageView = view.findViewById(R.id.categories_item_image);
        TextView name=view.findViewById(R.id.categories_item_name);
        imageView.setBackgroundResource(categoriesList.get(position).getCategoryImg());
        name.setText(categoriesList.get(position).getCategoryName());
        return view;
    }

//    class SpinnerHolder extends RecyclerView.ViewHolder {
//        TextView categoryName;
//        ImageView categoryImage;
//
//        public CategoriesHolder(@NonNull View itemView) {
//            super(itemView);
//            categoryName = (TextView) itemView.findViewById(R.id.categories_item_name);
//            categoryImage = (ImageView) itemView.findViewById(R.id.categories_item_image);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int position = getAdapterPosition();
//
//                    if(listener != null && position != RecyclerView.NO_POSITION) {
//                        listener.onItemClick(categoriesList.get(position), position);
//                    }
//                }
//            });
//        }
//
//        public void setData(Categories categories) {
//            this.categoryName.setText(categories.getCategoryName());
//            this.categoryImage.setBackgroundResource(categories.getCategoryImg());
//        }
//    }

    public interface OnItemClickListener {
        void onItemClick(Categories categories, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}