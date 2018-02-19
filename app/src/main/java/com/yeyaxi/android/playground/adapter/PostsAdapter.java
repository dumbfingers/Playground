package com.yeyaxi.android.playground.adapter;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeyaxi.android.playground.R;
import com.yeyaxi.android.playground.interfaces.OnPostClick;
import com.yeyaxi.android.playground.model.Post;
import com.yeyaxi.android.playground.util.AvatarUriUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    private List<Post> dataSource;
    private OnPostClick delegate;

    @Override
    public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostsViewHolder holder, int position) {
        if (this.dataSource == null) {
            return;
        }
        Post post = this.dataSource.get(position);
        if (post == null) {
            return;
        }
        holder.title.setText(post.getTitle());
        holder.contentPreview.setText(post.getBody());
        holder.userName.setText(post.getUser().getName());
        holder.itemView.setOnClickListener(view -> {
            if (this.delegate != null) {
                this.delegate.onPostClick(holder.imageView, post);
            }
        });

        String uri = AvatarUriUtil.getAvatarUri(post.getUser().getEmail());
        Picasso.with(holder.itemView.getContext()).load(uri).into(holder.imageView);
        Picasso.with(holder.itemView.getContext()).load(uri).into(holder.avatarView);
    }

    @Override
    public int getItemCount() {
        return this.dataSource == null ? 0 : this.dataSource.size();
    }

    public void setDelegate(OnPostClick delegate) {
        this.delegate = delegate;
    }

    public void setDataSource(List<Post> dataSource) {
        this.dataSource = dataSource;
    }

    class PostsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.avatar_view)
        AppCompatImageView avatarView;
        @BindView(R.id.user_name)
        TextView userName;
        @BindView(R.id.content_preview)
        TextView contentPreview;

        public PostsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
