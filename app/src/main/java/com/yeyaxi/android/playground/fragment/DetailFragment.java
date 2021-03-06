package com.yeyaxi.android.playground.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yeyaxi.android.playground.R;
import com.yeyaxi.android.playground.api.ApiClient;
import com.yeyaxi.android.playground.constant.Params;
import com.yeyaxi.android.playground.model.Comment;
import com.yeyaxi.android.playground.model.Post;
import com.yeyaxi.android.playground.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailFragment extends Fragment {

    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.comments_count)
    TextView commentsCount;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.body)
    TextView body;
    Unbinder unbinder;

    private ApiClient apiClient;

    public static DetailFragment newInstance(Bundle  extra) {
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(extra);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_post_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        this.apiClient = new ApiClient();

        if (args != null) {
            Long postId = args.getLong(Params.PARAM_POST_ID);
            Long userId = args.getLong(Params.PARAM_USER_ID);

            Observable<List<Comment>> commentObservable = this.apiClient.getApiInterface().getComments(postId);
            Observable<User> userObservable = this.apiClient.getApiInterface().getUser(userId);
            Observable<Post> postObservable = this.apiClient.getApiInterface().getPost(postId);

            Observable zipped = Observable.zip(commentObservable, userObservable, postObservable, (comments, user, post) -> {
                post.setUser(user);
                post.setComments(comments);
                return post;
            });

            zipped.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            post -> {
                                this.fillView((Post)post);
                            },
                            throwable -> {
                                showError();
                            }
                    );
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void fillView(Post post) {
        if (!isResumed() || post == null) {
            return;
        }

        this.title.setText(post.getTitle());
        this.body.setText(post.getBody());

        User user = post.getUser();
        if (user != null) {
            this.userName.setText(user.getName());
            String path = Params.IMAGE_BASE_PATH + user.getEmail() + ".png";
            Picasso.with(getContext()).load(path).into(this.imageView);
        }

        List<Comment> comments = post.getComments();
        if (comments != null) {
            this.commentsCount.setText(String.format(getResources().getQuantityString(R.plurals.plural_comment, comments.size()), comments.size()));
        }
    }

    private void showError() {
        if (!isResumed()) {
            return;
        }
        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
    }
}

