package edu.signin.android.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import edu.signin.android.R;
import edu.signin.android.bean.ActivityBean;
import edu.signin.android.databinding.ItemActivityBinding;
import edu.signin.android.ui.ActivityDetailActivity;
import edu.signin.android.utils.DateUtils;
import edu.signin.android.utils.GsonUtils;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityHolder> {

    private List<ActivityBean> data = new ArrayList<>();

    @NonNull
    @Override
    public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActivityBinding itemActivityBinding = ItemActivityBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ActivityHolder(itemActivityBinding.getRoot(), itemActivityBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {

        ActivityBean bean = this.data.get(holder.getAdapterPosition());
        holder.dataBinding.tvName.setText(bean.getName());
        holder.dataBinding.tvLocation.setText(bean.getLocation());
        if (bean.getDate() > System.currentTimeMillis()){
            holder.dataBinding.tvDate.setTextColor(holder.itemView.getResources().getColor(R.color.teal_700));
        }else {
            holder.dataBinding.tvDate.setTextColor(Color.parseColor("#ff0000"));
        }
        holder.dataBinding.tvDate.setText(DateUtils.formatMill(bean.getDate()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ActivityDetailActivity.class);
                intent.putExtra("item", GsonUtils.toJson(bean));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    public void setData(List<ActivityBean> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public static class ActivityHolder extends RecyclerView.ViewHolder{

        private final ItemActivityBinding dataBinding;

        public ActivityHolder(@NonNull View itemView, ItemActivityBinding dataBinding) {
            super(itemView);
            this.dataBinding = dataBinding;
        }

    }
}
