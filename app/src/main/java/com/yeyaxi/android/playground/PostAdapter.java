package com.yeyaxi.android.playground;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeyaxi.android.playground.model.Post;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> dataSource;

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        if (this.dataSource == null) {
            return;
        }
        Post post = this.dataSource.get(position);
        if (post == null) {
            return;
        }
        holder.title.setText(post.getTitle());
    }

    @Override
    public int getItemCount() {
        return this.dataSource == null ? 0 : this.dataSource.size();
    }

    public void setDataSource(List<Post> dataSource) {
        this.dataSource = dataSource;
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.title)
        TextView title;

        public PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
